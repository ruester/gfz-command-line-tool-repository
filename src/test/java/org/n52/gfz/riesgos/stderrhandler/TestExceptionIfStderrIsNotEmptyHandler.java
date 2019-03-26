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
import org.n52.gfz.riesgos.functioninterfaces.ILogger;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test case for ExceptionIfStderrIsNotEmptyHandler
 */
public class TestExceptionIfStderrIsNotEmptyHandler {

    /**
     * In case that there is no text at all, then there should be no
     * exception
     */
    @Test
    public void testEmpty() {
        final String stderrText = "";

        final IStderrHandler handler = new ExceptionIfStderrIsNotEmptyHandler();
        final ILogger logger = (text) -> {};

        try {
            handler.handleSterr(stderrText, logger);
        } catch(final NonEmptyStderrException exception) {
            fail("There must be no exception");
        }
    }

    /**
     * In case that there is some error text
     * an exeption must be thrown
     */
    @Test
    public void testWithErrorText() {
        final String stderrText = "Segmentation fault";

        final IStderrHandler handler = new ExceptionIfStderrIsNotEmptyHandler();
        final ILogger logger = (text) -> {};

        try {
            handler.handleSterr(stderrText, logger);
            fail("There must be an exception");
        } catch(final NonEmptyStderrException excepction) {
            assertNotNull("There is an exception", excepction);
        }
    }

    /**
     * In case that there is only whitespace
     * than there should be no exception
     */
    @Test
    public void testWithWhitespaceOnly() {
        final String stderrText = "    \n\t\t\t\n";

        final IStderrHandler handler = new ExceptionIfStderrIsNotEmptyHandler();
        final ILogger logger = (text) -> {};

        try {
            handler.handleSterr(stderrText, logger);
        } catch(final NonEmptyStderrException exception) {
            fail("There must be no exception");
        }
    }
}
