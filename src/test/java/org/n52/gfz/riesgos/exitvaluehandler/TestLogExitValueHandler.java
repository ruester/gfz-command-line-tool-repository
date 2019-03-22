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

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

/**
 * This is a test class for the LogExitValueHandler class
 */
public class TestLogExitValueHandler {

    /**
     * This class always just logs the value, no matter what value it is
     * It will do so using zero as exit value
     */
    @Test
    public void testLogZero() {

        final int exitValue = 0;

        final StringBuilder strBuilder = new StringBuilder();
        final IExitValueHandler handler = new LogExitValueHandler(strBuilder::append);

        try {
            handler.handleExitValue(exitValue);

            final String text = strBuilder.toString();
            assertEquals("The exit value is logged", "Exit value: 0", text);

        } catch(final NonZeroExitValueException exception) {
            fail("There must be no exception");
        }
    }

    /**
     * This class always just logs the value, no matter what value it is
     * It will do so using two as exit value
     */
    @Test
    public void testLogTwo() {

        final int exitValue = 2;

        final StringBuilder strBuilder = new StringBuilder();
        final IExitValueHandler handler = new LogExitValueHandler(strBuilder::append);

        try {
            handler.handleExitValue(exitValue);

            final String text = strBuilder.toString();
            assertEquals("The exit value is logged", "Exit value: 2", text);

        } catch(final NonZeroExitValueException exception) {
            fail("There must be no exception");
        }
    }


}
