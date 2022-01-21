#!/bin/bash

# send ShakeMap style to GeoServer

set -e

cd "$(dirname "$0")"

URL="__GEOSERVER_URL__"
STYLE_NAME="shakemap-pga"
SLD_FILE="shakemap-pga.sld"
GS_USER="admin"
GS_PASSWORD="__GEOSERVER_PASSWORD__"

# remove style if it exists already
curl -v -u "${GS_USER}:${GS_PASSWORD}" \
    -XDELETE -H "Content-type: text/xml" \
    "${URL}/rest/styles/${STYLE_NAME}" || true

# create style
curl --fail -v -u "${GS_USER}:${GS_PASSWORD}" \
    -XPOST -H "Content-type: text/xml" \
    -d "<style><name>${STYLE_NAME}</name><filename>${SLD_FILE}</filename></style>" \
    "${URL}/rest/styles"

# modify the style to use this SLD
curl --fail -v -u "${GS_USER}:${GS_PASSWORD}" \
    -XPUT -H "Content-type: application/vnd.ogc.sld+xml" \
    -d "@${SLD_FILE}" \
    "${URL}/rest/styles/${STYLE_NAME}"
