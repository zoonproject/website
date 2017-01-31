# arg[1] is the module path
# arg[2] is the path to the file to write to

source('module2json.R')

args <- commandArgs(TRUE)

module_json <- try(module2json(module_path = args[1]), 
                   silent = TRUE)

if(!class(module_json) == 'try-error'){

  if(module_json == "{}") { # no roxygen tags
    
    cat('This module has no roxygen metadata')
    write('This module has no roxygen metadata', file = args[2])
    
  } else{
    
    # If success write to console and file
    print(module_json)
    write(module_json, file = args[2])
    
  }
  
} else {
  
  error_message <- paste('Parsing failed, here are the error messages:\n\n',
                         attr(module_json, 'condition')$message)
  
  # else write out failure to console and file
  cat(error_message)
  write(error_message, file = args[2])
  
}