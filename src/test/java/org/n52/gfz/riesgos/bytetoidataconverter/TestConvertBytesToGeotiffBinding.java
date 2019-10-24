package org.n52.gfz.riesgos.bytetoidataconverter;

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
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;

import static junit.framework.TestCase.assertEquals;

/**
 * Test class for ConvertBytesToGeotiffBinding
 */
public class TestConvertBytesToGeotiffBinding {

    /**
     * Tests if two instances are equal
     */
    @Test
    public void testEquals() {
        final IConvertByteArrayToIData<GeotiffBinding> converter1 = new ConvertBytesToGeotiffBinding();
        final IConvertByteArrayToIData<GeotiffBinding> converter2 = new ConvertBytesToGeotiffBinding();

        assertEquals("Both are the same", converter1, converter2);
    }
}
