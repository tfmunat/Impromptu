import os
import unittest
import json
import flask_pymongo
import mongomock
import mock
from flask import Flask, request, current_app
from mockupdb import MockupDB

from Impromptu.Model.user_model import User

from Impromptu.Controllers.user_controller import validateUser, getUserDetails, fetchEvents
from Impromptu.Controllers.event_controller import searchEventsDist, searchEvents, searchEventsKeyword

from Impromptu import app

class TestCaseUserController(unittest.TestCase):
	def setUp(self):
		# Test to check if Flask App runs properly or not		
		self.mock_server = mongomock.MongoClient()
		#self.mock_server.run()
		#assert self.mock_server.uri == "Something"
		#self.app = make_app(self.mock_server.uri).test_client()
		#app = generate_app()
        # app.testing = True
		self.context = app.app_context()
		self.app = app.test_client()

	def tearDown(self):
		# Test to see if the application closes gracefully
		pass

	@mock.patch('flask_pymongo.MongoClient')
	def signUp(self, dataload, mock_MongoClient):
		mock_MongoClient.return_value = self.mock_server
		return self.app.post('/signup', data = dataload, 
		content_type= "application/json")

	def test_signup(self):

		correct_dataload = json.dumps({
			'first_name':"John",
			'last_name':"Doe",
			'likes':['testing'],
			'fb_id':"23245665", 
			'is_active': True})

		rv = self.signUp(correct_dataload)
		assert rv.status_code == 200

		incorrect_dataload = json.dumps({
			'first_name':"$ohn",
			'last_name':"Doe",
			'likes':['testing'],
			'fb_id':"23245665", 
			'is_active': True})

		rv = self.signUp(incorrect_dataload)
		assert rv.status_code == 400
		
	def userCreate(self):
		return User({'first_name':"John",'last_name':"Doe",'likes':['testing'],'fb_id':"23245665"})

	def test_userCreate(self):
		u = self.userCreate()
		assert u.first_name == "John"
		assert u.last_name == "Doe"
		assert 'testing' in u.likes
		assert u.fb_id == '23245665'
		assert u.is_active == True

	def test_validateUser(self):
		valid_message = validateUser(self.userCreate())

		assert valid_message == ""

		User_firstName_invalid = User({'first_name':"J$",'last_name':"Doe",'likes':['testing'],'fb_id':"23245665"})
		User_lastName_invalid = User({'first_name':"John",'last_name':"Do$e",'likes':['testing'],'fb_id':"23245665"})
		User_likes_invalid = User({'first_name':"John",'last_name':"Doe",'likes':[],'fb_id':"23245665"})

		invalid_message1 = validateUser(User_firstName_invalid)
		assert invalid_message1 == "Add proper first name, "

		invalid_message2 = validateUser(User_lastName_invalid)
		assert invalid_message2 == "Add proper last name, "

		invalid_message3 = validateUser(User_likes_invalid)
		assert invalid_message3 == 'Please add at least one interest '

	@mock.patch('flask_pymongo.MongoClient')
	def test_getUserDetails(self, mock_MongoClient):
		mock_MongoClient.return_value = self.mock_server
		with self.context:
			userID_valid = ["5a1b712f7f66218ffdcc8536"]

			assert len(getUserDetails(userID_valid)) == 1

			userID_invalid = ['5a1b712f7f66218ffdcc8525']

			assert len(getUserDetails(userID_invalid)) == 0

	@mock.patch('flask_pymongo.MongoClient')
	def test_fetchEvents(self, mock_MongoClient):
		mock_MongoClient.return_value = self.mock_server
		with self.context:
			userID_valid = "5a1b712f7f66218ffdcc8536"

			rv = fetchEvents(userID_valid)
			assert rv.status_code == 200

			userID_invalid = '5a1b712f7f66218ffdcc8525'

			rv = fetchEvents(userID_invalid)
			assert rv.status_code == 500

			rv = fetchEvents(None)
			assert rv == 'No'
	#@mock.patch('flask_pymongo.MongoClient')
	#def

class TestCaseAttendanceController(unittest.TestCase):
	def setUp(self):
		# Test to check if Flask App runs properly or not		
		self.mock_server = mongomock.MongoClient()
		#self.mock_server.run()
		#assert self.mock_server.uri == "Something"
		#self.app = make_app(self.mock_server.uri).test_client()
		self.app = app.test_client()

	def tearDown(self):
		# Test to see if the application closes gracefully
		pass

	@mock.patch('flask_pymongo.MongoClient')
	def notify(self, dataload, mock_MongoClient):
		mock_MongoClient.return_value = self.mock_server
		return self.app.post('/notify', data = dataload, 
		content_type= "application/json")

	def test_notify(self):

		correct_dataload = json.dumps({
			'event_id':"5a1b86997f66217805531fa8",
			'user_id':"5a1dbc9425149725f8667c47"})
		rv = self.notify(correct_dataload)
		assert rv.status_code == 200

		incorrect_dataload = json.dumps({
			'event_id':"1",
			'user_id':"2"})
		rv = self.notify(incorrect_dataload)
		assert rv.status_code == 500

		incorrect_dataload = json.dumps({
			'a':"5a1b86997f66217805531f12",
			'b':"5a1dbc9425149725f8667c22"})
		rv = self.notify(incorrect_dataload)
		print(rv)
		assert rv.status_code == 400

