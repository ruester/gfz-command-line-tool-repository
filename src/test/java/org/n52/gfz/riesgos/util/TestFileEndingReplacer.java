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

package org.n52.gfz.riesgos.util;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Test to replace file endings
 */
public class TestFileEndingReplacer {

    /**
     * Test with a shp file ending that should be replaced by .shx
     */
    @Test
    public void testReplaceShpFileEnding() {
        final String filename = "feature.shp";
        final String ending = ".shp";
        final String replacement = ".shx";

        final String expected = "feature.shx";

        final String out = FileEndingReplacer.INSTANCE.replaceFileEnding(filename, ending, replacement);

        assertEquals("The output matches the expected shx filename", expected, out);
    }

    /**
     * Test with no file ending, where the result should have a .shx ending
     */
    @Test
    public void testNoEnding() {
        final String filename = "feature";
        final String ending = ".shp";
        final String replacement = ".shx";

        final String expected = "feature.shx";

        final String out = FileEndingReplacer.INSTANCE.replaceFileEnding(filename, ending, replacement);

        assertEquals("The output matches the expected shx filename", expected, out);
    }
}
