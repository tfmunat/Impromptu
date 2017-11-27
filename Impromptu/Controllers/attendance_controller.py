from Impromptu import app, mongo_mlab
from flask import render_template, request, jsonify
import json
from Impromptu.Model.event_model import Event
import re
from bson.objectid import ObjectId

@app.route("/notify", methods=['POST'])
def notify():
	data = request.get_data()
	if len(data) == 0:
		print('Data load is empty!')
		return 'No'
	# print data
	user_id = json.loads(data.decode('utf8'))['user_id']
	event_id = json.loads(data.decode('utf8'))['event_id']
	
	err_msg = "" #validateUser(user_obj)
	if err_msg != "":
		response = app.response_class(
				response=json.dumps({"message" : err_msg}),
				status=400,
				mimetype='application/json'
			)
	else:
		try:
			
			mongo_mlab.db.events.update(
				{ "_id" : ObjectId(event_id) },
				{ "$push" : { "attendees" : user_id }}
			)
			mongo_mlab.db.active_users.update(
				{ "_id" : ObjectId(user_id) },
				{ "$push" : { "events_attending" : event_id}}
			)

			#print(res)
			response = app.response_class(
		        response=json.dumps({"message" : "Event owner notified."}),
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