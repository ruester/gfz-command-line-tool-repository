# Supported in- and output formats

Most of the supported in- and output formats are created to support the
included processes.

If you realize that there is format missing that you need take a look
at the [how-to guide on adding own formats](HowToAddOwnFormats.md).

Please note, that there are different formats supported for different
kinds of input and output mechanisms.

So it makes a lot of sense to give a boolean is a command line argument
(so that if true, there is flag else there is none), but it does not
make any sense to give a boolean as in input file.

The following input mechanisms are possible for command line programs:

1. Command line arguments
2. Input files
3. Text via stdin

The following output mechisms are possible for command line programs:

1. Exit value
2. Text via stdout
3. Text via stderr
4. Output files

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

Please note that for the literal values (int, double, string) it is possible
to provide a default value and a list of allowed values.

For boolean a default value is possible to, but a list of allowed values would
make no sense for a datatype that can have only two different states.
As mentioned above for booleans there will be a set a flag if the value is true.
For example you want to have a verbose flag the flag will be set as -v if
the boolean value is true. If it is false the flag will not be set.

The bbox needs a list of supported coordinate reference systems.
The corners of the bounding box will be given as a list of command line
arguments.
The first is the minimum longitude, than the maximum longitude, than
the the minimum latitude and last the maximum latitude.
This is according to the order that the quakeledger uses for its
bounding box.

For the other complex data formats (xml, geotiff, geojson, shapefile, file, quakeml), 
the input of the data will be written
to a temporary file with a unique name. The argument given to the 
command line program is the file name.
 
For the shakefile type the name of the .shp file is given and none of the other files.

The file type is the most generic type. It will only be used if no
further information is available. There is no possibility to transform
the data with other generators.

The quakeml is a type that is derived from the basic xml type, because it uses
xml under the hood, but provides a mechanism to transform to other file formats.

All the possible values are listed in ToCommandLineArgumentOption enum
in the ParseJsonForInputImpl class. They all provide to transform the data to 
a command line argument.

## Input files

The input files are mostly the same as the complex formats for the
command line arguments, just that the path of the file is always the same.

The supported types are:
- geotiff
- geojson
- shapefile
- file
- quakeml
- shakemap

All this options are listed in the ToFileInputOption enum in the ParseJsonForInputImpl.

## Stdin

For stdin input at the moment just the string type is supported.
The text will be piped into the stdin stream of the program.