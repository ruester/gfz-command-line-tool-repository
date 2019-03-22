package org.n52.gfz.riesgos.commandlineparametertransformer;

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
import org.n52.gfz.riesgos.exceptions.ConvertToStringCmdException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

/**
 * Test case for the LiteralDoubleBindingToStringCmd class
 */
public class TestLiteralDoubleBindingToStringCmd {

    @Test
    public void testValid() {
        final IData iData = new LiteralDoubleBinding(1.0);

        final IConvertIDataToCommandLineParameter converter = new LiteralDoubleBindingToStringCmd();

        try {
            final List<String> result = converter.convertToCommandLineParameter(iData);

            assertEquals("There is one element", 1, result.size());

            assertEquals("This value is as expected", String.valueOf(1.0), result.get(0));

        } catch(final ConvertToStringCmdException exception) {
            fail("There should be no exception");
        }
    }

    @Test
    public void testNonValid() {
        final IData iData = new LiteralStringBinding("Dummy");

        final IConvertIDataToCommandLineParameter converter = new LiteralDoubleBindingToStringCmd();

        try {
            converter.convertToCommandLineParameter(iData);
            fail("There mus be an exception");
        } catch(final ConvertToStringCmdException exception) {
            assertNotNull("There must be an exception", exception);
        }
    }

    @Test
    public void testWithDefaultFlag() {
        final IData iData = new LiteralDoubleBinding(1.0);

        final IConvertIDataToCommandLineParameter converter = new LiteralDoubleBindingToStringCmd("--lat");

        try {
            final List<String> result = converter.convertToCommandLineParameter(iData);

            assertEquals("There are two elements", 2, result.size());

            assertEquals("The flag is before the value", "--lat", result.get(0));
            assertEquals("This value is as expected", String.valueOf(1.0), result.get(1));

        } catch(final ConvertToStringCmdException exception) {
            fail("There should be no exception");
        }
    }
}
