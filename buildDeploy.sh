#!/bin/sh
#build war file
mvn clean install;
#deploy to local tomcat
mvn tomcat:deploy -Psimulator-deploy;
