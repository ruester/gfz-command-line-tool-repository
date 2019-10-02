# gfz-riesgos-wps-repository

[![pipeline status](https://gitext.gfz-potsdam.de/riesgos/gfz-riesgos-wps-repository/badges/master/pipeline.svg)](https://gitext.gfz-potsdam.de/riesgos/gfz-riesgos-wps-repository/commits/master)

## Description

This is the dockerhub branch of the gfz-riesgos-wps-repository project which is
automatically build and uploaded to Docker Hub if there is a new commit.

## How it works

You need to have docker installed on the host system and pull all needed images
used by the processes you want to configure.

For the GFZ services of the RIESGOS project following images are available
and need to be pulled:

```bash
docker pull gfzriesgos/quakeledger:latest
docker pull gfzriesgos/shakyground:latest
docker pull gfzriesgos/assetmaster:latest
docker pull gfzriesgos/modelprop:latest
docker pull gfzriesgos/flooddamage:latest
docker pull gfzriesgos/flooddamage-tiff-downloader:latest
docker pull gfzriesgos/deus:latest
```

Start the RIESGOS WPS docker image with:

```bash
docker run -p8080:8080 -v /var/run/docker.sock:/var/run/docker.sock gfzriesgos/riesgos-wps
```

Following services can then be accessed at localhost port 8080:

The WPS itself:

```text
http://localhost:8080/wps/WebProcessingService?Request=GetCapabilities&Service=WPS
```

WPS administration console with login wps/wps:

```text
http://localhost:8080/wps
```

WPS JS client:

```text
http://localhost:8080/wps-js-client/
```

Tomcat manager with login admin/admin:

```text
http://localhost:8080/manager
```

GeoServer instance with login admin/geoserver:

```text
http://localhost:8080/geoserver
```

## Adding processes

You can add a process to the WPS by adding a configuration file to
`/usr/share/riesgos/json-configurations` within the docker container.

Therefore you can use the `docker cp` command:

```bash
docker cp myprocess.json CONTAINER-ID:/usr/share/riesgos/json-configurations
```

To add and enable the GFZ services you need to copy the configuration files
shipped with
[this repository](https://github.com/riesgos/gfz-command-line-tool-repository)
located at
[src/main/resources/org/n52/gfz/riesgos/configuration](src/main/resources/org/n52/gfz/riesgos/configuration)
to `/usr/share/riesgos/json-configurations` within the docker container.

It is also possible to use a volume for this directory so that the configuration
files are persistent between containers creations (for example when updating the
RIESGOS-WPS docker image):

```bash
docker volume create riesgos-json-config
# add additional volume parameter when starting the container
# (see above section for the whole command)
docker run ... -v riesgos-json-config:/usr/share/riesgos/json-configurations ...
```

## Security hardening

Do not forget to change the default passwords once the server is running productively.
This includes changing the default login for:

- WPS administration console (can be changed within the web console itself)
- GeoServer instance (can be changed within the web console itself)
- Tomcat manager console (changed by editing the file `/usr/local/tomcat/conf/tomcat-users.xml` within the container and restarting it)

When changing the GeoServer password you also need to enter the same password within
the GeoServer generator configurations located at the WPS admin console at:
[http://localhost:8080/wps/generators](http://localhost:8080/wps/generators).

To persist those passwords and configurations of the WPS you can also use
volumes for following directories/files:

- `/usr/local/tomcat/webapps/wps/WEB-INF/classes/db/data`
- `/usr/local/tomcat/webapps/geoserver/data/security/usergroup/default/users.xml`
- `/usr/local/tomcat/conf/tomcat-users.xml`

## Developing

If you want to use your own version of the gfz-command-line-tool-repository
just mount your `.jar` file to
`/usr/local/tomcat/webapps/wps/WEB-INF/lib/gfz-riesgos-wps.jar`
with an additional volume parameter:

```bash
-v /path/to/gfz-riesgos-wps.jar:/usr/local/tomcat/webapps/wps/WEB-INF/lib/gfz-riesgos-wps.jar
```

## Logging

You can change the log levels of the different components/classes within the
administration web console of the WPS server located at
[http://localhost:8080/wps/log](http://localhost:8080/wps/log).

To set for example the log level for the process executions you can add the
value `org.n52.gfz.riesgos.algorithm.impl` to the "Loggers" configuration.
After changing the configuration of the log levels you need to restart
the docker container to enable the changes.
