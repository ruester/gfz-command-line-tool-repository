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
 * Binding class that contains a simple json object.
 */
public class JsonDataBinding implements IComplexData {

    private static final long serialVersionUID = 8386437107877117360L;

    /**
     * Inner json object.
     */
    private final JsonObjectOrArray jsonObject;

    /**
     * Default constructor for JsonDataBinding.
     * @param aJsonObject jsonObject to wrap
     */
    public JsonDataBinding(final JsonObjectOrArray aJsonObject) {
        this.jsonObject = aJsonObject;
    }

    /**
     * Disposes the data binding.
     */
    @Override
    public void dispose() {
        // do nothing
    }

    /**
     *
     * @return the content of the data binding
     */
    @Override
    public JsonObjectOrArray getPayload() {
        return jsonObject;
    }

    /**
     *
     * @return supported class for the data binding
     */
    @Override
    public Class<?> getSupportedClass() {
        return JsonObjectOrArray.class;
    }
}
```

Here we use an JSONObject to store all our data in. We just could have gone
with a simple string to read and write JSON out, but this way we at least
have the validation that the data is valid json (but not 
necessary valid geojson).

Because we use a custom payload class we have to create this is well:
```java
package org.n52.gfz.riesgos.formats.json.binding;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Optional;

/**
 * Wrapper to support both arrays and objects.
 */
public class JsonObjectOrArray implements Serializable {

    private static final long serialVersionUID = -3251367313463873184L;

    /**
     * This contains the json object if it used.
     */
    private final JSONObject jsonObject;
    /**
     * This contains the json array if it used.
     */
    private final JSONArray jsonArray;

    /**
     * This is the constructor with the json object.
     * @param aJsonObject json object to store
     */
    public JsonObjectOrArray(final JSONObject aJsonObject) {
        this.jsonObject = aJsonObject;
        this.jsonArray = null;
    }

    /**
     * This is the constructor with the json array.
     * @param aJsonArray json array to store.
     */
    public JsonObjectOrArray(final JSONArray aJsonArray) {
        this.jsonObject = null;
        this.jsonArray = aJsonArray;
    }

    /**
     *
     * @return optional json object
     */
    public Optional<JSONObject> getJsonObject() {
        return Optional.ofNullable(jsonObject);
    }

    /**
     *
     * @return optional json array
     */
    public Optional<JSONArray> getJsonArray() {
        return Optional.ofNullable(jsonArray);
    }
}
````

If you just want to use an existing binding class you can skip this step.

## Add the entry to the DefaultFormatOption enum

In the DefaultFormatOption enum in the org.n52.gfz.riesgos.configuration.parse.defaultformats 
package are the format entries that can be
used for the defaultFormat attribute.
Just add the following line:

```
    /**
     * Enum for json.
     */
    JSON("json",
            new FormatEntry(
                    MIME_TYPE_JSON,
                    null,
                    DEFAULT_ENCODING,
                    true)),
```

## Write a parser

Next step is to write a parser for your new binding class:

