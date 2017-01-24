#!/bin/bash -e

arg_count=$#
if [ ${arg_count} -ne 1 ]; then
  echo ""
  echo "  Use is `basename $0` <module_file>"
  echo ""

  exit 1
fi

module_file=$1

for count in {1..10}; do
  sleep 1s
  echo "Count is ${count}" >> "${module_file}.verified"
done