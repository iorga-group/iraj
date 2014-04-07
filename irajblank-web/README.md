# iORGA REST Architecture for Java - Blank Web app

## Introduction

IRAJ is a framework that combines Java (RESTEasy + CDI) for web services and AngularJS to create powerful modern applications.

This example is an out of the box application to demonstrate how easy it is to develop an IRAJ-powered application.

For easier development, this application is built on top of well-proven technologies like Grunt, Bower and SASS.

## Prerequisites

* Install [Maven](http://maven.apache.org/)

* Install [node.js](http://nodejs.org)

* Install [Grunt](http://gruntjs.com)

* Install [Bower](http://bower.io/) (Optional)

* Install [Ruby](https://www.ruby-lang.org/en/) & [Compass](http://compass-style.org/) (Optional)


## Database

Because IRAJ Blank uses an in-memory embedded database engine ([h2](http://h2database.com)), it requires a JDBC driver to deal with it. It is available [here](http://h2database.com/html/download.html).

If you use [Tomcat](http://tomcat.apache.org/), you just have to put it in the TOMCAT_DIR/lib.

## Build

Deploy irajblank-web on your favorite Java application server (tested with Tomcat 7).

Execute `npm install` to download front-end dependencies and run one of these two configurations :

### Development workflow

`grunt serve`

Grunt will download front-end dependencies, build the files and track your changes.

### Production workflow

`grunt build`

Grunt will download front-end dependencies, and minify all your files automatically.