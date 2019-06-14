# How to add an own process

This is step-by-step guide on how to add a service with custom code.

## Write a command line program

In this case we will write a python script.
It processes a feature collection and gives only those features
back that have an area equal or larger than a given size (square kilometers).

```python
#!/usr/bin/env python3

'''
This script takes a feature collection
and filters them according to a size.

The input collection is given on the path input_file.geojson.

The minimum size is given as a command line argument.
The unit used here is km².

The remaining features are written to output_file.geojson.
'''

import os
import sys
import geopandas as gpd

def get_with_min_size(feature_collection, min_size):
    '''
    Return the elements of the feature collection with
    have an area equal or greater the the given
    minimum size.

    Parameters:
    - `feature_collection`: Geopandas Dataframe with the elements
    - `min_size`: Mimum size for the filtering in square km
    '''
    return feature_collection[get_area_skm(feature_collection) >= min_size]

def get_area_skm(feature_collection):
    '''
    Return the area of the features in square km as a series.

    To calculate the area of the features in square km it is necessary
    to transform the data to an equal area projection.

    According to
    https://gis.stackexchange.com/questions/218450/getting-polygon-areas-using-geopandas
    we choose an equal area cylindrical projection.


    Parameters:
    - `feature_collection`: Geopandas Dataframe with the elements
    '''
    in_eq = feature_collection.to_crs({'proj': 'cea'})
    area_in_square_m = in_eq.geometry.area
    return square_m_to_square_km(area_in_square_m)

def square_m_to_square_km(area_square_m):
    '''
    Return the area in square km.

    Parameters:
    - `area_square_m`: Area in square meters
    '''
    return area_square_m / 10**6

def write_geojson(feature_collection, filename):
    '''
    Write the feature collection to a file as GeoJSON.
    Because GeoJSON officially supports only WGS 84 data,
    we make sure here that we transform the data into this
    projection (Geopandas and GDAL are able to read and write
    other projections too, but other tools may fail on reading).

    Parameters:
    - `feature_collection`: Geopandas Dataframe with the elements to write
    - `filename`: destination filename
    '''
    feature_collection_wgs84 = feature_collection.to_crs({'init': 'epsg:4326'})
    # geotools has a problem to parse geojson files with an crs
    # so we set the crs to None to get around this problem
    # (geojson default is wgs84)
    feature_collection_wgs84.crs = None

    # delete the file if it exists already
    # (there is no overwriting in the to_file method)
    if os.path.exists(filename):
        os.unlink(filename)

    feature_collection_wgs84.to_file(filename, 'GeoJSON')

def read_input_filter_and_write_output(input_file, min_size, output_file):
    '''
    Read the data, filter according to the size and write the
    result to an output file.

    Parameters:
    - `input_file`: Filename of the input file
    - `min_size`: minimum size of the features to be written to the output file
    - `output_file`: Filename of the output file
    '''
    feature_collection = gpd.read_file(input_file)
    big_features = get_with_min_size(feature_collection, min_size)
    write_geojson(big_features, output_file)


def main():
    '''
    Reads the minimum size from the command line arguments
    and runs the process of reading, filtering and writing
    output.
    '''
    if len(sys.argv) < 2:
        print("Usage ./filter_big.py size", file=sys.stderr)
        sys.exit(-1)
    min_size = float(sys.argv[1])
    input_file = 'input_file.geojson'
    output_file = 'output_file.geojson'

    read_input_filter_and_write_output(input_file, min_size, output_file)

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
In most cases we checkout a git repository (see the Dockerfiles for
quakeledger or shakyground - in this 
dockerfiles we also have to install git) but here it is enough to 
just copy the script into the image.

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

It is important to build the docker image on the very same computer
that will run the code later.
If the server runs inside of docker it is no problem to build the
image on the host system, because the docker socket will be shared
between the WPS server and the host system.

## Write the json configuration

We have to provide a description of how the skeleton-process 
can find and execute the process and how to 
handle input and output data.

```javascript
{
  "title": "FilterBigProcess",
  "abstract": "Process to filter polygons that have an area equal or bigger than the given value.",
  "imageId": "filter_big:latest",
  "workingDirectory": "/usr/share/git/filter_big",
  "commandToExecute": "python3 filter_big.py",
  "exitValueHandler": "errorIfNotZero",
  "stderrHandler": "errorIfNotEmpty",
  "input": [
    { 
        "title": "input-file",
        "abstract": "geojson feature collection to filter",
        "useAs": "file",
        "path": "input_file.geojson",
        "type": "geojson"
    },
    { 
        "title": "min-area",
        "abstract": "double value to filter give back all elements that are equal or greater than this area (in square km)",
        "useAs": "commandLineArgument",
        "type": "double" 
    }
  ],
  "output": [
    { 
        "title": "output-file",
        "abstract": "remaining features in wgs84 geojson",
        "readFrom": "file", 
        "path": "output_file.geojson", 
        "type": "geojson"
    }
  ]
}
```

We save this file as filter_big.json

Lets to through the elements:

- title is the title under which you can access to the process.
  Make sure that you insert a new title which every new process.
  
- abstract is a description that will be shown on the description of
  the wps service. While this field is optional, please make sure that
  you provide a meaningful description for the users of your service.
  
- imageId is the tag or the image id of the docker image that contains
  the code. Here we use the filter_big image we created in the last step.
  It is possible to refer to the exact image id as well as to the
  tag that we used creating the docker image.
  While the docker image id gives you more certainty which exact version
  of the image is used (because they are unique), it is necessary to
  change image id if the docker image must be rebuild.
  Another important aspect is that the docker image id is not
  stable building the image on different machines. That is why we
  rely on the tag here.
  
- workingDirectory gives the name of the directory that we use to run
  the program. In this case we copied the file to the /usr/share/git/filter_big
  folder inside of the image. All your paths in the script are relative to
  the script location so this is the directory to use for the process.
  
- exitValueHandler gives us the handler for the exit value.
  Because we may give back a non zero exit value, we want to make sure
  that the wps server realized that there was an error.
  
- same for the stderrHandler. We are sure, that we don't have any
  warning that we want to ignore (warnings on python are normally given back
  on stderr). So any text on stderr is clearly a
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

For input files that must always be the same it is the best 
approach to make sure they are copied also into the docker 
image next to the script.

For an overview of the supported input and output formats
you can look [here](SupportedFormats.md).

If you realize that you need to add your own format you can take
a look [here](HowToAddOwnFormat.md).


## Copy the json file to the configuration folder

To add the service to the server you just simply have to copy
the config json file to the folder that the server uses to
search and parse process configurations.

The file must provide a .json ending, because at the moment
only those configuration files are parsed so that the processes
can be included.

You can see the path for adding the configuration files
in the wps server adminstration interface (using
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
For examples on how to use wps services with R, python or QGIS please
refer to the [ExampleUsage-Page](ExampleUsage.md).

Anyway, we will provide a test script here, that will query
the new process and parse it to a geopandas dataframe.
Please make sure that the server runs.

```python
#!/usr/bin/env python3

