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

package org.n52.gfz.riesgos.formats.nrml.functions;

import com.vividsolutions.jts.geom.Point;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.Test;
import org.n52.gfz.riesgos.formats.nrml.INrml;
import org.n52.gfz.riesgos.formats.nrml.Nrml;
import org.n52.gfz.riesgos.util.StringUtils;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * This is the test class for NrmlToFeatureCollection.
 */
public class TestNrmlToFeatureCollection {

    /**
     * Tests the conversion with an example file.
     */
    @Test
    public void testConversion() {
        try {
            final XmlObject xml = readFile();

            // here are some problems

            final INrml nrml = Nrml.fromOriginalXml(xml);
            final Function<INrml, SimpleFeatureCollection> transformer = new NrmlToFeatureCollection();

            final SimpleFeatureCollection collection = transformer.apply(nrml);

            assertEquals("The size is as excepted", 68, collection.size());

            final SimpleFeatureIterator iterator = collection.features();

            final SimpleFeature feature1 = iterator.next();
            final SimpleFeature feature2 = iterator.next();

            for(final SimpleFeature feature : Arrays.asList(feature1, feature2)) {
                // the following is the same for all the features
                assertEquals("The exposureModelId is as excepted", "SARA_v1.0", feature.getAttribute("exposureModelId"));
                assertEquals("The exposureModelCategory is as excepted", "buildings", feature.getAttribute("exposureModelCategory"));
                assertEquals("The exposureModelTaxonomySource is as expected", "GEM", feature.getAttribute("exposureModelTaxonomySource"));

                assertEquals("The description is as expected", "GEM-SARA Model, project RIESGOS", feature.getAttribute("description"));

                assertEquals("The costTypeName is as expected", "structural", feature.getAttribute("costTypeName"));
                assertEquals("The costTypeType is as expected", "per_asset", feature.getAttribute("costTypeType"));
                assertEquals("The costTypeUnit is as expected", "USD", feature.getAttribute("costTypeUnit"));
            }

            // just testing the first feature
            assertTrue("The lon is as expected", Math.abs( ((Point) feature1.getDefaultGeometry()).getCoordinate().x - (-71.2888956)) < 0.0001);
            assertTrue("The lat is as expected",  Math.abs( ((Point) feature1.getDefaultGeometry()).getCoordinate().y - (-33.0532539)) < 0.0001);

            assertEquals("The costType is as expected", "structural", feature1.getAttribute("costType"));
            assertTrue("The cost is as expected", Math.abs( ((double) feature1.getAttribute("cost")) -  985250.0) <  0.0001);

            assertEquals("The occupanciesInday is as expected", 3, feature1.getAttribute("occupanciesInday"));
            assertEquals("The occupanciesInnight is as expected", 6, feature1.getAttribute("occupanciesInnight"));

            assertEquals("The assetId is as expected", "CHL.16.7.3_1", feature1.getAttribute("assetId"));
            assertEquals("The number is as expected", 91, feature1.getAttribute("number"));


        } catch (final IOException | XmlException exception) {
            fail("There should be no exception on reading the content");
        }
    }

    private XmlObject readFile() throws IOException, XmlException {
        final String content = StringUtils.readFromResourceFile("org/n52/gfz/riesgos/formats/nrml.xml");
        return XmlObject.Factory.parse(content);
    }
}
