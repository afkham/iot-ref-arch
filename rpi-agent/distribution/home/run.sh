#!/bin/sh
# ----------------------------------------------------------------------------
#  Copyright 2005-2012 WSO2, Inc. http://www.wso2.org
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

# ----------------------------------------------------------------------------
# Main Script for the WSO2 Carbon Server
#
# Environment Variable Prequisites
#
#   RPI_AGENT_HOME   Home of RPi Agent installation. If not set I will  try
#                   to figure it out.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#
#   JAVA_OPTS       (Optional) Java runtime options used when the commands
#                   is executed.
#
# NOTE: Borrowed generously from Apache Tomcat startup scripts.
# -----------------------------------------------------------------------------

# OS specific support.  $var _must_ be set to either true or false.
#ulimit -n 100000

cygwin=false;
darwin=false;
os400=false;
mingw=false;
case "`uname`" in
CYGWIN*) cygwin=true;;
MINGW*) mingw=true;;
OS400*) os400=true;;
Darwin*) darwin=true
        if [ -z "$JAVA_VERSION" ] ; then
             JAVA_VERSION="CurrentJDK"
           else
             echo "Using Java version: $JAVA_VERSION"
           fi
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/${JAVA_VERSION}/Home
           fi
           ;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set RPI_AGENT_HOME if not already set
[ -z "$RPI_AGENT_HOME" ] && RPI_AGENT_HOME=`cd "$PRGDIR/.." ; pwd`

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$RPI_AGENT_HOME" ] && RPI_AGENT_HOME=`cygpath --unix "$RPI_AGENT_HOME"`
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  QIBM_MULTI_THREADED=Y
  export QIBM_MULTI_THREADED
fi

# For Migwn, ensure paths are in UNIX format before anything is touched
if $mingw ; then
  [ -n "$RPI_AGENT_HOME" ] &&
    RPI_AGENT_HOME="`(cd "$RPI_AGENT_HOME"; pwd)`"
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME="`(cd "$JAVA_HOME"; pwd)`"
fi

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=java
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo " RPi Agent cannot execute $JAVACMD"
  exit 1
fi

# if JAVA_HOME is not set we're not happy
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable before running RPi Agent."
  exit 1
fi

if [ -e "$RPI_AGENT_HOME/rpi-agent.pid" ]; then
  PID=`cat "$RPI_AGENT_HOME"/rpi-agent.pid`
fi

# ----- Process the input command ----------------------------------------------
args=""
for c in $*
do
    if [ "$c" = "--debug" ] || [ "$c" = "-debug" ] || [ "$c" = "debug" ]; then
          CMD="--debug"
          continue
    elif [ "$CMD" = "--debug" ]; then
          if [ -z "$PORT" ]; then
                PORT=$c
          fi
    elif [ "$c" = "--stop" ] || [ "$c" = "-stop" ] || [ "$c" = "stop" ]; then
          CMD="stop"
    elif [ "$c" = "--start" ] || [ "$c" = "-start" ] || [ "$c" = "start" ]; then
          CMD="start"
    elif [ "$c" = "--version" ] || [ "$c" = "-version" ] || [ "$c" = "version" ]; then
          CMD="version"
    elif [ "$c" = "--restart" ] || [ "$c" = "-restart" ] || [ "$c" = "restart" ]; then
          CMD="restart"
    elif [ "$c" = "--test" ] || [ "$c" = "-test" ] || [ "$c" = "test" ]; then
          CMD="test"
    else
        args="$args $c"
    fi
done

if [ "$CMD" = "--debug" ]; then
  if [ "$PORT" = "" ]; then
    echo " Please specify the debug port after the --debug option"
    exit 1
  fi
  if [ -n "$JAVA_OPTS" ]; then
    echo "Warning !!!. User specified JAVA_OPTS will be ignored, once you give the --debug option."
  fi
  CMD="RUN"
  JAVA_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=$PORT"
  echo "Please start the remote debugging client to continue..."
elif [ "$CMD" = "start" ]; then
  if [ -e "$RPI_AGENT_HOME/rpi-agent.pid" ]; then
    if  ps -p $PID >&- ; then
      echo "Process is already running"
      exit 0
    fi
  fi
  export RPI_AGENT_HOME=$RPI_AGENT_HOME
