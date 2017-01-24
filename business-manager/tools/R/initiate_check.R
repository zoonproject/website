# initiate_test
suppressWarnings({
  
  library(testthat, quietly = TRUE)
  args <- commandArgs(TRUE)
  
  capture.output({
    
    test_file(path = 'CheckModule.R')
    
  }, file = args[2])
  
})

write('END', file = args[2], append = TRUE)
