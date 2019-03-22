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
 *
 *
 */

import org.junit.Test;
import org.n52.gfz.riesgos.exceptions.ConvertToStringCmdException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.IData;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;

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

        final IConvertIDataToCommandLineParameter converter = new FileToStringCmd(filename);


        // the converter just care about the filename
        // not about the content
        final IData iData = null;

        try {
            final List<String> result = converter.convertToCommandLineParameter(iData);

            assertEquals("There is one element", 1, result.size());
            assertEquals("It is the filename", filename, result.get(0));

        } catch(final ConvertToStringCmdException convertToStringCmdException) {
            fail("Should not happen");
        }
    }

    /**
     * If there is no filename than an exception must be thrown
     */
    @Test
    public void testNonValid() {
        final String filename = null;

        final IConvertIDataToCommandLineParameter converter = new FileToStringCmd(filename);


        final IData iData = null;

        try {
            converter.convertToCommandLineParameter(iData);
            fail("There must be an exception");
        } catch(final ConvertToStringCmdException convertToStringCmdException) {
            assertNotNull("There must be an exception", convertToStringCmdException);
        }
    }
}
