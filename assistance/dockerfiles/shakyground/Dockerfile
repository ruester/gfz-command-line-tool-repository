FROM ubuntu:18.04

# for not having interaction on installation process
ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update && apt-get install python3.7 python3-pip git gmt gmt-dcw gmt-gshhg python3-tk wget unzip -y

# in the moment there is a problem here on installing GMT
RUN pip3 install pandas scipy lxml h5py shapely zmq mock matplotlib decorator scipy psutil
RUN mkdir -p /tmp/install_gmt/
RUN cd /tmp/install_gmt/ && git clone https://github.com/GenericMappingTools/pygmt.git
RUN pip3 install pygmt

RUN mkdir /usr/share/git

RUN cd /usr/share/git && git clone https://github.com/GFZ-Centre-for-Early-Warning/shakyground.git

RUN cd /usr/share/git/shakyground && wget https://earthquake.usgs.gov/static/lfs/data/global_vs30_grd.zip && unzip global_vs30_grd.zip -d . && rm global_vs30_grd.zip
