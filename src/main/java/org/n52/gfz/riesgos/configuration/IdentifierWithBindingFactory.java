package org.n52.gfz.riesgos.configuration;

/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGTVectorDataBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGenericFileDataBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGenericXMLDataBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGeotiffBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToLiteralStringBinding;
import org.n52.gfz.riesgos.commandlineparametertransformer.BoundingBoxDataToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.FileToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralBooleanBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralDoubleBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralIntBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralStringBindingToStringCmd;
import org.n52.gfz.riesgos.configuration.impl.IdentifierWithBindingImpl;
import org.n52.gfz.riesgos.exitvaluetoidataconverter.ConvertExitValueToLiteralIntBinding;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGTVectorDataBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGenericFileDataBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGenericXMLDataBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGenericXMLDataBindingToBytesWithoutHeader;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGeotiffBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertLiteralStringToBytes;
import org.n52.gfz.riesgos.readidatafromfiles.ReadShapeFileFromPath;
import org.n52.gfz.riesgos.readidatafromfiles.ReadSingleByteStreamFromPath;
import org.n52.gfz.riesgos.validators.LiteralStringBindingWithAllowedValues;
import org.n52.gfz.riesgos.writeidatatofiles.WriteShapeFileToPath;
import org.n52.gfz.riesgos.writeidatatofiles.WriteSingleByteStreamToPath;
import org.n52.wps.io.data.binding.bbox.BoundingBoxData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;
import org.n52.wps.io.data.binding.literal.LiteralBooleanBinding;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.List;
import java.util.UUID;

/**
 * Factory for several predefined kinds of input and output data
 */
public class IdentifierWithBindingFactory {
    private IdentifierWithBindingFactory() {
        // static
    }

    /**
     * Creates a command line int argument
     * @param identifier identifier of the data
     * @param flag optional command line flag (--x for a parameter x)
     * @param defaultValue optional default value of the argument
     * @param allowedValues optional list with allowed values
     * @return object with information about how to use the value as a int command line argument input parameter
     */
    public static IIdentifierWithBinding createCommandLineArgumentInt(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues) {
        final IdentifierWithBindingImpl.Builder builder = new IdentifierWithBindingImpl.Builder(identifier, LiteralIntBinding.class);
        builder.withFunctionToTransformToCmd(new LiteralIntBindingToStringCmd(flag));

        if(defaultValue != null) {
            builder.withDefaultValue(defaultValue);
        }

        if(allowedValues != null && (! allowedValues.isEmpty())) {
            builder.withAllowedValues(allowedValues);
        }

        return builder.build();
    }

    /**
     * Creates a command line double argument
     * @param identifier identifier of the data
     * @param flag optional command line flag (--x for a parameter x)
     * @param defaultValue optional default value of the argument
     * @param allowedValues optional list with allowed values
     * @return object with information about how to use the value as a double command line argument input parameter
     */
    public static IIdentifierWithBinding createCommandLineArgumentDouble(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues) {
        final IdentifierWithBindingImpl.Builder builder = new IdentifierWithBindingImpl.Builder(identifier, LiteralDoubleBinding.class);
        builder.withFunctionToTransformToCmd(new LiteralDoubleBindingToStringCmd(flag));

        if(defaultValue != null) {
            builder.withDefaultValue(defaultValue);
        }

        if(allowedValues != null && (! allowedValues.isEmpty())) {
            builder.withAllowedValues(allowedValues);
        }

        return builder.build();
    }

