import os
import unittest
import json

from Impromptu import app, mongo_mlab

from Impromptu.Model.user_model import User

class TestCase(unittest.TestCase):
	def setUp(self):
		# Test to check if Flask App runs properly or not
		app.config['TESTING'] = True
		self.app = app.test_client()

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
			'email':"john.doe@gmail.com", 
			'is_active': True}), 
		content_type= "application/json")

	def test_signup(self):
		rv = self.signUp()
		assert rv.status_code == 200
		
	def userCreate(self):
		return User({'first_name':"John",'last_name':"Doe",'likes':['testing'],'email':"john.doe@gmail.com"})

	def test_userCreate(self):
		u = self.userCreate()
		assert u.first_name == "John"
		assert u.last_name == "Doe"
		assert 'testing' in u.likes
		assert u.email == 'john.doe@gmail.com'
		assert u.is_active == True