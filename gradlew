#!/bin/sh

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

APP_HOME="`pwd -P`"

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

DEFAULT_JVM_OPTS=""
JAVA_OPTS=""
GRADLE_OPTS=""

warn() {
    echo "$*"
}

die() {
    echo
    echo "$*"
    echo
    exit 1
}

case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
esac

if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

if [ -z "$JAVA_OPTS" ] ; then
    JAVA_OPTS=""
fi

exec "$JAVACMD" $JAVA_OPTS $DEFAULT_JVM_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"