    /**
     * Creates a command line string argument
     * @param identifier identifier of the data
     * @param flag optional command line flag (--x for a parameter x)
     * @param defaultValue optional default value of the argument
     * @param allowedValues optional list with allowed values
     * @return object with information about how to use the value as a string command line argument input parameter
     */
    public static IIdentifierWithBinding createCommandLineArgumentString(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues) {
        final IdentifierWithBindingImpl.Builder builder = new IdentifierWithBindingImpl.Builder(identifier, LiteralStringBinding.class);
        builder.withFunctionToTransformToCmd(new LiteralStringBindingToStringCmd(flag));

        if(defaultValue != null) {
            builder.withDefaultValue(defaultValue);
        }

        if(allowedValues != null && (! allowedValues.isEmpty())) {
            builder.withAllowedValues(allowedValues);
            builder.withValidator(new LiteralStringBindingWithAllowedValues(allowedValues));
        }

        return builder.build();
    }

    /**
     * Creates a command line boolean argument
     * @param identifier identifier of the data
     * @param flag command line flag to insert if the value is true
     * @param defaultValue optional default value
     * @return object with information about how to use the value as a boolean command line argument input parameter
     */
    public static IIdentifierWithBinding createCommandLineArgumentBoolean(
            final String identifier,
            final String flag,
            final String defaultValue) {
        final IdentifierWithBindingImpl.Builder builder = new IdentifierWithBindingImpl.Builder(identifier, LiteralBooleanBinding.class);
        builder.withFunctionToTransformToCmd(new LiteralBooleanBindingToStringCmd(flag));

        if (defaultValue != null) {
            builder.withDefaultValue(defaultValue);
        }

        return builder.build();
    }

    /**
     * Creates a command line argument (input) which contains a boolean
     * @param identifier identifier of the data
     * @param commandLineFlag flag that is added if the value is true
     * @return Command line argument that contains a boolean
     */
    public static IIdentifierWithBinding createCommandLineArgumentBoolean(
            final String identifier,
            final String commandLineFlag) {
        return new IdentifierWithBindingImpl.Builder(identifier, LiteralBooleanBinding.class)
                .withFunctionToTransformToCmd(new LiteralBooleanBindingToStringCmd(commandLineFlag))
                .build();
    }

    /**
     * Creates a command line argument (input) which contains a boolean with a default value
     * @param identifier identifier of the data
     * @param commandLineFlag flag that is added if the value is true
     * @param defaultValue default value
     * @return Command line argument that contains a boolean with a default value
     */
    public static IIdentifierWithBinding createCommandLineArgumentBooleanWithDefaultValue(
            final String identifier, final String commandLineFlag, final boolean defaultValue) {
        return new IdentifierWithBindingImpl.Builder(identifier, LiteralBooleanBinding.class)
                .withFunctionToTransformToCmd(new LiteralBooleanBindingToStringCmd(commandLineFlag))
                .withDefaultValue(String.valueOf(defaultValue))
                .build();
    }

    /**
     * Creates a command line argument bounding box.
     * (This will add four single command line arguments in that order:
     *  lonmin, lonmax, latmin, latmax)
     *
     * @param identifier identifier of the data
     * @param supportedCRSForBBox list with the supported CRS for the bounding box
     * @return bounding box command line argument
     */
    public static IIdentifierWithBinding createCommandLineArgumentBBox(
            final String identifier,
            final List<String> supportedCRSForBBox) {

        return new IdentifierWithBindingImpl.Builder(identifier, BoundingBoxData.class)
                .withFunctionToTransformToCmd(new BoundingBoxDataToStringCmd())
                .withSupportedCRSForBBox(supportedCRSForBBox)
                .build();
    }

    /**
     * Creates a command line argument (xml file) with a file path that will be written down as a temporary file
     * @param identifier identifier of the data
     * @param schema schema of the xml
     * @return xml file command line argument
     */
    public static IIdentifierWithBinding createCommandLineArgumentXmlFileWithSchema(
            final String identifier, final String schema) {

        final String filename = createUUIDFilename("inputfile", ".xml");

        return new IdentifierWithBindingImpl.Builder(identifier, GenericXMLDataBinding.class)
                .withFunctionToTransformToCmd(new FileToStringCmd(filename))
                .withPath(filename)
                .withFunctionToWriteToFiles(new WriteSingleByteStreamToPath(new ConvertGenericXMLDataBindingToBytes()))
                .withSchema(schema)
                .build();

    }

