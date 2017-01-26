#!/bin/bash

# e.g. <Rscript location> = /home/me/apps/R/current/bin/Rscript (or perhaps just Rscript)
xvfb-run <Rscript location> --no-save "$@"