'''
This script send three polygons in geojson format
to the server and starts the defined process.
It tries to reveice the result also in geojson.
This script does not use the owslib package in order
to be able to specify the result data format.
'''

import io
import requests
from lxml import etree
import fiona
import geopandas as gpd

# here the server runs on localhost
# you may have to change this url
URL = 'http://localhost:8080/wps/WebProcessingService'

# in the following request data
# we start a request on the server
#
# with a feature collection in json format
# with three elements
# we also specify the minimum size with an
# area of 1000000 square km
#
# the last part is that we specify the output format
# that we want to process
#
REQUEST_DATA = '''
<wps:Execute service="WPS" version="2.0.0" 
  xmlns:wps="http://www.opengis.net/wps/2.0"
  xmlns:ows="http://www.opengis.net/ows/2.0"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/wps/2.0
      http://schemas.opengis.net/wps/2.0/wpsExecute.xsd"
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
            { "type": "Feature", "properties": { "id": 1 }, 
                "geometry": { "type": "Polygon", "coordinates": [ [ 
                [ -74.176616915422969, -14.330845771144094 ], 
                [ -72.743781094527435, -15.186567164178921 ], 
                [ -72.743781094527435, -15.186567164178921 ], 
                [ -73.818407960199082, -16.420398009950066 ], 
                [ -74.992537313432919, -15.604477611940116 ], 
                [ -74.176616915422969, -14.330845771144094 ] ] ] } },
            { "type": "Feature", "properties": { "id": 2 }, 
                "geometry": { "type": "Polygon", "coordinates": [ [ 
                [ -72.664179104477682, -16.340796019900313 ], 
                [ -70.952736318408043, -17.116915422885388 ], 
                [ -70.952736318408043, -17.116915422885388 ], 
                [ -71.310945273631916, -18.131840796019716 ], 
                [ -73.12189054726376, -17.276119402984889 ], 
                [ -72.664179104477682, -16.340796019900313 ] ] ] } },
            { "type": "Feature", "properties": { "id": 3 }, 
                "geometry": { "type": "Polygon", "coordinates": [ [ 
                [ -69.659203980099576, -13.992537313432653 ], 
                [ -58.514925373134403, -13.435323383084395 ], 
                [ -59.151741293532417, -26.927860696517229 ], 
                [ -71.470149253731421, -26.052238805969964 ], 
                [ -71.470149253731421, -26.052238805969964 ], 
                [ -69.659203980099576, -13.992537313432653 ] ] ] } }
          ]
      }
      </wps:Data>
  </wps:Input>
  <wps:Input id="min-area">
      <wps:Data>
          <wps:LiteralValue>1000000</wps:LiteralValue>
      </wps:Data>
  </wps:Input>
  <wps:Output 
      id="output-file"
      transmission="value"
      mimeType="application/vnd.geo+json">
  </wps:Output>
</wps:Execute>
'''

