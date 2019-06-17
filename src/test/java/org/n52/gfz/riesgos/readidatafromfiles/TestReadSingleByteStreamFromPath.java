package org.n52.gfz.riesgos.readidatafromfiles;

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
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGeotiffBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToLiteralStringBinding;
import org.n52.gfz.riesgos.functioninterfaces.IReadIDataFromFiles;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test class for ReadSingleByteStreamFromPath
 */
public class TestReadSingleByteStreamFromPath {

    /**
     * Tests equality
     */
    @Test
    public void testEquals() {
        final IReadIDataFromFiles<LiteralStringBinding> reader1 = new ReadSingleByteStreamFromPath(new ConvertBytesToLiteralStringBinding());
        final IReadIDataFromFiles<LiteralStringBinding> reader2 = new ReadSingleByteStreamFromPath(new ConvertBytesToLiteralStringBinding());

        assertEquals("Both are equal", reader1, reader2);

        final IReadIDataFromFiles<GeotiffBinding> reader3 = new ReadSingleByteStreamFromPath(new ConvertBytesToGeotiffBinding());

        assertNotEquals("The third one is different", reader1, reader3);
    }
}
