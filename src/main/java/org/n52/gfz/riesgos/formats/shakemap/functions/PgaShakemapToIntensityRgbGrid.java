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

import javax.media.jai.RasterFactory;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.function.Function;

/**
 * To display the shakemap with meaningful colors
 * we recreate the image in rgb mode for the
 * pga which is - if the data comes from shakyground -
 * always the first band.
 *
 * The usgs has written some documentation on the
 * handling of the intensities (see below for the enum).
 */
public class PgaShakemapToIntensityRgbGrid
        implements Function<GridCoverage2D, GridCoverage2D> {

    /**
     * The default band for the pga is the first one (zero based
     * indexing).
     * This may be specific to the shakemaps created by shakyground,
     * however this is the program we support here.
     */
    private static final int BAND_INDEX_PGA = 0;

    /**
     * Band index for the red band.
     */
    private static final int BAND_RED = 0;
    /**
     * Band index for the green band.
     */
    private static final int BAND_GREEN = 1;
    /**
     * Band index for the blue band.
     */
    private static final int BAND_BLUE = 2;

    /**
     * This function transforms a grid coverage with the pga values
     * from a shakemap to a grid with rgb values based on a classification
     * of the pga value to intensities.
     *
     * @param shakemap GridCoverage with the pga values on the first band
     * @return GridCoverage with RGB colors for the intensity level
     */
    @Override
    public GridCoverage2D apply(final GridCoverage2D shakemap) {
        final int width = shakemap.getRenderedImage().getWidth();
        final int height = shakemap.getRenderedImage().getHeight();

        final WritableRaster raster = RasterFactory.createBandedRaster(
                DataBuffer.TYPE_INT,
                width,
                height,
                3,
                null);

        final Raster baseRaster = shakemap.getRenderedImage().getData();

        for (int indexWidth = 0; indexWidth < width; indexWidth += 1) {
            for (int indexHeight = 0; indexHeight < height; indexHeight += 1) {
                final double pgaValue = baseRaster
                        .getSampleDouble(
                                indexWidth,
                                indexHeight,
                                BAND_INDEX_PGA);
                final PgaIntensityLevel intensity =
                        PgaIntensityLevel.classifyPga(pgaValue);

                raster.setSample(indexWidth, indexHeight,
                        BAND_RED, intensity.getRed());
                raster.setSample(indexWidth, indexHeight,
                        BAND_GREEN, intensity.getGreen());
                raster.setSample(indexWidth, indexHeight,
                        BAND_BLUE, intensity.getBlue());


            }
        }
        return CoverageFactoryFinder.getGridCoverageFactory(null)
                .create(
                        shakemap.getName(),
                        raster,
                        shakemap.getEnvelope());
    }

}
