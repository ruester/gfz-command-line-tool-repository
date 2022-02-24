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

package org.n52.gfz.riesgos.formats.shakemap.functions;

import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.n52.gfz.riesgos.formats.shakemap.IShakemap;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapData;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapField;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapSpecification;
import org.n52.gfz.riesgos.formats.shakemap.mixins.ShakemapToGridCoverageMixin;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.media.jai.RasterFactory;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Function to convert the IShakemap to a map of GridCoverages.
 * It stores all the different columns in the shakemap as their own
 * grids & puts them into the resulting map.
 *
 * This way we can access the different imts as keys in the map.
 */
public class ShakemapToGridCoverageForMultipleRegularGrids
        implements Function<IShakemap, Map<String, GridCoverage2D>> {

    /**
     * Our mixin with some helper methods.
     * (We currently don't use dependency injection here, nor does Java
     * supports multiple inheritence, so we use composition here).
     */
    private final ShakemapToGridCoverageMixin mixin =
            new ShakemapToGridCoverageMixin();

    /**
     * DataBuffer type for the outputs.
     * Should be either DataBuffer.TYPE_FLOAT or DataBuffer.TYPE_DOUBLE.
     */
    private final int type;

    /**
     * Create the converter for a specific data type.
     *
     * @param atype DataBuffer.TYPE_FLOAT or DataBuffer.TYPE_DOUBLE
     */
    public ShakemapToGridCoverageForMultipleRegularGrids(final int atype) {
        this.type = atype;
    }


    /**
     * Converts the shakemap to a mutliple grid coverages (one per column
     * in the shakemap).
     * @param shakemap shakemap to convert
     * @return Grid coverage with some bands for the data rows in shakemap
     */
    @Override
    public Map<String, GridCoverage2D> apply(final IShakemap shakemap) {
        final Map<String, GridCoverage2D> result = new HashMap<>();

        final IShakemapSpecification specification =
                shakemap.getSpecification();

        final int width = specification.getNLon();
        final int height = specification.getNLat();

        final double minX = specification.getLonMin();
        final double maxX = specification.getLonMax();
        final double minY = specification.getLatMin();
        final double maxY = specification.getLatMax();

        final double diffX = specification.getNominalLonSpacing();
        final double diffY = specification.getNominalLatSpacing();

        final List<IShakemapField> customFields = shakemap.getFields().stream()
                .filter(IShakemapField::isCustom)
                .collect(Collectors.toList());

        // long (x), lat (y)
        final CoordinateReferenceSystem crs = mixin.findWgs84();

        // making the envelope bigger means that the
        // locations are in the middle of the points
        final double minXExtended = minX - diffX / 2;
        final double maxXExtended = maxX + diffX / 2;
        final double minYExtended = minY - diffY / 2;
        final double maxYExtended = maxY + diffY / 2;

        final Envelope envelope = new ReferencedEnvelope(
                minXExtended, maxXExtended,
                minYExtended, maxYExtended,
                crs);

        final GridCoverageFactory factory =
                CoverageFactoryFinder.getGridCoverageFactory(null);

        final List<WritableRaster> rasters = new ArrayList<>();

        for (
                int fieldIndex = 0;
                fieldIndex < customFields.size();
                fieldIndex += 1
        ) {
            rasters.add(RasterFactory.createBandedRaster(
                    type,
                    width,
                    height,
                    1,
                    null
            ));
        }

        for (final IShakemapData data : shakemap.getData()) {
            final double lon = data.getLon();
            final double lat = data.getLat();

            final int x = mixin.transformLonToImageCoordinate(
                    lon, minX, maxX, width
            );
            final int y = mixin.transformLatToImageCoordinate(
                    lat, minY, maxY, height
            );

            for (
                    int fieldIndex = 0;
                    fieldIndex < customFields.size();
                    fieldIndex += 1
            ) {
                final IShakemapField field = customFields.get(fieldIndex);

                final double value = data.getCustomValues().getOrDefault(
                        field.getName(), Double.NaN);
                rasters.get(fieldIndex).setSample(x, y, 0, value);
            }
        }

        for (
                int fieldIndex = 0;
                fieldIndex < customFields.size();
                fieldIndex += 1
        ) {
            final IShakemapField field = customFields.get(fieldIndex);
            final GridCoverage2D grid = factory.create(
                    field.getName(), rasters.get(fieldIndex), envelope
            );
            result.put(field.getName(), grid);
        }

        return result;
    }


}
