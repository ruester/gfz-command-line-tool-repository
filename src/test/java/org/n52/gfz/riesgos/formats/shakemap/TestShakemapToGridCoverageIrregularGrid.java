/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.n52.gfz.riesgos.formats.shakemap;

import org.apache.xmlbeans.XmlObject;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.junit.Test;
import org.n52.gfz.riesgos.formats.shakemap.functions.ShakemapToGridCoverageIrregularGrid;
import org.opengis.geometry.Envelope;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;

public class TestShakemapToGridCoverageIrregularGrid implements ICommonTestShakemapFunctions {


    /*
    @Test
    public void testExample2() {
        final XmlObject veryBasicShakemap = createExampleShakemapExtended();
        final GridCoverage2D gridCoverage = new ShakemapToGridCoverageIrregularGrid().apply(Shakemap.fromOriginalXml(veryBasicShakemap));
        //final GridCoverage2D gridCoverage = new ShakemapToGridCoverageForRegularGrid().apply(Shakemap.fromOriginalXml(veryBasicShakemap));

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

        /*
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



        try {
            final GeoTiffWriter geoTiffWriter = new GeoTiffWriter(new FileOutputStream(new File("/home/nbck/tmp/riesgos/irregular.tiff")));
            geoTiffWriter.write(gridCoverage, null);
            geoTiffWriter.dispose();
        } catch(final IOException ioException) {
            fail("There should be no io exception");
        }


    }


    @Test
    public void testExampleFull() {
        final XmlObject veryBasicShakemap = createExampleShakemapFull();
        final GridCoverage2D gridCoverage = new ShakemapToGridCoverageIrregularGrid().apply(Shakemap.fromOriginalXml(veryBasicShakemap));
        //final GridCoverage2D gridCoverage = new ShakemapToGridCoverageForRegularGrid().apply(Shakemap.fromOriginalXml(veryBasicShakemap));

        assertNotNull("The gridcoverage is not null", gridCoverage);
        assertEquals("There is two bands", 2, gridCoverage.getRenderedImage().getData().getNumBands());

        final Envelope envelope = gridCoverage.getEnvelope();
        final double lowerCornerX = envelope.getLowerCorner().getCoordinate()[0];
        final double lowerCornerY = envelope.getLowerCorner().getCoordinate()[1];

        final double upperCornerX = envelope.getUpperCorner().getCoordinate()[0];
        final double upperCornerY = envelope.getUpperCorner().getCoordinate()[1];

        /*
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



        try {
            final GeoTiffWriter geoTiffWriter = new GeoTiffWriter(new FileOutputStream(new File("/home/nbck/tmp/riesgos/irregular.tiff")));
            geoTiffWriter.write(gridCoverage, null);
            geoTiffWriter.dispose();
        } catch(final IOException ioException) {
            fail("There should be no io exception");
        }


    }

     */
}
