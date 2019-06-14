# I have to recreate the dockerfile from the
# 
FROM ubuntu:18.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install python3 git python3-scipy python3-numpy python3-gdal python3-pip libspatialindex-dev -y

RUN pip3 install sklearn scikit-learn==0.19.1 geopandas==0.5.0 Rtree==0.8.3 

RUN mkdir /usr/share/git

# for the moment we don't have the repo on a public
# github (just the gfz internal gitlab)
# so we have to checkout it first
# and then copy it into the image
# at the moment
# it is used the commit 
# 4898263262c5adebe568329602c6ff77b063b336
COPY riesgos_flooddamage /usr/share/git/riesgos_flooddamage
