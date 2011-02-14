# Java Syncplicity API

## Description
This is a first effort to create a Java version of the Synplicity API.
It uses the JSON format to communicate with the Syncplicity REST API.
And small example is included

## JUnit tests
To execute the test it is necessary to include the Syncplicity user and password in src/test/resources/JUnit.properties

## Dependencies
- org.apache.httpcomponents:httpclient
- org.apache.httpcomponents:httpmime
- com.google.code.gson:gson
- commons-cli:commons-cli

## TODO
- Add Test
- Add Error Control
- Add SLF4J
- All the API methods completed except the users methods, as I don't have a corporate account.
