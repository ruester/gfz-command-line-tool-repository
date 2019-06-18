# JSON configuration files

The json configuration files are one core component of the integration
of command line programs as web processing services.

It is used to:
- provide a process description with the name, input and output paramters
  of the service
- it provides data on how to run the command line program
- it defines the strategy on how to propagate errors from the command line
  program to the server
- it provides information about how to give the input to the process and
  how to read the output from the command line program

## Example configuration

The following is the configuration of the quakeledger process:

```javascript
{
    "title": "QuakeledgerProcess",
    "abstract": "The quakeledger process fills the role of a filterable earth quake catalogue.",
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

## title

This is the title that is used to identify the process.
In the example it is called QuakeledgerProcess.
The name itself is more or less arbitrarily, but it should indicate which
program will be executed. Here we followed Java naming conventions
with upper camel case.

## abstract

The abstract is a description of the service. This field is optional,
but as a wps service is a black box for the user it is important to
provide some information on what the process will do, how it works
and what assumptions are made.

## imageId

This field gives the image id of the docker image that should be used to execute
the command line program.

It can contain a tag like in the example or the specific image id.
This can use either the short version (7e95161d44d6) or the full image id
(sha256:7e95161d44d6d61b44325243d220a9a06b75579c938faf2066c579eae102e20a).

Because of the non-transferability of image ids which are build
on different computers ([see the role of docker](RoleOfDocker.md)),
we recommend using image tags.

## workingDirectory

This is the folder that should be used to execute the command line
program. It is always a path inside of the docker image.

The command to execute the program and the paths of input and output-files
are relative to this directory.

## commandToExecute

This is the command that will be executed.
In most cases it will be the call of a executable itself, like

```bash
./my-c-program
```

or a call of a scripting language interpreter with the script name

```bash
python3 my-script.py
```

## exitValueHandler

In this field you can give a mechanism to handle the exit value of the
program. In this way it is possible to stop the process execution
after receiving a non zero exit value which often can indicate
an error on the execution of the command line program.

You have the following possibilities:

| value | explanation |
|-------|-------------|
| ignore | The exit value will be ignored. This is the default case if no exitValueHandler is given.|
| logging | The exit value will be logged no matter what the value is. |
| errorIfNotZero | The exit value will be tested if it is zero. If not the basic process skeleton will take it as an error and will stop the processing of the command line program output.|

## stderrHandler

Similar to the exitValueHandler it is possible to specify an handler for
the content of the stderr stream.

You can chose from the following options:

| value | explanation |
|-------|-------------|
| ignore | The text on stderr will be ignored. This is the default case if no stderrHandler is given.|
| logging | The text on stderr will be logged if it is not empty. |
| errorIfNotEmpty | If the text on stderr is not empty, the basic process skeleton will take it as an error and will stop the processing of the other output. |
| pythonTraceback | Same as errorIfNotEmpty, but it filters first if there is some text which indicates an python traceback. Other text (for example warnings) on stderr will be ignored. This is only conceived for processes that run python scripts. |
| rError | Scan the text for a error massage for the R programming language. Warnings will be ignored. |

If you realize that your command line program shows an behaviour that must
that must be supported please refer to 
[our guide to add your own error handler.](HowToAddOwnErrorHandler.md)

## stdoutHandler

At the moment there is no stdoutHandler given in the example json configuration.
However we provide the option to set it explicit to "ignore", which at
the moment the only supported type for this field.

Once it will be necessary to search for errors on stdout output stream, we
will implement some other stdoutHandlers as well.

## input

The input section is one of the most important parts of the json
configuration files.
It specifies how the input data is given to the command line program.

You have to provide an array with at least the following fields:

- title
- useAs
- type

The title specifies an identifier that will also be used in the
process description provided by the wps server.

As the same as with the overall process it is also possible to 
include a field "abstract" and to provide a string with the
description of the input parameter here. Please notice that this field
is optional.

Also there is the option to add a key value pair
```
"optional": true
```

to make the field optional. If no such field is given than the
field will me handled as non optional.

The useAs gives the basic process skeleton the information about
how to transfer the data to the command line program.

The following values are possible:

| useAs | explanation |
|-------|-------------|
| stdin | Take the content of the input and pipe it to the program via the stdin stream. |
| commandLineArgument | Add the content (or the name of a temporary file) to the command to execute. |
| file | The content of the input will be written to the file with a given path, so that the file is there when the program gets executed. | 

For input files there must always be a path attribute as well, so that the
basic process skeleton knows where to create the file.

## output

Same as the iinput section this is also a very important part of the
json configuration file and here must be provided a array with
the at least the following fields as well:

- title
- readFrom
- type

Same as for input parameters it is possible here too to provide
a "abstract" field with a string, so the user get a description
of the output parameter. As for inputs this field is optional here too.

Also it is possible here to mark the output as optional as well (same as
for input values. This will treat the server to try to load the value
after the processing but to not thrown an error in case the file/stream
is not there. 

The term readFrom may be difficult to understand for output parameters,
but it specifies the moment on which the command line program runs
and the basic process skeleton reads the output from the running container.

There are the following readFrom values possible:

| readFrom | explanation |
|----------|-------------|
| exit value | Read from the exit value. |
| stderr | Read from stderr stream. |
| stdout | Read from stdout stream. |
| file | Read the content from a file that was created inside of the container. |

You may wonder about having an option of reading from the exit value or stderr
while there are seperate stderrHandler and exitValueHandlers.
Those handlers are meant to handle situations that indicate errors and
stop the processing of the basic process skeleton, while
the readFrom options are for reading data for the overall process output.

## input and output types

For an overview of supported input and output types please refer to
the [site about supported formats](SupportedFormats.md).

If you have an xml input or output format you can add a schema field.
This field it used to validate the data. If you don't need the
validation - or you know that the data will not be valid according to
the schema but still can be processed by the program - you should
remove the schema field, so that the system does not try to validate it.
Same is true if the xsd does not exist or is not accessible.

Please also note, that you need different titles per input and output
element. The data is identified by this names, so please make sure
there are unique to the process.