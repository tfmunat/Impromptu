from Impromptu import app, mongo_mlab
from flask import render_template, request, jsonify
import json
from Impromptu.Model.user_model import User


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


@app.route("/save", methods=['GET', 'POST'])
def saveData():
	data = request.get_data()

	list_response = data.split('&')

	first_name = list_response[0].split('=')[1]
	last_name = list_response[1].split('=')[1]
	likes = list_response[2].split('=')[1]

	#print first_name, last_name, likes
	#print list_response[1], list_response[3], list_response[5]

	docid = mongo_mlab.db.active_users.insert({
			'first_name': first_name, 
			'last_name': last_name,
			'likes': likes
			})

	return "Saved with id: ", docid
	#return data & id generated in db.

@app.route("/signup", methods=['POST'])
def signUp():
	data = request.get_data()
	user_obj = User(json.loads(data.decode('utf8')))
	docid = mongo_mlab.db.active_users.insert({
    		"first_name" : user_obj.get_first_name(),
    		"last_name" : user_obj.get_last_name(),
    		"email" : user_obj.get_email(),
    		"likes" : user_obj.get_likes(),
    		"is_active" : user_obj.get_is_active()
     	})
	print(docid)
	response = app.response_class(
        response=json.dumps({"id" : str(docid)}),
        status=200,
        mimetype='application/json'
    )
	return response