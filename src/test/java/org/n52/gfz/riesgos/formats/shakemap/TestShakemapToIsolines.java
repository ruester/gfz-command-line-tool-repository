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

import org.geotools.data.simple.SimpleFeatureCollection;
import org.junit.Test;
import org.n52.gfz.riesgos.formats.shakemap.functions.ShakemapToIsolines;

import java.util.function.Function;

import static junit.framework.TestCase.assertNotNull;

/**
 * This is the test class for the shakemap to isolines function
 */
public class TestShakemapToIsolines implements ICommonTestShakemapFunctions {

    /**
     * This test is just to see that the conversion works and throws no
     * exception.
     */
    @Test
    public void testThatFullExampleWorks() {
        final IShakemap shakemap = Shakemap.fromOriginalXml(createExampleShakemapFull());

        final Function<IShakemap, SimpleFeatureCollection> transformer = new ShakemapToIsolines();

        final SimpleFeatureCollection collection = transformer.apply(shakemap);

        assertNotNull("The collection is not null", collection);
    }
}
