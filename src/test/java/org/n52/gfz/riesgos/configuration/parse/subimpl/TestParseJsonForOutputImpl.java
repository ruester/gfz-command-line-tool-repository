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
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGTVectorDataBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGenericFileDataBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGenericXMLDataBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGeotiffBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToLiteralStringBinding;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.parse.json.subimpl.ParseJsonForOutputImpl;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.gfz.riesgos.exitvaluetoidataconverter.ConvertExitValueToLiteralIntBinding;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.gfz.riesgos.functioninterfaces.IReadIDataFromFiles;
import org.n52.gfz.riesgos.readidatafromfiles.ReadShapeFileFromPath;
import org.n52.gfz.riesgos.readidatafromfiles.ReadSingleByteStreamFromPath;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Test class for ParseJsonForOutputImpl
 */
public class TestParseJsonForOutputImpl {

    private JSONObject parseJson(final String text) {
        try {
            return (JSONObject) new JSONParser().parse(text);
        } catch(final ParseException parseException) {
            fail("There should be no exception on just parsing the json");
        }
        return null;
    }

    @Test
    public void testParseEmpty() {
        final String text = "{}";
        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        try {

            parser.parseOutput(parseJson(text));
            fail("There must be an exception");

        } catch(final ParseConfigurationException parseConfigException) {
            assertNotNull("There is a exception", parseConfigException);
        }
    }

    @Test
    public void testParseTitleOnly() {
        final String text = "{" +
                "\"title\": \"a\"" +
                "}";
        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        try {

            parser.parseOutput(parseJson(text));
            fail("There must be an exception");

        } catch(final ParseConfigurationException parseConfigException) {
            assertNotNull("There is a exception", parseConfigException);
        }
    }

    @Test
    public void testStdoutString() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"type\": \"string\"," +
                "\"readFrom\": \"stdout\"" +
                "}";

        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        final IConvertByteArrayToIData converter = new ConvertBytesToLiteralStringBinding();

