import json
import os
import flask_pymongo

def connect(app):
    mydir = os.path.dirname(os.path.abspath(__file__))
    with open(mydir+'/config/dbConfig.json') as json_data:
        db_config = json.load(json_data)

    app.config['MONGOMLAB_HOST'] = db_config['MONGOMLAB_HOST']
    app.config['MONGOMLAB_PORT'] = db_config['MONGOMLAB_PORT']
    app.config['MONGOMLAB_DBNAME'] = db_config['MONGOMLAB_DBNAME']
    app.config['MONGOMLAB_USERNAME'] = db_config['MONGOMLAB_USERNAME']
    app.config['MONGOMLAB_PASSWORD'] = db_config['MONGOMLAB_PASSWORD']
    mongo_mlab = flask_pymongo.PyMongo(app, config_prefix='MONGOMLAB')

    return mongo_mlab
    