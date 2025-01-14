FROM tomcat:9-jdk8-openjdk-slim-buster

# You need to have docker installed on the host system and pull all needed
# images as configured by the repository or the configuration directory
# /usr/share/riesgos/json-configurations

# Start the RIESGOS WPS docker image with:
# docker run -p8080:8080 -v /var/run/docker.sock:/var/run/docker.sock gfzriesgos/riesgos-wps
# (or any other port like for example -p80:8080)

# To manage the configured processes add a volume parameter for /usr/share/riesgos/json-configurations
# for example: -v /path/to/my/json-configs:/usr/share/riesgos/json-configurations

# Following services can then be accessed at localhost:

# RIESGOS WPS:
# http://localhost:8080/wps/WebProcessingService?Request=GetCapabilities&Service=WPS

# WPS administration with login wps/wps:
# http://localhost:8080/wps

# WPS JS client:
# http://localhost:8080/wps-js-client/

# Tomcat manager with login admin/admin:
# http://localhost:8080/manager

# GeoServer instance with login admin/geoserver:
# http://localhost:8080/geoserver

# For production environments do not forget to change those default passwords!
# For persisting the changed passwords you can use volumes for the following files:
# /usr/local/tomcat/conf/tomcat-users.xml
# /usr/local/tomcat/webapps/geoserver/data/security/usergroup/default/users.xml

# If you changed the GeoServer password you have to change the passwords also
# for the WMS and WFS generator configurations at the WPS server admin console.
# To persist all the configurations of the WPS server you need to use a volume for:
# /usr/local/tomcat/webapps/wps/WEB-INF/classes/db/data

# If you want to use your own version of the gfz-command-line-tool-repository
# just mount the built jar file to /usr/local/tomcat/webapps/wps/WEB-INF/lib/gfz-riesgos-wps.jar
# for example with a volume:
# -v /path/to/gfz-riesgos-wps.jar:/usr/local/tomcat/webapps/wps/WEB-INF/lib/gfz-riesgos-wps.jar

ARG WPS_VERSION=4.0.0-beta.10
ARG WPS_JS_COMMIT=e56ffe34540f4b0261442eccd9cee0557ae1b5e5
ARG WPS_JS_CLIENT_COMMIT=0945f0282424c56324c98897d50ffc7e11fffde6

ENV DEBIAN_FRONTEND noninteractive

VOLUME [ "/usr/share/riesgos/json-configurations" ]

RUN apt update && \
    apt upgrade -y && \
    apt install -y \
        git \
        apt-transport-https \
        ca-certificates \
        maven \
        curl \
        software-properties-common \
        gnupg2 \
        wget \
        unzip \
        docker.io && \
    docker --version

# install nodejs, grunt and bower
# nodejs package already contains npm
# installing the debian buster npm version would lead to package conflicts
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash - && \
    apt install -y nodejs && \
    npm install -g grunt-cli@v1.4.3 && \
    npm install -g bower@1.8.13

# setup tomcat
RUN printf '<?xml version="1.0" encoding="UTF-8"?>\n\
<tomcat-users xmlns="http://tomcat.apache.org/xml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd" version="1.0">\n\
  <role rolename="manager-gui" />\n\
  <user username="admin" password="admin" roles="manager-gui" />\n\
</tomcat-users>\n\
' > /usr/local/tomcat/conf/tomcat-users.xml

# activate tomcat manager
RUN rmdir /usr/local/tomcat/webapps && \
    mv /usr/local/tomcat/webapps.dist /usr/local/tomcat/webapps && \
    rm -rf /usr/local/tomcat/webapps/examples

RUN printf '<?xml version="1.0" encoding="UTF-8"?>\n\
<Context antiResourceLocking="false" privileged="true">\n\
  <Valve className="org.apache.catalina.valves.RemoteAddrValve"\n\
         allow="^.*$" />\n\
  <Manager sessionAttributeValueClassNameFilter="java\.lang\.(?:Boolean|Integer|Long|Number|String)|org\.apache\.catalina\.filters\.CsrfPreventionFilter\$LruCache(?:\$1)?|java\.util\.(?:Linked)?HashMap"/>\n\
</Context>\n\
' > /usr/local/tomcat/webapps/manager/META-INF/context.xml

# do not use the git protocol for GitHub dependencies as it may cause problems
RUN printf '[url "https://github"]\n\
    insteadOf = git://github\n\
' > /etc/gitconfig

# change the default tomcat front page
RUN mv /usr/local/tomcat/webapps/ROOT/index.jsp /usr/local/tomcat/webapps/ROOT/_index.jsp && \
    printf '<html><body><a href="/wps-js-client">WPS JS Client</a><br><a href="/wps/WebProcessingService?Request=GetCapabilities&Service=WPS">WebProcessingService</a></body></html>\
' > /usr/local/tomcat/webapps/ROOT/index.html


# add or build needed webapps:

# geoserver
RUN mkdir -pv /usr/local/tomcat/webapps/geoserver

