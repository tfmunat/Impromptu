from flask import render_template, request, jsonify
import json
import re
from bson.objectid import ObjectId
from bson.son import SON

from Impromptu import app, mongo_mlab
from Impromptu.Model.event_model import Event
from Impromptu.Controllers.user_controller import getUserDetails

def validateEvent(obj):
	err_msg = ""
	if obj.title == "":
		err_msg += "Add proper title "
	if obj.description == "":
		err_msg += "Add proper description "
	if len(obj.geo_loc) != 2:
		err_msg += 'Please specify location properly '
	if obj.category == "":
		err_msg += 'Please specify category properly '
	if obj.owner == "":
		err_msg += 'Please specify owner properly '
	return err_msg

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

@app.route("/eventadd", methods=['POST'])
def createEvent():
	data = request.get_data()

	try:
		user_obj = Event(json.loads(data.decode('utf8')))
		e = validateEvent(user_obj)
		if (e != ""):
			response = app.response_class(
				response=json.dumps({"message" : e}),
				status=400,
				mimetype='application/json'
			)
		else:
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
				status=400,
				mimetype='application/json'
			)		
	return response

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
				status=400,
				mimetype='application/json'
			)		
	return response