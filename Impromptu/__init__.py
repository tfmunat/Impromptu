from flask import Flask
from Impromptu.dbAdapter import connect
from flask import render_template

def make_app(app, environment = "Production"):
	if environment == "Production":
		mongo_connection = connect(app)
	return mongo_connection

app = Flask(__name__)
mongo_mlab = make_app(app)

from Impromptu.Controllers.user_controller import *
from Impromptu.Controllers.event_controller import *
from Impromptu.Controllers.attendance_controller import *