# ZOON -- `business-manager` -- Installation Instructions

**Warning! Incomplete documentation**

## Requirements - Install the following before proceeding!

 * [Elasticsearch](http://www.elastic.co/ "Elasticsearch home") (currently using vers. 1.4.4) (deploy)  
   Elastic stores representations of ZOON public modules and workflow calls.
 * [Gitblit](http://www.gitblit.com/ "Gitblit home") (currently using vers. 1.7.1) (deploy)  
   Gitblit stores representations of ZOON private modules and workflow calls.
 * [R](https://www.r-project.org/ "R home") (deploy)  
   Used to run Tom's ZOON R scripts such as those which verify modules.

## Installation

Download the project source and go to this application's root directory (i.e. the directory of this
`INSTALL.md` file!) and follow the steps below.

### Installation 1: (One-time) Property and configuration files from templates.

 1. Copy `src/properties/sample.filter.properties` to `src/properties/filter.properties`
 1. Copy `src/properties/sample.spring.properties` to `src/properties/spring.properties`
 1. Copy `src/main/resources/META-INF/spring/ctx/ws/sample.appCtx.ws.security-incoming.xml` to
         `src/main/resources/META-INF/spring/ctx/ws/appCtx.ws.security-incoming.xml`
 1. Copy `src/main/resources/META-INF/spring/ctx/ws/sample.appCtx.ws.security-outgoing.xml` to
         `src/main/resources/META-INF/spring/ctx/ws/appCtx.ws.security-outgoing.xml`
 1. Copy `src/main/resources/META-INF/spring/ctx/config/sample.appCtx.fileSystemStores.xml` to
         `src/main/resources/META-INF/spring/ctx/config/appCtx.fileSystemStores.xml`
 1. Copy `src/main/resources/META-INF/spring/ctx/config/sample.appCtx.gitblitStores.xml` to
         `src/main/resources/META-INF/spring/ctx/config/appCtx.gitblitStores.xml`
 1. Copy `src/main/resources/META-INF/spring/ctx/config/sample.appCtx.gitHubStores.xml` to
         `src/main/resources/META-INF/spring/ctx/config/appCtx.gitHubStores.xml`

### Installation 2: Edit all the copied files according to your deployment configurations.

#### `src/properties/filter.properties`

 * `elasticsearch.cluster-nodes=`  
   Location of elastic cluster nodes, e.g 127.0.0.1:9300
 * `processors.limit=`  
   Maximum number of processor to use concurrently, e.g. 6
 * `log.file.business_manager=`  
   Log file name/location, e.g. logs/zoon-business-manager.log
 * `log.level.business_manager=`  
   Component logging level, e.g. Any of `trace|debug|info|warn|error|fatal`
 * `log.level.general=`  
   Non-component logging level, e.g. Any of `trace|debug|info|warn|error|fatal`

#### `src/properties/spring.properties`

 * `parse.R=`  
   Name of Tom's module parsing R module, e.g. parse_module.R
 * `parse.dir=`  
   Local directory under which module parsing is invoked, e.g. /home/me/zoon/parse/
 * `parse.prepare.sh=`  
   Location of the script which prepares the module parsing local directory's subdirectories, e.g.
   /home/me/zoon/parse_prepare.sh
 * `parse.runner.sh=`  
   Location of the script which runs the module parsing code, e.g. /home/me/zoon/parse_runner.sh
 * `module_parse_process.frequency=`  
   Module parsing monitoring frequency (in milliseconds), e.g. 500
 * `module_parse_output.frequency=`  
   Module parsing output monitoring frequency (in milliseconds), e.g. 250
 * `verify.R=`  
   Name of Tom's module verifing R module, e.g. initiate_check.R
 * `verify.dir=`  
   Local directory under which module verification is invoked, e.g. /home/me/zoon/verify/
 * `verify.prepare.sh=`  
   Location of the script which prepares the module verification local directory's subdirectories, e.g
   /home/me/zoon/verify_prepare.sh
 * `verify.runner.sh=`  
   Location of the script which runs the module verifying code, e.g. /home/me/zoon/verify_runner.sh
 * `monitor_process.frequency=`  
   Process monitoring frequency (in seconds), e.g. 5
 * `monitor_output.frequency=`  
   Output monitoring frequency (in seconds), e.g. 2
 * `modules.preload_test=`  
   Instruct testing of modules prior to loading, e.g. `true` or `True` if testing, otherwise `false`
   or `False`.
 * `modules.initial_load_count=`  
   Maximum number of modules to initially load from public repositories, e.g. 500. Use a low number
   if testing the application.
 * `figshare.api=`  
   Figshare's search API, e.g. https://api.figshare.com/v2/articles/search
 * `figshare.search_for_tag=`  
   Tag search term for Figshare, e.g. zoonWorkflow
 * `figshare.downloader=`  
   URL for Figshare file downloads, e.g. https://ndownloader.figshare.com/files/
 * `securement.business.username=`  
   Username for web service security credentials (Ignore if not using WSS for communication)
 * `securement.business.password=`  
   Password for web service security credentials (Ignore if not using WSS for communication)

#### `src/main/resources/META-INF/spring/ctx/config/appCtx.fileSystemStores.xml`

 **TODO!**

#### `src/main/resources/META-INF/spring/ctx/config/appCtx.gitblitStores.xml`

 **TODO!**

#### `src/main/resources/META-INF/spring/ctx/config/appCtx.gitHubStores.xml`

 **TODO!**

### Installation 3: Building the `business-manager` war file. 

 1. `mvn clean verify

### Installation 4. Deploy into the Java servlet container.

 1. If the building of the `.war` file was successful then there should be a `.war` file in the
    `target/` directory for deployment to the Java servlet container.