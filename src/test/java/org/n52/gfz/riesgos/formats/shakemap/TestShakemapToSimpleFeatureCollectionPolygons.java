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
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Test;
import org.n52.gfz.riesgos.formats.shakemap.functions.ShakemapToSimpleFeatureCollectionPolygons;
import org.n52.gfz.riesgos.formats.shakemap.impl.ShakemapXmlImpl;
import org.opengis.feature.simple.SimpleFeatureType;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * This is the test class to convert the shakemaps to polygons
 */
public class TestShakemapToSimpleFeatureCollectionPolygons implements  ICommonTestShakemapFunctions {

    /**
     * Tests the conversion of the shakemap with two additional fields
     * (+ lat + lon) into polygons
     */
    @Test
    public void testConversionExtended() {
        final XmlObject extendedShakemap = createExampleShakemapExtended();

        final SimpleFeatureCollection collection = transformToSimpleFeatureCollectionPolygons(extendedShakemap);

        assertNotNull("The collection should not be null", collection);

        final SimpleFeatureType schema = collection.getSchema();
        assertNotNull("There is a schema", schema);
        assertNotNull("There is a column VAL", schema.getType("VAL"));
        assertNotNull("There is a column VAL2", schema.getType("VAL2"));
        assertEquals("There are 25 features", 25, collection.size());

        final ReferencedEnvelope bbox = collection.getBounds();
        final double maxLat = bbox.getMaxY();
        final double minLat = bbox.getMinY();
        final double maxLon = bbox.getMaxX();
        final double minLon = bbox.getMinX();

        // the bounding box is bigger
        // because the points are now like raster cells
        // and the origin point is in the middle of the point
        assertTrue("maxLat is around 10", Math.abs(maxLat - 10.0) < 0.0001);
        assertTrue("minLat is around -10", Math.abs(minLat - -10.0) < 0.0001);
        assertTrue("maxLon is around 2", Math.abs(maxLon - 2.0) < 0.0001);
        assertTrue("minLon is around -18", Math.abs(minLon - -18.0) < 0.0001);

    }

    private SimpleFeatureCollection transformToSimpleFeatureCollectionPolygons(final XmlObject xmlShakemap) {
        return new ShakemapToSimpleFeatureCollectionPolygons().apply(new ShakemapXmlImpl(xmlShakemap));
    }
}
