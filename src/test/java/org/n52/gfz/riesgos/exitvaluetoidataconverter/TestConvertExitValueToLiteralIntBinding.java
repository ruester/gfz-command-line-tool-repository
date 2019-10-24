package org.n52.gfz.riesgos.exitvaluetoidataconverter;

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
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test class for ConvertExitValueToLiteralIntBinding
 */
public class TestConvertExitValueToLiteralIntBinding {

    /**
     * Tests equality
     */
    @Test
    public void testEquals() {
        final IConvertExitValueToIData<LiteralIntBinding> converter1 = new ConvertExitValueToLiteralIntBinding();
        final IConvertExitValueToIData<LiteralIntBinding> converter2 = new ConvertExitValueToLiteralIntBinding();

        assertEquals("Both are equal", converter1, converter2);
    }

    /**
     * Test for the converting
     */
    @Test
    public void testConvert() {
        final IConvertExitValueToIData<LiteralIntBinding> converter = new ConvertExitValueToLiteralIntBinding();
        try {
            final LiteralIntBinding intBinding = converter.convertToIData(0);

            assertEquals("The value is the right one", 0, intBinding.getPayload().intValue());

        } catch(final ConvertToIDataException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test for the converting
     */
    @Test
    public void testConvert2() {
        final IConvertExitValueToIData<LiteralIntBinding> converter = new ConvertExitValueToLiteralIntBinding();
        try {
            final LiteralIntBinding intBinding = converter.convertToIData(2);
            assertEquals("The value is the right one", 2, intBinding.getPayload().intValue());
        } catch(final ConvertToIDataException exception) {
            fail("There should be no exception");
        }
    }
}
