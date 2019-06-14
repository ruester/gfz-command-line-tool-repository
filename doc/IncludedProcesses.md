# Included processes

The following processes are already included in this repository:

## Quakeledger

Quakeledger is the first service provided here.
It is used as an earth quake catalogue that you can query with
an bounding box for location constraints, as well as
ranges for magnitude, depth and so on.
It is a python3 script that creates an quakeml file.

The main version of Quakeledger you can find here:
https://github.com/GFZ-Centre-for-Early-Warning/quakeledger

However, there was some need for improvements:
- more input parameters for better search capabilities
- better performance on searching in small areas by using a sqlite
  database instead of an csv file
- creation of valid quakeml according to the 
  [xsd file](https://quake.ethz.ch/quakeml/docs/xml?action=AttachFile&do=get&target=QuakeML-BED-1.2.xsd).

Those improvements are included in the following github repository:
 
https://github.com/nbrinckm/quakeledger

which itself sits on top of the changes that Benjamin Proß did:

https://github.com/bpross-52n/quakeledger

The source code that is used in the moment is the one from
https://github.com/nbrinckm/quakeledger.

We wrote a Dockerfile for this process to include all of the
dependencies and the actual version of the source code.

You can find the [Dockerfile](../assistance/dockerfiles/quakeledger/Dockerfile)
in the assistance folder of this repository.

The 
[json configuration](../src/main/resources/org/n52/gfz/riesgos/configuration/quakeledger.json)
for the input and output handling of the process can
be found in the resources folder of the this repository.
 
## Shakyground

Shakyground is the second service provided here.
It is a tool to calculate the ground motion field for an event in a quakeml
file. The output of the process is a shakemap.
Similar to Quakeledger Shakyground is a python3 script.

The main version of Shakyground you can find here:
https://github.com/GFZ-Centre-for-Early-Warning/shakyground

We included the following improvements:
- Possibility to read valid quakeml according to the xsd file 
  (see at the section for quakeml)
- creation of valid shakemap

Those are included in the following repository, that is also used
for the process that we provide in this repository:

https://github.com/ruester/shakyground

You can find the dockerfile in the 
[assistance folder](../assistance/dockerfiles/shakyground/Dockerfile)
and the json configuration in the 
[resources folder](../src/main/resources/org/n52/gfz/riesgos/configuration/shakyground.json). 

## Flooddamage

Flooddamage is a process to compute damage on the manzanas river
in ecuador.
The code is provided in the gfz gitlab:
https://git.gfz-potsdam.de/fbrill/riesgos_flooddamage

You can find the dockerfile in the
[assistance folder](../assistance/dockerfiles/flooddamage/Dockerfile)
and the json configuration in the
[resource folder](../src/main/resources/org/n52/gfz/riesgos/configuration/flooddamage.json).

This configuration uses json as output instead of geojson because of an
issue on reading geojson files created by geopandas in java.

Also at the moment there are no input parameter which will change with
the next versions.

## assetmaster

Assetmaster is a process to provide exposure models in the riesgos
project.

The code is provided here:
https://github.com/GFZ-Centre-for-Early-Warning/assetmaster

The dockerfile is in the 
[assistance folder](../assistance/dockerfiles/assetmaster/Dockerfile).
The json configuration is based on the metadata/assetmaster.json
file you can find in the https://github.com/GFZ-Centre-for-Early-Warning/assetmaster
github repository, but uses a no xml schema but a different stderr
error handler.
You can find the fine in the 
[resource folder](../src/main/resources/org/n52/gfz/riesgos/configuration/assetmaster.json).




## Format conversion processes

We also provide some services for converting the output of quakeledger
or shakyground to OGC complient file formats.
You can find an overview [here](FormatConversionProcesses.md).
