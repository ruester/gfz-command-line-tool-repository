#!/bin/bash

# helper script to get all configuration files of the services
# needs the docker-compose file as a parameter
# copies the service configuration files from the docker images to ./conf/

set -e

cd "$(dirname "$0")"

if [ -d "${PWD}/conf" ]; then
    rm -rf "${PWD}/conf"
fi

mkdir "${PWD}/conf"

ARGS=("$@")
COMPOSE_FILE=${ARGS[0]}
SERVICES=(assetmaster deus eve flooddamage modelprop quakeledger shakyground)
TEMPFILE=$(mktemp)

function cleanup {
    rm -f "${TEMPFILE}"
}

trap cleanup SIGINT SIGTERM ERR EXIT

if [ -z "${COMPOSE_FILE}" ]; then
    echo "Need docker-compose file as parameter"
    exit 1
fi

for service in ${SERVICES[@]}; do
    echo ${service} >> ${TEMPFILE}
done

# get yq for yaml parsing
wget "https://github.com/mikefarah/yq/releases/download/v4.16.2/yq_linux_amd64.tar.gz"
tar xf yq_linux_amd64.tar.gz './yq_linux_amd64'

# get image names from services with yq
IMAGES=$(
    ./yq_linux_amd64 eval '.services.*.image' ${COMPOSE_FILE} \
        | grep -F -f "${TEMPFILE}"
)

for image in ${IMAGES}; do
    echo "Copying configuration from image ${image}"

    SERVICE=$(echo ${image} | grep -F -o -f ${TEMPFILE})
    echo "Image \"${image}\" belongs to service \"${SERVICE}\""

    CONFFILE="/usr/share/git/${SERVICE}/metadata/${SERVICE}.json"

    if [ "${SERVICE}" = "flooddamage" ]; then
        CONFFILE="/usr/share/git/riesgos_${SERVICE}/metadata/${SERVICE}.json"
    fi

    echo "Getting configuration file \"${CONFFILE}\""

    CONTAINER_ID=$(docker create "${image}")
    docker cp "${CONTAINER_ID}:${CONFFILE}" conf/
    docker rm -v "${CONTAINER_ID}" 1>/dev/null 2>&1

    # also get volcanus and neptunus services
    if [ "${SERVICE}" = "deus" ]; then
        CONFFILE="/usr/share/git/deus/metadata/volcanus.json"
        echo "Getting configuration file \"${CONFFILE}\""

        CONTAINER_ID=$(docker create "${image}")
        docker cp "${CONTAINER_ID}:${CONFFILE}" conf/

        # And same for neptunus
        CONFFILE="/usr/share/git/deus/metadata/neptunus.json"
        docker cp "${CONTAINER_ID}:${CONFFILE}" conf/

        docker rm -v "${CONTAINER_ID}" 1>/dev/null 2>&1
    fi
done

echo "All configuration files copied to ${PWD}/conf"