```java
package org.n52.gfz.riesgos.formats.json.parsers;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.formats.json.binding.JsonObjectOrArray;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.parser.AbstractParser;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Parser for json input.
 */
public class JsonParser
        extends AbstractParser {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JsonParser.class);

    /**
     * This is the default constructor for the JsonParser.
     */
    public JsonParser() {
        super();

        final FormatEntry json = DefaultFormatOption.JSON.getFormat();
        supportedIDataTypes.add(JsonDataBinding.class);
        supportedFormats.add(json.getMimeType());
        supportedEncodings.add(json.getEncoding());
        formats.add(json);
    }

    /**
     * Parses the stream to a JsonDataBinding.
     * @param stream stream with the content
     * @param mimeType mimeType of the content
     * @param schema schema of the content
     * @return JsonDataBinding
     */
    @Override
    public IData parse(
            final InputStream stream,
            final String mimeType,
            final String schema) {
        try {
            final ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream();
            IOUtils.copy(stream, byteArrayOutputStream);
            final String content =
                    new String(byteArrayOutputStream.toByteArray());
            final JSONParser parser = new JSONParser();
            final Object parsed = parser.parse(content);
            if (parsed instanceof  JSONObject) {
                final JSONObject jsonObject = (JSONObject) parsed;
                return new JsonDataBinding(new JsonObjectOrArray(jsonObject));
            } else if (parsed instanceof JSONArray) {
                final JSONArray jsonArray = (JSONArray) parsed;
                return new JsonDataBinding(new JsonObjectOrArray(jsonArray));
            }
            throw new RuntimeException(
                    "Can't parse the content to an json object");
        } catch (final IOException | ParseException exception) {
            throw new RuntimeException(exception);
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.formats.json.binding.JsonObjectOrArray;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

/**
 * Generator for json data.
 */
public class JsonGenerator
        extends AbstractGenerator {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JsonGenerator.class);

    /**
     * default constructor.
     */
    public JsonGenerator() {
        super();

        final FormatEntry json = DefaultFormatOption.JSON.getFormat();
        supportedIDataTypes.add(JsonDataBinding.class);
        supportedFormats.add(json.getMimeType());
        supportedEncodings.add(json.getEncoding());
        formats.add(json);
    }

    /**
     * Generates an input stream with the content of the data.
     * @param data data binding with information to put in the stream
     * @param mimeType mime type to generate
     * @param schema schema to generate
     * @return input stream with the data
     */
    @Override
    public InputStream generateStream(
            final IData data,
            final String mimeType,
            final String schema) {
        if (data instanceof JsonDataBinding) {
            final JsonDataBinding binding = (JsonDataBinding) data;
            final JsonObjectOrArray jsonObject = binding.getPayload();

            final Optional<JSONObject> asJsonObject = jsonObject.getJsonObject();

            if(asJsonObject.isPresent()) {
                return new ByteArrayInputStream(asJsonObject.get().toJSONString().getBytes());
            }

            final Optional<JSONArray> asJsonArray = jsonObject.getJsonArray();

            if(asJsonArray.isPresent()) {
                return new ByteArrayInputStream(asJsonArray.get().toJSONString().getBytes());
            }
            
            LOGGER.error("JSON not an object nor an array");

        } else {
            LOGGER.error(
                    "Can't convert another data binding as JsonDataBinding");
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.formats.json.binding.JsonObjectOrArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;

import java.util.Objects;

/**
 * Function to convert the content of a byte array to a JsonDataBinding.
 */
public class ConvertBytesToJsonDataBinding
        implements IConvertByteArrayToIData<JsonDataBinding> {

    private static final long serialVersionUID = -643297229677437705L; /**
     * Converts the byte array to an IData element.
     * @param content byte array to convert
     * @return IData element
     * @throws ConvertToIDataException exception if there is an
     * internal error / exception on conversion
     */
    @Override
    public JsonDataBinding convertToIData(final byte[] content)
            throws ConvertToIDataException {
        final String text = new String(content);
        final JSONParser parser = new JSONParser();
        try {
            final Object parsed = parser.parse(text);
            if (parsed instanceof JSONObject) {
                final JSONObject jsonObject = (JSONObject) parsed;
                return new JsonDataBinding(new JsonObjectOrArray(jsonObject));
            } else if (parsed instanceof JSONArray) {
                final JSONArray jsonArray = (JSONArray) parsed;
                return new JsonDataBinding(new JsonObjectOrArray(jsonArray));
            }
        } catch (final ParseException parseException) {
            throw new ConvertToIDataException(parseException);
        }
        return null;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.n52.gfz.riesgos.exceptions.ConvertToBytesException;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.formats.json.binding.JsonObjectOrArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;

import java.util.Objects;
import java.util.Optional;

/**
 * Function to convert a json data binding to a byte array.
 */
public class ConvertJsonDataBindingToBytes
        implements IConvertIDataToByteArray<JsonDataBinding> {

    /**
     * Converts the IData to a byte array.
     * @param binding element to convert
     * @return byte array
     * @throws ConvertToBytesException exception that indicates that the
     * element could not converted to byte array
     */
    @Override
    public byte[] convertToBytes(final JsonDataBinding binding)
            throws ConvertToBytesException {
        final JsonObjectOrArray jsonObject = binding.getPayload();
        final Optional<JSONObject> asObject = jsonObject.getJsonObject();
        final Optional<JSONArray> asArray = jsonObject.getJsonArray();
        final String content;
        if(asObject.isPresent()) {
            content = asObject.get().toJSONString();
        } else if(asArray.isPresent()) {
            content = asArray.get().toJSONString();
        } else {
            throw new ConvertToBytesException(
                    "Can't convert as json object nor as json array");
        }
        return content.getBytes();
    }
}
```