        try {
            final IIdentifierWithBinding outputIdentifier = parser.parseOutput(parseJson(text));
            assertEquals("the identifier is the title", "a", outputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralStringBinding", LiteralStringBinding.class, outputIdentifier.getBindingClass());
            assertTrue("There is a function to read from stdout", outputIdentifier.getFunctionToHandleStdout().isPresent());
            assertEquals("It is a function to read bytes to string", converter, outputIdentifier.getFunctionToHandleStdout().get());
            assertFalse("There is no function to read from stderr", outputIdentifier.getFunctionToHandleStderr().isPresent());
            assertFalse("There is no function to read from exit value", outputIdentifier.getFunctionToHandleExitValue().isPresent());
            assertFalse("There is no function to read from files", outputIdentifier.getFunctionToReadIDataFromFiles().isPresent());
            assertFalse("There is no path", outputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", outputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", outputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", outputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", outputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", outputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is a function to convert it to a cmd argument", outputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertFalse("There is no default value", outputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }

    }

    @Test
    public void testStderrString() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"type\": \"string\"," +
                "\"readFrom\": \"stderr\"" +
                "}";

        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        final IConvertByteArrayToIData converter = new ConvertBytesToLiteralStringBinding();

        try {
            final IIdentifierWithBinding outputIdentifier = parser.parseOutput(parseJson(text));
            assertEquals("the identifier is the title", "a", outputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralStringBinding", LiteralStringBinding.class, outputIdentifier.getBindingClass());
            assertTrue("There is a function to read from stderr", outputIdentifier.getFunctionToHandleStderr().isPresent());
            assertEquals("It is a function to read bytes to string", converter, outputIdentifier.getFunctionToHandleStderr().get());
            assertFalse("There is no function to read from stdout", outputIdentifier.getFunctionToHandleStdout().isPresent());
            assertFalse("There is no function to read from exit value", outputIdentifier.getFunctionToHandleExitValue().isPresent());
            assertFalse("There is no function to read from files", outputIdentifier.getFunctionToReadIDataFromFiles().isPresent());
            assertFalse("There is no path", outputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", outputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", outputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", outputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", outputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", outputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is a function to convert it to a cmd argument", outputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertFalse("There is no default value", outputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    @Test
    public void testExitValueInt() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"type\": \"int\"," +
                "\"readFrom\": \"exitValue\"" +
                "}";

        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        final IConvertExitValueToIData converter = new ConvertExitValueToLiteralIntBinding();

        try {
            final IIdentifierWithBinding outputIdentifier = parser.parseOutput(parseJson(text));
            assertEquals("the identifier is the title", "a", outputIdentifier.getIdentifier());
            assertEquals("It uses a LiteralIntBinding", LiteralIntBinding.class, outputIdentifier.getBindingClass());
            assertTrue("There is a function to read from exit value", outputIdentifier.getFunctionToHandleExitValue().isPresent());
            assertEquals("It is a function to read bytes to exit value", converter, outputIdentifier.getFunctionToHandleExitValue().get());
            assertFalse("There is no function to read from stdout", outputIdentifier.getFunctionToHandleStdout().isPresent());
            assertFalse("There is no function to read from stderr", outputIdentifier.getFunctionToHandleStderr().isPresent());
            assertFalse("There is no function to read from files", outputIdentifier.getFunctionToReadIDataFromFiles().isPresent());
            assertFalse("There is no path", outputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", outputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", outputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", outputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", outputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", outputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is a function to convert it to a cmd argument", outputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertFalse("There is no default value", outputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    @Test
    public void testStdoutXmlWithoutSchema() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"type\": \"xml\"," +
                "\"readFrom\": \"stdout\"" +
                "}";

        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        final IConvertByteArrayToIData converter = new ConvertBytesToGenericXMLDataBinding();

        try {
            final IIdentifierWithBinding outputIdentifier = parser.parseOutput(parseJson(text));
            assertEquals("the identifier is the title", "a", outputIdentifier.getIdentifier());
            assertEquals("It uses a GenericXMLDataBinding", GenericXMLDataBinding.class, outputIdentifier.getBindingClass());
            assertTrue("There is a function to read from stdout", outputIdentifier.getFunctionToHandleStdout().isPresent());
            assertEquals("It is a function to read bytes to xml", converter, outputIdentifier.getFunctionToHandleStdout().get());
            assertFalse("There is no function to read from stderr", outputIdentifier.getFunctionToHandleStderr().isPresent());
            assertFalse("There is no function to read from exit value", outputIdentifier.getFunctionToHandleExitValue().isPresent());
            assertFalse("There is no function to read from files", outputIdentifier.getFunctionToReadIDataFromFiles().isPresent());
            assertFalse("There is no path", outputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertFalse("There is no schema", outputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", outputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", outputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", outputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", outputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is a function to convert it to a cmd argument", outputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertFalse("There is no default value", outputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    @Test
    public void testStdoutXmlWithSchema() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"type\": \"xml\"," +
                "\"schema\": \"abc\"," +
                "\"readFrom\": \"stdout\"" +
                "}";

        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        final IConvertByteArrayToIData converter = new ConvertBytesToGenericXMLDataBinding();

        try {
            final IIdentifierWithBinding outputIdentifier = parser.parseOutput(parseJson(text));
            assertEquals("the identifier is the title", "a", outputIdentifier.getIdentifier());
            assertEquals("It uses a GenericXMLDataBinding", GenericXMLDataBinding.class, outputIdentifier.getBindingClass());
            assertTrue("There is a function to read from stdout", outputIdentifier.getFunctionToHandleStdout().isPresent());
            assertEquals("It is a function to read bytes to xml", converter, outputIdentifier.getFunctionToHandleStdout().get());
            assertFalse("There is no function to read from stderr", outputIdentifier.getFunctionToHandleStderr().isPresent());
            assertFalse("There is no function to read from exit value", outputIdentifier.getFunctionToHandleExitValue().isPresent());
            assertFalse("There is no function to read from files", outputIdentifier.getFunctionToReadIDataFromFiles().isPresent());
            assertFalse("There is no path", outputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertTrue("There is a schema", outputIdentifier.getSchema().isPresent());
            assertEquals("The schema is abc", "abc", outputIdentifier.getSchema().get());
            assertFalse("There is no validator", outputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", outputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", outputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", outputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is a function to convert it to a cmd argument", outputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertFalse("There is no default value", outputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }

    }

    @Test
    public void testFileOutXmlWithoutSchema() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"type\": \"xml\"," +
                "\"path\": \"test.xml\"," +
                "\"readFrom\": \"file\"" +
                "}";

        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        final IReadIDataFromFiles reader = new ReadSingleByteStreamFromPath(new ConvertBytesToGenericXMLDataBinding());

        try {
            final IIdentifierWithBinding outputIdentifier = parser.parseOutput(parseJson(text));
            assertEquals("the identifier is the title", "a", outputIdentifier.getIdentifier());
            assertEquals("It uses a GenericXMLDataBinding", GenericXMLDataBinding.class, outputIdentifier.getBindingClass());
            assertFalse("There is no function to read from stdout", outputIdentifier.getFunctionToHandleStdout().isPresent());
            assertFalse("There is no function to read from stderr", outputIdentifier.getFunctionToHandleStderr().isPresent());
            assertFalse("There is no function to read from exit value", outputIdentifier.getFunctionToHandleExitValue().isPresent());
            assertTrue("There is a function to read from files", outputIdentifier.getFunctionToReadIDataFromFiles().isPresent());
            assertEquals("The function is to read xml", reader, outputIdentifier.getFunctionToReadIDataFromFiles().get());
            assertTrue("There is a path", outputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertEquals("The path is as expected", "test.xml", outputIdentifier.getPathToWriteToOrReadFromFile().get());
            assertFalse("There is no schema", outputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", outputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", outputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", outputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", outputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is a function to convert it to a cmd argument", outputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertFalse("There is no default value", outputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }

    }

    @Test
    public void testFileOutXmlWithSchema() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"type\": \"xml\"," +
                "\"path\": \"test.xml\"," +
                "\"schema\": \"abc\", " +
                "\"readFrom\": \"file\"" +
                "}";

        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        final IReadIDataFromFiles reader = new ReadSingleByteStreamFromPath(new ConvertBytesToGenericXMLDataBinding());

        try {
            final IIdentifierWithBinding outputIdentifier = parser.parseOutput(parseJson(text));
            assertEquals("the identifier is the title", "a", outputIdentifier.getIdentifier());
            assertEquals("It uses a GenericXMLDataBinding", GenericXMLDataBinding.class, outputIdentifier.getBindingClass());
            assertFalse("There is no function to read from stdout", outputIdentifier.getFunctionToHandleStdout().isPresent());
            assertFalse("There is no function to read from stderr", outputIdentifier.getFunctionToHandleStderr().isPresent());
            assertFalse("There is no function to read from exit value", outputIdentifier.getFunctionToHandleExitValue().isPresent());
            assertTrue("There is a function to read from files", outputIdentifier.getFunctionToReadIDataFromFiles().isPresent());
            assertEquals("The function is to read xml", reader, outputIdentifier.getFunctionToReadIDataFromFiles().get());
            assertTrue("There is a path", outputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertEquals("The path is as expected", "test.xml", outputIdentifier.getPathToWriteToOrReadFromFile().get());
            assertTrue("There is a schema", outputIdentifier.getSchema().isPresent());
            assertEquals("The schema is abc", "abc", outputIdentifier.getSchema().get());
            assertFalse("There is no validator", outputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", outputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", outputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", outputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is a function to convert it to a cmd argument", outputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertFalse("There is no default value", outputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    @Test
    public void testFileOutShapefile() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"type\": \"shapefile\"," +
                "\"path\": \"test.shp\"," +
                "\"readFrom\": \"file\"" +
                "}";

        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        final IReadIDataFromFiles reader = new ReadShapeFileFromPath();

        try {
            final IIdentifierWithBinding outputIdentifier = parser.parseOutput(parseJson(text));
            assertEquals("the identifier is the title", "a", outputIdentifier.getIdentifier());
            assertEquals("It uses a GTVectorDataBinding", GTVectorDataBinding.class, outputIdentifier.getBindingClass());
            assertFalse("There is no function to read from stdout", outputIdentifier.getFunctionToHandleStdout().isPresent());
            assertFalse("There is no function to read from stderr", outputIdentifier.getFunctionToHandleStderr().isPresent());
            assertFalse("There is no function to read from exit value", outputIdentifier.getFunctionToHandleExitValue().isPresent());
            assertTrue("There is a function to read from files", outputIdentifier.getFunctionToReadIDataFromFiles().isPresent());
            assertEquals("The function is to read from shapefiles", reader, outputIdentifier.getFunctionToReadIDataFromFiles().get());
            assertTrue("There is a path", outputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertEquals("The path is as expected", "test.shp", outputIdentifier.getPathToWriteToOrReadFromFile().get());
            assertFalse("There is no schema", outputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", outputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", outputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", outputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", outputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is a function to convert it to a cmd argument", outputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertFalse("There is no default value", outputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    @Test
    public void testFileOutGeneric() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"type\": \"file\"," +
                "\"path\": \"test.txt\"," +
                "\"readFrom\": \"file\"" +
                "}";

        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        final IReadIDataFromFiles reader = new ReadSingleByteStreamFromPath(new ConvertBytesToGenericFileDataBinding());

        try {
            final IIdentifierWithBinding outputIdentifier = parser.parseOutput(parseJson(text));
            assertEquals("the identifier is the title", "a", outputIdentifier.getIdentifier());
            assertEquals("It uses a GenericFileDataBinding", GenericFileDataBinding.class, outputIdentifier.getBindingClass());
            assertFalse("There is no function to read from stdout", outputIdentifier.getFunctionToHandleStdout().isPresent());
            assertFalse("There is no function to read from stderr", outputIdentifier.getFunctionToHandleStderr().isPresent());
            assertFalse("There is no function to read from exit value", outputIdentifier.getFunctionToHandleExitValue().isPresent());
            assertTrue("There is a function to read from files", outputIdentifier.getFunctionToReadIDataFromFiles().isPresent());
            assertEquals("The function is to read from generic files", reader, outputIdentifier.getFunctionToReadIDataFromFiles().get());
            assertTrue("There is a path", outputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertEquals("The path is as expected", "test.txt", outputIdentifier.getPathToWriteToOrReadFromFile().get());
            assertFalse("There is no schema", outputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", outputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", outputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", outputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", outputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is a function to convert it to a cmd argument", outputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertFalse("There is no default value", outputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    @Test
    public void testFileOutGeojson() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"type\": \"geojson\"," +
                "\"path\": \"test.json\"," +
                "\"readFrom\": \"file\"" +
                "}";

        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        final IReadIDataFromFiles reader = new ReadSingleByteStreamFromPath(new ConvertBytesToGTVectorDataBinding(ConvertBytesToGTVectorDataBinding.Format.JSON));

        try {
            final IIdentifierWithBinding outputIdentifier = parser.parseOutput(parseJson(text));
            assertEquals("the identifier is the title", "a", outputIdentifier.getIdentifier());
            assertEquals("It uses a GTVectorDataBinding", GTVectorDataBinding.class, outputIdentifier.getBindingClass());
            assertFalse("There is no function to read from stdout", outputIdentifier.getFunctionToHandleStdout().isPresent());
            assertFalse("There is no function to read from stderr", outputIdentifier.getFunctionToHandleStderr().isPresent());
            assertFalse("There is no function to read from exit value", outputIdentifier.getFunctionToHandleExitValue().isPresent());
            assertTrue("There is a function to read from files", outputIdentifier.getFunctionToReadIDataFromFiles().isPresent());
            assertEquals("The function is to read from json files", reader, outputIdentifier.getFunctionToReadIDataFromFiles().get());
            assertTrue("There is a path", outputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertEquals("The path is as expected", "test.json", outputIdentifier.getPathToWriteToOrReadFromFile().get());
            assertFalse("There is no schema", outputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", outputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", outputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", outputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", outputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is a function to convert it to a cmd argument", outputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertFalse("There is no default value", outputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    @Test
    public void testFileOutGeotiff() {
        final String text = "{" +
                "\"title\": \"a\"," +
                "\"type\": \"geotiff\"," +
                "\"path\": \"test.tiff\"," +
                "\"readFrom\": \"file\"" +
                "}";

        final ParseJsonForOutputImpl parser = new ParseJsonForOutputImpl();
        final IReadIDataFromFiles reader = new ReadSingleByteStreamFromPath(new ConvertBytesToGeotiffBinding());

        try {
            final IIdentifierWithBinding outputIdentifier = parser.parseOutput(parseJson(text));
            assertEquals("the identifier is the title", "a", outputIdentifier.getIdentifier());
            assertEquals("It uses a GeotiffBinding", GeotiffBinding.class, outputIdentifier.getBindingClass());
            assertFalse("There is no function to read from stdout", outputIdentifier.getFunctionToHandleStdout().isPresent());
            assertFalse("There is no function to read from stderr", outputIdentifier.getFunctionToHandleStderr().isPresent());
            assertFalse("There is no function to read from exit value", outputIdentifier.getFunctionToHandleExitValue().isPresent());
            assertTrue("There is a function to read from files", outputIdentifier.getFunctionToReadIDataFromFiles().isPresent());
            assertEquals("The function is to read from json files", reader, outputIdentifier.getFunctionToReadIDataFromFiles().get());
            assertTrue("There is a path", outputIdentifier.getPathToWriteToOrReadFromFile().isPresent());
            assertEquals("The path is as expected", "test.tiff", outputIdentifier.getPathToWriteToOrReadFromFile().get());
            assertFalse("There is no schema", outputIdentifier.getSchema().isPresent());
            assertFalse("There is no validator", outputIdentifier.getValidator().isPresent());
            assertFalse("There are no supported crs for bbox", outputIdentifier.getSupportedCRSForBBox().isPresent());
            assertFalse("There is no function to write the data to files", outputIdentifier.getFunctionToWriteIDataToFiles().isPresent());
            assertFalse("There is no function to write the data to stdin", outputIdentifier.getFunctionToWriteToStdin().isPresent());
            assertFalse("There is a function to convert it to a cmd argument", outputIdentifier.getFunctionToTransformToCmd().isPresent());
            assertFalse("There is no default value", outputIdentifier.getDefaultValue().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }
}
