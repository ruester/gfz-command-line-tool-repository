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
import org.n52.gfz.riesgos.functioninterfaces.IReadIDataFromFiles;

import static junit.framework.TestCase.assertEquals;

/**
 * Test class for ReadShapeFileFromPath
 */
public class TestReadShapeFileFromPath {

    /**
     * Tests equality
     */
    @Test
    public void testEquals() {
        final IReadIDataFromFiles reader1 = new ReadShapeFileFromPath();
        final IReadIDataFromFiles reader2 = new ReadShapeFileFromPath();

        assertEquals("Both must be equal", reader1, reader2);
    }
}
