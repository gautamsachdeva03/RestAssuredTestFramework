# RESTful APIs Automation test using Rest Assured 
## Pre-requisite
1. Java above 1.8.
2. Maven version above 3.0.
3. Api should be running on local machine on localhost i.e. `http://localhost:51544`
## How to install & Run 
1. Please extract the project at your desired path
2. Go to `/src/main/java/com/apitesting/resources/Application.properties` file and update configurations. 
	* Update `url` in application.properties file ,i.e. where api is hosted  e.g. `localhost:51544`
3. Open the command prompt and go to the project path.
4. Run `mvn clean install` command to download all dependencies
5. Run `mvn test`to command to execute all tests and generate a html report for test results.Please find html report under `target\surefire-reports` directory. 


