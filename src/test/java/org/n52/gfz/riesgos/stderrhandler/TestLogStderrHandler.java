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
 *
 *
 */

import org.junit.Test;
import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

/**
 * Test case for LogStderrHandler
 */
public class TestLogStderrHandler {

    /**
     * If the text is empty, this still should be logged
     */
    @Test
    public void testLogEmpty() {

        final String stderrText = "";

        final StringBuilder logger = new StringBuilder();
        final IStderrHandler handler = new LogStderrHandler();

        try {
            handler.handleSterr(stderrText, logger::append);

            final String text = logger.toString();

            assertEquals("The logged text is just the indication that it is logged, but no text", "Text on stderr:\n", text);

        } catch(final NonEmptyStderrException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Any error message should be logged anyway
     */
    @Test
    public void testLogWithText() {
        final String stderrText = "There is an error in line 7 of example_script.sh";

        final StringBuilder logger = new StringBuilder();
        final IStderrHandler handler = new LogStderrHandler();

        try {
            handler.handleSterr(stderrText, logger::append);

            final String text = logger.toString();

            assertEquals("The logged text is also inside of the message", "Text on stderr:\nThere is an error in line 7 of example_script.sh", text);
        } catch(final NonEmptyStderrException exception) {
            fail("There should be no exception");
        }
    }
}
