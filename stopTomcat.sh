#!/bin/sh
/opt/tomcat6-6.0.20/bin/shutdown.sh
sleep 5
kill -9 `ps aux | grep org.apache.catalina.startup | grep -v grep |awk '{print $2}'`
