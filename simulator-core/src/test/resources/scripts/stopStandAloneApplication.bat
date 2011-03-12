@echo off

set scriptdir=%~dp0

java -classpath "%scriptdir%..\lib\activeio-core-3.1.2.jar:%scriptdir%..\lib\log4j-1.2.14.jar:%scriptdir%..\lib\simulator-core.jar:%scriptdir%..\lib\slf4j-log4j12-1.5.6.jar:%scriptdir%..\lib\slf4j-api-1.5.6.jar:%scriptdir%..\lib\commons-logging-1.0.4.jar com.tacitknowledge.simulator.standalone.StandAloneStopper" 
