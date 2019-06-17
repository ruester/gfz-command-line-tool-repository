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
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

/**
 * Test case for the LiteralIntBindingToStringCmd class
 */
public class TestLiteralIntBindingToStringCmd {

    /**
     * Test with valid input
     */
    @Test
    public void testValid() {
        final LiteralIntBinding iData = new LiteralIntBinding(1);

        final IConvertIDataToCommandLineParameter<LiteralIntBinding> converter = new LiteralIntBindingToStringCmd();

        try {
            final List<String> result = converter.convertToCommandLineParameter(iData);

            assertEquals("There is one element", 1, result.size());

            assertEquals("This value is as expected", String.valueOf(1), result.get(0));
        } catch(final ConvertToStringCmdException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * test with a default flag
     */
    @Test
    public void testWithDefaultFlat() {
        final LiteralIntBinding iData = new LiteralIntBinding(1);

        final IConvertIDataToCommandLineParameter<LiteralIntBinding> converter = new LiteralIntBindingToStringCmd("--level");

        try {
            final List<String> result = converter.convertToCommandLineParameter(iData);

            assertEquals("There are two element", 2, result.size());

            assertEquals("The flag is before the value", "--level", result.get(0));
            assertEquals("This value is as expected", String.valueOf(1), result.get(1));
        } catch(final ConvertToStringCmdException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * test for equality
     */
    @Test
    public void testEquals() {
        final IConvertIDataToCommandLineParameter<LiteralIntBinding> converter1 = new LiteralIntBindingToStringCmd();
        final IConvertIDataToCommandLineParameter<LiteralIntBinding> converter2 = new LiteralIntBindingToStringCmd();

        assertEquals("The converter are equal", converter1, converter2);

        final IConvertIDataToCommandLineParameter converter3 = new LiteralIntBindingToStringCmd(null);

        assertEquals("The third is also equal", converter1, converter3);

        final IConvertIDataToCommandLineParameter converter4 = new LiteralIntBindingToStringCmd("--int");
        assertNotEquals("The fourth is different", converter1, converter4);
    }


}
