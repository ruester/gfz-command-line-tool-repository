package org.n52.gfz.riesgos.exitvaluehandler;

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
import org.n52.gfz.riesgos.exceptions.NonZeroExitValueException;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.ILogger;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test case for ExceptionIfExitValueIsNotEmptyHandler
 */
public class TestExceptionIfExitValueIsNotEmptyHandler {

    /**
     * Test with an exit value of zero.
     * There should be no exception, because this indicates successful process termination
     * (see EXIT_SUCCESS macro in C)
     */
    @Test
    public void noException() {
        final int exitValue = 0;

        final IExitValueHandler handler = new ExceptionIfExitValueIsNotEmptyHandler();
        final ILogger logger = (text) -> {};

        try {
            handler.handleExitValue(exitValue, logger);
        } catch(final NonZeroExitValueException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Now there is an exit value that is not zero.
     * There should be a exception
     */
    @Test
    public void withException() {
        final int exitValue = 1;

        final IExitValueHandler handler = new ExceptionIfExitValueIsNotEmptyHandler();
        final ILogger logger = (text) -> {};

        try {
            handler.handleExitValue(exitValue, logger);
            fail("There must be an exception");
        } catch(final NonZeroExitValueException exception) {
            assertNotNull("There is an exception", exception);
        }
    }


}
