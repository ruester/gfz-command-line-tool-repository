# JSON configuration files

## Features

All the processes are using a generic template for working with command line tools.
This includes input via
* stdin
* command line parameter
* input files

For output there are the following options:
* handling of the exit value
* handling of stdout
* handling of stderr
* output files

The processes itself run inside of docker containers.
Example Dockerfiles to create the images necessary for this are included in the
assistance folder.

## Configuration

The configuration of the processes is done in json files.
You can take a look at them in the resources/org/n52/gfz/riesgos/configuration
folder.

The json files provide several information:

| key | Explanation |
|-----|-------------|
| title | This provides the title of the process |
| imageId | ImageID or tag of the docker image to run the script |
| workingDirectory | Directory that is used to run the script inside the docker container |
| commandToExecute | The command to execute in the working directory to run the program in the docker container |
| exitValueHandler | Optional field to add a handler for the exit value |
| stderrHandler | Optional field to add a handler for the stderr output |
| stderrHandler | Optional field to add a handler for the stdout output |
| input | Array of input fields and how they are used |
| output | Array of output fields and where they get the data from |

Example for the quakeledger process:
```javascript
{
    "title": "QuakeledgerProcess",
    "imageId": "quakeledger:latest",
    "workingDirectory": "/usr/share/git/quakeledger",
    "commandToExecute": "python3 eventquery.py",
    "exitValueHandler": "logging",
    "stderrHandler": "pythonTraceback",
    "input": [
        { "title" : "input-boundingbox", "useAs": "commandLineArgument", "type": "bbox",   "crs": ["EPSG:4326", "EPSG:4328"]},
        { "title" : "mmin",              "useAs": "commandLineArgument", "type": "double", "default": "6.6"},
        { "title" : "mmax",              "useAs": "commandLineArgument", "type": "double", "default": "8.5"},
        { "title" : "zmin",              "useAs": "commandLineArgument", "type": "double", "default": "5"},
        { "title" : "zmax",              "useAs": "commandLineArgument", "type": "double", "default": "140"},
        { "title" : "p",                 "useAs": "commandLineArgument", "type": "double", "default": "0.1"},
        { "title" : "etype",             "useAs": "commandLineArgument", "type": "string", "default": "deaggregation", "allowed": ["observed", "deaggregation", "stochastic", "expert"]},
        { "title" : "tlon",              "useAs": "commandLineArgument", "type": "double", "default": "-71.5730623712764"},
        { "title" : "tlat",              "useAs": "commandLineArgument", "type": "double", "default": "-33.1299174879672"}
    ],
    "output": [
        { "title": "selectedRows", "readFrom": "file", "path": "test.xml", "type": "quakeml", "schema": "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd"}
    ]
}
```

## Exit Value Handler

If no exitValueHandler is provided in the json configuration the exit value of the program will be ignored.
There are the following options for the handler:
#### logging

The exit value will be added to the log file. This is for development and debugging processes.

#### errorIfNotZero

If the exit value is not zero than the process will be terminated without producing output, because the internal script
has thrown an error.

## Stderr Handler

Same as for exit value, the stderr output is ignored by default.

However there are some other handlers

#### logging

The stderr output text will be added to the log file. This is for development and debugging processes.

##### errorIfNotEmpty

If the stderr text is not empty than the process will be terminated without producing output, because the internal
script has thrown an error.

#### pythonTraceback

This stderr handler searches for the text of a python traceback. It will not
care about warnings on stderr, but recognizes an error in a python script.

## Stdout Handler

There are no stdout handler at the moment. You can read from stdout by providing an output.

## Supported input

The input field in the configuration must provide an array with the data fields and how they should be given to the
command line program.

Each input must provide a title, and a useAs value.

The following useAs values are possible

#### commandLineArgument

This will be given as a command line argument. The ordering of the elements matters as it is the ordering
the data is given as command line parameter.

The following types are supported via "type" value:
* int
* double
* boolean
* string
* xml
* xmlWithoutHeader
* geotiff
* geojson
* shapefile

For the following types the "flag" attribute is supported:
* int
* double
* string
* boolean (it is mandatory here)
* xml
* xmlWithoutHeader
* geotiff
* geojson
* shapefile

If there is a "flag" attribute then it is inserted before the command line argument.
For example with the flag --etype and the value "expert"
```
[...] --etype "expert"
```

For booleans the flag is mandatory, because no value will be given as command line argument but the flag - but this
only if the value is true.

For the bbox the "crs" attribute must be provided with the list of the supported coordinate reference systems.

For the xml and xmlWithoutHeader types there is an optional "schema" attribute, that is used to validate
the input of the data.

#### stdin

You also can use the stdin as input. At the moment only string as type is supported.

#### file

If you use file as useAs value, than you must provide a "path" attribute.
This path is relative to the working directory.

The following file types are supported:
* geotiff
* geojson
* shapfile

## Supported output

The output field in the configuration must provide an array with the data fields and how they can be read from
the command line program.

Each input must provide a title, and a readFrom value.

The following readFrom values are supported:

#### stdout

You can convert the text from stdout to provide output.
The following values for "type" are supported:

* string
* xml
* quakeml

The xml type can accept an additional "schema" attribute, that is used to validate the output.

#### stderr

Same as stdout it is possible to read from stderr.
Here only the string type is supported.

#### exitValue

You can also read from the exit value.

At the moment only the int type is supported.

#### file

The file value for the "readFrom" attribute needs to have an additional "path" attribute which the file path
of the given output file. This is relative to the working directory.

The following "type" values are supported:
* xml
* geojson
* geotiff
* shapefile
* quakeml

The xml type can also accept an "schema" attribute to validate the output.
