from flask import render_template, request, jsonify
import json
import re
from bson.objectid import ObjectId

from Impromptu.Model.event_model import Event
from Impromptu import app, mongo_mlab

@app.route("/notify", methods=['POST'])
def notify():
	data = request.get_data()

	try:
		user_id = json.loads(data.decode('utf8'))['user_id']
		event_id = json.loads(data.decode('utf8'))['event_id']

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

	except Exception as e:
		response = app.response_class(
				response=json.dumps({"message" : str(e)}),
				status=400,
				mimetype='application/json'
			)

	return response