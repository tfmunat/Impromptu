# Impromptu

[![Build Status](https://travis-ci.com/tfmunat/Impromptu.svg?token=RAHeoyGh3tm2ZseyagpW&branch=master)](https://travis-ci.com/tfmunat/Impromptu)

App for ASE 4156

This branch contains the code for the backend server of Impromptu. It has been written in Python Flask and uses MongoDB in the backend for data storage.

*****************
Installing and starting the server:
*****************
- Please install all the dependencies by executing the following command:
    pip install -r requirements.txt
- Please update your MongoDB Connection details in dbConnect.json file
- Set the FLASK_APP environment variable to Impromptu. 
  For Windows,
    set FLASK_APP=Impromptu
  For Linux,
    export FLASK_APP=Impromptu
- Now, execute following command to start the server:
   flask run
