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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
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
 * Abstract base class for all the functions that convert the shakemap to a
 * simple feature collection (no matter if points or polygons).
 */
public abstract class AbstractShakemapToSimpleFeatureCollection
        implements Function<IShakemap, SimpleFeatureCollection> {
    /**
     * Name of the geometry column.
     */
    private static final String GEOM_COLUMN = "the_geom";
    /**
     * Name of the features that will be created.
     */
    private static final String FEATURE_NAME = "Shakemap";

    /**
     * Function to transform into a simple feature collection.
     * @param shakemap shakemap to transform
     * @return SimpleFeatureCollection
     */
    @Override
    public SimpleFeatureCollection apply(final IShakemap shakemap) {

        final IShakemapSpecification specification =
                shakemap.getSpecification();

        final List<IShakemapField> fields = shakemap.getFields();
        final List<IShakemapField> customFields = fields.stream()
                .filter(IShakemapField::isCustom)
                .collect(Collectors.toList());

        final SimpleFeatureTypeBuilder simpleFeatureTypeBuilder =
                new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName(FEATURE_NAME);

        for (final IShakemapField field : customFields) {
            simpleFeatureTypeBuilder.add(field.getName(), Double.class);
        }

        simpleFeatureTypeBuilder.setCRS(DefaultGeographicCRS.WGS84);
        simpleFeatureTypeBuilder.add(GEOM_COLUMN, getGeometryClass());
        simpleFeatureTypeBuilder.setDefaultGeometry(GEOM_COLUMN);

        final GeometryFactory geometryFactory = new GeometryFactory();
        final SimpleFeatureType simpleFeatureType =
                simpleFeatureTypeBuilder.buildFeatureType();
        final SimpleFeatureBuilder simpleFeatureBuilder =
                new SimpleFeatureBuilder(simpleFeatureType);

        final DefaultFeatureCollection collection =
                new DefaultFeatureCollection();

        final Sequence seq = new Sequence();

        final List<IShakemapData> data = shakemap.getData();
        for (final IShakemapData singleRow : data) {
            simpleFeatureBuilder.set(
                    GEOM_COLUMN,
                    createGeometry(geometryFactory, singleRow, specification));
            for (final Map.Entry<String, Double> customValue
                    : singleRow.getCustomValues().entrySet()) {
                simpleFeatureBuilder.set(
                        customValue.getKey(),
                        customValue.getValue());
            }

            final SimpleFeature feature = simpleFeatureBuilder.buildFeature(
                    String.valueOf(seq.nextValue()));
            collection.add(feature);
            simpleFeatureBuilder.reset();
        }

        return collection;
    }

    /**
     * Abstract method to reaturn the geometry class.
     * @return geometry class to use for the actual implementation
     */
    protected abstract Class<? extends Geometry> getGeometryClass();

    /**
     * Creation of the geomtry from the data point.
     * @param geometryFactory geomtry factory to create the data
     * @param singleRow data point
     * @param specification specification of the grid.
     * @return geometry
     */
    protected abstract Geometry createGeometry(
            GeometryFactory geometryFactory,
            IShakemapData singleRow,
            IShakemapSpecification specification);
}
