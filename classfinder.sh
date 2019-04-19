#!/bin/sh

# From Perelle. Too bad people are not educated...
die()
{
	echo $* 1>&2
	exit 1
}

findHighestJarVersion()
{
	find "$1" -name "$2*.jar" -print | sort | tail -1
}

# Find relevant directories.
THIS_SCRIPT=$(basename "$0" .sh)
THIS_DIR=$(dirname "$0")
if [ "$THIS_DIR" = "." ]
then
	THIS_DIR=$(pwd)
fi
PARENT_DIR=$(dirname "$THIS_DIR")
LIB_DIR="$PARENT_DIR"/lib
SHARE_DIR="$PARENT_DIR/share/java/$THIS_SCRIPT"

OS=$(uname -s)

# Find the icon for Mac.
unset MAC_ICON_SWITCH
if [ "$OS" = 'Darwin' ]
then
	ICON_BASENAME=iconfinder_magnifier-data_532758-64x64.png
	for ICON_DIR in "$THIS_DIR" "$LIB_DIR" "$SHARE_DIR" "$THIS_DIR/src/main/resources"
	do
		ICON="$ICON_DIR/$ICON_BASENAME"
		if [ -f "$ICON" ] 
		then
			echo Using icon: $ICON
    		MAC_ICON_SWITCH=-Xdock:icon="$ICON"
			break
		fi
	done
fi

# Find the application jar file.
unset JAR
for JAR_DIR in "$THIS_DIR" "$LIB_DIR" "$SHARE_DIR" "$THIS_DIR"/build/libs
do
	if [ -d "$JAR_DIR" ]
	then
		JAR=$(findHighestJarVersion "$JAR_DIR" "$THIS_SCRIPT")
		if [ -n "$JAR" ]
		then
			echo Using jar: $JAR
			break
		fi
	fi
done

# Try building it, if it wasn't found.
if [ -z "$JAR" ]
then
	if [ -f "$THIS_DIR/build.gradle" ]
	then
		gradle build
		if [ $? -eq 0 ]
		then
			JAR=$(findHighestJarVersion "$THIS_DIR/build/libs" "$THIS_SCRIPT")
			if [ -n "$JAR" ]
			then
				echo Using jar: $JAR
			fi
		fi
	fi
fi

if [ -z "$JAR" ]
then
	die Jar file not found
fi

# Run
if [ -z "$MAC_ICON_SWITCH" ]
then
	exec java -jar "$JAR" "$@"
else
	exec java "$MAC_ICON_SWITCH" -jar "$JAR" "$@"
fi