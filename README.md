# gfz-riesgos-wps-repository

## Description

This is the java source code for the wps-repository for the riesgos project.

It aims to be an framework for easy integration of command line programs
as web processing services and provide a bunch of services within the scope
of the [RIESGOS project](http://www.riesgos.de/en/). This focus
mainly on those processes provided by the [GFZ](https://www.gfz-potsdam.de/en/home/).

## How it works

The processes that are integrated here are command line programs.
Most processes integrated so far use python3 but any executable command line
program can be integrated.

Each process must be wrapped in a docker image to provide fully independent
execution of the processes (also in case of some hard coded temporary files)
and to manage the dependencies of the programs.

For each processes a json configuration file must be provided, so that
the basic process skeleton - which is the same for all processes - 
knows how to provide the input data, how to
start the process and how to read the output of the programs. It is
also used to specify the way of error handling in the process skeleton.

## Requirements
All of the code here runs on top of the WPS Server provided by
[52° North](https://github.com/52North/WPS).

For all other details please refer to the [installation guide](doc/Installationguide.md).


## Currently implemented processes

Please refer to the following [sub page](doc/IncludedProcesses.md)
for an overview of the
processes that are already on board.

Additionally to the main processes there are also some [format conversion
processes](doc/FormatConversionProcesses.md) in the repository.

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

## Known problems with docker

Using docker means that there is an overhead in the whole execution process.
A container for each run must be created before and removed after the execution.
For all input and output files there must be communication with the underlying
docker file system. So also temporary files that are already on the server must be
copied to the container.

The overhead may course longer run times - and may also course timeouts on 
client side.

Another problem is that the image id differ when created on different machines.
The reason for that is that in most docker build processes there is a kind
of update for a linux package manager. Executed on different machines and in a 
different time forces differences in the files of the system. That means that
the check sums of the layers of the docker build process will differ.


Also the whole process of the installation is much more complicated.

However the use of docker gives some advantages:

* All the process executions are separated. There is no way that they may influence
each other.
* All temporary files created by the command line executable are removed 
after the execution by removing the docker container.
* Docker separates the dependencies of the command line programs. Each process has
its own Dockerfile, which specifies the dependencies for this single process.
So it is possible to run several services that uses conflicting libraries.
* It is possible to test the execution of a program inside of docker
but outside of the WPS server. Because of docker, running the image will
make sure that the execution will work - even on a different server.

At the moment there is no final decision on running the services in docker or not.

## How to add a service

If you want to know how to add your own service, we provide a
step-by-step guide to add a service [here](doc/HowToAddOwnProcess.md).

## Notes about quakeml

This services now support a custom type quakeml.
It is provided as xml that is confirm to the schema http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd.
The early version of the quakeledger process uses a different version of the xml.
We try to support it as input and output, but the underlying process now uses the
validated xml only.

There is also the possibility to convert the quakeml to geojson. 
