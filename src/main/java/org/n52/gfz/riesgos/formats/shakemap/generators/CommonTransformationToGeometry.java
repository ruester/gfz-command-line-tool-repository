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

package org.n52.gfz.riesgos.formats.shakemap.generators;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.n52.gfz.riesgos.formats.shakemap.IShakemap;
import org.n52.gfz.riesgos.formats.shakemap.functions.ShakemapToIsolines;

import java.util.function.Function;

/**
 * Class to store the share
 * the feature collection creation
 * for multiple formats.
 */
public final class CommonTransformationToGeometry {
    /**
     * Private constructor. Just use the static methods.
     */
    private CommonTransformationToGeometry() {
        // static
    }

    /**
     *
     * @return function to transform the shakemap
     * to a feature collection
     */
    public static Function<IShakemap, SimpleFeatureCollection>
    getFunctionToConvertShakemapToFeatureCollection() {
        return new ShakemapToIsolines();
    }
}
