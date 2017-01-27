# ZOON -- `business-manager` -- Installation Instructions

## Requirements - Install the following before proceeding!

 * [Elasticsearch](http://www.elastic.co/ "Elasticsearch home") (currently using vers. 1.4.4) (deploy)  
   Elastic stores representations of ZOON public modules and workflow calls.
 * [Gitblit](http://www.gitblit.com/ "Gitblit home") (currently using vers. 1.7.1) (deploy)  
   Gitblit stores representations of ZOON private modules and workflow calls.
 * [R](https://www.r-project.org/ "R home") **at least vers. 3.2.4 and with `zoon` and `testthat` packages installed** (deploy)  
   Used to run Tom's ZOON R scripts such as those which verify modules.
 * xvfb-run (deploy)  
   Provided, for example, by the xorg-x11-server-Xvfb package.

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
   Maximum number of processor to use concurrently, e.g. 6.  
   (**NOTE:** Seems to have problems if set to physical number of available processors!?)
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

Edit this file if you intend to use a local filesystem directory as a store of ZOON modules, e.g.
perhaps a git clone of https://github.com/zoonproject/modules  which is used for module parsing on
component start-up.

See [Start up problems](#start-up-problems) for some heads-up info.

 * `location`  
   Local directory holding ZOON modules, e.g. /home/me/git/modules/R/
 * `storeName`  
   Textual representation of the store, e.g. ZOON modules clone on fs
 * `hasPublicArtifacts`  
   Indicator as to whether store contains public modules, e.g. `true` if cloned from github.
 * `temporaryStore`  
   Indicator as to whether private ZOON modules and workflow calls should be stored here, e.g.
   `false` if cloned from github.
 * `zoonStore`  
   Indicator as to whether to publish private ZOON modules to this store, e.g. `false` if cloned
   from github.

#### `src/main/resources/META-INF/spring/ctx/config/appCtx.gitblitStores.xml`

 * `applicationURL`  
   Gitblit website, e.g. http://localhost:8080/gitblit-1.7.1/
 * `repositoryName`  
   Name of the git repository, e.g. zoon_artifacts
 * `repositoryPath`  
   Local location of the repository, e.g. /var/lib/tomcat/webapps/gitblit-1.7.1/WEB-INF/data/git/zoon_artifacts.git
 * `repositoryURL`  
   URL of repository, e.g. http://admin@localhost:8080/gitblit-1.7.1/r/zoon_artifacts.git
 * `adminName`  
   Gitblit adminstrator's name, e.g. admin
 * `adminPassword`  
   Gitblit administrator's password, e.g. admin
 * `storeName`  
   Textual representation of the store, e.g Local gitblit
 * `hasPublicArtifacts`  
   Indicator as to whether store contains public modules, e.g. `false` if only used for private
   artifacts (modules and workflow calls).
 * `temporaryStore`  
   Indicator as to whether private ZOON modules and workflow calls should be stored here, e.g.
   `true` if only used for private artifacts (modules and workflow calls).
 * `zoonStore`  
   Indicator as to whether to publish private ZOON modules to this store, e.g. `false` if only used
   for private artifacts (modules and workflow calls).

#### `src/main/resources/META-INF/spring/ctx/config/appCtx.gitHubStores.xml`

 * `credentialsUser`  
   GitHub user name.
 * `credentialsPassword`  
   GitHub user password.
 * `repositoryOwner`  
   GitHub repository owner user name.
 * `repositoryPassword`  
   GitHub repository owner password.
 * `repositoryBranch`  
   GitHub repository branch.
 * `contentPath`  
   Repository path to write to.
 * `storeName`  
   Textual representation of the store.
 * `hasPublicArtifacts`  
   Indicator as to whether store contains public modules, e.g. `false` if not hosting ZOON public
   modules.
 * `temporaryStore`  
   Indicator as to whether private ZOON modules and workflow calls should be stored here, e.g. 
   `false` if using Gitblit as the temporary store.
 * `zoonStore`  
   Indicator as to whether to publish private ZOON modules to this store, e.g. `true` if using as
   an example public ZOON module upload store.

### Installation 3: The ZOON module R files and `bash` script files

The `tools` directory contains a plethora of R and script files which prepare and run R invocations
on the command line.

#### Tom's ZOON R modules.

 * ZOON module parsing  
  `module2json.R`  
  `parse_module.R`
 * ZOON module verification  
  `CheckModule.R`  
  `initiate_check.R`

#### Bash preparation and invocation scripts.

Probably only `parse_prepare.sh`, `verify_prepare.sh` and `Rscript.sh` will need modification!

 * `parse_prepare.sh`  
   Create symlinks to the necessary files used to run the parsing R module in a target directory.  
 * `parse_runner.sh`  
   R parsing invocation and system process id data file placing script.  
   So long as there are the appropriate system access and invocation permissions, if something fails
   then useful debug information is written by this script.
 * `verify_prepare.sh`  
   Creates symlinks to the necessary files used to run the verifying R module in a target directory.
 * `verify_runner.sh`  
   R verifying invocation and system process id data file placing script.  
   So long as there are the appropriate system access and invocation permissions, if something fails
   then useful information debug is written by this script.
 * `Rscript.sh`  
   This is a wrapper to the `xvfb-run` command.

### Installation 4: Building the `business-manager` war file. 

 1. `mvn clean verify`

### Installation 5. Deploy into the Java servlet container.

 1. If the building of the `.war` file was successful then there should be a `.war` file in the
    `target/` directory for deployment to the Java servlet container.

## Start up problems.

 1. If a system tomcat is being used, e.g. a package install, then assuming that you are in the
    appropriate group and can deploy to tomcat `webapps`, e.g. `/var/lib/tomcat/webapps/`, then
    the tomcat user/group may need to be able to read and execute files/directories in whichever
    directories you have specified in the configuration files!