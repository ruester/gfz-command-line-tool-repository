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
 *
 *
 */


import org.junit.Test;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test class for ConvertBytesToGenericFileDataBinding
 */
public class TestConvertBytesToGenericFileDataBinding {

    @Test
    public void testEquals() {
        final IConvertByteArrayToIData converter1 = new ConvertBytesToGenericFileDataBinding();
        final IConvertByteArrayToIData converter2 = new ConvertBytesToGenericFileDataBinding();

        assertEquals("Both are the same", converter1, converter2);

        final IConvertByteArrayToIData converter3 = new ConvertBytesToGenericFileDataBinding("application/octet-stream");

        assertEquals("the octet-stream is the default", converter1, converter3);

        final IConvertByteArrayToIData converter4 = new ConvertBytesToGenericFileDataBinding("text/xml");

        assertNotEquals("The converter4 is different", converter1, converter4);

    }
}
