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
 */

package org.n52.gfz.riesgos.formats.shakemap.functions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapData;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapSpecification;

/**
 * This function provides a way to transform the
 * data in a Shakemap to a simpleFeatureCollection.
 */
public class ShakemapToSimpleFeatureCollection
        extends AbstractShakemapToSimpleFeatureCollection {

    /**
     *
     * @return Point geometry
     */
    @Override
    protected Class<? extends Geometry> getGeometryClass() {
        return Point.class;
    }

    /**
     * Factory methode to create the geometry.
     * @param geometryFactory a geometry factory
     * @param singleRow a point with data
     * @param specification the grid specification
     * @return Point geometry of the data
     */
    @Override
    protected Geometry createGeometry(
            final GeometryFactory geometryFactory,
            final IShakemapData singleRow,
            final IShakemapSpecification specification) {
        return geometryFactory.createPoint(
                new Coordinate(
                        singleRow.getLon(),
                        singleRow.getLat()));
    }
}
