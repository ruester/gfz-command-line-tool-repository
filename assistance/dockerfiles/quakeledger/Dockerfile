FROM ubuntu:18.04

RUN apt-get update && apt-get install python3 python3-pip git -y

# quakeml is provided as stand alone python script inside of the github repository
RUN pip3 install pandas scipy lxml

RUN mkdir /usr/share/git

RUN cd /usr/share/git && git clone https://github.com/bpross-52n/quakeledger.git