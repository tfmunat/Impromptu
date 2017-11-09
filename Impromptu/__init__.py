from flask import Flask
from Impromptu.dbAdapter import connect
from flask import render_template

app = Flask(__name__)
mongo_mlab = connect(app)

from Impromptu.Controllers.user_controller import *
