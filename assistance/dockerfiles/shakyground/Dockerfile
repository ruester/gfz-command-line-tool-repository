FROM ubuntu:18.04

# for not having interaction on installation process
ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update && apt-get install python3.7 python3-pip git gmt gmt-dcw gmt-gshhg python3-tk wget unzip -y

# in the moment there is a problem here on installing GMT
RUN pip3 install pandas scipy lxml h5py shapely zmq mock matplotlib decorator scipy psutil
RUN mkdir -p /tmp/install_gmt/
RUN cd /tmp/install_gmt/ && git clone https://github.com/GenericMappingTools/pygmt.git
RUN pip3 install pygmt

RUN mkdir -pv /usr/share/git/shakyground

RUN cd /usr/share/git/shakyground && wget https://earthquake.usgs.gov/static/lfs/data/global_vs30_grd.zip && unzip global_vs30_grd.zip -d . && rm global_vs30_grd.zip

RUN cd /usr/share/git/shakyground && git init . && git remote add origin https://github.com/ruester/shakyground.git --fetch && git checkout -b current tags/20190523
