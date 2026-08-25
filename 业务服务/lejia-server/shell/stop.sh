#!/bin/bash

if [ ! $TF_SERVER_HOME ]; then
    TF_SERVER_HOME=..
fi

cd $TF_SERVER_HOME

JAR=`ls -1 *.jar | head -1`
SERVER_NAME=`grep spring.application.name config/application.properties | awk -F = '{print $2}'`

echo -e "Stoping the $SERVER_NAME ...\c"
if [ -f "pid" ];then
    PID=`cat pid`
else
    echo "The $SERVER_NAME does not started!"
    exit 1
fi

if [ "$MSYSTEM" = "MINGW64" ]; then
    taskkill -f -pid $PID > /dev/null 2>&1
else
    kill $PID > /dev/null 2>&1
fi

COUNT=1
while [ $COUNT -gt 0 ]; do    
    echo -e ".\c"
    sleep 1
    PID_EXIST=`jps -l | grep "^$PID " | wc -l`
    if [ $PID_EXIST -eq 0 ]; then
        `rm -f pid`
        break
    fi
    ((COUNT++))
    if [ $COUNT -gt 15 ];then
       DIR_NAME=`pwd | awk -F '/' '{print $NF}'`
       STDOUT_DIR=../../out/$DIR_NAME
       OUT=`ls -S --sort=time -1 $STDOUT_DIR | head -1`
       DOWN=`cat $STDOUT_DIR/$OUT | grep 安全关闭 | grep " $PID " | wc -l`
       if [ $DOWN -gt 0 ];then
          kill -9 $PID > /dev/null 2>&1
          echo -e " 强制停止"$SERVER_NAME
       fi
    fi
done

echo "OK!"
