package org.n52.gfz.riesgos.configuration;


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

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * test class for the identifierWithBindingFactory
 */
public class TestInputParameterFactory {

    /**
     * Tests equality
     */
    @Test
    public void testEquals() {
        final IInputParameter identifier1 = InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("val1", false, null, null, null, null);
        final IInputParameter identifier2 = InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("val1", false, null, null, null, null);

        assertEquals("Both are the same", identifier1, identifier2);

        final IInputParameter identifier3 = InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("val1", false, null, null, "0", null);
        assertNotEquals("The third one is different", identifier1, identifier3);

        final IInputParameter identifier4 = InputParameterFactory.INSTANCE.createCommandLineArgumentString("val2", false, null, null, "x", Arrays.asList("x", "y"));
        final IInputParameter identifier5 = InputParameterFactory.INSTANCE.createCommandLineArgumentString("val2", false, null, null, "x", Arrays.asList("x", "y"));

        assertEquals("4 and 5 are equal", identifier4, identifier5);

        final IInputParameter identifier6 = InputParameterFactory.INSTANCE.createCommandLineArgumentDateTime("starttime", false, null, null, null, null);
        final IInputParameter identifier7 = InputParameterFactory.INSTANCE.createCommandLineArgumentDateTime("starttime", false, null, null, null, null);

        assertEquals("6 and 7 are also equal", identifier6, identifier7);
    }
}
