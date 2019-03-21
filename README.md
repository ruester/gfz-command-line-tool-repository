# gfz-riesgos-wps-repository

This is the java source code for the wps-repository for the riesgos project.
Processes that are used in this project by the GFZ that uses command line tools
should be included here.

## Currently implemented processes

* Quakeledger

## Features

All the processes are using a generic template for working with command line tools.
This includes input via
* stdin
* command line parameter
* input files

For output there are the following options:
* dispatch by exit value
* use of stdout
* use of stderr
* output files

The processes itself run inside of docker containers.
Example Dockerfiles to create the images necessary for this are included in the
assistence folder.

## Installation

1. Make sure that your WPS Server can run docker

For running executables inside of docker it is necessary that docker is installed
on the WPS Server itself.

If the WPS server itself runs inside of docker than you can edit the dockerfile for 
the server by adding the following lines:

```
RUN apt update 
RUN apt install apt-transport-https ca-certificates curl software-properties-common gnupg2 -y
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable"
RUN apt update 
RUN apt install docker-ce -y
```

Please note that this is the installation process for tomcat:9-jre8 as base image.
This is using a debian stretch distribution. When you use another distribution the 
installation process may change.

If you use docker-compose you must also add a line for adding a volume.
This is used to pass through access to the docker socket:

```
volumes:
    [...]
    - /var/run/docker.sock:/var/run/docker.sock
```

Then rebuild the image used by docker-compose by 
```
docker-compose build
```

2. Compile the project and create a package

The next step is to create a jar file out of the java code:

```
mvn clean package
```

3. Copy the jar to the WEB-INF/lib folder of the server

The created jar must be copied to the WEB-INF/lib folder of the server.

4. Check dependencies

A - maybe - new dependencies that must be included is the jar for Apache Commons Compress.
In the repository here the version 1.9 is used.

Please check if the commons-compress-1.9.jar file is found in the WEB-INF/lib folder
of the server.

If you dont find the jar in that folder you may find it in the 
```
~/.m2/repository/org/apache/commons/commons-compress/1.9/
```
folder. This is the folder used by maven for storing downloaded jar files in an 
Ubuntu 18.10 distribution.
The name of the folder may differ on your system.

5. Edit the WEB-INF/classes/dispatcher-servlet.xml file

Same as the in the https://github.com/riesgos/52north-wps-osmtovector-process repository
the WEB-INF/classes/dispatcher-servlet.xml file must be edited.

Change the line
```
<context:component-scan base-package="org.n52.wps">
```

into 
```
<context:component-scan base-package="org.n52">
```

This is necessary for the server that it searches for repostoties that are not
part of the org.n52.wps package.

6. Create the docker image for the processes

You must create the docker images for each process you want to use.
At the moment there is only the Quakeledger process.

If you run the WPS Server on a decicated server you must build them on that server.
If you run the WPS Server in docker than it is easily possible to build the images
on the host system (because of sharing the same docker demon).

Go into the assistance/dockerfile/quakeledger folder and run
```
docker build .
```

This command needs some time to run.

You need to save the image id for later use.

You can extract it right after the creation by running

```
docker images | head
```

It should be the most recent one.

7. Configure the process

Now it is time to start/restart the WPS server.

Once the server is ready you can goto to the configuration board of the server.
If the server runs inside of docker you may access it via

```
localhost:8080/wps
```

Under Repositories you find the GFZ RIESGOS Configuration Module.
There is a text field for inserting a JSON-Configuration.

After the imageId may change when build on another system, you have to edit this
configuration.

Insert the following:

```$xslt
[{"title": "Quakeledger", "imageId", "<INSERT_YOUR_IMAGE_ID_HERE>"}]
```

(Of course you have to insert the id of the image that you created one step before).

After a click on save you should be able to run the Quakeledger process.

