# Add your own Format

You may want to add your own process but you realize that you want
to work with a data format that is currently not supported in
the parsing of the configuration process.

We have to differ between using a predefined binding class (similar
that we use it for xml, geojson, geotiff and some others) and
between the case of defining an own binding class.

The tasks for both are mostly the same, but for own binding classes,
it is necessary to write own parsers and generators as well and
to register them in the GfzRiesgosRepository class, so we will go
through the whole process on the example of an alternative
json binding.

The reason we do this is to provide a fallback mode
for geojson data that use different projections than wgs84.
Other tools like gdal/ogr and geopandas are able
to write and read those files, but geotools have some problems
with this.

To make the stuff as general as we can, we will only care about simple
json and not about some more extended features of geojson.

## Creating an own binding class

Lets create the JsonBinding class.

```java
package org.n52.gfz.riesgos.formats.json.binding;

import org.json.simple.JSONObject;
import org.n52.wps.io.data.IComplexData;

/**
 * Binding class that contains a simple json object
 */
public class JsonDataBinding implements IComplexData {

    private static final long serialVersionUID = 8386437107877117360L;

    private final JSONObject jsonObject;

    /**
     * Default constructor
     * @param jsonObject jsonObject to wrap
     */
    public JsonDataBinding(final JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public void dispose() {
        // do nothing
    }

    @Override
    public JSONObject getPayload() {
        return jsonObject;
    }

    @Override
    public Class<?> getSupportedClass() {
        return JSONObject.class;
    }
}

```

Here we use an JSONObject to store all our data in. We just could have gone
with a simple string to read and write JSON out, but this way we at least
have the validation that the data is valid json (but not 
necessary valid geojson).

If you just want to use an existing binding class you can skip this step.

## Write a parser

Next step is to write a parser for your new binding class:

```java
package org.n52.gfz.riesgos.formats.json.parsers;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.n52.gfz.riesgos.formats.IMimeTypeAndSchemaConstants;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.parser.AbstractParser;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Parser for json input
 */
public class JsonParser extends AbstractParser implements IMimeTypeAndSchemaConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonParser.class);

    /**
     * default constructor
     */
    public JsonParser() {
        super();

        supportedIDataTypes.add(JsonDataBinding.class);
        supportedFormats.add(MIME_TYPE_JSON);
        supportedEncodings.add(DEFAULT_ENCODING);
        formats.add(new FormatEntry(MIME_TYPE_JSON, null, DEFAULT_ENCODING, true));
    }

    @Override
    public IData parse(final InputStream stream, final String mimeType, final String schema) {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            IOUtils.copy(stream, byteArrayOutputStream);
            final String content = new String(byteArrayOutputStream.toByteArray());
            final JSONParser parser = new JSONParser();
            final Object parsed = parser.parse(content);
            if(parsed instanceof  JSONObject) {
                final JSONObject jsonObject = (JSONObject) parsed;
                return new JsonDataBinding(jsonObject);
            }
            throw new RuntimeException("Can't parse the content to an json object");
        } catch(final IOException ioException) {
            throw new RuntimeException(ioException);
        } catch(final ParseException parseException) {
            throw new RuntimeException(parseException);
        }
    }
}
```

We added the mime type in the IMimeTypeAndSchemaConstants interface
with the value "application/json".

If you just want to use an existing binding class and an existiong parser
you can skip this step.

## Write a generator

```java
package org.n52.gfz.riesgos.formats.json.generators;

import org.json.simple.JSONObject;
import org.n52.gfz.riesgos.formats.IMimeTypeAndSchemaConstants;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Generator for json data
 */
public class JsonGenerator extends AbstractGenerator implements IMimeTypeAndSchemaConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonGenerator.class);

    /**
     * default constructor
     */
    public JsonGenerator() {
        super();

        supportedIDataTypes.add(JsonDataBinding.class);
        supportedFormats.add(MIME_TYPE_JSON);
        supportedEncodings.add(DEFAULT_ENCODING);
        formats.add(new FormatEntry(MIME_TYPE_JSON, null, DEFAULT_ENCODING, true));
    }

    @Override
    public InputStream generateStream(final IData data, final String mimeType, final String schema) throws IOException {
        if(data instanceof JsonDataBinding) {
            final JsonDataBinding binding = (JsonDataBinding) data;
            final JSONObject jsonObject = binding.getPayload();
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toJSONString().getBytes());
            return inputStream;
        } else {
            LOGGER.error("Can't convert another data binding as JsonDataBinding");
        }
        return null;
    }
}
```

If you just want to use an existing binding class and an existing generator class
you can skip this step.