## Add the type as input for stdin

If we want to add json as input type for stdin we must register this
option in the org.n52.gfz.riesgos.configuration.parse packages.

We will add the following line to the ToStdinInputOption enum:

```
JSON("json", new StdinJsonFactory()),
```

And we also need to add the a factory class mentioned there:

```java
package org.n52.gfz.riesgos.configuration.parse.input.stdin;

import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.InputParameterFactory;
import org.n52.gfz.riesgos.configuration.parse.ParseUtils;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.List;

/**
 * Implementation to create a stdin input with json.
 */
public class StdinJsonFactory implements  IAsStdinInputFactory {
    /**
     *  Checks some attributes and deligates the creation.
     * @param identifier identifier of the data
     * @param isOptional true if the parameter is optional
     * @param optionalAbstract optional description of the parameter
     * @param defaultFormat optional default format
     * @param defaultValue optional default value
     * @param allowedValues optional list with allowed values
     * @param schema optional schema
     * @return input parameter
     * @throws ParseConfigurationException exception that may be thrown if there
     * are values given that can't be used in this implementation.
     */
    @Override
    public IInputParameter create(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final FormatEntry defaultFormat,
            final String defaultValue,
            final List<String> allowedValues,
            final String schema) throws ParseConfigurationException {
        if (ParseUtils.strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for json");
        }
        if (ParseUtils.strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "defaultValue is not supported for json");
        }
        if (ParseUtils.listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowedValues are not supported for json");
        }
        return InputParameterFactory.INSTANCE.createStdinJson(
                identifier,
                isOptional,
                optionalAbstract,
                defaultFormat
        );
    }
}
```

We also want to add the createStdinJson method to the 
InputParameterFactory.INSTANCE:

```
    /**
     * Creates a stdin input with json.
     * @param identifier identifier of the data
     * @param isOptional true if the value is optional
     * @param optionalAbstract optional description of the parameter
     * @param defaultFormat optional default format
     * @return object with information about how to use the value
     * as a json stdin input parameter
     */
    public IInputParameter createStdinJson(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final FormatEntry defaultFormat) {
        return new InputParameterImpl.Builder<>(
                identifier, JsonDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToWriteToStdin(
                        new ConvertJsonDataBindingToBytes())
                .withDefaultFormat(defaultFormat)
                .build();
    }
```

## Add the type as input as commandLineArgument

Similar to the process for adding it as stdin type, we add
a line on the ToCommandLineArgumentOption enum.

```
JSON("json",
            new CommandLineArgumentJsonFileFactory()),
```

And we also add the factory class:

