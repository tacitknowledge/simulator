# Absolute path to this script. /home/user/bin/foo.sh
SCRIPT=$(readlink -f $0)
# Absolute path this script is in. /home/user/bin
SCRIPTPATH=`dirname $SCRIPT`  
echo $SCRIPTPATH

java -classpath $SCRIPTPATH/../lib/activeio-core-3.1.2.jar:$SCRIPTPATH/../lib/log4j-1.2.14.jar:$SCRIPTPATH/../lib/simulator-core.jar:$SCRIPTPATH/../lib/slf4j-log4j12-1.5.6.jar:$SCRIPTPATH/../lib/slf4j-api-1.5.6.jar:$SCRIPTPATH/../lib/commons-logging-1.0.4.jar:$SCRIPTPATH/../ com.tacitknowledge.simulator.standalone.StandAloneStopper 
