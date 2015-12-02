# AEM Translation Framework Bootstrap Connector

The Bootstrap connector is built using the Translation Framework API for AEM 6.1.0. The purpose of the connector is to

* Provide sample code to Translation partners and Service providers to start building their connector
* Provide best practices for building/packaging the connector
* Highlight the coding standards for the Connector certification process
* Serve as a reference implementation of the Translation API

## Modules

The main parts of the template are:

* bundle: Java bundle containing all core functionality like OSGi service as well as component-related Java code.
* content: contains the /apps (and /etc) parts of the project, components, templates, configurations.

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallPackagePublish
    
Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle

## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html
