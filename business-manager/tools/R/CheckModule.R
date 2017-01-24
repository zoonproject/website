args <- commandArgs(TRUE)
#args <- 'LocalOccurrenceData.R'

library(zoon, quietly = TRUE)

# A function for testing parameters (within a context)
test_parameters <- function(roxy_parse, defaultParams = NULL, modulePath){
  
  # Extract the parameters
  params <- formals(source(modulePath)$value)
  
  test_that('Check parameter names', {
    
    # Extract names from tags
    paramNames <- unlist(lapply(roxy_parse[names(roxy_parse) == 'param'],
                                function(x) x$name))
    
    # Check for defult parameter
    if(!is.null(defaultParams)) expect_true(all(defaultParams %in% paramNames),
                                            info = 'Default parameter is not documented')
    
    # Check all parameters are documented
    expect_true(all(names(params) %in% paramNames),
                info = 'Parameters are missing documentation')
    
  })
  
  test_that('Check default values', {
    
    # Expect that all non-default parameters have defaults
    paramClasses <- lapply(params, function(x) class(x))
    
    # Remove ellipsis
    paramClasses <- paramClasses[!names(paramClasses) %in% '...']
    
    # remove defaults
    if(!is.null(defaultParams)) paramClasses <- paramClasses[!names(paramClasses) %in% defaultParams]
    
    expect_true(all(paramClasses != 'name'),
                info = 'Parameters are missing default values')
    
  })
}

