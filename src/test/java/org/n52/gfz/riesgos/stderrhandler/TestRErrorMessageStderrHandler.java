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

package org.n52.gfz.riesgos.stderrhandler;

import org.junit.Test;
import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Test class for the RErrorMessageStderrHandler
 */
public class TestRErrorMessageStderrHandler {

    /**
     * Test with empty stderr text
     */
    @Test
    public void testEmpty() {
        final String stderrText = "";

        final IStderrHandler stderrHandler = new RErrorMessageStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();

        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
        } catch (final NonEmptyStderrException exception) {
            assertTrue("The logging is still empty", stringBuilder.toString().isEmpty());
            fail("There should be no exception on empty stderr text");
        }
    }

    /**
     * Test with the error message of a non existing function
     */
    @Test
    public void testNonExistingFunction() {
        final String stderrText = "Error: could not find function \"non_existing_function\"";

        final IStderrHandler stderrHandler = new RErrorMessageStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();

        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
            fail("There must be an exception");
        } catch (final NonEmptyStderrException exception) {
            assertTrue("The logging is still empty", stringBuilder.toString().isEmpty());
            assertNotNull("There is a exception because of the error", exception);
        }
    }

    /**
     * Test the error message of reading a non existing file
     */
    @Test
    public void testNonExistingFile() {
        final String stderrText = "Error in file(file, \"rt\") : cannot open the connection\n" +
                "In addition: Warning message:\n" +
                "In file(file, \"rt\") :\n" +
                "  cannot open file 'non_existing_file.txt': No such file or directory";

        final IStderrHandler stderrHandler = new RErrorMessageStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();

        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
            fail("There mjust be an exception");
        } catch (final NonEmptyStderrException exception) {
            assertTrue("The logging is still empty", stringBuilder.toString().isEmpty());
            assertNotNull("There is a exception because of the error", exception);
        }
    }

    /**
     * This is a test with a warning that contains just a warning and no error message.
     * Done via warning("This may can cause an Error: Be careful! It is NO error!")
     */
    @Test
    public void testWarningText() {
        final String stderrText = "Warning message:\n" +
                "This may can cause an Error: Be careful! It is NO error!";

        final IStderrHandler stderrHandler = new RErrorMessageStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();

        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
        } catch(final NonEmptyStderrException exception) {
            assertTrue("The logging is still empty", stringBuilder.toString().isEmpty());
            fail("There should be no exception on a warning on stderr text");
        }
    }

    /**
     * This text has the warning message before a real error message.
     * It should find the Error: could not find function part.
     */
    @Test
    public void testWarningTextBeforeErrorMessage() {
        final String stderrText = "Warning message:\n" +
                "This may can cause an Error: Be careful! It is NO error!\n" +
                "Error: could not find function \"non_existing_function\"";

        final IStderrHandler stderrHandler = new RErrorMessageStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();

        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
            fail("There must be an exception");
        } catch (final NonEmptyStderrException exception) {
            assertTrue("The logging is still empty", stringBuilder.toString().isEmpty());
            assertNotNull("There is a exception because of the error", exception);
        }
    }
}