    /**
     * Same as createCommandLineArgumentXmlFileWithSchema, but it removes the xml header before
     * writing it to a file
     * @param identifier identifier of the data
     * @param schema schema of the xml
     * @return xml file command line argument
     */
    public static IIdentifierWithBinding createCommandLineArgumentXmlFileWithSchemaWithoutHeader(
            final String identifier, final String schema) {

        final String filename = createUUIDFilename("inputfile", ".xml");

        return new IdentifierWithBindingImpl.Builder(identifier, GenericXMLDataBinding.class)
                .withFunctionToTransformToCmd(new FileToStringCmd(filename))
                .withPath(filename)
                .withFunctionToWriteToFiles(new WriteSingleByteStreamToPath(new ConvertGenericXMLDataBindingToBytesWithoutHeader()))
                .withSchema(schema)
                .build();
    }

    /**
     * Creates a command line argument (geotiff file) with a file path that will be written down as a
     * temporary file
     * @param identifier identifier of the data
     * @return geotiff file command line argument
     */
    public static IIdentifierWithBinding createCommandLineArgumentGeotiff(
            final String identifier) {
        final String filename = createUUIDFilename("inputfile", ".tiff");

        return new IdentifierWithBindingImpl.Builder(identifier, GeotiffBinding.class)
                .withFunctionToTransformToCmd(new FileToStringCmd(filename))
                .withPath(filename)
                .withFunctionToWriteToFiles(new WriteSingleByteStreamToPath(new ConvertGeotiffBindingToBytes()))
                .build();
    }

    /**
     * Creates a command line argument (geojson) with a file path that will be written down as a
     * temporary file
     * @param identifier identifier of the data
     * @return geojson file command line argument
     */
    public static IIdentifierWithBinding createCommandLineArgumentGeojson(
            final String identifier) {
        final String filename = createUUIDFilename("inputfile", ".json");

        return new IdentifierWithBindingImpl.Builder(identifier, GTVectorDataBinding.class)
                .withFunctionToTransformToCmd(new FileToStringCmd(filename))
                .withPath(filename)
                .withFunctionToWriteToFiles(new WriteSingleByteStreamToPath(new ConvertGTVectorDataBindingToBytes(
                        ConvertGTVectorDataBindingToBytes.Format.JSON
                )))
                .build();
    }

    /**
     * Creates a command line argument (shapefile) with a file path that wlle be written down as a temporary file
     * (or multiple files, because one shapefile contains multiple files)
     * @param identifier identifier of the data
     * @return shapefile command line argument
     */
    public static IIdentifierWithBinding createCommandLineArgumentShapeFile(
            final String identifier) {

        final String filename = createUUIDFilename("inputfile", ".shp");

        return new IdentifierWithBindingImpl.Builder(identifier, GTVectorDataBinding.class)
                .withFunctionToTransformToCmd(new FileToStringCmd(filename))
                .withPath(filename)
                .withFunctionToWriteToFiles(new WriteShapeFileToPath())
                .build();
    }


    /**
     * Creates a command line argument (generic file) with a file path that will be written down as a
     * temporary file
     * @param identifier identifier of the data
     * @return file command line argument
     */
    public static IIdentifierWithBinding createCommandLineArgumentFile(
            final String identifier) {
        final String filename = createUUIDFilename("inputfile", ".dat");

        return new IdentifierWithBindingImpl.Builder(identifier, GenericFileDataBinding.class)
                .withFunctionToTransformToCmd(new FileToStringCmd(filename))
                .withFunctionToWriteToFiles(new WriteSingleByteStreamToPath((new ConvertGenericFileDataBindingToBytes())))
                .build();
    }

    /**
     * Creates a stdin input with a string
     * @param identifier identifier of the data
     * @return stdin input
     */
    public static IIdentifierWithBinding createStdinString(
            final String identifier) {
        return new IdentifierWithBindingImpl.Builder(identifier, LiteralStringBinding.class)
                .withFunctionToWriteToStdin(new ConvertLiteralStringToBytes())
                .build();
    }

