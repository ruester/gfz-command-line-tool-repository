FROM tomcat:9-jre8

# you need to have docker installed on the host system and pull all needed images:
# docker pull ruestergfz/quakeledger:latest
# docker pull ruestergfz/shakyground:latest
# docker pull ruestergfz/assetmaster:latest
# docker pull ruestergfz/modelprop:latest
# docker pull ruestergfz/flooddamage:latest

# start the RIESGOS WPS docker image with:
# docker run -p8080:8080 -v /var/run/docker.sock:/var/run/docker.sock ruestergfz/riesgos-wps

# following services can then be accessed at localhost:

# RIESGOS WPS:
# http://localhost:8080/wps/WebProcessingService?Request=GetCapabilities&Service=WPS

# WPS administration with login wps/wps:
# http://localhost:8080/wps

# WPS JS client:
# http://localhost:8080/wps-js-client/

# Tomcat manager with login admin/admin:
# http://localhost:8080/manager

# developer hint:
# if you want to use your own version of the gfz-command-line-tool-repository
# just mount your .jar file to /usr/local/tomcat/webapps/wps/WEB-INF/lib/gfz-riesgos-wps.jar
# -v /path/to/gfz-riesgos-wps.jar:/usr/local/tomcat/webapps/wps/WEB-INF/lib/gfz-riesgos-wps.jar


VOLUME [ "/usr/share/riesgos/json-configurations" ]

RUN apt update && \
    apt install git apt-transport-https ca-certificates maven curl software-properties-common openjdk-8-jdk gnupg2 -y


# install nodejs, npm and grunt
RUN curl -sL https://deb.nodesource.com/setup_8.x | bash -
RUN apt install nodejs npm -y && \
    npm install -g grunt-cli && \
    npm install -g bower
RUN node --version
RUN npm --version
RUN grunt --version
RUN bower --version


# install docker
ENV DOCKER_CHANNEL stable
ENV DOCKER_VERSION 18.09.6
ENV DOCKER_ARCH x86_64

RUN wget -O /tmp/docker.tgz "https://download.docker.com/linux/static/${DOCKER_CHANNEL}/${DOCKER_ARCH}/docker-${DOCKER_VERSION}.tgz" && \
    tar --extract --file /tmp/docker.tgz --strip-components 1 --directory /usr/local/bin/ && \
    rm /tmp/docker.tgz
RUN dockerd --version
RUN docker --version

# setup tomcat
RUN printf '<?xml version="1.0" encoding="UTF-8"?>\n\
<tomcat-users xmlns="http://tomcat.apache.org/xml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd" version="1.0">\n\
  <role rolename="manager-gui" />\n\
  <user username="admin" password="admin" roles="manager-gui" />\n\
</tomcat-users>\n\
' > /usr/local/tomcat/conf/tomcat-users.xml

RUN printf '<?xml version="1.0" encoding="UTF-8"?>\n\
<Context antiResourceLocking="false" privileged="true">\n\
  <Valve className="org.apache.catalina.valves.RemoteAddrValve"\n\
         allow="^.*$" />\n\
  <Manager sessionAttributeValueClassNameFilter="java\.lang\.(?:Boolean|Integer|Long|Number|String)|org\.apache\.catalina\.filters\.CsrfPreventionFilter\$LruCache(?:\$1)?|java\.util\.(?:Linked)?HashMap"/>\n\
</Context>\n\
' > /usr/local/tomcat/webapps/manager/META-INF/context.xml

RUN wget https://repo1.maven.org/maven2/xerces/xercesImpl/2.7.1/xercesImpl-2.7.1.jar -O /usr/local/tomcat/lib/xercesImpl-2.7.1.jar


# add or build needed webapps
WORKDIR /usr/local/tomcat/webapps
RUN wget https://datapacket.dl.sourceforge.net/project/geoserver/GeoServer/2.10.4/geoserver-2.10.4-war.zip -O geoserver-2.10.4-war.zip && \
    unzip geoserver-2.10.4-war.zip && \
    rm -rf target geoserver-2.10.4-war.zip

RUN mkdir /root/git

WORKDIR /root/git

# if you want to use the master branch from wps-js you need the complete history:
# git clone -b master https://github.com/52North/wps-js.git

RUN git clone -b dev --depth=1 https://github.com/52North/wps-js.git && \
    git clone -b develop --depth=1 https://github.com/52North/wps-js-client.git && \
    git clone https://github.com/52North/WPS.git && \
    git clone -b master --depth=1 https://github.com/ruester/gfz-command-line-tool-repository.git

WORKDIR /root/git/wps-js

# for master branch:
#RUN mvn clean install -P -js-unit-tests && \
#    cp -v target/wps-js-*.war /usr/local/tomcat/webapps/

# for dev branch:
RUN npm install && \
    bower --allow-root install && \
    grunt -v && \
    cp -vr dist /usr/local/tomcat/webapps/wps-js

WORKDIR /root/git/wps-js-client
RUN npm install && \
    bower --allow-root install && \
    grunt -v && \
    cp -vr dist /usr/local/tomcat/webapps/wps-js-client

WORKDIR /root/git/WPS
# checkout last tested commit to work with gfz-command-line-tool-repository
RUN git checkout -b current 1d1a7b9abf0e8f0b8c302651f7206f175866c4a8 && \
    mvn clean install -P with-geotools && \
    cp -vr 52n-wps-webapp/target/52n-wps-webapp-*-SNAPSHOT/ /usr/local/tomcat/webapps/wps && \
    sed -i -e 's@base-package="org\.n52\.wps"@base-package="org\.n52"@' /usr/local/tomcat/webapps/wps/WEB-INF/classes/dispatcher-servlet.xml

WORKDIR /root/git/gfz-command-line-tool-repository
RUN sed -i -e 's@assetmaster:latest@ruestergfz/assetmaster:latest@' src/main/resources/org/n52/gfz/riesgos/configuration/assetmaster.json && \
    sed -i -e 's@flooddamage:latest@ruestergfz/flooddamage:latest@' src/main/resources/org/n52/gfz/riesgos/configuration/flooddamage.json && \
    sed -i -e 's@modelprop:latest@ruestergfz/modelprop:latest@'     src/main/resources/org/n52/gfz/riesgos/configuration/modelprop.json && \
    sed -i -e 's@quakeledger:latest@ruestergfz/quakeledger:latest@' src/main/resources/org/n52/gfz/riesgos/configuration/quakeledger.json && \
    sed -i -e 's@shakyground:latest@ruestergfz/shakyground:latest@' src/main/resources/org/n52/gfz/riesgos/configuration/shakyground.json && \
    mvn clean install && \
    cp -v target/*.jar /usr/local/tomcat/webapps/wps/WEB-INF/lib/gfz-riesgos-wps.jar

RUN wget https://repo1.maven.org/maven2/org/apache/commons/commons-compress/1.9/commons-compress-1.9.jar -O /usr/local/tomcat/webapps/wps/WEB-INF/lib/commons-compress-1.9.jar && \
    wget https://repo1.maven.org/maven2/org/apache/ant/ant/1.10.5/ant-1.10.5.jar -O /usr/local/tomcat/webapps/wps/WEB-INF/lib/ant-1.10.5.jar
