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

import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.n52.gfz.riesgos.formats.shakemap.IShakemap;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapData;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapField;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapSpecification;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.media.jai.RasterFactory;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Function to convert the IShakemap to a GridCoverage
 */
public class ShakemapToGridCoverage implements Function<IShakemap, GridCoverage2D> {

    private static final String COVERAGE_NAME = "Shakemap";
    private static final int DATA_TYPE = DataBuffer.TYPE_DOUBLE;

    @Override
    public GridCoverage2D apply(final IShakemap shakemap) {
        final IShakemapSpecification specification = shakemap.getSpecification();

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

        for(int bandIndex = 0; bandIndex < customFields.size(); bandIndex += 1) {
            final IShakemapField field = customFields.get(bandIndex);

            for(final IShakemapData data : shakemap.getData()) {
                final double lon = data.getLon();
                final double lat = data.getLat();

                final double value = data.getCustomValues().getOrDefault(field.getName(), Double.NaN);
                raster.setSample(
                        transformLonToImageCoordinate(lon, minX, maxX, width),
                        transformLatToImageCoordinate(lat, minY, maxY, height),
                        bandIndex,
                        value);
            }
        }

        final CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;

        // making the envelope bigger means that the locations are in the middle of the points
        final double minXExtended = minX - diffX / 2;
        final double maxXExtended = maxX + diffX / 2;
        final double minYExtended = minY - diffY / 2;
        final double maxYExtended = maxY + diffY / 2;

        final Envelope envelope = new ReferencedEnvelope(
                minXExtended, maxXExtended,
                minYExtended, maxYExtended,
                crs);

        final GridCoverageFactory factory = CoverageFactoryFinder.getGridCoverageFactory(null);

        return factory.create(COVERAGE_NAME, raster, envelope);
    }

    private int transformLonToImageCoordinate(
            final double lon,
            final double lonMin,
            final double lonMax,
            final int width) {

        final double diff = lonMax - lonMin;

        final double normed01 = (lon - lonMin) / diff;
        final double normed0x = normed01 * (width - 1);
        return (int) Math.round(normed0x);
    }

    private int transformLatToImageCoordinate(
            final double lat,
            final double latMin,
            final double latMax,
            final int height) {
        final double diff = latMax - latMin;

        final double normed01 = (lat - latMin) / diff;
        final double normed0x = normed01 * (height - 1);
        final int asInt = (int) Math.round(normed0x);
        // the coordinates for the y part is inverted (upper left is 0,0)
        return height - asInt - 1;
    }
}
