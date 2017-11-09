from flask import Flask
from Impromptu.dbAdapter import connect
from flask import render_template

def make_app(environment, app):
	if environment == "Production":
		mongo_connection = connect(app)
	return mongo_connection

app = Flask(__name__)
mongo_mlab = make_app("Production", app)

from Impromptu.Controllers.user_controller import *
