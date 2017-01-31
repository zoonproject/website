#!/bin/bash -e

if [ $# -ne 1 ]; then
  echo "Usage : `basename $0` <destination_dir>"
  exit 8
else
  destination_dir=$1
  if [ "${destination_dir: -1}" != "/" ]; then
    destination_dir="${destination_dir}/"
  fi
fi

#
# Create the destination directory if it doesn't exist.
#
if [ ! -d ${destination_dir} ] ; then 
  mkdir -p ${destination_dir}
fi

pushd ${destination_dir}

ln -s /home/me/R/Rscript.sh .
ln -s /home/me/R/initiate_check.R .
ln -s /home/me/R/CheckModule.R .
ln -s /home/me/R/module2json.R .
ln -s /home/me/R/test_*.R .
ln -s /home/me/R/.data_*.data .

popd

exit 0