# A function for testing the outputs conform to the expected
# (within a context)
test_outputs <- function(roxy_parse, modulePath){
  
  test_that('Check output formats',{ 
    
    # download.file sends tonnes of stuff to my console
    # it is used internally in a number of modules
    # This is a naughty fix to shut it up (I tried everything)
    formals(download.file)$quiet <- TRUE
    
    ## OCCURRENCE MODULES ##
    if(roxy_parse$family == 'occurrence'){
      
      # Load the script
      source(modulePath) 
      
      # Run the module with defaults
      suppressWarnings({
        occ_return <- do.call(roxy_parse$name, args = list())
      })
      
      # Check the data.frame returned is as expected
      expect_is(occ_return, 'data.frame', info = 'Occurrence modules must return a data.frame')
      expect_named(occ_return, expected = c('longitude',
                                            'latitude',
                                            'value',
                                            'type',
                                            'fold'), 
                   info = "Some of the required columns are missing ('longitude', 'latitude', 'value', 'type', 'fold')")
      expect_is(occ_return$longitude, c('numeric', 'integer'), info = 'longitude must be a numeric or integer')
      expect_is(occ_return$latitude, c('numeric', 'integer'), info = 'latitude must be a numeric or integer')
      expect_is(occ_return$value, c('numeric', 'integer'), info = 'value must be a numeric or integer')
      expect_is(occ_return$type, 'character', info = 'type must be a character')
      expect_is(occ_return$fold, c('numeric', 'integer'), info = 'info must be a numeric or integer')
      
    }
    
    ## COVARIATE MODULES ##
    if(roxy_parse$family == 'covariate'){
      
      # Load the script
      source(modulePath) 
      
      # Run the module with defaults
      suppressWarnings({
        cov_return <- do.call(roxy_parse$name, args = list())
      })
      
      # Check projection
      expect_true(all(grepl("+proj=longlat", projection(cov_return)),
                      grepl("+ellps=WGS84", projection(cov_return))),
                  info = 'Covariate module output must have WGS84 projection: proj4 string is expected to contain the elements "+proj=longlat" and "+ellps=WGS84"')
      
      # Check raster returned is as expected
      expect_is(cov_return, c('RasterLayer', 'RasterStack', 'RasterBrick'), info = 'Covariate module output must be either a RasterLayer or a RasterStack')
      
    }
    
    ## PROCESS MODULES ##
    if(roxy_parse$family == 'process'){
      
      # Load the script
      source(modulePath) 
      
      # Run the module with defaults
      data_types <- gsub('Data type: ', '', roxy_parse$section)
      
      for(data_type in c("presence/absence", "presence-only")){
        
        if(grepl(data_type, data_types)){
          
          if(data_type == "presence/absence") load('.data_PA.rdata')
          if(data_type == "presence-only") load('.data_PO.rdata')
          
          suppressWarnings({
            pro_return <- do.call(roxy_parse$name, args = list(.data = .data))
          })
          
          ## Check pro_return structure
          expect_is(pro_return, 'list', info = 'The object returned from a process module must be a list')
          expect_named(pro_return, expected = c('df', 'ras'), info = 'The elements of the list returned from a process module must be named "df" and "ras"')
          
          ## Check 'df'
          # Check the data.frame returned is as expected
          expect_is(pro_return$df, 'data.frame', info = 'Occurrence modules must return a data.frame')
          expect_true(all(c('longitude','latitude','value','type','fold') %in% names(pro_return$df)), 
                      info = "Some of the required columns from the 'df' element returned by the process module, are missing ('longitude', 'latitude', 'value', 'type', 'fold')")
          expect_is(pro_return$df$longitude, c('numeric', 'integer'), info = 'longitude column, from the "df" element returned from a process module, must be a numeric or integer')
          expect_is(pro_return$df$latitude, c('numeric', 'integer'), info = 'latitude column, from the "df" element returned from a process module, must be a numeric or integer')
          expect_is(pro_return$df$value, c('numeric', 'integer'), info = 'value column, from the "df" element returned from a process module, must be a numeric or integer')
          expect_is(pro_return$df$type, c('character', 'factor'), info = 'type column, from the "df" element returned from a process module, must be a character')
          expect_is(pro_return$df$fold, c('numeric', 'integer'), info = 'info column, from the "df" element returned from a process module, must be a numeric or integer')
          expect_true(ncol(pro_return$df) >= 6, info = 'The "df" element returned from a process module is expected to contain 6 or more columns')
          
          ## Check 'ras'
          # Check projection
          expect_true(all(grepl("+proj=longlat", projection(pro_return$ras)),
                          grepl("+ellps=WGS84", projection(pro_return$ras))),
                      info = 'The "ras" element returned by a process module must have WGS84 projection: proj4 string is expected to contain the elements "+proj=longlat" and "+ellps=WGS84"')
          
          # Check raster returned is as expected
          expect_is(pro_return$ras, c('RasterLayer', 'RasterStack', 'RasterBrick'), info = 'The "ras" element returned by a process module must be either a RasterLayer or a RasterStack')
          
        }
      }
    }
    
    ## MODEL MODULES ##
    if(roxy_parse$family == 'model'){
      
      # Load the script
      source(modulePath) 
      
      # Run the module with defaults
      data_types <- gsub('Data type: ', '', roxy_parse$section)
      
      for(data_type in c("presence/absence", "presence-only")){
        
        if(grepl(data_type, data_types)){
          
          if(data_type == "presence/absence") load('.data_PA.rdata')
          if(data_type == "presence-only") load('.data_PO.rdata')
          
          suppressWarnings({
            mod_return <- do.call(roxy_parse$name, args = list(.data = .data))
          })
          
          ## Check mod_return structure
          expect_is(pro_return, 'list', info = 'The object returned from a process module must be a list')
          expect_named(pro_return, expected = c('df', 'ras'), info = 'The elements of the list returned from a process module must be named "df" and "ras"')
          
          ## Check 'df'
          # Check the data.frame returned is as expected
          expect_is(pro_return$df, 'data.frame', info = 'Occurrence modules must return a data.frame')
          expect_true(all(c('longitude','latitude','value','type','fold') %in% names(pro_return$df)), 
                      info = "Some of the required columns from the 'df' element returned by the process module, are missing ('longitude', 'latitude', 'value', 'type', 'fold')")
          expect_is(pro_return$df$longitude, c('numeric', 'integer'), info = 'longitude column, from the "df" element returned from a process module, must be a numeric or integer')
          expect_is(pro_return$df$latitude, c('numeric', 'integer'), info = 'latitude column, from the "df" element returned from a process module, must be a numeric or integer')
          expect_is(pro_return$df$value, c('numeric', 'integer'), info = 'value column, from the "df" element returned from a process module, must be a numeric or integer')
          expect_is(pro_return$df$type, c('character', 'factor'), info = 'type column, from the "df" element returned from a process module, must be a character')
          expect_is(pro_return$df$fold, c('numeric', 'integer'), info = 'info column, from the "df" element returned from a process module, must be a numeric or integer')
          expect_true(ncol(pro_return$df) >= 6, info = 'The "df" element returned from a process module is expected to contain 6 or more columns')
          
          ## Check 'ras'
          # Check projection
          expect_true(all(grepl("+proj=longlat", projection(pro_return$ras)),
                          grepl("+ellps=WGS84", projection(pro_return$ras))),
                      info = 'The "ras" element returned by a process module must have WGS84 projection: proj4 string is expected to contain the elements "+proj=longlat" and "+ellps=WGS84"')
          
          # Check raster returned is as expected
          expect_is(pro_return$ras, c('RasterLayer', 'RasterStack', 'RasterBrick'), info = 'The "ras" element returned by a process module must be either a RasterLayer or a RasterStack')
          
        }
      }
      
    }
  })
}

