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
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToLiteralStringBinding;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.parse.json.subimpl.ParseJsonForOutputImpl;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
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
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }

    }
}
