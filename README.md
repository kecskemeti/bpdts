
# Overview

This demoes an application interface for a service using a swagger based description.

License: GNU General Public License 3 and later

#Compilation & Installation

Prerequisites: Apache Maven 3, Java 14

After cloning and installing the prerequisites, run the following in the main dir of the checkout:

`mvn clean install javadoc:javadoc`

The installed examples will be located in the default maven repository's (e.g., `~/.m2/repository`) following directory: `bpdts/bpdts/[VERSION]/bpdts-[VERSION].jar`. The current version of BPDTS is 0.0.1-SNAPSHOT.

The documentation for the java code will be generated in the following subfolder of the main dir of the git clone:
`target/site/apidocs`

#Getting started

There is a demo application that uses the API. This will load the `GreaterLondon.json` to search for citizens and nearby users listed by the swagger described service. The application is located in `src/main/java/bpdts/demo`. To make it work it should receive the url of the service as a command line parameter. When it is executed it assumes it is executed in the main folder where the git repository was cloned (because it uses the city descriptions from the `src/main/resources/*.json`).