HEADERS = {
    'Content-Type': 'application/xml',
}

NS = {
    'wps': 'http://www.opengis.net/wps/2.0',
}

def extract_json_str_result(response_text):
    '''
    Extract the data in json format from the
    xml text.

    Parameter:
    - `response_text`: Text with the response from the WPS server
    '''
    parsed = etree.parse(io.BytesIO(response_text.encode()))
    xml_root = parsed.getroot()
    return xml_root.xpath('//wps:Output/wps:Data', namespaces=NS)[0].text

def parse_json_str_to_feature_collection(json_str):
    '''
    Parses the json text to a geopandas dataframe

    Parameters:
    - `json_str`: Geojson text
    '''

    json_bytes = json_str.encode()

    with fiona.BytesCollection(json_bytes) as collection:
        crs = collection.crs
        data_frame = gpd.GeoDataFrame.from_features(collection, crs=crs)
        return data_frame


def main():
    '''
    Runs the request on the server
    and extracts the data as feature collection
    '''
    response = requests.post(URL, data=REQUEST_DATA, headers=HEADERS)
    if response.ok:
        json_str = extract_json_str_result(response.text)
        feature_collection = parse_json_str_to_feature_collection(json_str)
        print(feature_collection.head())
    else:
        print("Status-Code = {0}".format(response.status_code))
        print("Text = {0}".format(response.text))

if __name__ == '__main__':
    main()
```

You can execute this script with python3. It does not need any
arguments.

You may also check the output of the WPS-Server logs. In case that there
is an error on the script, the configuration file or a missing
dependency in the docker image, reading the logs will be the fastest
way to identifier an error.

## Optional: Change your process

If you want to change your process you may have to change different files
and have to do some of the steps mentioned above.

We will care about some scenarios here.

### Change of the dependencies

Maybe the first change that is necessary on a new process is to
add dependencies to the dockerfile because of an error while running
the process on the server.

The command line script does not need any update, so we don't have to care
about this.

The dockerfile is the place where we must must change the content and
in most cases just have to install some packages.

In case you already build a dockerfile it may be the best to just
add the additional installation steps on the bottom of the dockerfile, because
docker needs a caching mechanism to reuse existing images and installation
steps.

Adding the instructions on the bottom will give docker the possibility to
reuse your existing image. But don't be afraid if you change the content
somewhere else. Docker will run the build command anyway. It just may take
more time and consume more space on the file system.

After building the new docker image, you have to check your json configuration file.
If you refer to the old image id, than you have to update this field to
use the new image id. You also have to copy the json configuration to the
configuration folder (you can just overwrite the file).

In case that you use a docker tag (say: "filter_big:latest") 
in the json configuration and you
set this tag on the docker build command (--tag "filter_big"), 
the configuration already refers, than docker will already refer to
your new image. No change on the configuration file is necessary
to the tag. The process will refer to exactly this tag, so no further
editing of the json configuration is necessary.

### Change of the json configuration

If all you have to change is the configuration - say a change of the
paths or formats of the input or output data - just edit the file and
copy it to the configurations folder.
Changes on the source code or the docker file are not necessary.

### Change of the source code

Any time you change the source code, you also have to rebuild the image.
You may not have to update the dockerfile but the recreation of the
image is necessary because the source code is included in the docker image.

After that you have to check if you have to change the json configuration
because of different formats, paths or other input and output parameters.

## Existing processes as templates

In the basic skeleton some processes are already integrated.
All this processes have source code, a dockerfile and a configuration
that you can take as templates for your own processes.

Please refer to the overview of the already included processes 
[here](IncludedProcesses.md).

At the moment you can replace the existing processes by giving
json configurations that take the same process title as the existing ones.
The idea is to give you the possibility to improve the processes
without the necessity of changing the java code and recompiling and
deploying the jar file.

## Optional: Add the configuration to the core of the repository

If you want to include the configuration to the core of the respository
similar to quakeledger and shakyground, than you must follow some of steps:

1. Make sure your repository with the code of your process can be accessed

   It makes it easier to improve processes (and create docker images for
   other people than yourself) if one can access the source code of
   your process.
   
2. Add your dockerfile to the assistance folder in the repository

3. Add your json configuration to the  src/main/resources/org/n52/gfz/riesgos folder

4. Write a factory method in the ConfigurationFactory class

5. Call this factory method in the createPredefinedConfigurations method of the 
   GfzRiesgosRepositoryCM class and insert the configuration in the list

6. Recreate the package and deploy it in the same way as it is mentioned
   in the [installation guide](Installationguide.md).