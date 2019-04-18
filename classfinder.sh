#!/bin/sh

# From Perelle. Too bad people are not educated...
die()
{
	echo $* 1>&2
	exit 1
}

findHighestJarVersion()
{
	find "$1" -name $2'*'.jar -printf "%h%P\n" | sort | tail -1
}

# Find relevant directories.
SCRIPT_BASENAME=$(basename "$0")
SCRIPT_DIR=$(dirname "$0"}
if [ "$SCRIPT_DIR" = "." ]
then
	SCRIPT_DIR=$(pwd)
fi
PARENT_DIR=$(dirname "$SCRIPT_DIR")
LIB_DIR="$PARENT_DIR"/lib

OS=$(uname -s)
unset ICON_SWITCH

# Find the icon for Mac.
if [ "$OS" = 'Darwin' ]
then
	ICON_BASENAME=iconfinder_magnifier-data_532758-64x64.png
	for ICON in "$LIB_DIR/$ICON_BASENAME" "$SCRIPT_DIR/src/main/resources/$ICON_BASENAME"
	do
		if [ -f "$ICON" ] 
		then
    		ICON_SWITCH=-Xdock:icon="$ICON"
			break
		fi
	done
fi

# Find the application jar file.
unset JAR
for JAR_DIR in "$LIB_DIR" "$PARENT_DIR"/build/libs
do
	JAR=$(findHighestJarVersion "$JAR_DIR" "$SCRIPT_BASENAME")
	if [ -n "$JAR" ]
		break
	fi
fi

if [ -z "$JAR" ]
then
	die Jar file not found
fi

exec java "$OPTS" -jar "$JAR" "$@"
