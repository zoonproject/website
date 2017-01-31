# Script for parseing module information and outputting as json

module2json<- function(module_path){
  
  library(roxygen2, quietly = TRUE)
  library(jsonlite, quietly = TRUE)
  
  # Use roxygen to read in the metadata
  roxy_parse <- roxygen2:::parse_file(module_path, environment())[[1]]
  
  # translate it into JSON
  json_raw <- jsonlite::toJSON(x = roxy_parse,
                               pretty = TRUE,
                               auto_unbox = TRUE)
  
  # Index the first value of the 'lists'
  json <- gsub('  "section":', '  "section.0":', gsub('  "param":', '  "param.0":', json_raw))
  
  # return
  return(json)
    
}