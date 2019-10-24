package org.n52.gfz.riesgos.configuration.parse;


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
 */

import org.junit.Test;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.configuration.InputParameterFactory;
import org.n52.gfz.riesgos.configuration.OutputParameterFactory;
import org.n52.gfz.riesgos.configuration.parse.formats.json.ParseJsonConfigurationImpl;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.gfz.riesgos.exitvaluehandler.LogExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.stderrhandler.LogStderrHandler;
import org.n52.gfz.riesgos.stderrhandler.PythonTracebackStderrHandler;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Testcases for parsing of json configuration
 */
public class TestParseJsonConfigurationImpl {

    /**
     * Test with null input
     */
    @Test
    public void testNull() {
        final IParseConfiguration parser = new ParseJsonConfigurationImpl();

        try {
            parser.parse(null);
            fail("There must be an exception");
        } catch(final ParseConfigurationException exception) {
            assertNotNull("There must be an exception", exception);
        }
    }

    /**
     * Test with nonvalid json input
     */
    @Test
    public void testNonValidJson() {
        final IParseConfiguration parser = new ParseJsonConfigurationImpl();

        try {
            parser.parse("{[}");
            fail("There must be an exception");
        } catch(final ParseConfigurationException exception) {
            assertNotNull("There must be an exception", exception);
        }
    }