```java
package org.n52.gfz.riesgos.configuration.parse.input.commandlineargument;

import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.InputParameterFactory;
import org.n52.gfz.riesgos.configuration.parse.ParseUtils;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.List;

/**
 * Implementation to create a command line argument with a json file.
 */
public class CommandLineArgumentJsonFileFactory
        implements IAsCommandLineArgumentFactory {

    /**
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param isOptional true if the input is optional
     * @param optionalAbstract optional description of the parameter
     * @param defaultFormat optional default format
     * @param defaultCommandLineFlag optional default command line flag
     * @param defaultValue optional default value
     * @param allowedValues optional list with allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return input parameter
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    @Override
    public IInputParameter create(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final FormatEntry defaultFormat,
            final String defaultCommandLineFlag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if (ParseUtils.strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "default is not supported for json");
        }
        if (ParseUtils.listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowed values are not supported for json");
        }
        if (ParseUtils.listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for json");
        }
        if (ParseUtils.strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for json");
        }
        return InputParameterFactory.INSTANCE.createCommandLineArgumentJson(
                identifier,
                isOptional,
                optionalAbstract,
                defaultFormat,
                defaultCommandLineFlag
        );
    }
}

```

And we add the createCommandLineArgumentJson method to the 
InputParameterFactory.INSTANCE:

```
    /**
     * Creates a command line argument (json file) with a file path that will
     * be written down as a temporary file.
     * @param identifier identifier of the data
     * @param isOptional true if the value is optional
     * @param optionalAbstract optional description of the parameter
     * @param defaultFormat optional default format
     * @param flag optional command line flag
     * @return json command line argument
     */
    public IInputParameter createCommandLineArgumentJson(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final FormatEntry defaultFormat,
            final String flag) {
        final String filename = createUUIDFilename(".json");

        final InputParameterImpl.Builder<JsonDataBinding> builder =
                new InputParameterImpl.Builder<>(
                        identifier,
                        JsonDataBinding.class,
                        isOptional,
                        optionalAbstract);
        builder.withFunctionToTransformToCmd(
                new FileToStringCmd<>(filename, flag));
        builder.withPath(filename);
        builder.withFunctionToWriteToFiles(
                new WriteSingleByteStreamToPath<>(
                        new ConvertJsonDataBindingToBytes()));
        builder.withDefaultFormat(defaultFormat);
        return builder.build();
    }
```

## Add the type as file input

This is again similar to adding the type as stdin input.
First we have to add an entry for the ToFileInputOption enum:

```
JSON("json", new InputFileJsonFactory())
```

Then we add the factory class:

```java
package org.n52.gfz.riesgos.configuration.parse.input.file;

import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.InputParameterFactory;
import org.n52.gfz.riesgos.configuration.parse.ParseUtils;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.wps.webapp.api.FormatEntry;

/**
 * Implementation to create a file input with json.
 */
public class InputFileJsonFactory implements IAsFileInputFactory {

    /**
     * Factory method to create the input parameter with the given data.
     * Not all implementations support all of this arguments.
     *
     * @param identifier       identifier of the data
     * @param isOptional       true if the input is optional
     * @param optionalAbstract optional abstract (description) of the data
     * @param defaultFormat optional default format
     * @param path             path to the file
     * @param schema           optional schema
     * @return input parameter
     * @throws ParseConfigurationException exception that will be thrown
     * if an unsupported argument is given to the implementation.
     */
    @Override
    public IInputParameter create(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final FormatEntry defaultFormat,
            final String path,
            final String schema)

            throws ParseConfigurationException {

        if (ParseUtils.strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for json");
        }
        return InputParameterFactory.INSTANCE.createFileInJson(
                identifier,
                isOptional,
                optionalAbstract,
                defaultFormat,
                path
        );
    }
}
```

And we add the createFileInJson to the InputParameterFactory.INSTANCE:

```
    /**
     * Creates an input file argument with json.
     * @param identifier identifier of the data
     * @param isOptional true if the value is optional
     * @param optionalAbstract optional description of the parameter
     * @param path path of file to write before starting the process
     * @return json input file
     */
    public IInputParameter createFileInJson(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final FormatEntry defaultFormat,
            final String path) {

        final InputParameterImpl.Builder<JsonDataBinding> builder =
                new InputParameterImpl.Builder<>(
                        identifier,
                        JsonDataBinding.class,
                        isOptional,
                        optionalAbstract);
        builder.withPath(path);
        builder.withFunctionToWriteToFiles(
                new WriteSingleByteStreamToPath<>(
                        new ConvertJsonDataBindingToBytes()));
        builder.withDefaultFormat(defaultFormat);
        return builder.build();
    }
```

