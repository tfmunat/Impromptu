# Impromptu

| OS        | CI Server   | Status | Release     |
| --------- | -------     | ------ | ----------- |
| Android   | Travis CI   | [![Build Status](https://travis-ci.org/tfmunat/Impromptu.svg?branch=master)](https://travis-ci.org/tfmunat/Impromptu) | Official CI build v3.0 |

## Project Description

'Impromptu' is an Android app built for ASE 4156 at Columbia University. It lets users create and join nearby events on the fly.

The API is available [here.](https://app.swaggerhub.com/apis/impromptu/impromptu_server/1.0.0)

************************************
Installing and starting the server:
************************************
- Install all dependencies by executing the following command:

        pip install -r requirements.txt
        
- Update your MongoDB connection details in the dbConfig.json file located at Impromptu/Impromptu/config/
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
- In order to test the frontend and backend packages, go to the root directory of the Impromptu package.
- To generate the backend coverge report, run the following command:

        py.test --cov=Impromptu Impromptu/Test
        
- To generate the frontend coverge report, run the following command:

        gradlew createDebugCoverageReport
        
- Executing the commands above will run all test cases and produce the code coverage reports.

*********
Reports:
*********
- Backend coverage [report](https://github.com/tfmunat/Impromptu/blob/master/Impromptu/Coverage%20Results.txt "Backend coverage report")
- Frontend coverage [report](https://github.com/tfmunat/Impromptu/blob/master/Impromptu/frontend_coverage.html "Frontend coverage report")
- Backend static analysis [report](https://github.com/tfmunat/Impromptu/tree/master/Impromptu/StaticAnalysis "Backend static analysis report")
- Frontend static analysis [report](https://github.com/tfmunat/Impromptu/tree/master/StaticAnalysisFrontEnd "Frontend static analysis report")
- [Travis CI](https://www.travis-ci.org/tfmunat/Impromptu "Travis CI")
- [Privacy Policy](https://github.com/tfmunat/Impromptu/blob/master/Privacy%20Policy.pdf)
