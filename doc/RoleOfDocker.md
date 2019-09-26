# Role of Docker

All the services that are implemented in this framework and use
a command line program stronly rely on docker.

## Advantages of using docker

The main aims in docker usage for this repository:

- Dependency handling

  It is possible to bundle the source code and the dependencies in
  a docker image. Different scripts may have conflicting versions
  of dependencies. With the docker image it is possible to bundle
  all the libraries and package together with the command line
  programs.

- Independent executions

  It is the aim of the framework to provide a mechanism to separate
  the executions of different runs of one service without any inference
  especially for file handling. Without the use of docker there must
  be a lot of care in making sure that no execution overwrites
  a temporary file of another execution. This mostly affects
  compiled programs for which the source code is not available, so that
  there is no chance to change it on source code level.
  With docker it relies on a seperate container for each execution.
  The write-on-change filesystem can be used to reuse all common files
  of the containers and manages the write access specific to each
  container. This changes can be removed together with the container
  after the process terminates and all the output data from the container
  is loaded into the execution of the process skeleton.
  So the whole resource management is done after removing the docker
  container.

- Easier testing

  Having a docker image it is easy to test the process. Once a
  program runs inside of the docker container it will run on other
  computers as well, because all the dependencies are already included,
  all programs are already configured.

## Known problems with docker

- Overhead

  Using docker means that there is an overhead in the whole execution process.
  A container for each run must be created before and removed after the execution.
  For all input and output files there must be communication with the underlying
  docker file system.
  So also temporary files that are already on the server must be
  copied to the container.

  The overhead may course longer run times.

- Non-transferable image ids

  Another problem is that the image id differs when the image is
  created on different machines.
  The reason for that is that in most docker build processes there is a kind
  of update for one or several package managers. Executed on different machines and in a
  different time forces differences in the files of the system. That means that
  the check sums of the layers of the docker build process will be different.

  We try to avoid this problem by using tags for the images, which can point to
  an image regardless of the image id.

  Tags can be replaced with newer versions, which is both good (we can
  rebuild the image - say for a dependency we forgot on creating it first -
  and we not need to update the json configuration file) and bad
  (we can not rely on the image id as an integrity check - which means that
  exactly *this* version of the code will be executed).

- Higher complexity

  It would be way easier to install the server and the processes without
  having to care about docker (we must make sure that the server can run
  docker - both with the executables and access to the docker socket;
  we have to build dockerfiles on our server and we have to make sure
  your json configurations refer to the right images). Also the
  debugging process is much more difficult by using docker.

## Why we use docker here

Even which the non underestimable contra points, we use docker here
to provide a clean separation of the processes and their executions.

Also in most scientific scenarios the runtime for the command line programs
exceeds the overhead of the docker container management by far. Also most
scientific services are not run hundreds of times a second.
