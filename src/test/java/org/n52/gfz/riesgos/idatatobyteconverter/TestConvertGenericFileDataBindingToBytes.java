package org.n52.gfz.riesgos.idatatobyteconverter;


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
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;

import static junit.framework.TestCase.assertEquals;

/**
 * Test class for ConvertGenericFileDataBindingToBytes
 */
public class TestConvertGenericFileDataBindingToBytes {

    @Test
    public void testEquals() {
        final IConvertIDataToByteArray<GenericFileDataBinding> converter1 = new ConvertGenericFileDataBindingToBytes();
        final IConvertIDataToByteArray<GenericFileDataBinding> converter2 = new ConvertGenericFileDataBindingToBytes();

        assertEquals("Both are the same", converter1, converter2);
    }
}
