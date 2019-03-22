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

package org.n52.gfz.riesgos.commandlineparametertransformer;

import org.junit.Test;
import org.n52.gfz.riesgos.exceptions.ConvertToStringCmdException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralBooleanBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test case for the LiteralBooleanBindingToStringCmd
 */
public class TestLiteralBooleanBindingToStringCmd {

    /**
     * Test with valid input.
     * The value is true
     */
    @Test
    public void testsValidTrue() {
        final IData iData = new LiteralBooleanBinding(true);

        final IConvertIDataToCommandLineParameter converter = new LiteralBooleanBindingToStringCmd("--verbose");

        try {
            final List<String> result = converter.convertToCommandLineParameter(iData);
            assertEquals("There is one element", 1, result.size());
            assertEquals("The value is as excepted", "--verbose", result.get(0));
        } catch(final ConvertToStringCmdException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with valid input.
     * The value is false
     */
    @Test
    public void testValidFalse() {
        final IData iData = new LiteralBooleanBinding(false);

        final IConvertIDataToCommandLineParameter converter = new LiteralBooleanBindingToStringCmd("--verbose");

        try {
            final List<String> result = converter.convertToCommandLineParameter(iData);
            assertTrue("The list is empty", result.isEmpty());
        } catch(final ConvertToStringCmdException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test with non valid input
     */
    @Test
    public void tetNonValid() {
        final IData iData = new LiteralStringBinding("true");

        final IConvertIDataToCommandLineParameter converter = new LiteralBooleanBindingToStringCmd("--verbose");

        try {
            converter.convertToCommandLineParameter(iData);
            fail("There must be an exception");
        } catch(final ConvertToStringCmdException exception) {
            assertNotNull("There must be an exception", exception);
        }

    }
}
