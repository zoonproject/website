# initiate_test
# arg[1] is the path to the module
# arg[2] is the path to write test reporting to

#module_path <- 'check_module/MachineLearn_bad.R'
suppressWarnings({
  
  library(testthat, quietly = TRUE)
  args <- commandArgs(TRUE)

    capture.output({
    
    raw_test_results <- test_file(path = 'CheckModule.R')
    summary_results <- as.data.frame(raw_test_results)
    
  },
  file = args[2],
  split = TRUE)
  
})

if(any(summary_results$failed == 1) | any(summary_results$error == TRUE)){
  write('Failed', file = args[2], append = TRUE)
  print('Failed')
} else {
  write('Success', file = args[2], append = TRUE)
  print('Success')
}