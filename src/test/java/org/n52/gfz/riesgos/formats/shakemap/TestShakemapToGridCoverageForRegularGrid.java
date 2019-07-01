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

package org.n52.gfz.riesgos.formats.shakemap;

import org.apache.xmlbeans.XmlObject;

import org.junit.Test;
import org.n52.gfz.riesgos.formats.shakemap.functions.ShakemapToGridCoverageForRegularGrid;
import org.n52.gfz.riesgos.formats.shakemap.impl.ShakemapXmlImpl;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * This is the test class for the conversion of shakemaps to grids
 */
public class TestShakemapToGridCoverageForRegularGrid implements ICommonTestShakemapFunctions {

    /**
     * A very basic shakemap with just 0 values in one single band
     */
    @Test
    public void testExample1() {
        final XmlObject veryBasicShakemap = createExampleShakemap();
        final GridCoverage gridCoverage = transformToGridCoverage(veryBasicShakemap);

        assertNotNull("The gridcoverage is not null", gridCoverage);
        assertEquals("There is one band", 1, gridCoverage.getRenderedImage().getData().getNumBands());
        assertTrue("The value on the base corner on the first band has the value 0",
                Math.abs(gridCoverage.getRenderedImage().getData().getSampleDouble(0, 0, 0)) < 0.00001);
    }

    /**
     * A more interesting test case with a shakemap with different values and two
     * bands
     */
    @Test
    public void testExample2() {
        final XmlObject veryBasicShakemap = createExampleShakemapExtended();
        final GridCoverage gridCoverage = transformToGridCoverage(veryBasicShakemap);

        assertNotNull("The gridcoverage is not null", gridCoverage);
        assertEquals("There is two bands", 2, gridCoverage.getRenderedImage().getData().getNumBands());
        assertTrue("The value on the base corner on the first band has the value 5",
                Math.abs(gridCoverage.getRenderedImage().getData().getSampleDouble(0, 0, 0) - 5.0) < 0.00001);
        assertTrue("The value on the base corner on the second band has the value 7",
                Math.abs(gridCoverage.getRenderedImage().getData().getSampleDouble(0, 0, 1) - 7.0) < 0.00001);

        final Envelope envelope = gridCoverage.getEnvelope();
        final double lowerCornerX = envelope.getLowerCorner().getCoordinate()[0];
        final double lowerCornerY = envelope.getLowerCorner().getCoordinate()[1];

        final double upperCornerX = envelope.getUpperCorner().getCoordinate()[0];
        final double upperCornerY = envelope.getUpperCorner().getCoordinate()[1];

        // the envelope is a bit different from the min and max lon/lat values
        // so that the locations of the points from the shakemap is in the middle of
        // the cells
        assertFalse("The lowerCornerX is not exactly at -16.0", Math.abs(lowerCornerX - -16.0) < 0.0001);
        assertTrue("But the lowerCornerX is -18.0", Math.abs(lowerCornerX - -18.0) < 0.0001);
        assertFalse("The lowerCornerY is not exactly at -8", Math.abs(lowerCornerY - -8.0) < 0.0001);
        assertTrue("But the lowerCornerY is -10", Math.abs(lowerCornerY - -10.0) < 0.0001);

        assertFalse("The upperCornerX is not exactly at 0", Math.abs(upperCornerX) < 0.0001);
        assertTrue("But the upperCornerX is at 2", Math.abs(upperCornerX - 2.0) < 0.0001);
        assertFalse("The upperCornerY is not exactly at 8", Math.abs(upperCornerY - 8) < 0.0001);
        assertTrue("But The upperCornerY is at 10", Math.abs(upperCornerY - 10) < 0.0001);

        final int width = gridCoverage.getRenderedImage().getWidth();
        final int height = gridCoverage.getRenderedImage().getHeight();

        assertEquals("The width is 5", 5, width);
        assertEquals("The height is 5", 5, height);
    }


    /**
     * A more interesting test case with a shakemap with a nonquadratic shape.
     */
    @Test
    public void testExampleNonQuadratic() {
        final XmlObject veryBasicShakemap = createExampleShakemapNonQuadratic();
        final GridCoverage gridCoverage = transformToGridCoverage(veryBasicShakemap);

        assertNotNull("The gridcoverage is not null", gridCoverage);
        assertEquals("There is two bands", 2, gridCoverage.getRenderedImage().getData().getNumBands());
        assertTrue("The value on the base corner on the first band has the value 5",
                Math.abs(gridCoverage.getRenderedImage().getData().getSampleDouble(0, 0, 0) - 5.0) < 0.00001);
        assertTrue("The value on the base corner on the second band has the value 7",
                Math.abs(gridCoverage.getRenderedImage().getData().getSampleDouble(0, 0, 1) - 7.0) < 0.00001);

        final Envelope envelope = gridCoverage.getEnvelope();
        final double lowerCornerX = envelope.getLowerCorner().getCoordinate()[0];
        final double lowerCornerY = envelope.getLowerCorner().getCoordinate()[1];

        final double upperCornerX = envelope.getUpperCorner().getCoordinate()[0];
        final double upperCornerY = envelope.getUpperCorner().getCoordinate()[1];

        // the envelope is a bit different from the min and max lon/lat values
        // so that the locations of the points from the shakemap is in the middle of
        // the cells
        assertFalse("The lowerCornerX is not exactly at -16.0", Math.abs(lowerCornerX - -16.0) < 0.0001);
        assertTrue("But the lowerCornerX is -18.0", Math.abs(lowerCornerX - -18.0) < 0.0001);
        assertFalse("The lowerCornerY is not exactly at -8", Math.abs(lowerCornerY - -8.0) < 0.0001);
        assertTrue("But the lowerCornerY is -10", Math.abs(lowerCornerY - -10.0) < 0.0001);

        assertFalse("The upperCornerX is not exactly at 4", Math.abs(upperCornerX - 4.0) < 0.0001);
        assertTrue("But the upperCornerX is at 6", Math.abs(upperCornerX - 6.0) < 0.0001);
        assertFalse("The upperCornerY is not exactly at 8", Math.abs(upperCornerY - 8) < 0.0001);
        assertTrue("But The upperCornerY is at 10", Math.abs(upperCornerY - 10) < 0.0001);

        final int width = gridCoverage.getRenderedImage().getWidth();
        final int height = gridCoverage.getRenderedImage().getHeight();

        assertEquals("The width is 6", 6, width);
        assertEquals("The height is 5", 5, height);
    }

    /**
     * This tests the conversion of a full shakemap.
     * The most interesting part here is the time it needs to do that.
     */
    @Test
    public void testFull() {
        final XmlObject shakemap = createExampleShakemapFull();
        final GridCoverage grid = transformToGridCoverage(shakemap);

        assertNotNull("The collection is not null", grid);
    }

    private GridCoverage transformToGridCoverage(final XmlObject xmlShakemap) {
        return new ShakemapToGridCoverageForRegularGrid().apply(new ShakemapXmlImpl(xmlShakemap));
    }
}
