# Impromptu

| CI Server | OS      | Status | Description |
| --------- | ------- | ------ | ----------- |
| Travis CI   | Android   | [![Build Status](https://travis-ci.org/tfmunat/Impromptu.svg?branch=master)](https://travis-ci.org/tfmunat/Impromptu) | Official CI build v1.0 |
| Travis CI | Android   | [![Build Status](https://travis-ci.com/tfmunat/Impromptu.svg?token=RAHeoyGh3tm2ZseyagpW&branch=master)](https://travis-ci.com/tfmunat/Impromptu) | Official CI build v0.1 |

App for ASE 4156

This branch contains the code for the backend server of Impromptu. It has been written in Python Flask and uses MongoDB in the backend for data storage.

************************************
Installing and starting the server:
************************************
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

***************************
Testing and Code coverage:
***************************
- In order to test the backend package, go to root directory of the Impromptu package.
- Run the following command :
    py.test --cov=Impromptu Impromptu/Test
- Executing the above command will run all the test cases and give a report for the code coverage for the test cases executed.
- This code coverage report has been stored in the file : CoverageResults.txt
