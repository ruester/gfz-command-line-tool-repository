# Processes for format conversion

We also provide services for converting data from one format to an other.
This mostly focus on having an OGC complient data format for
some of the more expert and domain focused file formats that are around
in risk analysis. This is true for the quakeml file format as well
as for shakemaps.


## quakeml

This services now support a custom type quakeml.
It is provided as xml that is confirm to the schema http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd.
The early version of the quakeledger process uses a different version of the xml.
We try to support it as input and output, but the underlying process now uses the
validated xml only.

There is also the possibility to convert the quakeml to geojson.

## shakemap 
