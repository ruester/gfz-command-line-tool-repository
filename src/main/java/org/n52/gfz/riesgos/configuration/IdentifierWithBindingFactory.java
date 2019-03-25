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

import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesAsGeojsonToGTVectorDataBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGenericXMLDataBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGeotiffBinding;
import org.n52.gfz.riesgos.commandlineparametertransformer.BoundingBoxDataToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.FileToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralBooleanBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralDoubleBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralIntBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralStringBindingToStringCmd;
import org.n52.gfz.riesgos.configuration.impl.IdentifierWithBindingImpl;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGTVectorDataBindingAsGeojsonToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGenericXMLDataBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGenericXMLDataBindingToBytesWithoutHeader;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGeotiffBindingToBytes;
import org.n52.gfz.riesgos.validators.LiteralStringBindingWithAllowedValues;
import org.n52.wps.io.data.binding.bbox.BoundingBoxData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
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
     * Creates a command line argument (input) which contains a int with a default value
     * @param identifier identifier of the data
     * @param defaultValue default value of the data
     * @return Command line argument that contains an int with a default value
     */
    public static IIdentifierWithBinding createCommandLineArgumentIntWithDefaultValue(
            final String identifier, final int defaultValue) {
        return new IdentifierWithBindingImpl.Builder(identifier, LiteralIntBinding.class)
                .withFunctionToTransformToCmd(new LiteralIntBindingToStringCmd())
                .withDefaultValue(String.valueOf(defaultValue))
                .build();
    }

    /**
     * Creates a command line argument (input) which contains a double with a default value
     * @param identifier identifier of the data
     * @param defaultValue default value of the data
     * @return Command line argument that contains a double with a default value
     */
    public static IIdentifierWithBinding createCommandLineArgumentDoubleWithDefaultValue(
            final String identifier, final double defaultValue) {
        return new IdentifierWithBindingImpl.Builder(identifier, LiteralDoubleBinding.class)
                .withFunctionToTransformToCmd(new LiteralDoubleBindingToStringCmd())
                .withDefaultValue(String.valueOf(defaultValue))
                .build();
    }

    /**
     * Creates a command line argument (input) which contains a string with a default value
     * and a set of allowed values
     * @param identifier identifier of the data
     * @param defaultValue default value of the data
     * @param allowedValues allowed values of the dat
     * @return Command line argument that contains a string with a default value and some allowed values
     */
    public static IIdentifierWithBinding createCommandLineArgumentStringWithDefaultValueAndAllowedValues(
            final String identifier, final String defaultValue, final List<String> allowedValues) {
        return new IdentifierWithBindingImpl.Builder(identifier, LiteralStringBinding.class)
                .withValidator(new LiteralStringBindingWithAllowedValues(allowedValues))
                .withFunctionToTransformToCmd(new LiteralStringBindingToStringCmd())
                .withDefaultValue(defaultValue)
                .withAllowedValues(allowedValues)
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
                .withFunctionToGetBytesToWrite(new ConvertGenericXMLDataBindingToBytes())
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
                .withFunctionToGetBytesToWrite(new ConvertGenericXMLDataBindingToBytesWithoutHeader())
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
                .withFunctionToGetBytesToWrite(new ConvertGeotiffBindingToBytes())
                .build();
    }

    /**
     * Creates a command line argument (geojson) with a file path that will be written down as a
     * temporary file
     * @param identifier identifier of the data
     * @return geojson fiel command line argument
     */
    public static IIdentifierWithBinding createCommandLineArgumentGeojson(
            final String identifier) {
        final String filename = createUUIDFilename("inputfile", ".json");

        return new IdentifierWithBindingImpl.Builder(identifier, GTVectorDataBinding.class)
                .withFunctionToTransformToCmd(new FileToStringCmd(filename))
                .withPath(filename)
                .withFunctionToGetBytesToWrite(new ConvertGTVectorDataBindingAsGeojsonToBytes())
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
                .withFunctionToGetBytesToWrite(new ConvertGeotiffBindingToBytes())
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
                .withFunctionToGetBytesToWrite(new ConvertGTVectorDataBindingAsGeojsonToBytes())
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
                .withFunctionToReadFromBytes(new ConvertBytesToGenericXMLDataBinding())
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
                .withFunctionToReadFromBytes(new ConvertBytesToGeotiffBinding())
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
                .withFunctionToReadFromBytes(new ConvertBytesAsGeojsonToGTVectorDataBinding())
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

    /*
     * creates a unique filename
     */
    private static String createUUIDFilename(final String prefix, final String ending) {
        return prefix + UUID.randomUUID() + ending;
    }
}