    /**
     * Creates a stdin input with a string with a default value
     * @param identifier identifier of the data
     * @param defaultValue default value of the data
     * @return stdin input with a default value
     */
    public static IIdentifierWithBinding createStdinStringWithDefaultValue(
            final String identifier,
            final String defaultValue) {
        return new IdentifierWithBindingImpl.Builder(identifier, LiteralStringBinding.class)
                .withFunctionToWriteToStdin(new ConvertLiteralStringToBytes())
                .withDefaultValue(defaultValue)
                .build();
    }

    /**
     * Creates a input file argument (geotiff file)
     * @param identifier identifier of the data
     * @param path path of the file to write before starting the process
     * @return geotiff input file
     */
    public static IIdentifierWithBinding createFileInGeotiff(
            final String identifier,
            final String path) {
        return new IdentifierWithBindingImpl.Builder(identifier, GeotiffBinding.class)
                .withPath(path)
                .withFunctionToWriteToFiles(new WriteSingleByteStreamToPath(new ConvertGeotiffBindingToBytes()))
                .build();
    }

    /**
     * Creates a input file argument (geojson file)
     * @param identifier identifier of the data
     * @param path path of the file to write before staring the process
     * @return geojson input file
     */
    public static IIdentifierWithBinding createFileInGeojson(
            final String identifier,
            final String path) {
        return new IdentifierWithBindingImpl.Builder(identifier, GTVectorDataBinding.class)
                .withPath(path)
                .withFunctionToWriteToFiles(new WriteSingleByteStreamToPath(new ConvertGTVectorDataBindingToBytes(
                        ConvertGTVectorDataBindingToBytes.Format.JSON
                )))
                .build();
    }

    /**
     * Creates a input file argument (shapefile - with all the other files to care about)
     * @param identifier identifier of the data
     * @param path path of the file to write before starting the process (just the .shp file)
     * @return shapefile input file
     */
    public static IIdentifierWithBinding createFileInShapeFile(
            final String identifier,
            final String path) {
        return new IdentifierWithBindingImpl.Builder(identifier, GTVectorDataBinding.class)
                .withPath(path)
                .withFunctionToWriteToFiles(new WriteShapeFileToPath())
                .build();
    }

    /**
     * Creates a input file argument (generic)
     * @param identifier identifier of the data
     * @param path path of the file to write before staring the process
     * @return generic input file
     */
    public static IIdentifierWithBinding createFileInGeneric(
            final String identifier,
            final String path) {
        return new IdentifierWithBindingImpl.Builder(identifier, GenericFileDataBinding.class)
                .withPath(path)
                .withFunctionToWriteToFiles(new WriteSingleByteStreamToPath(new ConvertGenericFileDataBindingToBytes()))
                .build();
    }

    /**
     * Creates a xml file (output) on a given path with an additional schema
     * @param identifier identifier of the data
     * @param path path of the file to read after process termination
     * @param schema schema of the xml
     * @return output argument containing xml that will be read from a given file
     */
    public static IIdentifierWithBinding createFileOutXmlWithSchema(
            final String identifier,
            final String path,
            final String schema) {
        return new IdentifierWithBindingImpl.Builder(identifier, GenericXMLDataBinding.class)
                .withPath(path)
                .withFunctionToReadFromFiles(new ReadSingleByteStreamFromPath(new ConvertBytesToGenericXMLDataBinding()))
                .withSchema(schema)
                .build();
    }

    /**
     * creates a geotiff file (output) on a given path
     * @param identifier identifier of the data
     * @param path path of the file to read after process termination
     * @return output argument containing the geotiff data that will be read from a given file
     */
    public static IIdentifierWithBinding createFileOutGeotiff(
            final String identifier,
            final String path) {
        return new IdentifierWithBindingImpl.Builder(identifier, GeotiffBinding.class)
                .withPath(path)
                .withFunctionToReadFromFiles(new ReadSingleByteStreamFromPath(new ConvertBytesToGeotiffBinding()))
                .build();
    }

