package org.n52.gfz.riesgos.validators;

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
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

/**
 * Test case for LiteralStringBindingWithAllowedValues
 */
public class TestLiteralStringBindingWithAllowedValues {

    /**
     * If the value is one of the allowed values than everything is fine
     * and no error message must be given back
     */
    @Test
    public void testValid() {
        final List<String> allowedValues = Arrays.asList("expert", "simulation", "random");

        final ICheckDataAndGetErrorMessage<LiteralStringBinding> validator = new LiteralStringBindingWithAllowedValues(allowedValues);

        final LiteralStringBinding value = new LiteralStringBinding("expert");

        final Optional<String> errorMessage = validator.check(value);

        assertFalse("There is no message indicating that there is a problem with the data", errorMessage.isPresent());
    }

    /**
     * The value is non of the allowed values, so there must be a message
     */
    @Test
    public void testNonAllowedValue() {
        final List<String> allowedValues = Arrays.asList("expert", "simulation", "random");

        final ICheckDataAndGetErrorMessage<LiteralStringBinding> validator = new LiteralStringBindingWithAllowedValues(allowedValues);

        final LiteralStringBinding value = new LiteralStringBinding("unexpected");

        final Optional<String> errorMessage = validator.check(value);

        assertTrue("There is a message indicating that there is a problem with the data", errorMessage.isPresent());
    }


    /**
     * Tests equality
     */
    @Test
    public void testEquals() {
        final ICheckDataAndGetErrorMessage<LiteralStringBinding> validator1 = new LiteralStringBindingWithAllowedValues(Arrays.asList("1", "2", "3"));
        final ICheckDataAndGetErrorMessage<LiteralStringBinding> validator2 = new LiteralStringBindingWithAllowedValues(Arrays.asList("1", "2", "3"));

        assertEquals("Both are the same", validator1, validator2);

        final ICheckDataAndGetErrorMessage<LiteralStringBinding> validator3 = new LiteralStringBindingWithAllowedValues(Arrays.asList("3", "2", "1"));

        assertEquals("There should be the same no matter of the values", validator1, validator3);

        final ICheckDataAndGetErrorMessage<LiteralStringBinding> validator4 = new LiteralStringBindingWithAllowedValues(Arrays.asList("6", "7"));

        assertNotEquals("The fourth one is different", validator1, validator4);
    }
}