## Register the parser and generator in the GfzRiesgosRepository class

Next step is to add the parser and generator in the repository class.

Just call the constructors in the lists of the registerGenerators and 
registerParsers methods.
You only need to add those parsers and generators that you wrote in the
two steps before.

## Write an IConvertByteArrayToIData implementation

To read the content from docker we need to write an implementation
of the IConvertByteArrayToIData interface:

```java
package org.n52.gfz.riesgos.bytetoidataconverter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.IData;

import java.util.Objects;

/**
 * Function to convert the content of a byte array to a JsonDataBinding
 */
public class ConvertBytesToJsonDataBinding implements IConvertByteArrayToIData {

    @Override
    public IData convertToIData(final byte[] content) throws ConvertToIDataException {
        final String text = new String(content);
        final JSONParser parser = new JSONParser();
        try {
            final Object parsed = parser.parse(text);
            if (parsed instanceof JSONObject) {
                final JSONObject jsonObject = (JSONObject) parsed;
                return new JsonDataBinding(jsonObject);
            }
        } catch(final ParseException parseException) {
            throw new ConvertToIDataException(parseException);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName());
    }

}
```

Having this class we will be able to read the content from the container.

## Write an IConvertIDataToByteArray implementation

As we have the function to transform the byte array to an IData,
we also need the conversion back to a byte array to support the option
to write our json to the docker container.

```java
package org.n52.gfz.riesgos.idatatobyteconverter;

import org.json.simple.JSONObject;
import org.n52.gfz.riesgos.exceptions.ConvertToBytesException;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.wps.io.data.IData;

import java.util.Objects;

/**
 * Function to convert a json data binding to a byte array
 */
public class ConvertJsonDataBindingToBytes implements IConvertIDataToByteArray {

    @Override
    public byte[] convertToBytes(final IData data) throws ConvertToBytesException {
        if(data instanceof JsonDataBinding) {
            final JsonDataBinding binding = (JsonDataBinding) data;
            final JSONObject jsonObject = binding.getPayload();
            final String content = jsonObject.toJSONString();
            return content.getBytes();
        } else {
            throw new ConvertToBytesException("Wrong binding class");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName());
    }
}
```

## Add the type as input for stdin

If we want to add json as input type for stdin we must change
the content of the ParseJsonForInputImpl class.

We will add the following line to the ToStdinInputOption enum:

```
JSON("json", ParseJsonForInputImpl::createStdinJson),
```

And we also need to add the method mentioned there:

```
    private static IIdentifierWithBinding createStdinJson(
            final String identifier,
            final String optionalAbstract,
            final String defaultValue,
            final List<String> allowedValues,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for json");
        }
        if(strHasValue(defaultValue)) {
            throw new ParseConfigurationException("defaultValue is not supported for json");
        }
        if(listHasValue(allowedValues)) {
            throw new ParseConfigurationException("allowedValues are not supported for json");
        }
        return IdentifierWithBindingFactory.createStdinJson(identifier, optionalAbstract);
    }
```

We also want to add the createStdinJson method to the 
IdentifierWithBindingFactory class:

```
    /**
     * creates a stdin input with json
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @return object with information about how to use the value as a json stdin input parameter
     */
    public static IIdentifierWithBinding createStdinJson(
            final String identifier,
            final String optionalAbstract) {
        return new IdentifierWithBindingImpl.Builder(identifier, JsonDataBinding.class)
                .withAbstract(optionalAbstract)
                .withFunctionToWriteToStdin(new ConvertJsonDataBindingToBytes())
                .build();
    }
```

## Add the type as input as commandLineArgument

Similar to the process for adding it as stdin type, we add
a line on the ToCommandLineArgumentOption enum in the
ParseJsonForInputImpl class.

```
JSON("json", ParseJsonforInputImpl::createCommandLineArgumentJson)
```

And we also add the method createCommandLineArgumentJson in this class:

```
private static IIdentifierWithBinding createCommandLineArgumentJson(
            final String identifier,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValue,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(defaultValue)) {
            throw new ParseConfigurationException("default is not supported for json");
        }
        if(listHasValue(allowedValue)) {
            throw new ParseConfigurationException("allowed values are not supported for json");
        }
        if(listHasValue(supportedCrs)) {
            throw new ParseConfigurationException("crs are not supported for json");
        }
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for json");
        }
        return IdentifierWithBindingFactory.createCommandLineArgumentJson(identifier, optionalAbstract, flag);
}
```

And we add the createCommandLineArgumentJson method to the 
IdentifierWithBindingFactory class:

```
    /**
     * Creates a command line argument (json file) with a file path that will be written down as
     * a temporary file
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional command line flag
     * @return json command line argument
     */
    public static IIdentifierWithBinding createCommandLineArgumentJson(
            final String identifier,
            final String optionalAbstract,
            final String flag) {
        final String filename = createUUIDFilename(".json");

        return new IdentifierWithBindingImpl.Builder(identifier, JsonDataBinding.class)
                .withAbstract(optionalAbstract)
                .withFunctionToTransformToCmd(new FileToStringCmd(filename, flag))
                .withPath(filename)
                .withFunctionToWriteToFiles(new WriteSingleByteStreamToPath(new ConvertJsonDataBindingToBytes()))
                .build();
    }
```

## Add the type as file input

This is again similar to adding the type as stdin input.
First we have to add an entry for the ToFileInputOption enum in ParseJsonForInputImpl
class:

```
JSON("json", ParseJsonForInputImpl::createFileInputJson)
```

Then we add the method in this class:

```
private static IIdentifierWithBinding createFileInputJson(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for json");
        }
        return IdentifierWithBindingFactory.createFileInJson(
            identifier, optionalAbstract, path);
}
```

And we add the createFileInJson to the IdentifierWithBindingFactory class:

```
    /**
     * Creates an input file argument with json
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of file to write before starting the process
     * @return json input file
     */
    public static IIdentifierWithBinding createFileInJson(
            final String identifier,
            final String optionalAbstract,
            final String path) {

        return new IdentifierWithBindingImpl.Builder(identifier, JsonDataBinding.class)
                .withAbstract(optionalAbstract)
                .withPath(path)
                .withFunctionToWriteToFiles(new WriteSingleByteStreamToPath(new ConvertJsonDataBindingToBytes()))
                .build();
    }
```

## Add the type as output for stdout

To add the type as output type for stdout, we have to add the 
following line
to the FromStdoutOption enum in the ParseJsonForOutputImpl class:

```
JSON("json", (identifier, optionalAbstract, schema) -> IdentifierWithBindingFactory.createStdoutJson(identifier, optionalAbstract))
```

And we have to add the createStdoutJson method to the IdentifierWithBindingFactory class:

```
    /**
     * Creates a json output (via stdout)
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @return output argument containing json that will be read from stdout
     */
    public static IIdentifierWithBinding createStdoutJson(
            final String identifier,
            final String optionalAbstract) {
        return new IdentifierWithBindingImpl.Builder(identifier, JsonDataBinding.class)
                .withAbstract(optionalAbstract)
                .withFunctionToHandleStdout(new ConvertBytesToJsonDataBinding())
                .build();
    }
```

## Add the type as output for stderr

This is again very similar to adding it to stdout.
We have to add the line to the FromStderrOption enum in the ParseJsonForOutputImpl
class:

```
JSON("json", IdentifierWithBindingFactory::createStderrJson)
```

and the createStderrJson method in IdentifierWithBindingFactory:

```
    /**
     * Creates a json output (via stderr)
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @return output argument containing the json that will be read from stderr
     */
    public static IIdentifierWithBinding createStderrJson(
            final String identifier,
            final String optionalAbstract) {
        return new IdentifierWithBindingImpl.Builder(identifier, JsonDataBinding.class)
                .withAbstract(optionalAbstract)
                .withFunctionToHandleStderr(new ConvertBytesToJsonDataBinding())
                .build();
    }
```

## Add the type as output for files

We have to add the line to the FromFilesOption enum in the ParseJsonForOutputImpl
class:

```
JSON("json", (identifier, optionalAbstract, path, schema) -> IdentifierWithBindingFactory.createFileOutJson(identifier, optionalAbstract, path));
```

Then we have to add the method createFileOutJson method to the
IdentifierWithBindingFactory class.

```
    /**
     * Creates a xml file for json on a given path
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the file to read after process termination
     * @return output argument containing the json that will be read from a given file
     */
    public static IIdentifierWithBinding createFileOutJson(
            final String identifier,
            final Stirng optionalAbstract,
            final String path) {
        return new IdentifierWithBindingImpl.Builder(identifier, JsonDataBinding.class)
                .withAbstract(optionalAbstract)
                .withPath(path)
                .withFunctionToReadFromFiles(new ReadSingleByteStreamFromPath(new ConvertBytesToJsonDataBinding()))
                .build();
    }
```


## Check dependencies

Because we just use the org.json.simple.JSONObject, we must care about
the dependencies.
After a look in the pom.xml we are happy that this is already included
in the project.

## Contribute to this project

Once you have integrated your format we will be very happy if you
consider to contribute your format to this project as it allows aother
people to work with this format too.