# ZOON -- `client` -- Installation Instructions

**Warning! Partially complete documentation**

## Requirements - Install the following before proceeding!

 1. Maybe a database, depending on whether you're planning to use a non-embedded database for 
    persisting user data.

## Installation

Download the project source and go to this application's root directory (i.e. the directory of this
`INSTALL.md` file!) and follow the steps below.

### Installation 1: (One-time) Property and configuration files from templates.

 1. Copy `src/properties/sample.filter.properties` to `src/properties/filter.properties`
 1. Copy `src/properties/sample.spring.properties` to `src/properties/spring.properties`
 1. Copy `src/properties/database/sample.database.filter.properties` to
         `src/properties/database/database.filter.properties`
 1. Copy `src/main/resources/META-INF/data/spring-security/local/sample.users.sql` to
         `src/main/resources/META-INF/data/spring-security/local/users.sql`
 1. Copy `src/main/resources/META-INF/spring/ctx/ws/sample.appCtx.ws.security-outgoing.xml` to
         `src/main/resources/META-INF/spring/ctx/ws/appCtx.ws.security-outgoing.xml`
 1. Copy `src/main/webapp/WEB-INF/tiles/layout/common/sample.logo.jsp` to
         `src/main/webapp/WEB-INF/tiles/layout/common/logo.jsp`
 1. Copy `src/main/webapp/resources/css/site/sample.site.css` to
         `src/main/webapp/resources/css/site/site.css`

### Installation 2: (One-time) Database configuration files.

For simplicity I'm just going to use the embedded HSQL database in a "dev" environment.

 1. Copy `src/properties/database/sample.database.spring.properties` to
         `src/properties/database/dev.database.embedded.properties`
 1. `cp /dev/null src/properties/database/dev.database.embedded.properties`  
    This is because the embedded HSQL database doesn't use any JDBC drivers!

In reality, if you were to prefer to use, for example, MySQL, which is has been used in the source
code, then you would copy the content of `sample.database.spring.properties` to
`dev.database.mysql.properties` and assign the appropriate JDBC values.

### Installation 3: Edit all the copied files according to your deployment configurations.

#### `src/properties/filter.properties`

 * `business_services.ws.url=`  
   URL of `business-manager` WSDL.
 * `elasticsearch.cluster-nodes=`  
   Location of elastic cluster nodes, e.g 127.0.0.1:9300
 * `mail.smtp.host=`  
   Email SMTP host, e.g, smtp.myhost.com
 * `mail.smtp.port=`  
   Email SMTP port, e.g. 25
 * `mail.smtp.username=`  
   Email SMTP username.
 * `mail.smtp.password=`  
   Email SMTP password.
 * `mail.regn.from=`  
   Email new registrations 'from' address, e.g. noreply_registration@myhost.com
 * `mail.regn.to=`  
   Email new registrations 'to' address, e.g. reg_admin@myhost.com
 * `mail.regn.bcc=`  
   Email new registration Bcc address (using whitespace as address separator), e.g. reg_admin_dogsbody@myhost.com
 * `mail.regn.subject=`  
   Email new registration subject, e.g. New ZOON registration
 * `mail.transport.protocol=`  
   See `appCtx.mail.xml`.
 * `mail.smtp.auth=`  
   See `appCtx.mail.xml`.
 * `mail.smtp.connectiontimeout=`  
   See `appCtx.mail.xml`.
 * `mail.smtp.starttls.enable=`  
   See `appCtx.mail.xml`.
 * `mail.debug=`
   See `appCtx.mail.xml`.
 * `log.file.client=`  
   Log file name/location, e.g. logs/zoon-client.log
 * `log.level.client=`  
   Component logging level, e.g. Any of `trace|debug|info|warn|error|fatal`
 * `log.level.general=`  
   Non-component logging level, e.g. Any of `trace|debug|info|warn|error|fatal`

#### `src/properties/spring.properties`

 * `securement.business.username=`  
   Username for web service security credentials (Ignore if not using WSS for communication)
 * `securement.business.password=`  
   Password for web service security credentials (Ignore if not using WSS for communication)

#### `src/properties/database/database.filter.properties`

 * `zoon.database.queryTimeout=`  
   Hibernate query timeout (in milliseconds), e.g. 25

#### `src/properties/database/dev.database.embedded.properties`

See section 2 above. 

#### `src/main/resources/META-INF/data/spring-security/local/users.sql`

#### `src/main/resources/META-INF/spring/ctx/ws/appCtx.ws.security-outgoing.xml`

#### `src/main/webapp/WEB-INF/tiles/layout/common/logo.jsp`

#### `src/main/webapp/resources/css/site/site.css`

### Installation 3: Building the `client` war file. 

 1. `mvn clean verify -Ddeploy.db_vendor=embedded -Ddeploy.env=dev -Dspring.profiles.active=zoon_embedded`

If you are to deploy to an environment which uses MySQL then retain the `spring.profiles.active`
value of `zoon_embedded` because during the build process you want to be doing unit and/or 
integration testing on a local/embedded database, not your real one! Instead only adjust only the 
`deploy.db_vendor` value, e.g.

 1. `mvn clean verify -Ddeploy.db_vendor=mysql -Ddeploy.env=dev -Dspring.profiles.active=zoon_embedded`

### Installation 4. Deploy into the Java servlet container.

 1. - If the building of the `.war` file was successful then there should be a `.war` file in the
    `target/` directory for deployment to the Java servlet container.

## Start up problems.

 1. Tomcat will need to have the `spring.profiles.active` `JAVA_OPT` value assigned which is
    relevant to your deployment (not build!) database.  
    In a CentOS package-installed Tomcat the following can be appended to `/etc/tomcat/tomcat.conf`  
    `JAVA_OPTS="-Dspring.profiles.active=zoon_embedded"`