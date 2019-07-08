# gfz-riesgos-wps-repository

## Description

This is the dockerhub branch of the gfz-riesgos-wps-repository project which is
automatically build and uploaded to Docker Hub if there is a new commit.

## How it works

You need to have docker installed on the host system and pull all needed images:
```
docker pull ruestergfz/quakeledger:latest
docker pull ruestergfz/shakyground:latest
docker pull ruestergfz/assetmaster:latest
docker pull ruestergfz/modelprop:latest
docker pull ruestergfz/flooddamage:latest
```

Start the RIESGOS WPS docker image with:
```
docker run -p8080:8080 -v /var/run/docker.sock:/var/run/docker.sock ruestergfz/riesgos-wps
```

Following services can then be accessed at localhost:

RIESGOS WPS:
```
http://localhost:8080/wps/WebProcessingService?Request=GetCapabilities&Service=WPS
```

WPS administration with login wps/wps:
```
http://localhost:8080/wps
```

WPS JS client:
```
http://localhost:8080/wps-js-client/
```

Tomcat manager with login admin/admin:
```
http://localhost:8080/manager
```

---


Developer hint:
If you want to use your own version of the gfz-command-line-tool-repository
just mount your .jar file to /usr/local/tomcat/webapps/wps/WEB-INF/lib/gfz-riesgos-wps.jar
```
-v /path/to/gfz-riesgos-wps.jar:/usr/local/tomcat/webapps/wps/WEB-INF/lib/gfz-riesgos-wps.jar
```