WORKDIR /usr/local/tomcat/webapps/geoserver
RUN wget https://repo1.maven.org/maven2/xerces/xercesImpl/2.7.1/xercesImpl-2.7.1.jar -O /usr/local/tomcat/lib/xercesImpl-2.7.1.jar && \
    wget https://downloads.sourceforge.net/project/geoserver/GeoServer/2.22.2/geoserver-2.22.2-war.zip -O geoserver-2.22.2-war.zip && \
    unzip geoserver-2.22.2-war.zip && \
    rm -f geoserver-2.22.2-war.zip && \
    unzip geoserver.war && \
    wget https://repo1.maven.org/maven2/com/thetransactioncompany/cors-filter/1.9.3/cors-filter-1.9.3.jar \
        -O WEB-INF/lib/cors-filter-1.9.3.jar && \
    wget https://repo1.maven.org/maven2/com/thetransactioncompany/java-property-utils/1.9.1/java-property-utils-1.9.1.jar \
        -O WEB-INF/lib/java-property-utils-1.9.1.jar && \
    rm -f WEB-INF/web.xml && \
    rm -f geoserver.war

COPY ./assistance/geoserver-web.xml /usr/local/tomcat/webapps/geoserver/WEB-INF/web.xml

# clone needed git repos:
WORKDIR /root/git

# if you want to use the master branch from wps-js you need the complete history:
# git clone -b master https://github.com/52North/wps-js.git

RUN git config --global user.email "root@localhost" && \
    git config --global user.name "Root" && \
    git clone https://github.com/52North/wps-js.git && \
    git clone https://github.com/52North/wps-js-client.git && \
    git clone https://github.com/52North/WPS.git

WORKDIR /root/git/wps-js

# for master branch:
#RUN mvn clean install -P -js-unit-tests && \
#    cp -v target/wps-js-*.war /usr/local/tomcat/webapps/

# for dev branch:
RUN git reset --hard "${WPS_JS_COMMIT}" && \
    npm install && \
    bower --allow-root install && \
    grunt -v && \
    cp -vr dist /usr/local/tomcat/webapps/wps-js

WORKDIR /root/git/wps-js-client
RUN git reset --hard "${WPS_JS_CLIENT_COMMIT}" && \
    npm install && \
    bower --allow-root install && \
    grunt -v && \
    cp -vr dist /usr/local/tomcat/webapps/wps-js-client

WORKDIR /root/git/WPS
COPY ./assistance/repo-fix.patch .

# build the WPS war file
# if geoprocessing.demo.52north.org is down the tests would fail
# so we skip executing the tests for now
RUN git checkout -b current v${WPS_VERSION} && \
    git am repo-fix.patch && \
    mvn clean package -DskipTests=true --batch-mode -P with-geotools && \
    mkdir -pv /usr/local/tomcat/webapps/wps && \
    cp -v 52n-wps-webapp/target/52n-wps-webapp-${WPS_VERSION}.war /usr/local/tomcat/webapps/wps/wps.war && \
    cd /usr/local/tomcat/webapps/wps && \
    unzip wps.war && \
    rm /usr/local/tomcat/webapps/wps/wps.war && \
    sed -i -e 's@base-package="org\.n52\.wps"@base-package="org\.n52"@' /usr/local/tomcat/webapps/wps/WEB-INF/classes/dispatcher-servlet.xml

# add missing libraries needed by gfz-command-line-tool-repository:
RUN wget https://repo1.maven.org/maven2/org/apache/commons/commons-compress/1.19/commons-compress-1.19.jar -O /usr/local/tomcat/webapps/wps/WEB-INF/lib/commons-compress-1.19.jar && \
    wget https://repo1.maven.org/maven2/org/apache/ant/ant/1.10.5/ant-1.10.5.jar -O /usr/local/tomcat/webapps/wps/WEB-INF/lib/ant-1.10.5.jar && \
    wget https://repo.osgeo.org/repository/geotools-releases/org/geotools/gt-process-raster/13.5/gt-process-raster-13.5.jar -O /usr/local/tomcat/webapps/wps/WEB-INF/lib/gt-process-raster-13.5.jar && \
    wget https://repo.osgeo.org/repository/geotools-releases/org/geotools/gt-process/13.5/gt-process-13.5.jar -O /usr/local/tomcat/webapps/wps/WEB-INF/lib/gt-process-13.5.jar && \
    wget https://repo1.maven.org/maven2/org/jaitools/jt-contour/1.3.1/jt-contour-1.3.1.jar -O /usr/local/tomcat/webapps/wps/WEB-INF/lib/jt-contour-1.3.1.jar && \
    wget https://repo1.maven.org/maven2/org/jaitools/jt-attributeop/1.3.1/jt-attributeop-1.3.1.jar -O /usr/local/tomcat/webapps/wps/WEB-INF/lib/jt-attributeop-1.3.1.jar

WORKDIR /root/git/gfz-command-line-tool-repository

# download dependencies
# have this as an extra step in the Dockerfile to enable caching and not
# download the dependencies for every build (if only the source code changed)
COPY pom.xml .
RUN mvn --batch-mode dependency:resolve

COPY . .
RUN mvn --batch-mode clean package && \
    cp -v target/*.jar /usr/local/tomcat/webapps/wps/WEB-INF/lib/gfz-riesgos-wps.jar
