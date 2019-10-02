# Examples of using the service and algorithms

- [Requirements](#requirements)
- [Command line](#command-line)
  - [cURL](#curl)
  - [wget](#wget)
  - [Async execution](#async-execution)
- [Python](#python)
  - [OWSLib](#owslib)
  - [Other libraries](#other-libraries)
- [R](#r)

## Requirements

At first you need to know which process of the WPS you want to query. Therefore you could read the `GetCapabilities` document of the WPS server by querying the service with the `GetCapabilties` parameter:

```bash
https://URLtoWPS?service=WPS&request=GetCapabilities
```

The section `wps:Contents` contains all available processes which are provided by the WPS.
The identifier of the process is within the `ows:Identifier` tag, for example:

```xml
<wps:ProcessSummary processVersion="1.0.0" jobControlOptions="sync-execute async-execute" outputTransmission="value reference">
  <ows:Title>ShakygroundProcess</ows:Title>
  <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess</ows:Identifier>
  <ows:Metadata xlin:role="Process description" xlin:href="https://localhost:8080/wps/WebProcessingService?service=WPS&request=DescribeProcess&version=2.0.0&identifier=org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess"/>
</wps:ProcessSummary>
```

This identifier is needed to choose which process should be executed.
To get information (like input and output data types) about the process you can query the WPS server with the `DescribeProcess` parameter:

```bash
https://URLtoWPS?request=DescribeProcess&service=WPS&version=2.0.0&identifier=org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess
```

Which gives something like:

```xml
<wps:ProcessOfferings xsi:schemaLocation="http://www.opengis.net/wps/2.0 http://schemas.opengis.net/wps/2.0/wps.xsd">
  <wps:ProcessOffering processVersion="1.0.0" jobControlOptions="sync-execute async-execute" outputTransmission="value reference">
    <wps:Process>
      <ows:Title>ShakygroundProcess</ows:Title>
      <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess</ows:Identifier>
      <wps:Input minOccurs="1" maxOccurs="1">
        <ows:Title>quakeMLFile</ows:Title>
        <ows:Identifier>quakeMLFile</ows:Identifier>
        <wps:ComplexData>
          <ns:Format default="true" mimeType="text/xml"/>
          <ns:Format mimeType="text/xml"/>
          <ns:Format mimeType="text/xml; subtype=gml/2.1.2" schema="http://schemas.opengis.net/gml/2.1.2/feature.xsd"/>
        </wps:ComplexData>
      </wps:Input>
      <wps:Output>
        <ows:Title>shakeMapFile</ows:Title>
        <ows:Identifier>shakeMapFile</ows:Identifier>
        <wps:ComplexData>
          <ns:Format default="true" mimeType="text/xml" encoding="UTF-8" schema="http://earthquake.usgs.gov/eqcenter/shakemap"/>
          <ns:Format mimeType="application/vnd.geo+json" encoding="UTF-8"/>
          <ns:Format mimeType="text/xml" encoding="UTF-8" schema="http://schemas.opengis.net/gml/3.2.1/base/feature.xsd"/>
          <ns:Format mimeType="image/geotiff" encoding="UTF-8"/>
          <ns:Format mimeType="image/geotiff" encoding="base64"/>
        </wps:ComplexData>
      </wps:Output>
    </wps:Process>
  </wps:ProcessOffering>
</wps:ProcessOfferings>
```

## Command line

For querying a WPS server from the command line you can use `cURL` or `wget`.
It is best to prepare a XML file with the request to the WPS in it, so for example:

```xml
<wps:Execute service="WPS" version="2.0.0" xmlns:wps="http://www.opengis.net/wps/2.0" xmlns:ows="http://www.opengis.net/ows/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.opengis.net/wps/2.0 http://schemas.opengis.net/wps/2.0/wpsExecute.xsd" response="document" mode="sync">
  <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess</ows:Identifier>
  <wps:Input id="quakeMLFile">
    <wps:Data mimeType="text/xml">
      <eventParameters xmlns="http://quakeml.org/xmlns/bed/1.2" publicID="smi:org.globalcmt/origin/12345">
        <event publicID="smi:org.globalcmt/origin/84945">
          ...
        </event>
      </eventParameters>
    </wps:Data>
  </wps:Input>
  <wps:Output id="shakeMapFile" transmission="value" schema="http://earthquake.usgs.gov/eqcenter/shakemap" mimeType="text/xml" encoding="UTF-8">
  </wps:Output>
</wps:Execute>
```

You can then send the XML file with `cURL` or `wget` as described in the following:

### cURL

```bash
curl -X POST -H "Content-Type: text/xml" -d @myxmlfile.xml "https://URL/to/WPS"
```

### wget

```bash
wget "https://URL/to/WPS" --post-file "myxmlfile.xml" --header "Content-Type:text/xml"
```

### Async execution

For long execution times it is better to use the `async` execution. Therefore you set the parameter `mode` within `wps:Execute` to `"async"`:

```xml
<wps:Execute service="WPS" version="2.0.0" ... mode="async">
...
```

Now if you post the XML file to the server you get a different response:

```xml
<wps:StatusInfo xmlns:wps="http://www.opengis.net/wps/2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.opengis.net/wps/2.0 http://schemas.opengis.net/wps/2.0/wps.xsd">
  <wps:JobID>528cc842-c4ef-40ef-a84f-0642740229b6</wps:JobID>
  <wps:Status>Accepted</wps:Status>
</wps:StatusInfo>
```

With this job ID you can get the results later after execution has finished.
You can query the status of the job with:

```bash
https://URLtoWPS?service=WPS&version=2.0.0&request=GetStatus&jobId=YourJobID
```

Which returns the following if the job has finished:

```xml
<wps:StatusInfo xsi:schemaLocation="http://www.opengis.net/wps/2.0 http://schemas.opengis.net/wps/2.0/wps.xsd" xmlns:wps="http://www.opengis.net/wps/2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <wps:JobID>528cc842-c4ef-40ef-a84f-0642740229b6</wps:JobID>
  <wps:Status>Succeeded</wps:Status>
</wps:StatusInfo>
```

You can then get the results of the job with:

```bash
https://URLtoWPS?service=WPS&version=2.0.0&request=GetResult&jobId=YourJobId
```

## Python

### OWSLib

The library [OWSLib](https://geopython.github.io/OWSLib/) includes a WPS client you can use to communicate with a WPS server.
Here is an example of how to start a process and get the results:

```python
from owslib.wps import WebProcessingService
from owslib.wps import printInputOutput
from owslib.wps import monitorExecution
from owslib.wps import BoundingBoxDataInput

processid = 'org.n52.gfz.riesgos.algorithm.impl.QuakeledgerProcess'

wps = WebProcessingService('https://URLtoWPS', verbose=True)

print('Available processes:')
for process in wps.processes:
    print(process.identifier, process.title)

process = wps.describeprocess(processid)

print('Available inputs:')
for input in process.dataInputs:
    printInputOutput(input)

print('Available outputs:')
for output in process.processOutputs:
    printInputOutput(output)

bbox = BoundingBoxDataInput([-34.43409789359468, -72.0703125, -32.556073644920275, -70.02685546875], 'EPSG:4326')

inputs = [("input-boundingbox", bbox),
          ("mmin", "6.6"),
          ("mmax", "8.5"),
          ("zmin", "5"),
          ("zmax", "140"),
          ("p", "0.1"),
          ("etype", "deaggregation"),
          ("tlon", "-71.5730623712764"),
          ("tlat", "-33.1299174879672")]
output = [("selectedRows", False, "text/xml")]

execution = wps.execute(processid, inputs, output=output)

monitorExecution(execution, download=True)
```

The above code snippet needs an OWSLib version of at least 0.17.1 which you can install with `pip3`:

```bash
pip3 install OWSLib==0.17.1
```

### Other libraries

You could also use libraries which handle URLs and can send POST requests, for example:

- [urllib](https://docs.python.org/2/library/urllib2.html)
- [requests](http://python-requests.org)

## R

Currently there are no official libraries which support using WPS services with R. It seems like [ows4R](https://www.r-pkg.org/pkg/ows4R) will implement WPS client support in the future.
For the moment you can use request libraries like [RCurl](https://www.r-pkg.org/pkg/RCurl) to build your own requests and send them via HTTP POST to the server.
