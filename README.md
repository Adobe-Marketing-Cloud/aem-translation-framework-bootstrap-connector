# AEM Translation Framework Bootstrap Connector

The Bootstrap connector is built using the Translation Framework API for AEM 6.4.0. The purpose of the connector is to

* Provide sample code to Translation partners and Service providers to start building their connector
* Provide best practices for building/packaging the connector
* Highlight the coding standards for the Connector certification process
* Serve as a reference implementation of the Translation API

## Modules

The main parts of the template are:

* core: Java bundle containing all core functionality like OSGi service as well as component-related Java code.
* ui.apps: contains the /apps (and /etc) parts of the project, components, templates, configurations.

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallPackagePublish
    
Or alternatively

    mvn clean install -PautoInstallPackage -Daem.port=4503

Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle

## Additional Details

For detailed information visit:
    
[Overview of Translation Framework](https://docs.adobe.com/docs/en/aem/6-1/administer/sites/translation/tc-tic.html)

[Bootstrap connector Installation steps](https://helpx.adobe.com/experience-manager/using/bootstrap.html)

[State diagram](https://files.acrobat.com/a/preview/32824bd9-6cc6-41b4-bc7b-8e7c4d2c7d65) showing different states of translation as noted in the API
    
