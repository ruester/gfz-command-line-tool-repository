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

import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGenericXMLDataBinding;
import org.n52.gfz.riesgos.commandlineparametertransformer.BoundingBoxDataToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.FileToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralBooleanBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralDoubleBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralIntBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralStringBindingToStringCmd;
import org.n52.gfz.riesgos.configuration.impl.IdentifierWithBindingImpl;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGenericXMLDataBindingToBytes;
import org.n52.gfz.riesgos.validators.LiteralStringBindingWithAllowedValues;
import org.n52.wps.io.data.binding.bbox.BoundingBoxData;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
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

    public static IIdentifierWithBinding createCommandLineArgumentXmlFileWithSchema(
            final String identifier, final String schema) {

        final String filename = "inputfile" + UUID.randomUUID() + ".xml";

        return new IdentifierWithBindingImpl.Builder(identifier, GenericXMLDataBinding.class)
                .withFunctionToTransformToCmd(new FileToStringCmd(filename))
                .withPath(filename)
                .withFunctionToGetBytesToWrite(new ConvertGenericXMLDataBindingToBytes())
                .withSchema(schema)
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
}
