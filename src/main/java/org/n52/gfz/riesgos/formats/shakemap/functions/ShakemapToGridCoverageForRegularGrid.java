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
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Function to convert the IShakemap to a GridCoverage.
 * This just converts to grids and takes all the rows
 * in a shakemap to different bands.
 * There is no meaning between the different bands except that
 * they share the same geographic location (and no coding as
 * red, green or blue bands).
 *
 * This works for regular gridded shakemaps only.
 */
public class ShakemapToGridCoverageForRegularGrid
        implements Function<IShakemap, GridCoverage2D> {

    /**
     * Name for the coverage.
     */
    private static final String COVERAGE_NAME = "Shakemap";
    /**
     * Data type that should be used for the grids.
     */
    private static final int DATA_TYPE = DataBuffer.TYPE_DOUBLE;

    /**
     * Our mixin with some helper methods.
     * (We currently don't use dependency injection here, nor does Java
     * supports multiple inheritence, so we use composition here).
     */
    private final ShakemapToGridCoverageMixin mixin =
            new ShakemapToGridCoverageMixin();


    /**
     * Converts the shakemap to a grid coverage.
     * @param shakemap shakemap to convert
     * @return Grid coverage with some bands for the data rows in shakemap
     */
    @Override
    public GridCoverage2D apply(final IShakemap shakemap) {

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

        final List<IShakemapField> fields = shakemap.getFields();

        final List<IShakemapField> customFields = fields.stream()
                .filter(IShakemapField::isCustom)
                .collect(Collectors.toList());
        final WritableRaster raster = RasterFactory.createBandedRaster(
                DATA_TYPE,
                width,
                height,
                customFields.size(),
                null
        );

        for (
                int bandIndex = 0;
                bandIndex < customFields.size();
                bandIndex += 1
        ) {

            final IShakemapField field = customFields.get(bandIndex);

            for (final IShakemapData data : shakemap.getData()) {
                final double lon = data.getLon();
                final double lat = data.getLat();

                final double value = data.getCustomValues().getOrDefault(
                        field.getName(), Double.NaN);
                raster.setSample(
                        mixin.transformLonToImageCoordinate(
                                lon, minX, maxX, width
                        ),
                        mixin.transformLatToImageCoordinate(
                                lat, minY, maxY, height
                        ),
                        bandIndex,
                        value);
            }
        }

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

        return factory.create(COVERAGE_NAME, raster, envelope);
    }
}
