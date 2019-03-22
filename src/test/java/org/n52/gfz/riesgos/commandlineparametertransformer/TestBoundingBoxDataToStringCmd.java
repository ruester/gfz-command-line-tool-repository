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
import org.n52.wps.io.data.binding.bbox.BoundingBoxData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test case for the BoundingBoxDataToStringCmd class
 */
public class TestBoundingBoxDataToStringCmd {

    /**
     * Test with valid input
     */
    @Test
    public void testValid() {

        final double latmin = -10;
        final double lonmin = -20;
        final double latmax = 30;
        final double lonmax = 40;

        final IData boundingBox = new BoundingBoxData(new double[]{latmin, lonmin}, new double[]{latmax, lonmax}, "EPSG:4326");

        final IConvertIDataToCommandLineParameter converter = new BoundingBoxDataToStringCmd();

        try {
            final List<String> result = converter.convertToCommandLineParameter(boundingBox);


            assertEquals("The list has 4 elements", 4, result.size());

            assertEquals("lonmin is the first", String.valueOf(lonmin), result.get(0));
            assertEquals("lonmax is the second", String.valueOf(lonmax), result.get(1));
            assertEquals("latmin is the third", String.valueOf(latmin), result.get(2));
            assertEquals("latmax is the fourth", String.valueOf(latmax), result.get(3));
        } catch(final ConvertToStringCmdException exception) {
            fail("There should be no exception");
        }
    }

    @Test
    public void testNonValid() {
        final IData iData = new LiteralStringBinding("no usable value");
        final IConvertIDataToCommandLineParameter converter = new BoundingBoxDataToStringCmd();

        try {
            converter.convertToCommandLineParameter(iData);
            fail("There must be an exception");
        } catch(final ConvertToStringCmdException exception) {
            assertNotNull("There is an exception", exception);
        }
    }
}
