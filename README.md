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

For more information about dockerfiles you can take a look at
the [official docker documentation](https://docs.docker.com/engine/reference/builder/).
The role of docker for the overall framework here is explained on [its
own documentation page](doc/RoleOfDocker.md).

The json configuration is explained in more detail
[here](doc/JsonConfigurationExplaned.md).

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


## How to add a service

If you want to know how to add your own service, we provide a
step-by-step guide to add a service [here](doc/HowToAddOwnProcess.md).