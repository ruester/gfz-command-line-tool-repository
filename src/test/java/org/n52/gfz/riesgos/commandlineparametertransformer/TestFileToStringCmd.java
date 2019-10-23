package org.n52.gfz.riesgos.commandlineparametertransformer;

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
import org.n52.gfz.riesgos.exceptions.ConvertToStringCmdException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.IData;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotEquals;

/**
 * Test case for FileToStringCmd
 */
public class TestFileToStringCmd {

    /**
     * The converter just cares about the filename.
     * It will always add the filename, no matter what the input class is
     */
    @Test
    public void testValid() {
        final String filename = "test.xml";

        final IConvertIDataToCommandLineParameter<IData> converter = new FileToStringCmd<>(filename);


        // the converter just care about the filename
        // not about the content

        try {
            final List<String> result = converter.convertToCommandLineParameter(null);

            assertEquals("There is one element", 1, result.size());
            assertEquals("It is the filename", filename, result.get(0));

        } catch(final ConvertToStringCmdException convertToStringCmdException) {
            fail("Should not happen");
        }
    }

    /**
     * The converter can also work with a default flag
     */
    @Test
    public void testValidWithFlag() {
        final String filename = "test.xml";
        final String flag = "--test-file";

        final IConvertIDataToCommandLineParameter<IData> converter = new FileToStringCmd<>(filename, flag);

        try {
            final List<String> result = converter.convertToCommandLineParameter(null);

            assertEquals("There are two elements", 2, result.size());
            assertEquals("The first one is the flag", flag, result.get(0));
            assertEquals("The second one is the filename", filename, result.get(1));

        } catch(final ConvertToStringCmdException convertToStringCmdException) {
            fail("Should not happen");
        }
    }

    /**
     * If there is no filename than an exception must be thrown
     */
    @Test
    public void testNonValid() {

        final IConvertIDataToCommandLineParameter<IData> converter = new FileToStringCmd<>(null);

        try {
            converter.convertToCommandLineParameter(null);
            fail("There must be an exception");
        } catch(final ConvertToStringCmdException convertToStringCmdException) {
            assertNotNull("There must be an exception", convertToStringCmdException);
        }
    }

    /**
     * Tests equality
     */
    @Test
    public void testEquals() {
        final IConvertIDataToCommandLineParameter<IData> converter1 = new FileToStringCmd<>("file1.txt");
        final IConvertIDataToCommandLineParameter<IData> converter2 = new FileToStringCmd<>("file1.txt");
        final IConvertIDataToCommandLineParameter<IData> converter3 = new FileToStringCmd<>("file3.txt");

        final IConvertIDataToCommandLineParameter<IData> converter4 = new FileToStringCmd<>("file1.txt", "--file");

        assertEquals("1 and 2 are equal", converter1, converter2);
        assertNotEquals("1 and 3 are different", converter1, converter3);

        assertNotEquals("1 and 4 are different", converter1, converter4);

        final IConvertIDataToCommandLineParameter<IData> converter5 = new FileToStringCmd<>("file1.txt", "--file");
        assertEquals("4 and 5 are equal", converter4, converter5);
    }
}
