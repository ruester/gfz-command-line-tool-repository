package org.n52.gfz.riesgos.bytetoidataconverter;

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

import org.geotools.feature.FeatureCollection;
import org.junit.Test;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.opengis.feature.simple.SimpleFeature;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Testclass for ConvertBytesToGTVectorDataBinding
 */
public class TestConvertBytesToGTVectorDataBinding {

    /**
     * Tests equality
     */
    @Test
    public void testEquals() {
        final IConvertByteArrayToIData<GTVectorDataBinding> converter1 = new ConvertBytesToGTVectorDataBinding(ConvertBytesToGTVectorDataBinding.Format.JSON);
        final IConvertByteArrayToIData<GTVectorDataBinding> converter2 = new ConvertBytesToGTVectorDataBinding(ConvertBytesToGTVectorDataBinding.Format.JSON);

        assertEquals("Both converter are the same", converter1, converter2);
    }

    /**
     * Tests the conversion of geojson without an crs given.
     */
    @Test
    public void testGeojsonWithoutCrs() {
        final String geojsonStr = "{\n" +
                "\"type\": \"FeatureCollection\",\n" +
                "\"features\": [\n" +
                "{ \"type\": \"Feature\", \"properties\": { \"id\": 3 }, \"geometry\": { \"type\": \"Polygon\", \"coordinates\": [ [ [ -69.659203980099576, -13.992537313432653 ], [ -58.514925373134403, -13.435323383084395 ], [ -59.151741293532424, -26.927860696517229 ], [ -71.470149253731421, -26.052238805969964 ], [ -71.470149253731421, -26.052238805969964 ], [ -69.659203980099576, -13.992537313432653 ] ] ] } }\n" +
                "]\n" +
                "}\n";

        final byte[] geojsonBytes = geojsonStr.getBytes();

        final IConvertByteArrayToIData<GTVectorDataBinding> converter = new ConvertBytesToGTVectorDataBinding(ConvertBytesToGTVectorDataBinding.Format.JSON);

        try {
            final GTVectorDataBinding binding = converter.convertToIData(geojsonBytes);

            final FeatureCollection<?, ?> featureCollection = binding.getPayload();

            assertEquals("There is one feature", 1, featureCollection.size());

            final Object object = featureCollection.features().next();

            assertTrue("The object is a simple feature", object instanceof SimpleFeature);

            final SimpleFeature feature = (SimpleFeature) object;

            assertEquals("The id is 3", "3", feature.getID());

        } catch(final ConvertToIDataException convertToIDataException) {
            fail("There should be no exception from the conversion");
        }
    }
}
