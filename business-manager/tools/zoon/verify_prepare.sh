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

# e.g. <Rscript location> = /home/me/apps/R/current/bin/Rscript
ln -s <Rscript location> .
# e.g. <R location> = /home/me/R
ln -s <R location>/Rscript.sh .
ln -s <R location>/initiate_check.R .
ln -s <R location>/CheckModule.R .

popd

exit 0
