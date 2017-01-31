# ZOON demo web interface

## Components 

 1. `business-manager`  
    Does the processing and hard work via command-line invocations.
 1. `client`  
    User-interface operations.
 1. `parent-pom`  
    Parent component of the ([Maven](https://maven.apache.org/ "Maven")) application build process.
 1. `shared`  
    Source code shared between `business-manager` and `client`.

### Deployment diagram.

![Deployment diagram](https://github.com/gef-work/website/blob/master/overview.png "Deployment diagram")

### Possible client methods of interaction.

 1. Browser connection to client web interface.
 1. Read-only connection to elastic db (containing public parsed modules and workflow call data).
 1. Direct WS connection to `business-manager` to run `R` commands (as defined by the WSDL derived
    from https://github.com/gef-work/website/blob/master/business-manager/src/main/resources/META-INF/schema/business_manager.xsd )

### Some fundamental functionality.

 1. On start-up ....
    1. `business-manager` parses (by `R` invocation) ZOON modules from `{local-fs|GitHub}` and places
       data in elastic db. Then proceeds to query Figshare for ZOON workflow calls and parses (not
       `R` invocation!) and places parsed data in elastic db.
    1. `client` reads in user data and loads JDBC database.
 1. If a registered user is signed in they can upload their private ZOON modules into a temporary
    store (`gitblit`) and verify (by `R` invocation) them. They can also store their created ZOON
    workflow calls. These private modules and workflow calls do appear in other views.  
    A verified ZOON module can subsequently be uploaded to a GitHub repository.
 1. New users can register themselves on the website, in which case an email is sent to ZOON admins
    and one of them can log in to activate the newly registered person.

### Missing functionality.

 1. Not a dynamic update of modules newly uploaded to ZOON GitHub. Would currently need to restart
    `business-manager` manually for the start-up loading of elastic to draw in the new module.
 1. No "official" workflow call parsing possible.
 1. Clearly defined way of storing different versions of public ZOON modules (and workflow calls).
 1. Quite a lot else!
