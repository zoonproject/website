#!/bin/bash

WORKDIR=`pwd`

ERROR_FILE=$WORKDIR/ZOON_ERROR.`date +%y%m%d.%H%M%S`.xml
cp /dev/null $ERROR_FILE
echo '<?xml version="1.0" encoding="UTF-8"?>' >> $ERROR_FILE
echo "<zoon_error>" >> $ERROR_FILE

ARG_COUNT=$#
if [ $ARG_COUNT -ne 3 ] ; then
  echo "Usage : `basename $0` <local_base_dir> <job_id> <command_line>"

  echo "  arg[0] - $1" >> $ERROR_FILE
  echo "  arg[1] - $2" >> $ERROR_FILE
  echo "  arg[2] - $3" >> $ERROR_FILE
  echo "  arg[3] - $4" >> $ERROR_FILE
  echo "  arg[4] - $5" >> $ERROR_FILE
  echo "  arg[5] - $6" >> $ERROR_FILE
  echo "  arg[6] - $7" >> $ERROR_FILE
  echo "  arg[7] - $8" >> $ERROR_FILE
  echo "  arg[8] - $9" >> $ERROR_FILE
  echo "</zoon_error>" >> $ERROR_FILE
  exit 8
else
  LOCAL_BASE_DIR=$1
  JOB_ID=$2
  COMMAND_LINE=$3
fi

rm $ERROR_FILE

ERROR_FILE=$WORKDIR/ZOON_ERROR.$JOB_ID.xml
cp /dev/null $ERROR_FILE
echo '<?xml version="1.0" encoding="UTF-8"?>' >> $ERROR_FILE
echo "<zoon_error>" >> $ERROR_FILE
echo "  <start>"`date +%y%m%d.%H%M%S`"</start>" >> $ERROR_FILE
echo "  <local_base_dir>$LOCAL_BASE_DIR</local_base_dir>" >> $ERROR_FILE
echo "  <job_id>$JOB_ID</job_id>" >> $ERROR_FILE
echo "  <command_line>$COMMAND_LINE</command_line>" >> $ERROR_FILE

#
# cd to directory to run executable (supposedly already created and populated by the webapp)
#
if [ ! -d $LOCAL_BASE_DIR ] ; then 
  echo "  <error>Local base directory [$LOCAL_BASE_DIR] does not exist</error>" >> $ERROR_FILE
  echo "  <end>"`date +%y%m%d.%H%M%S`"</end>" >> $ERROR_FILE
  echo "</zoon_error>" >> $ERROR_FILE
  exit 9
fi
 
cd $LOCAL_BASE_DIR
CD_EXIT_STATUS=$?
if [ "${CD_EXIT_STATUS}" -ne "0" ] ; then
  echo "  <error>Could not change director to [$LOCAL_BASE_DIR]</error>" >> $ERROR_FILE
  echo "  <end>"`date +%y%m%d.%H%M%S`"</end>" >> $ERROR_FILE
  echo "</zoon_error>" >> $ERROR_FILE
  exit 10
fi

rm $ERROR_FILE                                                   # things seem fine so far having arrived in local base directory

INFO_FILE=ZOON_INFO.$JOB_ID.xml
OUTPUT_FILE=ZOON_OUTPUT.$JOB_ID
cp /dev/null $INFO_FILE
cp /dev/null $OUTPUT_FILE

echo '<?xml version="1.0" encoding="UTF-8"?>' >> $INFO_FILE
echo "<zoon_info>" >> $INFO_FILE
echo "  <local_base_dir>$LOCAL_BASE_DIR</local_base_dir>" >> $INFO_FILE
echo "  <job_id>$JOB_ID</job_id>" >> $INFO_FILE
echo "  <command_line>$COMMAND_LINE</command_line>" >> $INFO_FILE
echo "  <start>"`date +%y%m%d.%H%M%S`"</start>" >> $INFO_FILE
echo "  <uname>"`uname -a`"</uname>" >> $INFO_FILE
echo "  <cpuinfo>"`cat /proc/cpuinfo`"</cpuinfo>" >> $INFO_FILE
echo "  <meminfo>"`cat /proc/meminfo`"</meminfo>" >> $INFO_FILE
echo "  <env>"`env`"</env>" >> $INFO_FILE

#
# assume invoking mechanism is first word in param line
#
INVOKER=`echo $COMMAND_LINE | awk '/ / {print $1}'`
if [ ! -e ./$INVOKER ] ; then
  echo "  <error>$INVOKER not found in local base directory</error>" >> $INFO_FILE
  echo "  <end>"`date +%y%m%d.%H%M%S`"</end>" >> $INFO_FILE
  echo "</zoon_info>" >> $INFO_FILE
  exit 11
fi

#
# chmod relevant executables (... or shell script calling executable!!)
# e.g. in PredictTool v0.8+ invoker TorsadePredict.sh calls TorsadePredict
#
if [[ $INVOKER =~ \.sh$ ]] ; then
  BINARY=$(echo $INVOKER|sed 's/\.sh//g')
  chmod 700 $INVOKER
else
  BINARY=$INVOKER
fi

echo "  <ls_l>"`ls -l`"</ls_l>" >> $INFO_FILE
echo "  <end>"`date +%y%m%d.%H%M%S`"</end>" >> $INFO_FILE
echo "</zoon_info>" >> $INFO_FILE

./$COMMAND_LINE >>$OUTPUT_FILE 2>&1 &                            # command goes to background (so exit status is always 0). TODO : check limit of no. of background jobs possible!
sleep 0.2s                                                       # delay slightly to allow output to be written to file on failure before proceding
if [[ "$BINARY" = "$INVOKER" ]] ; then                           # we've used an executable, use the process id
  ZOON_BIN_PID=$!
else                                                             # we've used a shell script, get the child process id
  ppid=$!
  ZOON_BIN_PID=`ps -ef| awk '$3 == '$ppid' { print $2 }'`
fi
echo $ZOON_BIN_PID > vre_bin.pid                                  # write out process id for reference

PARSE_FILE=$WORKDIR/zoon_procdir/parse-$JOB_ID
echo "$JOB_ID|$ZOON_BIN_PID" > $PARSE_FILE

exit 0                                                           # everything went to plan
