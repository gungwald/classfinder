#!/bin/sh

OS=$(uname -s)

if [ "$OS" = 'Darwin' ]
then
    OPTS=-Xdock:icon=src/main/resources/iconfinder_magnifier-data_532758-64x64.png
fi

java $OPTS -jar build/libs/classfinder-1.0.jar
