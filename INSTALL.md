# ZOON demo web interface installation

**Warning! `business-manager` and `client` lack sufficient documentation**

## Requirements

 * [Java JDK (vers. 7+)](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html "Oracle downloads") (build, deploy)
 * [Maven](https://maven.apache.org/ "Maven") (build)
 * Java servlet container, e.g. [Apache Tomcat](https://tomcat.apache.org/ "Tomcat home"). (deploy)
 * Database, e.g. [MySQL](https://dev.mysql.com/downloads/mysql/ "MySQL downloads") (deploy).
 * [Elasticsearch](http://www.elastic.co/ "Elasticsearch home") (currently using vers. 1.4.4) (deploy)
 * [Gitblit](http://www.gitblit.com/ "Gitblit home") (currently using vers. 1.7.1) (deploy)
 * [R](https://www.r-project.org/ "R home") (deploy)

## Instructions

 1. Install `parent-pom` ([instructions](https://github.com/gef-work/website/raw/master/parent-pom/INSTALL.md "Install instructions"))
 1. Install `shared` ([instructions](https://github.com/gef-work/website/raw/master/shared/INSTALL.md "Install instructions"))
 1. Install `business-manager` ([instructions](https://github.com/gef-work/website/raw/master/business-manager/INSTALL.md "Install instructions"))
 1. Install `client` ([instructions](https://github.com/gef-work/website/raw/master/client/INSTALL.md "Install instructions"))