## Add the type as output for stdout

To add the type as output type for stdout, we have to add the 
following line
to the FromStdoutOption enum:

```
    /**
     * This is the enum to read json from stdout.
     */
    JSON("json",
            (
                    identifier,
                    isOptional,
                    optionalAbstract,
                    defaultFormat,
                    schema
            ) ->
                    // ignore schema
                    OutputParameterFactory.INSTANCE.createStdoutJson(
                            identifier,
                            isOptional,
                            optionalAbstract,
                            defaultFormat));
```

And we have to add the createStdoutJson method to the OutputParameterFactory.INSTANCE:

```
    /**
     * Creates a json output (via stdout).
     * @param identifier identifier of the data
     * @param isOptional true if the output is optional
     * @param optionalAbstract optional description of the parameter
     * @param defaultFormat optional default format
     * @return output argument containing json that will be read from stdout
     */
    public IOutputParameter createStdoutJson(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final FormatEntry defaultFormat) {
        return new OutputParameterImpl.Builder<>(
                identifier, JsonDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToHandleStdout(
                        new ConvertBytesToJsonDataBinding())
                .withDefaultFormat(defaultFormat)
                .build();
    }
```

## Add the type as output for stderr

This is again very similar to adding it to stdout.
We have to add the line to the FromStderrOption enum:

```
    /**
     * This is the enum to read json from stderr.
     */
    JSON("json", OutputParameterFactory.INSTANCE::createStderrJson);
```

and the createStderrJson method in OutputParameterFactory.INSTANCE:

```
    /**
     * Creates a json output (via stderr).
     * @param identifier identifier of the data
     * @param isOptional true if the output is optional
     * @param optionalAbstract optional description of the parameter
     * @param defaultFormat optional default format
     * @return output argument containing the json that will be read from
     * stderr
     */
    public IOutputParameter createStderrJson(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final FormatEntry defaultFormat) {
        return new OutputParameterImpl.Builder<>(
                identifier, JsonDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToHandleStderr(
                        new ConvertBytesToJsonDataBinding())
                .withDefaultFormat(defaultFormat)
                .build();
    }
```

## Add the type as output for files

We have to add the line to the FromFilesOption enum:

```
    /**
     * This is the enum to read json from files.
     */
    JSON("json",
            (
                    identifier,
                    isOptional,
                    optionalAbstract,
                    defaultFormat,
                    path,
                    schema
            ) ->
                    // ignore schema
                    OutputParameterFactory.INSTANCE.createFileOutJson(
                            identifier,
                            isOptional,
                            optionalAbstract,
                            defaultFormat,
                            path));
```

Then we have to add the method createFileOutJson method to the
OutputParameterFactory.INSTANCE.

```
    /**
     * Creates a xml file for json on a given path.
     * @param identifier identifier of the data
     * @param isOptional true if the output is optional
     * @param optionalAbstract optional description of the parameter
     * @param defaultFormat optional default format
     * @param path path of the file to read after process termination
     * @return output argument containing the json that will be
     * read from a given file
     */
    public IOutputParameter createFileOutJson(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final FormatEntry defaultFormat,
            final String path) {
        final OutputParameterImpl.Builder<JsonDataBinding> builder =
                new OutputParameterImpl.Builder<>(
                        identifier,
                        JsonDataBinding.class,
                        isOptional,
                        optionalAbstract);
        builder.withPath(path);
        builder.withFunctionToReadFromFiles(
                new ReadSingleByteStreamFromPath<>(
                        new ConvertBytesToJsonDataBinding()));
        builder.withDefaultFormat(defaultFormat);

        return builder.build();
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