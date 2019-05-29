# How to add an own process

This is step-by-step guide on how to add a service with custom code.

## Write a command line program

In this case we will write a simple python script:

```python
#!/usr/bin/env python3

'''
This script takes a feature collection
and filters them according to a size.

A more careful designed example would care about
projections and units.
'''

import sys
import geopandas as gpd

def main():
    if len(sys.argv) < 2:
        print("Usage ./filter_big.py size", file=sys.stderr)
        sys.exit(-1)
    min_size = float(sys.argv[1])
    input_file = 'input_file.geojson'
    output_file = 'output_file.geojson'
    df = gpd.read_file(input_file)
    
    df_big = df[df.geometry.area > min_size]
    df_big.to_file(output_file, 'GeoJSON')

if __name__ == '__main__':
    main()
```

We save this file as filter_big.py.
The script takes a geojson file as input_file.geojson and a floating point
number to filter for the area as a command line argument.
The output is saved in a geojson file called output_file.geojson.
All the paths are relative to the execution of the script.

## Write a Dockerfile

To insert this script we have to write a Dockerfile.
We will start with an Ubuntu 18.04 image and add the dependencies.
Some of the dependencies are added using apt, some using pip.
We have to care about the following:

- python3
- geopandas

The last step is to add the script to the docker image.
In most cases we checkout a repository (see the Dockerfiles for
quakeledger or shakyground) but here it is enough to just copy the
script into the image.

```
FROM ubuntu:18.04

# for not having interaction on installation process
ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update && apt-get install python3.7 python3-pip -y

RUN pip3 install geopandas

RUN mkdir -p /usr/share/git/filter_big/
COPY filter_big.py /usr/share/git/filter_big/filter_big.py
```

## Build the docker image

Just run the the docker build command and provide a tag for it.

```
docker build . --tag filter_big
```

## Write the json configuration

We have to provide a description of how where we can find and execute
the process and how to handle input and output data.

```javascript
{
  "title": "FilterBigProcess",
  "imageId": "filter_big:latest",
  "workingDirectory": "/usr/share/git/filter_big",
  "commandToExecute": "python3 filter_big.py",
  "exitValueHandler": "errorIfNotZero",
  "stderrHandler": "errorIfNotEmpty",
  "input": [
    { "title" : "input-file", "useAs": "file", "path": "input_file.geojson", "type": "geojson"},
    { "title": "min-area", "useAs": "commandLineArgument", "type": "double" }
  ],
  "output": [
    { "title": "output-file", "readFrom": "file", "path": "output_file.geojson", "type": "geojson"}
  ]
}
```
We save this file as filter_big.json

Lets to through the elements:

- title is the title under which you can access to the process.
  Make sure that you insert a new title which every new process.
- iamgeId is the tag or the image id of the docker image that contains
  the code. Here we use the filter_big image we created in the last step.
- workingDirectory gives the name of the directory that we use to run
  the program. In this case we copied the file to the /usr/share/git/filter_big
  folder inside of the image. All your paths in the script are relative
  so this is the directory to use for the process.
- exitValueHandler gives us the handler for the exit value.
  Because we may give back a non zero exit value, we want to make sure
  that the wps server realized that there was an error.
- same for the stderrHandler. We are sure, that we don't have any
  warning that we want to ignore. So any text on stderr is clearly a
  sign of an error in our script, that should be propagated to the server.
- for the input fields we have
  * an input-file that have a geojson format and is always on the path
    input_file.geojson
  * a command line argument that is a double
- for the output we just have one geojson file that is always written
  to output_file.geojson. Please notice that the reading process
  of the output data is done after the script ran, so after the script is
  done we say *read* this from a file and provide it as a output of the
  process.
 
It is not necessary to provide all inputs and outputs of the script
script as arguments here.
If we are not interested in one output the script produces we can
simply ignore it.

For input files that must always the same it is the best approach to make
sure they are copied also into the docker image next to the script.

## Copy the json file to the configuration folder

To add the service to the server you just simply have to copy
the config json file to the folder that the server uses to
search and parse process configurations.
You can see the path in the wps server adminstration interface (using
the wps server in the docker container and running the server locally
it is
http://localhost:8080/wps.
You find the folder name under Repositories (in the navigation bar
on the left) and under GFZ RIESGOS Configuration Module.
The default value there is /usr/share/riesgos/json-configurations.
If you use docker and followed the installation guide this folder is
mounted as a volume in the folder json-configs folder next to the
docker-compose.yml for the wps server.

## Test the process

Now the service is running on the server and you can query it using
python, R, QGIS or others.

We can run a little test script that runs the xml level:

```python

#!/usr/bin/env python3

'''
This script send three polygons in geojson format
to the server and starts the defined process.
It tries to reveice the result also in geojson.
This script does not use the owslib package in order
to be able to specify the result data format.
'''

import requests

URL = 'http://localhost:8080/wps/WebProcessingService'

request_data = """
<wps:Execute service="WPS" version="2.0.0" 
  xmlns:wps="http://www.opengis.net/wps/2.0"
  xmlns:ows="http://www.opengis.net/ows/2.0"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/wps/2.0 http://schemas.opengis.net/wps/2.0/wpsExecute.xsd"
  response="document" 
  mode="sync">
  <ows:Identifier>FilterBigProcess</ows:Identifier>
  <wps:Input id="input-file">
      <wps:Data mimeType="application/vnd.geo+json">
      {
          "type": "FeatureCollection",
          "name": "dummy",
          "crs": { "type": "name", "properties": { "name": "urn:ogc:def:crs:EPSG::4326" } },
          "features": [
            { "type": "Feature", "properties": { "id": 1 }, "geometry": { "type": "Polygon", "coordinates": [ [ [ -74.176616915422969, -14.330845771144094 ], [ -72.743781094527435, -15.186567164178921 ], [ -72.743781094527435, -15.186567164178921 ], [ -73.818407960199082, -16.420398009950066 ], [ -74.992537313432919, -15.604477611940116 ], [ -74.176616915422969, -14.330845771144094 ] ] ] } },
            { "type": "Feature", "properties": { "id": 2 }, "geometry": { "type": "Polygon", "coordinates": [ [ [ -72.664179104477682, -16.340796019900313 ], [ -70.952736318408043, -17.116915422885388 ], [ -70.952736318408043, -17.116915422885388 ], [ -71.310945273631916, -18.131840796019716 ], [ -73.12189054726376, -17.276119402984889 ], [ -72.664179104477682, -16.340796019900313 ] ] ] } },
            { "type": "Feature", "properties": { "id": 3 }, "geometry": { "type": "Polygon", "coordinates": [ [ [ -69.659203980099576, -13.992537313432653 ], [ -58.514925373134403, -13.435323383084395 ], [ -59.151741293532417, -26.927860696517229 ], [ -71.470149253731421, -26.052238805969964 ], [ -71.470149253731421, -26.052238805969964 ], [ -69.659203980099576, -13.992537313432653 ] ] ] } }
          ]
      }
      </wps:Data>
  </wps:Input>
  <wps:Input id="min-area">
      <wps:Data>
          <wps:LiteralValue>0.003</wps:LiteralValue>
      </wps:Data>
  </wps:Input>
  <wps:Output 
      id="output-file"
      transmission="value"
      mimeType="application/vnd.geo+json">
  </wps:Output>
</wps:Execute>
"""

headers = {
    'Content-Type': 'application/xml',
}

res = requests.post(URL, data=request_data, headers=headers)
print("Status-Code = {0}".format(res.status_code))
print("Text = {0}".format(res.text))

```

After you received the text you can parse the complex payload.