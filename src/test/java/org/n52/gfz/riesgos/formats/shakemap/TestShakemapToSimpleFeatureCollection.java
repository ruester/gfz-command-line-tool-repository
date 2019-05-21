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
import org.n52.gfz.riesgos.formats.shakemap.functions.ShakemapToSimpleFeatureCollection;
import org.n52.gfz.riesgos.formats.shakemap.impl.ShakemapXmlImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * This is a test class for ShakemapToSimpleFeatureCollection.
 */
public class TestShakemapToSimpleFeatureCollection implements ICommonTestShakemapFunctions {


    /**
     * This is test with a very simple shakemap that is converted to a simple feature collection
     */
    @Test
    public void testConversion() {


        final XmlObject veryBasicShakemap = createExampleShakemap();
        final SimpleFeatureCollection collection = transformToSimpleFeatureCollection(veryBasicShakemap);
        assertNotNull("The collection should not be null", collection);

        final SimpleFeatureType schema = collection.getSchema();
        assertNotNull("There is a schema", schema);
        assertNotNull("There is a column VAL", schema.getType("VAL"));
        assertEquals("There are 25 features", 25, collection.size());

        final ReferencedEnvelope bbox = collection.getBounds();
        final double maxLat = bbox.getMaxY();
        final double minLat = bbox.getMinY();
        final double maxLon = bbox.getMaxX();
        final double minLon = bbox.getMinX();

        assertTrue("maxLat is around 8", Math.abs(maxLat - 8.0) < 0.0001);
        assertTrue("minLat is around -8", Math.abs(minLat - -8.0) < 0.0001);
        assertTrue("maxLon is around 8", Math.abs(maxLon - 8.0) < 0.0001);
        assertTrue("minLon is around -8", Math.abs(minLon - -8.0) < 0.0001);

        final SimpleFeature feature = collection.features().next();
        assertTrue("The VAL field of the first feature is 0", Math.abs((Double) feature.getAttribute("VAL")) < 0.0001);
    }

    /**
     * This is a test with a more advanced shakemap (two additional variables) + different values
     */
    @Test
    public void testConversion2() {
        final XmlObject extendedShakemap = createExampleShakemapExtended();

        final SimpleFeatureCollection collection = transformToSimpleFeatureCollection(extendedShakemap);
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

        assertTrue("maxLat is around 8", Math.abs(maxLat - 8.0) < 0.0001);
        assertTrue("minLat is around -8", Math.abs(minLat - -8.0) < 0.0001);
        assertTrue("maxLon is around 0", Math.abs(maxLon) < 0.0001);
        assertTrue("minLon is around -16", Math.abs(minLon - -16.0) < 0.0001);

        final SimpleFeature feature = collection.features().next();
        assertTrue("The VAL field of the first feature is 1", Math.abs((Double) feature.getAttribute("VAL") - 1.0) < 0.0001);
        assertTrue("The VAL2 field of the first feature is 7", Math.abs((Double) feature.getAttribute("VAL2") - 7.0) < 0.0001);
    }

    private SimpleFeatureCollection transformToSimpleFeatureCollection(final XmlObject xmlShakemap) {
        return new ShakemapToSimpleFeatureCollection().apply(new ShakemapXmlImpl(xmlShakemap));
    }
}
