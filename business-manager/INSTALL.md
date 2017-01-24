# ZOON -- Business Manager -- Installation Instructions

** Warning! Incomplete documentation **

## Dependencies

 * Maven (build)
 * Java 7 (build, deploy)
 * Java-based Web Server, e.g. Apache Tomcat. (deploy)
 * Database (deploy).

## Installation

Download the project source and go to this application's root directory (i.e. the directory of this
`INSTALL.md` file!) and follow the steps below.

### Databases:

Currently only a MySQL or HSQL/embedded database for business manager persistence is available 
pre-configured (and HSQL doesn't work properly). If another database vendor is used then another
Spring profile (see section below) in `appCtx.database.business-manager.xml` is required and the
`pom.xml` modified to include the vendor-specific JDBC driver.

### Installation 1: (One-time) Properties files from templates.

 1. Decide your deployment business_manager database!
    Substitute `your business_manager db` below to whatever your database is, e.g. embedded, mysql, oracle. 
 1. Setup : Note: Filtering takes place at build time. Spring-related property value substitution occurs at
            application start-up (using `<context:property-placeholder` in application context xml files).
    1. Copy `src/properties/sample.filter.properties` to `src/properties/filter.properties`
    1. Edit `src/properties/filter.properties`
    1. Copy `src/properties/sample.spring.properties` to `src/properties/spring.properties`
    1. Edit `src/properties/spring.properties`
    1. Copy `src/properties/database/sample.database.filter.properties` to `src/properties/database/database.filter.properties`
    1. Edit `src/properties/database/database.filter.properties`
    1. Copy `src/properties/database/sample.database.spring.properties` to `src/properties/database/dev.database.<deploy.db_vendor>.properties`
    1. Edit `src/properties/database/dev.database.<deploy.db_vendor>.properties`
    1. Copy `src/main/resources/META-INF/spring/ctx/config/sample.appCtx.fileSystemLocations.xml` to `src/main/resources/META-INF/spring/ctx/config/appCtx.fileSystemLocations.xml`
    1. Edit `src/main/resources/META-INF/spring/ctx/config/appCtx.fileSystemLocations.xml`
    1. Copy `src/main/resources/META-INF/spring/ctx/config/sample.appCtx.gitHubLocations.xml` to `src/main/resources/META-INF/spring/ctx/config/appCtx.gitHubLocations.xml`
    1. Edit `src/main/resources/META-INF/spring/ctx/config/appCtx.gitHubLocations.xml`

### Installation 2: Building the Business Manager war file (for `site_business` use). 

 1. Full example command line :
    1. `mvn clean verify -Ddeploy.db_vendor=mysql -Ddeploy.env=dev -Dspring.profiles.active=business_manager_embedded`

### Installation 3: What next?

### Spring profiles.