class TestCaseEventController(unittest.TestCase):
	def setUp(self):
		# Test to check if Flask App runs properly or not		
		self.mock_server = mongomock.MongoClient()
		#self.mock_server.run()
		#assert self.mock_server.uri == "Something"
		#self.app = make_app(self.mock_server.uri).test_client()
		self.context = app.app_context()
		self.app = app.test_client()

	def tearDown(self):
		# Test to see if the application closes gracefully
		pass

	def test_searchEventsdist(self):
		with self.context:
			correct = [-73.8,40.80,10000]
			assert len(searchEventsDist(correct[0],correct[1],correct[2])) != 0

			incorrect = ['a','b','c']
			assert len(searchEventsDist(incorrect[0],incorrect[1],incorrect[2])) == 0

	@mock.patch('flask_pymongo.MongoClient')
	def createEvent(self, dataload, mock_MongoClient):
		mock_MongoClient.return_value = self.mock_server
		return self.app.post('/eventadd', data = dataload, 
		content_type= "application/json")

	def test_createEvent(self):

		correct_dataload = json.dumps({
			"title": "Karaoke",
			"description": "Join us for a fun Karaoke session - we'll be playing western songs!",
			"geo_loc": [
			41.09,
			78.0
			],
			"place_id": "Mel's Burgers",
			"time": 1493300103526,
			"owner": '5a1dbc9425149725f8667c47',
			"category": "Singing"
			})
		rv = self.createEvent(correct_dataload)
		assert rv.status_code == 200

		incorrect_dataload = json.dumps({
			'event_id':"1",
			'user_id':"2"})
		rv = self.createEvent(incorrect_dataload)
		assert rv.status_code == 500

		incorrect_dataload = json.dumps({
			"title": "",
			"description": "Join us for a fun Karaoke session - we'll be playing western songs!",
			"geo_loc": [
			41.09,
			78.0
			],
			"place_id": "Mel's Burgers",
			"time": 1493300103526,
			"owner": '5a1dbc9425149725f8667c47',
			"category": "Singing"
			})
		rv = self.createEvent(incorrect_dataload)
		print(rv)
		assert rv.status_code == 400

		incorrect_dataload = json.dumps({
			"title": "K",
			"description": "",
			"geo_loc": [
			41.09,
			78.0
			],
			"place_id": "Mel's Burgers",
			"time": 1493300103526,
			"owner": '5a1dbc9425149725f8667c47',
			"category": "Singing"
			})
		rv = self.createEvent(incorrect_dataload)
		print(rv)
		assert rv.status_code == 400

		incorrect_dataload = json.dumps({
			"title": "K",
			"description": "Join us for a fun Karaoke session - we'll be playing western songs!",
			"geo_loc": [
			41.09
			],
			"place_id": "Mel's Burgers",
			"time": 1493300103526,
			"owner": '5a1dbc9425149725f8667c47',
			"category": "Singing"
			})
		rv = self.createEvent(incorrect_dataload)
		print(rv)
		assert rv.status_code == 400

		incorrect_dataload = json.dumps({
			"title": "",
			"description": "Join us for a fun Karaoke session - we'll be playing western songs!",
			"geo_loc": [
			41.09,
			78.0
			],
			"place_id": "Mel's Burgers",
			"time": 1493300103526,
			"owner": '',
			"category": "Singing"
			})
		rv = self.createEvent(incorrect_dataload)
		print(rv)
		assert rv.status_code == 400

		incorrect_dataload = json.dumps({
			"title": "",
			"description": "Join us for a fun Karaoke session - we'll be playing western songs!",
			"geo_loc": [
			41.09,
			78.0
			],
			"place_id": "Mel's Burgers",
			"time": 1493300103526,
			"owner": '5a1dbc9425149725f8667c47',
			"category": ""
			})
		rv = self.createEvent(incorrect_dataload)
		print(rv)
		assert rv.status_code == 400

	@mock.patch('flask_pymongo.MongoClient')
	def test_searchEvents(self, mock_MongoClient):
		mock_MongoClient.return_value = self.mock_server
		with self.context:
			correct = [-73.8,40.80,10000]

			rv = searchEvents(correct[0],correct[1],correct[2])
			assert rv.status_code == 200

			incorrect = [40.8,100.0,1]
			rv = searchEvents(incorrect[0],incorrect[1],incorrect[2])
			assert rv.status_code == 400

	@mock.patch('flask_pymongo.MongoClient')
	def test_searchEventsKeyword(self, mock_MongoClient):
		mock_MongoClient.return_value = self.mock_server
		with self.context:
			correct = [-73.8,40.80,10000,'Singing']

			rv = searchEventsKeyword(correct[0],correct[1],correct[2],correct[3])
			assert rv.status_code == 200

			incorrect = [40.8,100.0,1,'Singing']
			rv = searchEventsKeyword(incorrect[0],incorrect[1],incorrect[2],incorrect[3])
			assert rv.status_code == 400