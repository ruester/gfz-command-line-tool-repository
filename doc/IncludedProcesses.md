# Included processes

The following processes are already included in this repository:

## quakeledger

You can find the source code of process at 
(this github page)[https://github.com/nbrinckm/quakeledger].
This is modified version of the original quakeledger code
you can find (here)[https://github.com/GFZ-Centre-for-Early-Warning/quakeledger].

(The changes are according to improved io speed by using an sqlite database
instead of an csv file. Also the output is now valid according
to the quakeml xsd file.)

The according dockerfile you can find in the assistance folder
[../assistance/dockerfiles/quakeledger/Dockerfile].
The configuration can be found in the resources folder 
[../src/main/resources/org/n52/gfz/riesgos/configuration/quakeledger.json].
 
## shakyground

The version of shakyground that we use is
[https://github.com/ruester/shakyground].
It is a improved version of
[https://github.com/GFZ-Centre-for-Early-Warning/shakyground].

The changes are to process valid quakeml and to produce a valid shakemap.

You can find the dockerfile in the assistance folder [../assistance/dockerfiles/shakyground/Dockerfile]
and the configuration in the resource folder [../src/main/resources/org/n52/gfz/riesgos/configuration/shakyground.json].

