#!/bin/bash

CHECKSTR="HTTP/1.1 200"
if [ "$MSYSTEM" = "MINGW64" ]; then
    CHECKSTR="awaiting response... 200"
fi

if [ ! $TF_SERVER_HOME ]; then
    TF_SERVER_HOME=..
fi

cd $TF_SERVER_HOME
JARNUM=`ls -1 *.jar | wc -l`
if [ $JARNUM -ge 2 ]; then
    echo "存在多个相同名称的JAR需要手动删除！！"
    ls |grep ".jar"
    exit 1
fi
JAR=`ls -1 *.jar | head -1`
SERVER_NAME=`grep spring.application.name config/application.properties | awk -F = '{print $2}'`
PORT=`grep server.port config/application.properties | awk -F = '{print $2}'`
TODAY=`date +%Y-%m-%d`
STDOUT_FILE=out/$SERVER_NAME.$TODAY.out

if [ ! -d "out" ];then
mkdir out
fi
`wget -S http://127.0.0.1:$PORT/actuator/info -t 1 2> tmp`
`rm -f info`
COUNT=`cat tmp | grep "$CHECKSTR" | wc -l`

if [ $COUNT -gt 0 ]; then
    echo "Starting the $SERVER_NAME ... Failed, please stop first!!!"
    sleep 1
    `rm -f tmp`
    `rm -f info`
    exit 0
else
     echo -e "Starting the $SERVER_NAME ...\c"
    `rm -f info`
    `rm -f tmp`
fi

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi
JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

if [ -f "config/memory.conf" ];then
    JAVA_MEM_OPTS=`cat config/memory.conf`
else
    JAVA_MEM_OPTS=""
    BITS=`java -version 2>&1 | grep -i 64-bit`
    if [ -n "$BITS" ]; then
        JAVA_MEM_OPTS=" -server -Xmx2g -Xms2g -Xmn256m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
    else
        JAVA_MEM_OPTS=" -server -Xms1g -Xmx1g -XX:PermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
    fi
fi

if [ "$MSYSTEM" = "MINGW64" ]; then
    nohup java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -jar $JAR > $STDOUT_FILE 2>&1 &
else
   DIR_NAME=`pwd | awk -F '/' '{print $NF}'`
   STDOUT_FILE=../../out/$DIR_NAME
   nohup java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -jar $JAR 2>&1 | /usr/local/sbin/cronolog "$STDOUT_FILE/$SERVER_NAME-%Y-%m-%d.out" >> /dev/null &
fi

SHELL_PID=$$

if [ "$MSYSTEM" = "MINGW64" ]; then
    PID=`ps | grep $PID | awk '{print $4}'`
else
    PID=`ps -ef | grep java | awk -v shellpid="$SHELL_PID" '{if ($3==shellpid && $8!="grep") print $2}'`
fi

echo $PID > pid

COUNT=0
while [ $COUNT -lt 1 ]; do
    sleep 1
    echo -e ".\c"
    P_EXIST=`jps -l | grep $JAR | grep $PID | wc -l`
    if [ $P_EXIST -gt 0 ]; then
       break
    fi
done

COUNT=0
sleep 1
while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    `wget -S -t1 -T1 http://127.0.0.1:$PORT/actuator/info -t 1 2> tmp`
    COUNT=`cat tmp | grep "$CHECKSTR" | wc -l`
    P_EXIST=`jps -l | grep $JAR | grep $PID | wc -l`
    sleep 1
    `rm -f tmp`
    `rm -f info`
    if [ $COUNT -gt 0 ]; then
        break
    elif [ $P_EXIST -eq 0 ]; then
        echo "Failed"
        `rm -f pid`
        exit 1
    fi
done

echo "OK!"
echo "    PID: $PID"
echo "    STDOUT: $STDOUT_FILE"

