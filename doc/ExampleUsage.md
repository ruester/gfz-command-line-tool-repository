# Examples of using the service and algorithms

- [Requirements](#requirements)
- [Command line](#commandline)
    - [cURL](#curl)
    - [wget](#wget)
- [Python](#python)
- [R](#r)
- [JavaScript](#javascript)

## Requirements <a name="#requirements"></a>

At first you need to know which process of the WPS you want to query. Therefore you could read the `GetCapabilities` document of the WPS server by querying the service with the `GetCapabilties` parameter:

```bash
http://URLtoWPS?service=WPS&request=GetCapabilities
```

The section `wps:Contents` contains all available processes which are provided by the WPS.
The identifier of the process is within the `ows:Identifier` tag, for example:

```xml
<wps:ProcessSummary processVersion="1.0.0" jobControlOptions="sync-execute async-execute" outputTransmission="value reference">
  <ows:Title>ShakygroundProcess</ows:Title>
  <ows:Identifier>ShakygroundProcess</ows:Identifier>
  <ows:Metadata xlin:role="Process description" xlin:href="https://localhost:8080/wps/WebProcessingService?service=WPS&request=DescribeProcess&version=2.0.0&identifier=ShakygroundProcess"/>
</wps:ProcessSummary>
```

This identifier is needed to choose which process should be executed.
To get information (like input and output data types) about the process you can query the WPS server with the `DescribeProcess` parameter:

```bash
http://URLtoWPS?request=DescribeProcess&service=WPS&version=2.0.0&identifier=ShakygroundProcess
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


## Command line <a name="#commandline"></a>

For querying a WPS server from the command line you can use `cURL` or `wget`.
It is best to prepare a XML file with the request to the WPS in it, so for example:

```xml
<wps:Execute service="WPS" version="2.0.0" xmlns:wps="http://www.opengis.net/wps/2.0" xmlns:ows="http://www.opengis.net/ows/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.opengis.net/wps/2.0 http://schemas.opengis.net/wps/2.0/wpsExecute.xsd" response="document" mode="sync">
  <ows:Identifier>ShakygroundProcess</ows:Identifier>
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


### cURL <a name="#curl"></a>




### wget <a name="#wget"></a>

```
wget "http://URL/to/WPS?param1=123&param2=abc" --post-file="xmlTestFile.xml" --header="Content-Type:text/xml"
```

or use `post-data` instead of `post-file` to send XML string:

```
--post-data="<eventParameters xmlns="http://quakeml.org/xmlns/bed/1.2">..."
```


## Python <a name="#python"></a>


## R <a name="#r"></a>


## JavaScript <a name="#javascript"></a>