# Test a module given its filepath
test_module <- function(modulePath){
  
  context(paste('Testing module', basename(gsub('.R$', '', modulePath))))
  
  time <- system.time({
    
    moduleName <- basename(gsub('.R$', '', modulePath))
    
    roxy_parse <- roxygen2:::parse_file(modulePath, environment())[[1]]
    
    ## GENERIC TESTS
    test_that(paste('Check roxy_parse', moduleName),{
      
      # Check roxy_parse is good
      expect_is(roxy_parse, 'list')
      
    })
    
    test_that(paste('Check for generic tags', moduleName),{
      
      # Check for the required generic tags
      expect_true('title' %in% names(roxy_parse))
      expect_true('description' %in% names(roxy_parse))
      expect_true('name' %in% names(roxy_parse))
      expect_true('family' %in% names(roxy_parse))
      expect_true('author' %in% names(roxy_parse))
      expect_true('section' %in% names(roxy_parse))
      expect_true(any(grepl('^Version: ', roxy_parse[grepl('section', names(roxy_parse))])))
      expect_true(any(grepl('^Date submitted: ', roxy_parse[grepl('section', names(roxy_parse))])))
      
    })
    
    # If the family tag is present continue (this error will be reported above)
    if('family' %in% names(roxy_parse)){
      
      ## Test for module specific tags
      test_that('Check for module specific tags', {
        
        if(roxy_parse$family == 'process'){
          expect_true('section' %in% names(roxy_parse))
          expect_true(any(grepl('^Data type: ', roxy_parse[grepl('section', names(roxy_parse))])))
        }
        
      })
      
      ## PARAMETER TESTS
      if(roxy_parse$family == 'occurrence'){
        
        test_parameters(roxy_parse, modulePath = modulePath)
        
      }
      
      if(roxy_parse$family == 'covariate'){
        
        test_parameters(roxy_parse, modulePath = modulePath)
        
      }  
      
      if(roxy_parse$family == 'process'){
        
        test_parameters(roxy_parse, defaultParams = '.data',
                        modulePath = modulePath)
        
      }  
      
      if(roxy_parse$family == 'model'){
        
        test_parameters(roxy_parse, defaultParams = '.df',
                        modulePath = modulePath)
        
      }
      
      if(roxy_parse$family == 'output'){
        
        test_parameters(roxy_parse, defaultParams = c('.model', '.ras'),
                        modulePath = modulePath)
        
      }
      
      ## FUNCTION OUTPUT TESTS
      test_outputs(roxy_parse, modulePath)
      
    }
  }) #time
  
  expect_true(time['elapsed'] < 60,
              info = paste('Module tests should not take a long time, yours took', time['elapsed'], 'seconds, please change your defualt values so that test workflow runs do not take too long'))
  
}

test_module(args[1])