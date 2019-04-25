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

public class TestConvertBytesToLiteralStringBinding {

    @Test
    public void testEquals() {
        final IConvertByteArrayToIData converter1 = new ConvertBytesToLiteralStringBinding();
        final IConvertByteArrayToIData converter2 = new ConvertBytesToLiteralStringBinding();

        assertEquals("Both must be the equal", converter1, converter2);
    }
}
