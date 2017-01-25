# ZOON -- `business-manager` -- Installation Instructions

**Warning! Incomplete documentation**

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

 **TODO: Explanation of each of the property and configuration file options!**

### Installation 3: Building the `business-manager` war file. 

 1. `mvn clean verify

### Installation 4. Deploy into the Java servlet container.

 1. If the building of the `.war` file was successful then there should be a `.war` file in the
    `target/` directory for deployment to the Java servlet container.