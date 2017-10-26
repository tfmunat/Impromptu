import json
import os
from flask_pymongo import PyMongo

def connect(app):
	with open(os.getcwd()+'/Impromptu/config/dbConfig.json') as json_data:
		dbConfig = json.load(json_data)

	app.config['MONGOMLAB_HOST'] = dbConfig['MONGOMLAB_HOST']
	app.config['MONGOMLAB_PORT'] = dbConfig['MONGOMLAB_PORT']
	app.config['MONGOMLAB_DBNAME'] = dbConfig['MONGOMLAB_DBNAME']
	app.config['MONGOMLAB_USERNAME'] = dbConfig['MONGOMLAB_USERNAME']
	app.config['MONGOMLAB_PASSWORD'] = dbConfig['MONGOMLAB_PASSWORD']
	mongo_mlab = PyMongo(app, config_prefix='MONGOMLAB')

	return mongo_mlab