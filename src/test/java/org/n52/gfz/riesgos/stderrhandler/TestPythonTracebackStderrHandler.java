package org.n52.gfz.riesgos.stderrhandler;

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
import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Test class for PythonTracebackStderrHandler
 */
public class TestPythonTracebackStderrHandler {

    /**
     * Test with a traceback from python
     */
    @Test
    public void testWithTraceBack() {
        final String stderrText = "Traceback (most recent call last):\n" +
                "File \"<stdin>\", line 1, in <module>\n" +
                "ZeroDivisionError: division by zero";

        final IStderrHandler stderrHandler = new PythonTracebackStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();
        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
            fail("There must be a exception");
        } catch(final NonEmptyStderrException exception) {
            assertTrue("the logging is still empty", stringBuilder.toString().isEmpty());
            assertNotNull("There is a exception because of the traceback", exception);
        }
    }

    /**
     * Test with empty text
     */
    @Test
    public void testEmpty() {
        final String stderrText = "";

        final IStderrHandler stderrHandler = new PythonTracebackStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();
        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
            assertTrue("the logging is still empty", stringBuilder.toString().isEmpty());
        } catch(final NonEmptyStderrException exception) {
            fail("There must be no exception");
        }
    }

    /**
     * Test with a warning
     */
    @Test
    public void testWarning() {
        final String stderrText = "Warning: You may change that later";

        final IStderrHandler stderrHandler = new PythonTracebackStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();
        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
            assertTrue("the logging is still empty", stringBuilder.toString().isEmpty());
        } catch(final NonEmptyStderrException exception) {
            fail("There must be no exception");
        }
    }

    /**
     * Test with some text before the traceback
     */
    @Test
    public void testWithTraceBackAfterSomeText() {
        final String stderrText = "Warning: Please care about some of the stuff.\n" +
                "Traceback (most recent call last):\n" +
                "File \"<stdin>\", line 1, in <module>\n" +
                "ZeroDivisionError: division by zero";

        final IStderrHandler stderrHandler = new PythonTracebackStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();
        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
            fail("There must be a exception");
        } catch(final NonEmptyStderrException exception) {
            assertTrue("the logging is still empty", stringBuilder.toString().isEmpty());
            assertNotNull("There is a exception because of the traceback", exception);
            assertEquals("The text is the one from the traceback",
                    "Traceback (most recent call last):\n" +
                            "File \"<stdin>\", line 1, in <module>\n" +
                            "ZeroDivisionError: division by zero",
                    exception.getMessage());
        }
    }

    /**
     * Tests equality
     */
    @Test
    public void testEquality() {

        final IStderrHandler handler1 = new PythonTracebackStderrHandler();
        final IStderrHandler handler2 = new PythonTracebackStderrHandler();

        assertEquals("The handlers are the same", handler1, handler2);

    }
}
