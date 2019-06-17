package org.n52.gfz.riesgos.configuration.parse.subimpl;

/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 *
 *
 */

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.n52.gfz.riesgos.commandlineparametertransformer.BoundingBoxDataToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.FileToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralBooleanBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralDoubleBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralIntBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralStringBindingToStringCmd;
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.parse.json.subimpl.ParseJsonForInputImpl;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IWriteIDataToFiles;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGTVectorDataBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGenericFileDataBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGenericXMLDataBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGeotiffBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertLiteralStringToBytes;
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

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Test class for ParseJsonForInputImpl
 */
public class TestParseJsonForInputImpl {

    private JSONObject parseJson(final String text) {
        try {
            return (JSONObject) new JSONParser().parse(text);
        } catch(final ParseException parseException) {
            fail("There should be no exception on just parsing the json");
        }
        return null;
    }

    /**
     * Test with some empty js-object
     */
    @Test
    public void testParseEmpty() {
        final String text = "{}";
        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        try {

            parser.parseInput(parseJson(text));
            fail("There must be an exception");

        } catch(final ParseConfigurationException parseConfigException) {
            assertNotNull("There is a exception", parseConfigException);
        }
    }

    /**
     * test with just a title which is too less information
     */
    @Test
    public void testParseTitleOnly() {
        final String text = "{" +
                "\"title\": \"a\"" +
                "}";
        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        try {

            parser.parseInput(parseJson(text));
            fail("There must be an exception");

        } catch(final ParseConfigurationException parseConfigException) {
            assertNotNull("There is a exception", parseConfigException);
        }
    }

    /**
     * test with an int (without any default value)
     */
    @Test
    public void parseCommandLineArgumentIntWithoutDefaultValue() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"int\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IConvertIDataToCommandLineParameter converter = new LiteralIntBindingToStringCmd();

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralIntBinding", LiteralIntBinding.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the int as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with an int with a default value
     */
    @Test
    public void parseCommandLineArgumentIntWithDefaultValue() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"default\": \"1\"," +
                "\"type\": \"int\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IConvertIDataToCommandLineParameter converter = new LiteralIntBindingToStringCmd();

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralIntBinding", LiteralIntBinding.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the int as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertTrue("There is a default value", inputIdentifier.getDefaultValue().isPresent());
            assertEquals("The default value is 1", "1", inputIdentifier.getDefaultValue().get());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * test with an int and a default command line flag
     */
    @Test
    public void parseCommandLineArgumentIntWithCommandLineFlag() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"commandLineFlag\": \"--a\"," +
                "\"type\": \"int\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IConvertIDataToCommandLineParameter converter = new LiteralIntBindingToStringCmd("--a");

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralIntBinding", LiteralIntBinding.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the int as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * test with a double without a default value
     */
    @Test
    public void parseCommandLineArgumentDoubleWithoutDefaultValue() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"double\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IConvertIDataToCommandLineParameter converter = new LiteralDoubleBindingToStringCmd();

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralDoubleBinding", LiteralDoubleBinding.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the double as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * test with a double with a default value
     */
    @Test
    public void parseCommandLineArgumentDoubleWithDefaultValue() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"default\": \"1.0\"," +
                "\"type\": \"double\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IConvertIDataToCommandLineParameter converter = new LiteralDoubleBindingToStringCmd();

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralDoubleBinding", LiteralDoubleBinding.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the double as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertTrue("There is a default value", inputIdentifier.getDefaultValue().isPresent());
            assertEquals("The default value is 1.0", "1.0", inputIdentifier.getDefaultValue().get());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * test with a double type with a command line flag
     */
    @Test
    public void parseCommandLineArgumentDoubleWithCommandLineFlag() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"commandLineFlag\": \"--a\"," +
                "\"type\": \"double\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IConvertIDataToCommandLineParameter converter = new LiteralDoubleBindingToStringCmd("--a");

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralDoubleBinding", LiteralDoubleBinding.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the double as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * test with a string without a default value
     */
    @Test
    public void parseCommandLineArgumentStringWithoutDefaultValue() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"string\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IConvertIDataToCommandLineParameter converter = new LiteralStringBindingToStringCmd();

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralStringBinding", LiteralStringBinding.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the string as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * test with a string with a default value
     */
    @Test
    public void parseCommandLineArgumentStringWithDefaultValue() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"default\": \"dummy\"," +
                "\"type\": \"string\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IConvertIDataToCommandLineParameter converter = new LiteralStringBindingToStringCmd();

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralStringBinding", LiteralStringBinding.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the string as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertTrue("There is a default value", inputIdentifier.getDefaultValue().isPresent());
            assertEquals("The default value is dummy", "dummy", inputIdentifier.getDefaultValue().get());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * test with a string with a command line flag
     */
    @Test
    public void parseCommandLineArgumentStringWithCommandLineFlag() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"commandLineFlag\": \"--a\"," +
                "\"type\": \"string\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IConvertIDataToCommandLineParameter converter = new LiteralStringBindingToStringCmd("--a");

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralStringBinding", LiteralStringBinding.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the string as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * test with a string with a default value and allowed values
     */
    @Test
    public void parseCommandLineArgumentStringWithDefaultValueAndAllowedValues() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"string\"," +
                "\"allowed\": [\"a\", \"b\", \"c\"]," +
                "\"default\": \"b\"" +
                "}";


        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IConvertIDataToCommandLineParameter converter = new LiteralStringBindingToStringCmd();

