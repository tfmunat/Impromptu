import os
import unittest
import json
from Impromptu.Model.user_model import User
from Impromptu.Controllers.user_controller import validateUser
from flask import Flask, request
from mockupdb import MockupDB
from flask_pymongo import PyMongo

def make_app(mongodb_uri):
    app = Flask("testing")
    app.config['MONGODB_URI'] = mongodb_uri
    mongo_mlab =PyMongo(app, config_prefix="MONGODB")

    @app.route("/signup", methods=['POST'])
    def signUp():
    	data = request.get_data()
    	user_obj = User(json.loads(data.decode('utf8')))
    	err_msg = validateUser(user_obj)    	
    	if err_msg != "":
    		response = app.response_class(
					response=json.dumps({"message" : err_msg}),
					status=400,
					mimetype='application/json'
				)
    		return response
    	else:
    		try:
    			docid = mongo_mlab.db.active_users.insert({
    				"first_name" : user_obj.get_first_name(),
    				"last_name" : user_obj.get_last_name(),
    				"fb_id" : user_obj.get_fb_id(),
    				"likes" : user_obj.get_likes(),
    				"is_active" : user_obj.get_is_active()
    				})
    			docid = '123456789'
    			return 'h'
    			response = app.response_class(response=json.dumps({"id" : str(docid)}), status=200, mimetype='application/json')
    		except Exception as e:
    			response = app.response_class(
    				response=json.dumps({"message":"An issue with server."}),
    				status=500,
    				mimetype='application/json'
    				)
    	return response

    return app

class TestCase(unittest.TestCase):
	def setUp(self):
		# Test to check if Flask App runs properly or not		
		self.mock_server = MockupDB(auto_ismaster=True)
		self.mock_server.run()
		assert self.mock_server.uri == "Something"
		self.app = make_app(self.mock_server.uri).test_client()
		

#	def dbAdapter(self):
		# Test to see if the mongodb is connected to properly

	def tearDown(self):
		# Test to see if the application closes gracefully
		pass

#	def integrityChecks(self):
		# Checking to see if Application handles error cases properly		

	def signUp(self):
		return self.app.post('/signup', data = json.dumps({
			'first_name':"John",
			'last_name':"Doe",
			'likes':['testing'],
			'fb_id':"23245665", 
			'is_active': True}), 
		content_type= "application/json")

	def test_signup(self):
		rv = self.signUp()
		assert rv.status_code == 200
		
	def userCreate(self):
		return User({'first_name':"John",'last_name':"Doe",'likes':['testing'],'fb_id':"23245665"})

	def test_userCreate(self):
		u = self.userCreate()
		assert u.first_name == "John"
		assert u.last_name == "Doe"
		assert 'testing' in u.likes
		assert u.fb_id == '23245665'
		assert u.is_active == True