#!/bin/bash

URL="http://rz-vm140.gfz-potsdam.de"
STYLE_NAME="shakemap-pga"
SLD_FILE="shakemap-pga.sld"
GS_USER="admin"

echo -n Password:
read -s password
echo

GS_PASSWORD=${password}

curl -v -u "${GS_USER}:${GS_PASSWORD}" \
    -XPOST -H "Content-type: text/xml" \
    -d "<style><name>${STYLE_NAME}</name><filename>${SLD_FILE}</filename></style>" \
    "${URL}/geoserver/rest/styles"

curl -v -u "${GS_USER}:${GS_PASSWORD}" \
    -XPUT -H "Content-type: application/vnd.ogc.sld+xml" \
    -d "@${SLD_FILE}" \
    "${URL}/geoserver/rest/styles/${STYLE_NAME}"
