# args[1] is the path to the module

args <- commandArgs(TRUE)
#args <- 'MachineLearn_good.R'

library(zoon, quietly = TRUE)

source(file = 'test_module.R')
source(file = 'test_outputs.R')
source(file = 'test_parameters.R')

test_module(args[1])