# using nohup bash to avoid erros in solaris OS.TODO
  nohup bash $RPI_AGENT_HOME/bin/wso2server.sh $args > /dev/null 2>&1 &
  exit 0
elif [ "$CMD" = "stop" ]; then
  export RPI_AGENT_HOME=$RPI_AGENT_HOME
  kill -term `cat $RPI_AGENT_HOME/rpi-agent.pid`
  exit 0
elif [ "$CMD" = "restart" ]; then
  export RPI_AGENT_HOME=$RPI_AGENT_HOME
  kill -term `cat $RPI_AGENT_HOME/rpi-agent.pid`
  process_status=0
  pid=`cat $RPI_AGENT_HOME/rpi-agent.pid`
  while [ "$process_status" -eq "0" ]
  do
        sleep 1;
        ps -p$pid 2>&1 > /dev/null
        process_status=$?
  done

# using nohup bash to avoid erros in solaris OS.TODO
  nohup bash $RPI_AGENT_HOME/bin/wso2server.sh $args > /dev/null 2>&1 &
  exit 0
elif [ "$CMD" = "test" ]; then
    JAVACMD="exec "$JAVACMD""
elif [ "$CMD" = "version" ]; then
  cat $RPI_AGENT_HOME/bin/version.txt
  exit 0
fi

# ---------- Handle the SSL Issue with proper JDK version --------------------
jdk_17=`$JAVA_HOME/bin/java -version 2>&1 | grep "1.[7|8]"`
if [ "$jdk_17" = "" ]; then
   echo " Starting WSO2 Carbon (in unsupported JDK)"
   echo " [ERROR] CARBON is supported only on JDK 1.7 and 1.8"
fi

JAVA_ENDORSED_DIRS="$RPI_AGENT_HOME/lib/endorsed":"$JAVA_HOME/jre/lib/endorsed":"$JAVA_HOME/lib/endorsed"

RPI_AGENT_CLASSPATH=""
if [ -e "$JAVA_HOME/lib/tools.jar" ]; then
    RPI_AGENT_CLASSPATH="$JAVA_HOME/lib/tools.jar"
fi
for f in "$RPI_AGENT_HOME"/bin/*.jar
do
    if [ "$f" != "$RPI_AGENT_HOME/bin/*.jar" ];then
        RPI_AGENT_CLASSPATH="$RPI_AGENT_CLASSPATH":$f
    fi
done
for t in "$RPI_AGENT_HOME"/lib/*.jar
do
    RPI_AGENT_CLASSPATH="$RPI_AGENT_CLASSPATH":$t
done
# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  RPI_AGENT_HOME=`cygpath --absolute --windows "$RPI_AGENT_HOME"`
  AXIS2_HOME=`cygpath --absolute --windows "$RPI_AGENT_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  JAVA_ENDORSED_DIRS=`cygpath --path --windows "$JAVA_ENDORSED_DIRS"`
  RPI_AGENT_CLASSPATH=`cygpath --path --windows "$RPI_AGENT_CLASSPATH"`
  CARBON_XBOOTCLASSPATH=`cygpath --path --windows "$CARBON_XBOOTCLASSPATH"`
fi

# ----- Execute The Requested Command -----------------------------------------

echo JAVA_HOME environment variable is set to $JAVA_HOME
echo RPI_AGENT_HOME environment variable is set to $RPI_AGENT_HOME

cd "$RPI_AGENT_HOME"

START_EXIT_STATUS=121
status=$START_EXIT_STATUS

#To monitor a Carbon server in remote JMX mode on linux host machines, set the below system property.
#   -Djava.rmi.server.hostname="your.IP.goes.here"

LIB_PATH=$RPI_AGENT_HOME/lib

while [ "$status" = "$START_EXIT_STATUS" ]
do
    $JAVACMD \
    -Xms56m -Xmx256m -XX:MaxPermSize=64m \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath="$RPI_AGENT_HOME/repository/logs/heap-dump.hprof" \
    $JAVA_OPTS \
    -Djava.library.path=$LIB_PATH \
    -classpath "$RPI_AGENT_CLASSPATH" \
    -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" \
    -Djava.io.tmpdir="$RPI_AGENT_HOME/tmp" \
    org.wso2.iot.refarch.rpi.agent.Main $*
    status=$?
done