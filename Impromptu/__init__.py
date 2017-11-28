from flask import Flask
from Impromptu.dbAdapter import connect

def make_app(application, environment="Production"):
    if environment == "Production":
        mongo_connection = connect(application)
    return mongo_connection

app = Flask(__name__)
mongo_mlab = make_app(app)

from Impromptu.Controllers.user_controller import *
from Impromptu.Controllers.event_controller import *
from Impromptu.Controllers.attendance_controller import *