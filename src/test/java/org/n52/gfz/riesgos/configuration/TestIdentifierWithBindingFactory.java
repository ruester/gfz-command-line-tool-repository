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
 *
 *
 */

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * test class for the identifierWithBindingFactory
 */
public class TestIdentifierWithBindingFactory {

    /**
     * Tests equality
     */
    @Test
    public void testEquals() {
        final IIdentifierWithBinding identifier1 = IdentifierWithBindingFactory.createCommandLineArgumentDouble("val1", null, null, null, null);
        final IIdentifierWithBinding identifier2 = IdentifierWithBindingFactory.createCommandLineArgumentDouble("val1", null, null, null, null);

        assertEquals("Both are the same", identifier1, identifier2);

        final IIdentifierWithBinding identifier3 = IdentifierWithBindingFactory.createCommandLineArgumentDouble("val1", null, null, "0", null);
        assertNotEquals("The third one is different", identifier1, identifier3);

        final IIdentifierWithBinding identifier4 = IdentifierWithBindingFactory.createCommandLineArgumentString("val2", null, null, "x", Arrays.asList("x", "y"));
        final IIdentifierWithBinding identifier5 = IdentifierWithBindingFactory.createCommandLineArgumentString("val2", null, null, "x", Arrays.asList("x", "y"));

        assertEquals("4 and 5 are equal", identifier4, identifier5);
    }
}