        final ICheckDataAndGetErrorMessage validator = new LiteralStringBindingWithAllowedValues(Arrays.asList("a", "b", "c"));

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralStringBinding", LiteralStringBinding.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertTrue("There is a validator", inputIdentifier.getValidator().isPresent());
            assertEquals("The validator is as expected", validator, inputIdentifier.getValidator().get());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the string as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertTrue("There is a default value", inputIdentifier.getDefaultValue().isPresent());
            assertEquals("The default value is c", "b", inputIdentifier.getDefaultValue().get());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * test with a boolean
     */
    @Test
    public void parseCommandLineArgumentBoolean() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"boolean\"," +
                "\"commandLineFlag\": \"--verbose\"," +
                "\"default\": \"true\"" +
                "}";


        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IConvertIDataToCommandLineParameter converter = new LiteralBooleanBindingToStringCmd("--verbose");

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralBoolean", LiteralBooleanBinding.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the boolean as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertTrue("There is a default value", inputIdentifier.getDefaultValue().isPresent());
            assertEquals("The default value is true", "true", inputIdentifier.getDefaultValue().get());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * test with a bounding box
     */
    @Test
    public void parseCommandLineArgumentBBox() {
        final String text = "{" +
                "\"title\" : \"a\"," +
                "\"useAs\": \"commandLineArgument\", " +
                "\"type\": \"bbox\", " +
                "\"crs\": [\"EPSG:4326\", \"EPSG:4328\"]" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IConvertIDataToCommandLineParameter converter = new BoundingBoxDataToStringCmd();

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a BoundingBoxData", BoundingBoxData.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertTrue("There are supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertEquals("The crs are as expected", Arrays.asList("EPSG:4326", "EPSG:4328"), inputIdentifier.getSupportedCRSForBBox().get());
            assertFalse("There is no function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the bbox as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with a xml file as command line argument with a schema
     */
    @Test
    public void parseCommandLineArgumentXmlFileWithSchema() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"xml\"," +
                "\"schema\": \"http://some-schema.org/path\" " +
                "}";
        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IWriteIDataToFiles writer = new WriteSingleByteStreamToPath(new ConvertGenericXMLDataBindingToBytes());

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a GenericXMLDataBinding", GenericXMLDataBinding.class, inputIdentifier.getBindingClass());
            assertTrue("There is a path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            final String path = inputIdentifier.getPathToWriteToOrReadFromFile().get();
            final IConvertIDataToCommandLineParameter converter = new FileToStringCmd(path);
            assertTrue("There is a schema", inputIdentifier.getSchema().isPresent());
            assertEquals("The schema is as expected", "http://some-schema.org/path", inputIdentifier.getSchema().get());
            assertTrue("There is a validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertTrue("There is a function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertEquals("The function to write the data to files is as expected", writer, inputIdentifier.getFunctionToWriteIDataToFiles().get());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the filename as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }


    /**
     * Test with a xml file as command line argument with a schema but with a flag
     */
    @Test
    public void parseCommandLineArgumentXmlFileWithSchemaWithCmdFlag() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"xml\"," +
                "\"commandLineFlag\": \"--xml\"," +
                "\"schema\": \"http://some-schema.org/path\" " +
                "}";
        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IWriteIDataToFiles writer = new WriteSingleByteStreamToPath(new ConvertGenericXMLDataBindingToBytes());

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a GenericXMLDataBinding", GenericXMLDataBinding.class, inputIdentifier.getBindingClass());
            assertTrue("There is a path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            final String path = inputIdentifier.getPathToWriteToOrReadFromFile().get();
            final IConvertIDataToCommandLineParameter converter = new FileToStringCmd(path, "--xml");
            assertTrue("There is a schema", inputIdentifier.getSchema().isPresent());
            assertEquals("The schema is as expected", "http://some-schema.org/path", inputIdentifier.getSchema().get());
            assertTrue("There is a validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertTrue("There is a function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertEquals("The function to write the data to files is as expected", writer, inputIdentifier.getFunctionToWriteIDataToFiles().get());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the filename as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with a xml file as command line argument without a schema
     */
    @Test
    public void parseCommandLineArgumentXmlFileWithoutSchema() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"xml\"," +
                "}";
        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IWriteIDataToFiles writer = new WriteSingleByteStreamToPath(new ConvertGenericXMLDataBindingToBytes());

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a GenericXMLDataBinding", GenericXMLDataBinding.class, inputIdentifier.getBindingClass());
            assertTrue("There is a path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            final String path = inputIdentifier.getPathToWriteToOrReadFromFile().get();
            final IConvertIDataToCommandLineParameter converter = new FileToStringCmd(path);
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertTrue("There is a function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertEquals("The function to write the data to files is as expected", writer, inputIdentifier.getFunctionToWriteIDataToFiles().get());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the filename as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with a xml file as command line argument without a schema but without the header but with a flag
     */
    @Test
    public void parseCommandLineArgumentXmlFileWithoutSchemaWithCmdFlag() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"xml\"," +
                "\"commandLineFlag\": \"--xml\"," +
                "}";
        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IWriteIDataToFiles writer = new WriteSingleByteStreamToPath(new ConvertGenericXMLDataBindingToBytes());

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a GenericXMLDataBinding", GenericXMLDataBinding.class, inputIdentifier.getBindingClass());
            assertTrue("There is a path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            final String path = inputIdentifier.getPathToWriteToOrReadFromFile().get();
            final IConvertIDataToCommandLineParameter converter = new FileToStringCmd(path, "--xml");
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertTrue("There is a function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertEquals("The function to write the data to files is as expected", writer, inputIdentifier.getFunctionToWriteIDataToFiles().get());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the filename as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with a geotiff file as command line argument
     */
    @Test
    public void parseCommandLineArgumentGeotiffFile() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"geotiff\"" +
                "}";
        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IWriteIDataToFiles writer = new WriteSingleByteStreamToPath(new ConvertGeotiffBindingToBytes());

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a GeotiffBinding", GeotiffBinding.class, inputIdentifier.getBindingClass());
            assertTrue("There is a path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            final String path = inputIdentifier.getPathToWriteToOrReadFromFile().get();
            final IConvertIDataToCommandLineParameter converter = new FileToStringCmd(path);
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertTrue("There is a function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertEquals("The function to write the data to files is as expected", writer, inputIdentifier.getFunctionToWriteIDataToFiles().get());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the filename as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with a geojson file as command line argument
     */
    @Test
    public void parseCommandLineArgumentGeojsonFile() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"geojson\"" +
                "}";
        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IWriteIDataToFiles writer = new WriteSingleByteStreamToPath(new ConvertGTVectorDataBindingToBytes(ConvertGTVectorDataBindingToBytes.Format.JSON));

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a GTVectorDataBinding", GTVectorDataBinding.class, inputIdentifier.getBindingClass());
            assertTrue("There is a path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            final String path = inputIdentifier.getPathToWriteToOrReadFromFile().get();
            final IConvertIDataToCommandLineParameter converter = new FileToStringCmd(path);
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertTrue("There is a function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertEquals("The function to write the data to files is as expected", writer, inputIdentifier.getFunctionToWriteIDataToFiles().get());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the filename as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with a shapefile as command line argument
     */
    @Test
    public void parseCommandLineArgumentShapefile() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"shapefile\"" +
                "}";
        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IWriteIDataToFiles writer = new WriteShapeFileToPath();

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a GTVectorDataBinding", GTVectorDataBinding.class, inputIdentifier.getBindingClass());
            assertTrue("There is a path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            final String path = inputIdentifier.getPathToWriteToOrReadFromFile().get();
            final IConvertIDataToCommandLineParameter converter = new FileToStringCmd(path);
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertTrue("There is a function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertEquals("The function to write the data to files is as expected", writer, inputIdentifier.getFunctionToWriteIDataToFiles().get());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the filename as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with a shapefile as command line argument
     */
    @Test
    public void parseCommandLineArgumentGenericFile() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"commandLineArgument\"," +
                "\"type\": \"file\"" +
                "}";
        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IWriteIDataToFiles writer = new WriteSingleByteStreamToPath(new ConvertGenericFileDataBindingToBytes());

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("the identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a GenericFileDataBinding", GenericFileDataBinding.class, inputIdentifier.getBindingClass());
            assertTrue("There is a path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            final String path = inputIdentifier.getPathToWriteToOrReadFromFile().get();
            final IConvertIDataToCommandLineParameter converter = new FileToStringCmd(path);
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertTrue("There is a function to write the data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertEquals("The function to write the data to files is as expected", writer, inputIdentifier.getFunctionToWriteIDataToFiles().get());
            assertFalse("There is no function to write the data to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertTrue("There is a function to convert it to a cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertEquals("The converter is to write the filename as a cmd argument", converter, inputIdentifier.getFunctionToTransformToCmd().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Tests a string as stdin
     */
    @Test
    public void parseStdinString() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"stdin\"," +
                "\"type\": \"string\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("The identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralStringBinding", LiteralStringBinding.class, inputIdentifier.getBindingClass());
            assertFalse("There is no path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
            assertTrue("There is a function to write to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertEquals("It is the function to write the string", new ConvertLiteralStringToBytes(), inputIdentifier.getFunctionToWriteToStdin().get());
            assertFalse("There is no function to convert it to cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Tests a file as geotiff
     */
    @Test
    public void parseFileInGeotiff() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"file\"," +
                "\"type\": \"geotiff\"," +
                "\"path\": \"test.tiff\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IWriteIDataToFiles writer = new WriteSingleByteStreamToPath(new ConvertGeotiffBindingToBytes());

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("The identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a GeotiffBinding", GeotiffBinding.class, inputIdentifier.getBindingClass());
            assertTrue("There is a path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertEquals("The path is as expected", "test.tiff", inputIdentifier.getPathToWriteToOrReadFromFile().get());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertTrue("There is a function to write data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertEquals("The function to write data to files is as expected", writer, inputIdentifier.getFunctionToWriteIDataToFiles().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
            assertFalse("There is no function to write to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is no function to convert it to cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Tests a file as geojson
     */
    @Test
    public void parseFileInGeojson() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"file\"," +
                "\"type\": \"geojson\"," +
                "\"path\": \"test.json\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IWriteIDataToFiles writer = new WriteSingleByteStreamToPath(new ConvertGTVectorDataBindingToBytes(ConvertGTVectorDataBindingToBytes.Format.JSON));

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("The identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a GTVectorDataBinding", GTVectorDataBinding.class, inputIdentifier.getBindingClass());
            assertTrue("There is a path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertEquals("The path is as expected", "test.json", inputIdentifier.getPathToWriteToOrReadFromFile().get());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertTrue("There is a function to write data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertEquals("The function to write data to files is as expected", writer, inputIdentifier.getFunctionToWriteIDataToFiles().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
            assertFalse("There is no function to write to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is no function to convert it to cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }


    /**
     * Tests a file as shapefile
     */
    @Test
    public void parseFileInShapefile() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"file\"," +
                "\"type\": \"shapefile\"," +
                "\"path\": \"test.shp\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IWriteIDataToFiles writer = new WriteShapeFileToPath();

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("The identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a GTVectorDataBinding", GTVectorDataBinding.class, inputIdentifier.getBindingClass());
            assertTrue("There is a path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertEquals("The path is as expected", "test.shp", inputIdentifier.getPathToWriteToOrReadFromFile().get());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertTrue("There is a function to write data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertEquals("The function to write data to files is as expected", writer, inputIdentifier.getFunctionToWriteIDataToFiles().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
            assertFalse("There is no function to write to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is no function to convert it to cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Tests a file as generic file
     */
    @Test
    public void parseFileInGenericFile() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"useAs\": \"file\"," +
                "\"type\": \"file\"," +
                "\"path\": \"test.txt\"" +
                "}";

        final ParseJsonForInputImpl parser = new ParseJsonForInputImpl();
        final IWriteIDataToFiles writer = new WriteSingleByteStreamToPath(new ConvertGenericFileDataBindingToBytes());

        try {
            final IInputParameter inputIdentifier = parser.parseInput(parseJson(text));
            assertEquals("The identifier is the title", "a", inputIdentifier.getIdentifier());
            assertEquals("It uses a GenericFileDataBinding", GenericFileDataBinding.class, inputIdentifier.getBindingClass());
            assertTrue("There is a path", inputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertEquals("The path is as expected", "test.txt", inputIdentifier.getPathToWriteToOrReadFromFile().get());
            assertFalse("There is no schema", inputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", inputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", inputIdentifier.getSupportedCRSForBBox().isPresent());
            assertTrue("There is a function to write data to files", inputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertEquals("The function to write data to files is as expected", writer, inputIdentifier.getFunctionToWriteIDataToFiles().get());
            assertFalse("There is no default value", inputIdentifier.getDefaultValue().isPresent());
            assertFalse("There is no function to write to stdin", inputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is no function to convert it to cmd argument", inputIdentifier.getFunctionToTransformToCmd().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

}
