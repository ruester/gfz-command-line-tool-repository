# Processes for format conversion

We provide services for converting data from one format to an other one.
This mostly focus on having an OGC compliant data format for
some of the more expert and domain focused file formats that are around
in risk analysis. 

This is true for the quakeml file format as well as for shakemaps.

## QuakeMLTransformationProcess

The basic format of the quakeledger process is quakeml that is valid according to the
xsd file at http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd.

We had some afford to improve quakeledger to support this format, because
the older quakeml format - and so the output of the original quakeledger - 
was not valid.

Because there may be already old quakeml around, we provide a parser for both
the valid and the old xml.

We don't support all of the possible elements in the quakeml xml structure,
but some data about the overall position of the events, the uncertainty,
the depth, the magnitude and the focal mechanism.

We don't use the hierarchical structure of the xml, but more a table like
structure. This way we can provide a mechanism to transform the 
quakeml data from and into into a simple feature collection. 
This way we can parse and generate quakeml
in GML and GeoJSON. Both this formats use WGS84.

All those formats are equivalent at moment, so we can use any of this
as input and as output.

## ShakemapTransformationProcess

For the shakemap the case is a bit different compared to the quakeml
format.

The shakemap is more structured than the basic table like approach of
a simple feature collection, so there is just the shakemap xml format
that supports the full range of information.

However we provide a mechanism to transform the data of the shakemap into
a raster that we can export as GeoTiff.
In this conversion the point for which the shakemap contains the values
is always in the middle of the raster cell.
We provide raster bands for each of the rows in the shakemap (excluding
the latitude and longitude rows of course).

We can also transform the shakemap to a simple feature collection with
points and then give GML or GeoJSON as output.

GML, GeoJSON and GeoTiff all of them use WGS84 as coordinate reference system.

## Nrml

Similar to shakemaps it is possible to convert nrml to geojson.
At the moment it maps the hierarchical structure in a table
structure duplicating all the information for the exposure model
in every row.


## How to add your own format conversion process

The format conversion processes only rely on the parser and generator
principle of the wps server. 
Additional code is only executed to validate the data 
so to make sure that the input is valid.

You can take a look in the src/main/java/formats folder to analyse some
of the parsers and generators.

Once you have written the parser and generators (and registered them
in the GfzRiesgosRepository class) you only have to add the 
ClassTransformationProcess to the addAlgorithmsOfFormatTransformations
method in the GfzRiesgosRepositoryCM class.