    /**
     * Creates a geojson file (output) on a given path
     * @param identifier identifier of the data
     * @param path path of the file to read after process termination
     * @return output argument containing the geojson data that will be read from a given file
     */
    public static IIdentifierWithBinding createFileOutGeojson(
            final String identifier,
            final String path) {
        return new IdentifierWithBindingImpl.Builder(identifier, GTVectorDataBinding.class)
                .withPath(path)
                .withFunctionToReadFromFiles(new ReadSingleByteStreamFromPath(new ConvertBytesToGTVectorDataBinding(
                        ConvertBytesToGTVectorDataBinding.Format.JSON
                )))
                .build();
    }

    /**
     * Creates a generic file (output) on a given path
     * @param identifier identifier of the data
     * @param path path of the file to read after process termination
     * @return output argument containing the data that will be read from a given file
     */
    public static IIdentifierWithBinding createFileOutGeneric(
            final String identifier,
            final String path) {
        return new IdentifierWithBindingImpl.Builder(identifier, GenericFileDataBinding.class)
                .withPath(path)
                .withFunctionToReadFromFiles(new ReadSingleByteStreamFromPath(new ConvertBytesToGenericFileDataBinding()))
                .build();
    }

    /**
     * Creates a shape file (output) on a given path
     * @param identifier identifier of the data
     * @param path path of the .shp file to read after process termination
     * @return output argument containing the data that will be read from the given files
     */
    public static IIdentifierWithBinding createFileOutShapeFile(
            final String identifier,
            final String path) {
        return new IdentifierWithBindingImpl.Builder(identifier, GTVectorDataBinding.class)
                .withPath(path)
                .withFunctionToReadFromFiles(new ReadShapeFileFromPath())
                .build();
    }

    /**
     * Creates a xml output (via stdout) with an additional schema
     * @param identifier identifier of the data
     * @param schema schema of the xml
     * @return output argument containing xml that will be read from stdout
     */
    public static IIdentifierWithBinding createStdoutXmlWithSchema(
            final String identifier,
            final String schema) {
        return new IdentifierWithBindingImpl.Builder(identifier, GenericXMLDataBinding.class)
                .withFunctionToHandleStdout(new ConvertBytesToGenericXMLDataBinding())
                .withSchema(schema)
                .build();
    }

    /**
     * Creates a string output (via stdout)
     * @param identifier identifier of the data
     * @return output argument containing the string that will be read from stdout
     */
    public static IIdentifierWithBinding createStdoutString(
            final String identifier) {
        return new IdentifierWithBindingImpl.Builder(identifier, LiteralStringBinding.class)
                .withFunctionToHandleStdout(new ConvertBytesToLiteralStringBinding())
                .build();
    }

    /**
     * Creates a string output (via stderr)
     * @param identifier identifier of the data
     * @return output argument containing the string that will be read from stderr
     */
    public static IIdentifierWithBinding createStderrString(
            final String identifier) {
        return new IdentifierWithBindingImpl.Builder(identifier, LiteralStringBinding.class)
                .withFunctionToHandleStderr(new ConvertBytesToLiteralStringBinding())
                .build();
    }

    /**
     * Creates a int output (via exit value)
     * @param identifier identifier of the data
     * @return output argument containing the integer that will be read from exit value
     */
    public static IIdentifierWithBinding createExitValueInt(
            final String identifier) {
        return new IdentifierWithBindingImpl.Builder(identifier, LiteralIntBinding.class)
                .withFunctionToHandleExitValue(new ConvertExitValueToLiteralIntBinding())
                .build();
    }

    /*
     * creates a unique filename
     */
    private static String createUUIDFilename(final String prefix, final String ending) {
        return prefix + UUID.randomUUID() + ending;
    }
}
