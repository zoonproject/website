#!/bin/bash

# e.g. <Rscript location> = /home/me/apps/R/current/bin/Rscript
xvfb-run <Rscript location> --no-save "$@"
