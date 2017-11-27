from Impromptu import app, mongo_mlab
from flask import render_template, request, jsonify
import json
from Impromptu.Model.event_model import Event
import re
from bson.objectid import ObjectId
from bson.son import SON

@app.route("/eventadd", methods=['POST'])
def createEvent():
	data = request.get_data()
	if len(data) == 0:
		print('Data load is empty!')
		return 'No'
	# print data
	user_obj = Event(json.loads(data.decode('utf8')))
	err_msg = "" #validateUser(user_obj)
	if err_msg != "":
		response = app.response_class(
				response=json.dumps({"message" : err_msg}),
				status=400,
				mimetype='application/json'
			)
	else:
		try:
			docid = mongo_mlab.db.events.insert({
	    		"title" : user_obj.get_title(),
	    		"description" : user_obj.get_description(),
	    		"geo_loc" : {
	    			"type" : "Point",
	    			"coordinates" : user_obj.get_geo_loc()
	    		},
	    		"place_id" : user_obj.get_place_id(),
	    		"time" : user_obj.get_time(),
	    		"owner" : user_obj.get_owner(),
	    		"category" : user_obj.get_category(),
	    		"attendees" : []
	     	})
			print(user_obj.get_owner())

			# docid_attendance = mongo_mlab.db.attendance.insert({
	  #   		"event_id" : docid,
	  #   		"owner" : user_obj.get_owner(),
	  #   		"attendees" : [user_obj.get_owner()]
	  #    	})

			res = mongo_mlab.db.active_users.find_one_and_update(
				{ "_id" : ObjectId(user_obj.get_owner()) },
				{ "$push" : { "events_attending" : str(docid) }}
			)

			print(res)
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


def searchEventsDist(longitude, latitude, dist):
	try:
		events = mongo_mlab.db.events.find(
			{
			"geo_loc" : SON(
				[("$nearSphere" , [ longitude, latitude ]), ("$maxDistance", dist)]
				)
			})

		response_arr = []

		for event in events:
			owner = getUserDetails([event['owner']])
			users = getUserDetails(event['attendees'])
			del event['attendees']
			event['owner_name'] = owner[0]
			event['_id'] = str(event['_id'])
			event['attendees'] = users
			response_arr.append(event)
		print(response_arr)
		return response_arr

	except Exception as e:
		print(e)
		return []


@app.route("/search/<longitude>/<latitude>/<dist>", methods=['GET'])
def searchEvents(longitude, latitude, dist):
	longitude = float(longitude)
	latitude = float(latitude)
	dist = int(dist)

	response_arr = searchEventsDist(longitude, latitude, dist)
	
	if len(response_arr) > 0:
		response = app.response_class(
			response=json.dumps(response_arr),
			status=200,
			mimetype='application/json'
			)
	else:
		response = app.response_class(
				response=json.dumps({"message":"No nearby events found."}),
				status=500,
				mimetype='application/json'
			)		
	return response


@app.route("/trial/<keywords>", methods=['GET'])
def tryIt(keywords):
	keywords_arr = map(lambda x : x.lower(), keywords.split(","))
	print(list(keywords_arr))
	return app.response_class(
		response=json.dumps({"m" : "o"}),
		status=200,
		mimetype='application/json'
		)

@app.route("/search/<longitude>/<latitude>/<dist>/<keywords>", methods=['GET'])
def searchEventsKeyword(longitude, latitude, dist, keywords):
	longitude = float(longitude)
	latitude = float(latitude)
	dist = int(dist)
	keywords_arr = list(map(lambda x : x.lower(), keywords.split(",")))
	nearby_events = searchEventsDist(longitude, latitude, dist)

	response_arr = []

	for event in nearby_events:
		if event['category'].lower() in keywords_arr:
			response_arr.append(event)

	
	if len(response_arr) > 0:
		response = app.response_class(
			response=json.dumps(response_arr),
			status=200,
			mimetype='application/json'
			)
	else:
		response = app.response_class(
				response=json.dumps({"message":"No nearby events with specified keywords found."}),
				status=500,
				mimetype='application/json'
			)		
	return response