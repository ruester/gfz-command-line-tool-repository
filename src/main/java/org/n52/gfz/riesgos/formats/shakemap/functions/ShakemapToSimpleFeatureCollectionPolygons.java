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

package org.n52.gfz.riesgos.formats.shakemap.functions;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.n52.gfz.riesgos.formats.shakemap.IShakemap;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapData;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapField;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapSpecification;
import org.n52.gfz.riesgos.util.Sequence;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is the same as ShakemapToSimpleFeatureCollection but the outputs are Polygons instead of points
 */
public class ShakemapToSimpleFeatureCollectionPolygons extends AbstractShakemapToSimpleFeatureCollection {

    @Override
    protected Class<? extends Geometry> getGeometryClass() {
        return Polygon.class;
    }


    @Override
    protected Geometry createGeometry(final GeometryFactory geometryFactory,
                                      final IShakemapData singleRow,
                                      final IShakemapSpecification specification) {

        final double addLon = specification.getNominalLonSpacing() / 2.0;
        final double addLat = specification.getNominalLatSpacing() / 2.0;

        final double lon = singleRow.getLon();
        final double lat = singleRow.getLat();

        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(
                        lon + addLon,
                        lat + addLat
                ),
                new Coordinate(
                        lon - addLon,
                        lat + addLat
                ),
                new Coordinate(
                        lon - addLon,
                        lat - addLat
                ),
                new Coordinate(
                        lon + addLon,
                        lat - addLat
                ),
                new Coordinate(
                        lon + addLon,
                        lat + addLat
                )
        };
        return geometryFactory.createPolygon(coordinates);
    }
}