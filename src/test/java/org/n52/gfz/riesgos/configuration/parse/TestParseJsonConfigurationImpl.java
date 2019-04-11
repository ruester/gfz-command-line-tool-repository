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
 *
 *
 */

import org.junit.Test;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.parse.json.ParseJsonConfigurationImpl;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
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
        } catch(final ParseConfigurationException exception) {
            fail("There should be no exception");
        }
    }

}
