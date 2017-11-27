from Impromptu import app, mongo_mlab
from flask import render_template, request, jsonify
import json
from Impromptu.Model.user_model import User
import re
from bson.objectid import ObjectId

# class UserController(object):
# 	"""docstring for ClassName"""
# 	def __init__(self):
# 		super(ClassName, self).__init__()
# 		pass



@app.route("/")
def hello():
    return render_template("input_form.html")

@app.route("/get", methods=['GET', 'POST'])
def getAllUsers():
	# Confirm how to get the list of users
	mongo_query = mongo_mlab.db.active_users.find()
	print(mongo_query)

	users_list = []

	for user in mongo_query:
		user_name = user['first_name'] + " " + user['last_name']
		user_likes =  user['likes']

		users_list.append([user_name,user_likes])

	return jsonify(users_list)


@app.route("/save", methods=['POST'])
def saveData():
	data = request.get_data()

	#print data

	list_response = data.split('&')

	first_name = list_response[0].split('=')[1]
	last_name = list_response[1].split('=')[1]
	likes = list_response[2].split('=')[1].split(',')

	# print 'First Name:', first_name
	# print 'Last Name:', last_name
	# print 'Likes: ', likes

	docid = mongo_mlab.db.active_users.insert({
			'first_name': first_name, 
			'last_name': last_name,
			'likes': likes
			})

	# print "Saved with id: ", docid

@app.route("/signup", methods=['POST'])
def signUp():
	data = request.get_data()
	if len(data) == 0:
		print('Data load is empty!')
		return 'No'
	# print data
	user_obj = User(json.loads(data.decode('utf8')))
	err_msg = validateUser(user_obj)
	if err_msg != "":
		response = app.response_class(
				response=json.dumps({"message" : err_msg}),
				status=400,
				mimetype='application/json'
			)
	else:
		try:
			print(mongo_mlab)
			docid = mongo_mlab.db.active_users.insert({
	    		"first_name" : user_obj.get_first_name(),
	    		"last_name" : user_obj.get_last_name(),
	    		"fb_id" : user_obj.get_fb_id(),
	    		"likes" : user_obj.get_likes(),
	    		"is_active" : user_obj.get_is_active(),
	    		"events_attending" : []
	     	})
			print(docid)
			response = app.response_class(
		        response=json.dumps({"id" : str(docid)}),
		        status=200,
		        mimetype='application/json'
		    )	
		except Exception as e:
			print(e)
			response = app.response_class(
					response=json.dumps({"message":"An issue with server."}),
					status=500,
					mimetype='application/json'
				)
		
	return response

def validateUser(user_obj):
	err_msg = ""
	if re.match(r"[A-Za-z]+", user_obj.first_name) is None:
		err_msg += "Add proper first name, "
	if re.match(r"[A-Za-z]+", user_obj.last_name) is None:
		err_msg += "Add proper last name, "
	if len(user_obj.likes) == 0:
		err_msg += 'Please add at least one interest '
	# if re.match(r"[A-Za-z0-9]+@[A-za-z]+\.[a-z]", user_obj.email) is None:
	# 	err_msg += 'Please send a valid FB ID.'
	return err_msg

def getUserDetails(attendees):
		attendees_obj_ids = [ObjectId(i) for i in attendees]
		print(attendees_obj_ids)
		user_names = []
		try:
			users = mongo_mlab.db.active_users.find(
					{"_id" : {"$in" : attendees_obj_ids} }
				)

			for user in users:
				user_names.append(user["first_name"] + " " + user["last_name"])

			return user_names

		except Exception as e:
			return []


@app.route("/view/<user_id>", methods=['GET'])
def fetchEvents(user_id):
	print(user_id)
	if user_id is None:
		print('Data load is empty!')
		return 'No'


	try:
		user = mongo_mlab.db.active_users.find_one(
			{"_id" : ObjectId(user_id)}
		)
		
		events = [ObjectId(e) for e in user['events_attending']]

		event_details = mongo_mlab.db.events.find(
			{"_id" : {"$in" : events} }
		)

		response_arr = []

		for event in event_details:
			owner = getUserDetails([event['owner']])
			users = getUserDetails(event['attendees'])
			del event['attendees']
			event['owner_name'] = owner[0]
			event['_id'] = str(event['_id'])
			event['attendees'] = users
			response_arr.append(event)
		print(response_arr)
		response = app.response_class(
			response=json.dumps(response_arr),
			status=200,
			mimetype='application/json'
			)	
	except Exception as e:
		print(e)
		response = app.response_class(
				response=json.dumps({"message":"An issue with server."}),
				status=500,
				mimetype='application/json'
			)
	return response
	

	

