# Supported in- and output formats

Most of the supported in- and output formats are created to support the
[included processes](IncludedProcesses.md).

If you realize that there is format missing that you need take a look
at the [how-to guide on adding own formats](HowToAddOwnFormats.md).

The formats here specify how the data will be given to or read from
the command line program. The conversion further conversion from
user input is done via parsers, the conversion to process output is
done via generators.

Please note, that there are different formats supported for different
kinds of input and output mechanisms.

So for example, it makes a sense to give a boolean is a command line argument
(so that if true, there is flag else there is none), but it does not
make any sense to give a boolean as in input file.

## Input

The following input mechanisms are possible for command line programs:

1. Command line arguments
2. Input files
3. Text via stdin

## Command line arguments

Giving data as command line arguments is the mechanism with the most
supported types. It includes:

- int
- double
- boolean
- string
- bbox
- xml
- geotiff
- geojson
- shapefile
- file
- quakeml
- json
- nrml

Please note that for the literal values (int, double, string) it is possible
to provide a default value and a list of allowed values. Please note
that the server does not recognize an empty string as a default value.

For boolean a default value is possible to, but a list of allowed values would
make no sense for a data type that can have only two different states.
As mentioned above for booleans there will be a flag set if the value is true.

The bbox needs a list of supported coordinate reference systems.
The corners of the bounding box will be given as a list of command line
arguments.
The first is the minimum longitude, than the maximum longitude, than
the the minimum latitude and last the maximum latitude.
This is according to the order that the quakeledger uses for its
bounding box.

For the other complex data formats (xml, geotiff, geojson, shapefile, file, quakeml),
the input of the data will be written to a temporary file with a
unique name. The argument given to the command line program is the file name.

For the shapefile type the name of the .shp file is given and none of the other files.

The file type is the most generic type. It will only be used if no
further information is available. There is no possibility to transform
the data with other generators.

The quakeml is a type that is derived from the basic xml type, because it uses
xml under the hood, but provides a mechanism to transform to other file formats.

All the possible values are listed in ToCommandLineArgumentOption enum
in the ParseJsonForInputImpl class. They all provide to transform the data to
a command line argument.

### Input files

The input files are mostly the same as the complex formats for the
command line arguments, just that the path of the file is always the same.

The supported types are:

- geotiff
- geojson
- shapefile
- file
- quakeml
- shakemap
- json
- nrml

All this options are listed in the ToFileInputOption enum
in the ParseJsonForInputImpl.

### Stdin

For stdin input at the moment just the string and json types are supported.
The text will be piped into the stdin stream of the program.

## Output

The following output mechisms are possible for command line programs:

1. Exit value
2. Text via stdout
3. Text via stderr
4. Output files

### Exit value

At the moment the exit value can just be read as in int (which gives
back exactly the value of the exit value from the command line program).

However it is much more common to use the exit value of a program
for error handling.

### Stdout

The supported types for the working with the stdout stream are:

- string
- xml
- quakeml
- shakemap
- nrml
- json

### Stderr

The supported types for the stderr stream are:

- string
- json

### Output files

The most common output mechanism are files.

The following are supported:

- xml
- file
- geojson
- geotiff
- shapefile
- quakeml
- shakemap
- json
- nrml

## Information about specific data formats

### xml

Xml is one of the most common data formats to handle complex in- and
output. To make sure that the given xml text is meaningful for the
command line program we provide the possibility to provide a
location for a schema, so that we can use it to validate the data
according to an xsd file for example.

### quakeml

Internally quakeml is just xml. However via parsers and generators
we provide additional ways to transform this data to OGC compliant
data types.

We also include a xsd file for faster validation of this data.

### shakemap

Same as quakeml shakemap is also pure xml.
You can find the schema in the src/main/resources/org/n52/gfz/riesgos/validators/xml folder.

### nrml

The nrml format is - similar to quakeml and shakemap - just an xml format.
At the moment we don't include a schema for this datatype, so there is
no validation of the xml content.

### boolean

Boolean parameters need an additional flag attribute that will be set
if the value is true.

### bbox

bbox parameter needs an additional attribute with a list of supported
coordinate reference systems.

```javascript
[...]
"input": [
        { "title" : "input-boundingbox", "useAs": "commandLineArgument", "type": "bbox",   "crs": ["EPSG:4326", "EPSG:4328"]},
[...]
```

### json

The json data type was added in order to go through the process
of writing the documentation of how to add an own format.

## default formats

For the complex data (so everything that is not a bbox, literal string,
double, int or boolean) you can also give a "defaultFormat" attribute.

The idea here is to have a work around to use the processes in
QGIS, which currently not support the choice of output formats. As it
will always use the default format, this field gives the possibility
to specify this.

The following values are supported:

- geojson
- gml
- xml (without a schema)
- quakeml (which is the validated one)
- nonValidQuakeml (which is the original one, that is not valid according to the schema)
- shakemap
- nrml
- json
- geotiff (which uses default encoding)
- geotiff64 (which uses base 64 encoding)
