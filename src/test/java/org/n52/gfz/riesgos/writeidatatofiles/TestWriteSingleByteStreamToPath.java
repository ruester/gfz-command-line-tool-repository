package org.n52.gfz.riesgos.writeidatatofiles;

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
import org.n52.gfz.riesgos.functioninterfaces.IWriteIDataToFiles;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGeotiffBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertLiteralStringToBytes;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test class for WriteSingleByteStreamToPath
 */
public class TestWriteSingleByteStreamToPath {

    /**
     * Tests equality
     */
    @Test
    public void testEquals() {
        final IWriteIDataToFiles<LiteralStringBinding> writer1 = new WriteSingleByteStreamToPath<>(new ConvertLiteralStringToBytes());
        final IWriteIDataToFiles<LiteralStringBinding> writer2 = new WriteSingleByteStreamToPath<>(new ConvertLiteralStringToBytes());
        final IWriteIDataToFiles<GeotiffBinding> writer3 = new WriteSingleByteStreamToPath<>(new ConvertGeotiffBindingToBytes());

        assertEquals("1 and 2 are equal", writer1, writer2);
        assertNotEquals("1 and 3 are different", writer1, writer3);

    }
}
