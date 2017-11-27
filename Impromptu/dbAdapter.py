import json
import os
import flask_pymongo

def connect(app):
	mydir = os.path.dirname(os.path.abspath(__file__))
	with open(mydir+'/config/dbConfig.json') as json_data:
		dbConfig = json.load(json_data)

	app.config['MONGOMLAB_HOST'] = dbConfig['MONGOMLAB_HOST']
	app.config['MONGOMLAB_PORT'] = dbConfig['MONGOMLAB_PORT']
	app.config['MONGOMLAB_DBNAME'] = dbConfig['MONGOMLAB_DBNAME']
	app.config['MONGOMLAB_USERNAME'] = dbConfig['MONGOMLAB_USERNAME']
	app.config['MONGOMLAB_PASSWORD'] = dbConfig['MONGOMLAB_PASSWORD']
	mongo_mlab = flask_pymongo.PyMongo(app, config_prefix='MONGOMLAB')

	return mongo_mlab