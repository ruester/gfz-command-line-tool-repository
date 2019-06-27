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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.n52.gfz.riesgos.formats.nrml.INrml;
import org.n52.gfz.riesgos.formats.nrml.INrmlAsset;
import org.n52.gfz.riesgos.formats.nrml.INrmlAssets;
import org.n52.gfz.riesgos.formats.nrml.INrmlConversions;
import org.n52.gfz.riesgos.formats.nrml.INrmlCostType;
import org.n52.gfz.riesgos.formats.nrml.INrmlCostTypes;
import org.n52.gfz.riesgos.formats.nrml.INrmlExposureModel;
import org.n52.gfz.riesgos.formats.nrml.INrmlLocation;
import org.n52.gfz.riesgos.formats.nrml.INrmlOccupancies;
import org.n52.gfz.riesgos.formats.nrml.INrmlOccupancy;
import org.n52.gfz.riesgos.util.Sequence;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Function to transform nrml data to a simple feature
 * collection.
 */
public class NrmlToFeatureCollection
        implements Function<INrml, SimpleFeatureCollection> {

    /**
     * Name for the geometry column.
     */
    private static final String GEOM_COLUMN = "the_geom";
    /**
     * Name of the feature class.
     */
    private static final String FEATURE_NAME = "Nrml";

    /**
     * Name of the field for the exposure model id.
     */
    private static final String FIELD_EXPOSURE_MODEL_ID =
            "exposureModelId";

    /**
     * Name of the field for the exposure model category.
     */
    private static final String FIELD_EXPOSURE_MODEL_CATEGORY =
            "exposureModelCategory";

    /**
     * Name of the field for the exposure model taxonomy source.
     */
    private static final String FIELD_EXPOSURE_MODEL_TAXONOMY_SOURCE =
            "exposureModelTaxonomySource";

    /**
     * Name of the field for the description.
     */
    private static final String FIELD_DESCRIPTION = "description";

    /**
     * Name of the field for the cost type name.
     */
    private static final String FIELD_COST_TYPE_NAME = "costTypeName";

    /**
     * Name of the field for the cost type type.
     */
    private static final String FIELD_COST_TYPE_TYPE = "costTypeType";
    /**
     * Name of the field for the cost type unit.
     */
    private static final String FIELD_COST_TYPE_UNIT = "costTypeUnit";

    /**
     * Name of the field for the asset id.
     */
    private static final String FIELD_ASSET_ID = "assetId";

    /**
     * Name of the field for the number of buildings in the class.
     */
    private static final String FIELD_NUMBER = "number";

    /**
     * Name for the field for the taxonomy.
     */
    private static final String FIELD_TAXONOMY = "taxonomy";

    /**
     * Name for the field for the cost type.
     */
    private static final String FIELD_COST_TYPE = "costType";

    /**
     * Name for the field for the cost value.
     */
    private static final String FIELD_COST = "cost";

    /**
     * Prefix for creating columns for all period values
     * for the occupants.
     */
    private static final String PREFIX_OCCUPANTS_IN = "occupanciesIn";

    /**
     *
     * @param nrml nrml data to transform
     * @return simple feature collection with the data
     */
    @Override
    public SimpleFeatureCollection apply(final INrml nrml) {

        final INrmlExposureModel exposureModel = nrml.getExposureModel();

        // common to all the points
        final String exposureModelId = exposureModel.getId();
        final String exposureModelCategory = exposureModel.getCategory();
        final String exposureModelTaxonomySource =
                exposureModel.getTaxonomySource();
        final String description = exposureModel.getDescription().getText();

        final INrmlConversions conversions = exposureModel.getConversions();
        final INrmlCostTypes costTypes = conversions.getCostTypes();
        final INrmlCostType costType = costTypes.getCostType();
        final String costTypeName = costType.getName();
        final String costTypeType = costType.getType();
        final String costTypeUnit = costType.getUnit();

        final INrmlAssets assets = exposureModel.getAssets();
        final List<INrmlAsset> assetList = assets.getAssetList();

        final Set<String> occupancyPeriodValues = new HashSet<>();
        for (final INrmlAsset asset : assetList) {
            final INrmlOccupancies occupancies = asset.getOccupancies();
            final List<INrmlOccupancy> occupancyList =
                    occupancies.getOccupancyList();
            for (final INrmlOccupancy occupancy : occupancyList) {
                final String period = occupancy.getPeriod();
                occupancyPeriodValues.add(period);
            }
        }

        final SimpleFeatureTypeBuilder simpleFeatureTypeBuilder =
                new SimpleFeatureTypeBuilder();
        simpleFeatureTypeBuilder.setName(FEATURE_NAME);
        simpleFeatureTypeBuilder.add(FIELD_EXPOSURE_MODEL_ID, String.class);
        simpleFeatureTypeBuilder.add(
                FIELD_EXPOSURE_MODEL_CATEGORY, String.class);
        simpleFeatureTypeBuilder.add(
                FIELD_EXPOSURE_MODEL_TAXONOMY_SOURCE, String.class);
        simpleFeatureTypeBuilder.add(FIELD_DESCRIPTION, String.class);
        simpleFeatureTypeBuilder.add(FIELD_COST_TYPE_NAME, String.class);
        simpleFeatureTypeBuilder.add(FIELD_COST_TYPE_TYPE, String.class);
        simpleFeatureTypeBuilder.add(FIELD_COST_TYPE_UNIT, String.class);

        final Map<String, String> columnNamesOccupancyPeriodValues =
                new HashMap<>();
        for (final String period : occupancyPeriodValues) {
            columnNamesOccupancyPeriodValues.put(
                    period, PREFIX_OCCUPANTS_IN + period);
        }

        for (final String columnName
                : columnNamesOccupancyPeriodValues.values()) {
            simpleFeatureTypeBuilder.add(columnName, Integer.class);
        }

        simpleFeatureTypeBuilder.add(FIELD_ASSET_ID, String.class);
        simpleFeatureTypeBuilder.add(FIELD_NUMBER, Integer.class);
        simpleFeatureTypeBuilder.add(FIELD_TAXONOMY, String.class);

        simpleFeatureTypeBuilder.add(FIELD_COST_TYPE, String.class);
        simpleFeatureTypeBuilder.add(FIELD_COST, Double.class);

        simpleFeatureTypeBuilder.setCRS(DefaultGeographicCRS.WGS84);
        simpleFeatureTypeBuilder.add(GEOM_COLUMN, Point.class);
        simpleFeatureTypeBuilder.setDefaultGeometry(GEOM_COLUMN);

        final GeometryFactory geometryFactory = new GeometryFactory();
        final SimpleFeatureType simpleFeatureType =
                simpleFeatureTypeBuilder.buildFeatureType();
        final SimpleFeatureBuilder simpleFeatureBuilder =
                new SimpleFeatureBuilder(simpleFeatureType);

        final DefaultFeatureCollection collection =
                new DefaultFeatureCollection();

        final Sequence seq = new Sequence();

        for (final INrmlAsset asset : assetList) {
            final INrmlLocation location = asset.getLocation();
            final double lat = location.getLat();
            final double lon = location.getLon();

            final Point point = geometryFactory.createPoint(
                    new Coordinate(lon, lat));
            simpleFeatureBuilder.set(
                    GEOM_COLUMN, point);

            simpleFeatureBuilder.set(
                    FIELD_EXPOSURE_MODEL_ID, exposureModelId);
            simpleFeatureBuilder.set(
                    FIELD_EXPOSURE_MODEL_CATEGORY, exposureModelCategory);
            simpleFeatureBuilder.set(
                    FIELD_EXPOSURE_MODEL_TAXONOMY_SOURCE,
                    exposureModelTaxonomySource);
            simpleFeatureBuilder.set(FIELD_DESCRIPTION, description);
            simpleFeatureBuilder.set(FIELD_COST_TYPE_NAME, costTypeName);
            simpleFeatureBuilder.set(FIELD_COST_TYPE_TYPE, costTypeType);
            simpleFeatureBuilder.set(FIELD_COST_TYPE_UNIT, costTypeUnit);

            for (final INrmlOccupancy occupancy
                   : asset.getOccupancies().getOccupancyList()) {
                final String columnName =
                        columnNamesOccupancyPeriodValues.get(
                                occupancy.getPeriod());
                simpleFeatureBuilder.set(columnName, occupancy.getOccupants());
            }

            simpleFeatureBuilder.set(FIELD_ASSET_ID, asset.getId());
            simpleFeatureBuilder.set(FIELD_NUMBER, asset.getNumber());
            simpleFeatureBuilder.set(FIELD_TAXONOMY, asset.getTaxonomy());

            simpleFeatureBuilder.set(
                    FIELD_COST_TYPE, asset.getCosts().getCost().getType());
            simpleFeatureBuilder.set(
                    FIELD_COST, asset.getCosts().getCost().getValue());


            final SimpleFeature feature = simpleFeatureBuilder.buildFeature(
                    String.valueOf(seq.nextValue()));
            collection.add(feature);
            simpleFeatureBuilder.reset();
        }

        return collection;

    }
}
