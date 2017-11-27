import os
import unittest
import json
from Impromptu.Model.user_model import User
from Impromptu.Controllers.user_controller import validateUser
from flask import Flask, request
from mockupdb import MockupDB
import flask_pymongo
import mongomock
import mock
from Impromptu import app

class TestCase(unittest.TestCase):
	def setUp(self):
		# Test to check if Flask App runs properly or not		
		self.mock_server = mongomock.MongoClient()
		#self.mock_server.run()
		#assert self.mock_server.uri == "Something"
		#self.app = make_app(self.mock_server.uri).test_client()
		self.app = app.test_client()
		

#	def dbAdapter(self):
		# Test to see if the mongodb is connected to properly

	def tearDown(self):
		# Test to see if the application closes gracefully
		pass

#	def integrityChecks(self):
		# Checking to see if Application handles error cases properly		

	@mock.patch('flask_pymongo.MongoClient')
	def signUp(self, mock_MongoClient):
		mock_MongoClient.return_value = self.mock_server
		return self.app.post('/signup', data = json.dumps({
			'first_name':"John",
			'last_name':"Doe",
			'likes':['testing'],
			'fb_id':"23245665", 
			'is_active': True}), 
		content_type= "application/json")

	def test_signup(self):
		rv = self.signUp()
		print(rv)
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