    /**
     * Test with a very simple configuration with no input and output and no
     * additional handlers
     */
    @Test
    public void testValidInputVerySimple() {
        final IParseConfiguration parser = new ParseJsonConfigurationImpl();

        final String text = "{" +
                "\"title\": \"Quakeledger\"," +
                "\"imageId\": \"123456\"," +
                "\"workingDirectory\": \"/usr/share/git/quakeledger\"," +
                "\"commandToExecute\": \"python3 eventquery.py\"" +
                "}";


        try {
            final IConfiguration conf = parser.parse(text);
            assertEquals("The title is as expected", "Quakeledger", conf.getIdentifier());
            assertEquals("The imageId is as expected", "123456", conf.getImageId());
            assertEquals("The workingDirectory is as expected", "/usr/share/git/quakeledger", conf.getWorkingDirectory());
            assertEquals("The commandToExecute is as expected", Arrays.asList("python3", "eventquery.py"), conf.getCommandToExecute());
            assertTrue("There are no default command line flags", conf.getDefaultCommandLineFlags().isEmpty());
            assertFalse("There is no stderr handler present", conf.getStderrHandler().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with a very simple configuration with no input and output and no
     * additional handlers, but with default command line flags
     */
    @Test
    public void testValidInputVerySimpleWithDefaultFlags() {
        final IParseConfiguration parser = new ParseJsonConfigurationImpl();

        final String text = "{" +
                "\"title\": \"Quakeledger\"," +
                "\"imageId\": \"123456\"," +
                "\"workingDirectory\": \"/usr/share/git/quakeledger\"," +
                "\"commandToExecute\": \"python3 eventquery.py\"," +
                "\"defaultCommandLineFlags\": [\"--verbose\", \"--recursive\"]" +
                "}";


        try {
            final IConfiguration conf = parser.parse(text);
            assertEquals("The title is as expected", "Quakeledger", conf.getIdentifier());
            assertEquals("The imageId is as expected", "123456", conf.getImageId());
            assertEquals("The workingDirectory is as expected", "/usr/share/git/quakeledger", conf.getWorkingDirectory());
            assertEquals("The commandToExecute is as expected", Arrays.asList("python3", "eventquery.py"), conf.getCommandToExecute());
            assertEquals("The default command line flags are as expected", Arrays.asList("--verbose", "--recursive"), conf.getDefaultCommandLineFlags());
            assertFalse("There is no stderr handler present", conf.getStderrHandler().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with a very simple configuration with no input and output but with
     * an logging stderr handler
     */
    @Test
    public void testValidInputSimpleWithStderrHandler() {
        final IParseConfiguration parser = new ParseJsonConfigurationImpl();

        final IStderrHandler stderrHandler = new LogStderrHandler();

        final String text = "{" +
                "\"title\": \"Quakeledger\"," +
                "\"imageId\": \"123456\"," +
                "\"workingDirectory\": \"/usr/share/git/quakeledger\"," +
                "\"commandToExecute\": \"python3 eventquery.py\"," +
                "\"stderrHandler\": \"logging\"" +
                "}";


        try {
            final IConfiguration conf = parser.parse(text);
            assertEquals("The title is as expected", "Quakeledger", conf.getIdentifier());
            assertEquals("The imageId is as expected", "123456", conf.getImageId());
            assertEquals("The workingDirectory is as expected", "/usr/share/git/quakeledger", conf.getWorkingDirectory());
            assertEquals("The commandToExecute is as expected", Arrays.asList("python3", "eventquery.py"), conf.getCommandToExecute());
            assertTrue("There are no default command line flags", conf.getDefaultCommandLineFlags().isEmpty());
            assertTrue("There is a stderr handler present", conf.getStderrHandler().isPresent());
            assertEquals("And it is a log handler", stderrHandler, conf.getStderrHandler().get());
            assertFalse("There is no exit value handler", conf.getExitValueHandler().isPresent());
            assertFalse("There is no stdout handler", conf.getStdoutHandler().isPresent());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with a very simple configuration with no input and output but with
     * an logging exit value handler
     */
    @Test
    public void testValidInputSimpleWithExitValueHandler() {
        final IParseConfiguration parser = new ParseJsonConfigurationImpl();

        final IExitValueHandler exitValueHandler = new LogExitValueHandler();

        final String text = "{" +
                "\"title\": \"Quakeledger\"," +
                "\"imageId\": \"123456\"," +
                "\"workingDirectory\": \"/usr/share/git/quakeledger\"," +
                "\"commandToExecute\": \"python3 eventquery.py\"," +
                "\"exitValueHandler\": \"logging\"" +
                "}";


        try {
            final IConfiguration conf = parser.parse(text);
            assertEquals("The title is as expected", "Quakeledger", conf.getIdentifier());
            assertEquals("The imageId is as expected", "123456", conf.getImageId());
            assertEquals("The workingDirectory is as expected", "/usr/share/git/quakeledger", conf.getWorkingDirectory());
            assertEquals("The commandToExecute is as expected", Arrays.asList("python3", "eventquery.py"), conf.getCommandToExecute());
            assertTrue("There are no default command line flags", conf.getDefaultCommandLineFlags().isEmpty());
            assertTrue("There is a exit value handler present", conf.getExitValueHandler().isPresent());
            assertEquals("And it is a log handler", exitValueHandler, conf.getExitValueHandler().get());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with a very simple configuration with no input and output but with
     * an logging exit value handler and a python traceback stderr handler
     */
    @Test
    public void testValidInputSimpleWithPythonTracebackHandlerValueHandler() {
        final IParseConfiguration parser = new ParseJsonConfigurationImpl();

        final IExitValueHandler exitValueHandler = new LogExitValueHandler();
        final IStderrHandler stderrHandler = new PythonTracebackStderrHandler();

        final String text = "{" +
                "\"title\": \"Quakeledger\"," +
                "\"imageId\": \"123456\"," +
                "\"workingDirectory\": \"/usr/share/git/quakeledger\"," +
                "\"commandToExecute\": \"python3 eventquery.py\"," +
                "\"exitValueHandler\": \"logging\"," +
                "\"stderrHandler\": \"pythonTraceback\"" +
                "}";


        try {
            final IConfiguration conf = parser.parse(text);
            assertEquals("The title is as expected", "Quakeledger", conf.getIdentifier());
            assertEquals("The imageId is as expected", "123456", conf.getImageId());
            assertEquals("The workingDirectory is as expected", "/usr/share/git/quakeledger", conf.getWorkingDirectory());
            assertEquals("The commandToExecute is as expected", Arrays.asList("python3", "eventquery.py"), conf.getCommandToExecute());
            assertTrue("There are no default command line flags", conf.getDefaultCommandLineFlags().isEmpty());
            assertTrue("There is a exit value handler present", conf.getExitValueHandler().isPresent());
            assertEquals("And it is a log handler", exitValueHandler, conf.getExitValueHandler().get());
            assertTrue("There is stderr handler", conf.getStderrHandler().isPresent());
            assertEquals("And it is the python traceback handler", stderrHandler, conf.getStderrHandler().get());
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test the quakeledger configuration
     */
    @Test
    public void testValidQuakeledger() {
        final IParseConfiguration parser = new ParseJsonConfigurationImpl();

        final IExitValueHandler exitValueHandler = new LogExitValueHandler();

        final String text = "{" +
                "\"title\": \"Quakeledger\"," +
                "\"imageId\": \"123456\"," +
                "\"workingDirectory\": \"/usr/share/git/quakeledger\"," +
                "\"commandToExecute\": \"python3 eventquery.py\"," +
                "\"exitValueHandler\": \"logging\"," +
                "\"input\": [" +
                "{ \"title\" : \"input-boundingbox\", \"useAs\": \"commandLineArgument\", \"type\": \"bbox\", \"crs\": [\"EPSG:4326\", \"EPSG:4328\"]}," +
                "{ \"title\" : \"mmin\", \"useAs\": \"commandLineArgument\", \"type\": \"double\", \"default\": \"6.6\"}," +
                "{ \"title\" : \"mmax\", \"useAs\": \"commandLineArgument\", \"type\": \"double\", \"default\": \"8.5\"}," +
                "{ \"title\" : \"zmin\", \"useAs\": \"commandLineArgument\", \"type\": \"double\", \"default\": \"5\"}," +
                "{ \"title\" : \"zmax\", \"useAs\": \"commandLineArgument\", \"type\": \"double\", \"default\": \"140\"}," +
                "{ \"title\" : \"p\", \"useAs\": \"commandLineArgument\", \"type\": \"double\", \"default\": \"0.1\"}," +
                "{ \"title\" : \"etype\", \"useAs\": \"commandLineArgument\", \"type\": \"string\", \"default\": \"deaggregation\", \"allowed\": [\"observed\", \"deaggregation\", \"stochastic\", \"expert\"]}," +
                "{ \"title\" : \"tlon\", \"useAs\": \"commandLineArgument\", \"type\": \"double\", \"default\": \"-71.5730623712764\"}," +
                "{ \"title\" : \"tlat\", \"useAs\": \"commandLineArgument\", \"type\": \"double\", \"default\": \"-33.1299174879672\"}" +
                "]," +
                "\"output\": [" +
                "{ \"title\": \"selectedRows\", \"readFrom\": \"file\", \"path\": \"test.xml\", \"type\": \"xml\", \"schema\": \"http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd\"}" +
                "]" +
                "}";


        try {
            final IConfiguration conf = parser.parse(text);
            assertEquals("The title is as expected", "Quakeledger", conf.getIdentifier());
            assertEquals("The imageId is as expected", "123456", conf.getImageId());
            assertEquals("The workingDirectory is as expected", "/usr/share/git/quakeledger", conf.getWorkingDirectory());
            assertEquals("The commandToExecute is as expected", Arrays.asList("python3", "eventquery.py"), conf.getCommandToExecute());
            assertTrue("There are no default command line flags", conf.getDefaultCommandLineFlags().isEmpty());
            assertTrue("There is a exit value handler present", conf.getExitValueHandler().isPresent());
            assertEquals("And it is a log handler", exitValueHandler, conf.getExitValueHandler().get());

            final List<IInputParameter> input = conf.getInputIdentifiers();
            final List<IOutputParameter> output = conf.getOutputIdentifiers();

            assertEquals("There are 9 input elements", 9, input.size());
            assertEquals("There is one output element", 1, output.size());
            assertEquals("The first input element is a bounding box",
                    InputParameterFactory.INSTANCE.createCommandLineArgumentBBox("input-boundingbox", false, null, Arrays.asList("EPSG:4326", "EPSG:4328")), input.get(0));
            assertEquals("The second one is the mmin double",
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("mmin", false, null, null, "6.6", null),
                    input.get(1));
            assertEquals("The third one is the mmax double",
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("mmax", false, null, null, "8.5", null),
                    input.get(2));
            assertEquals("The fourth one is the zmin double",
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("zmin", false, null, null, "5", null),
                    input.get(3));
            assertEquals("The fifth one is the zmax double",
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("zmax", false, null,  null,"140", null),
                    input.get(4));
            assertEquals("The sixt one is the p double",
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("p", false, null,null, "0.1", null),
                    input.get(5));
            assertEquals("The seventh one is the etype string",
                    InputParameterFactory.INSTANCE.createCommandLineArgumentString("etype", false, null,null, "deaggregation", Arrays.asList("observed", "deaggregation", "stochastic", "expert")),
                    input.get(6));
            assertEquals("The eighth one is the tlon double",
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("tlon", false, null,null, "-71.5730623712764", null),
                    input.get(7));
            assertEquals("The ninth one is the tlat double",
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("tlat", false, null,null, "-33.1299174879672", null),
                    input.get(8));

            assertEquals(
                "The output is xml",
                OutputParameterFactory.INSTANCE.createFileOutXmlWithSchema("selectedRows", false, null,null, "test.xml", "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd"),
                output.get(0)
            );

        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

}
