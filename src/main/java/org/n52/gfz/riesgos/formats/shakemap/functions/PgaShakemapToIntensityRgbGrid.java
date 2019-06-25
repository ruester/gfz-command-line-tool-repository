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
                final IntensityLevel intensity =
                        IntensityLevel.classifyPga(pgaValue);

                raster.setSample(indexWidth, indexHeight,
                        BAND_RED, intensity.red);
                raster.setSample(indexWidth, indexHeight,
                        BAND_GREEN, intensity.green);
                raster.setSample(indexWidth, indexHeight,
                        BAND_BLUE, intensity.blue);


            }
        }
        return CoverageFactoryFinder.getGridCoverageFactory(null)
                .create(
                        shakemap.getName(),
                        raster,
                        shakemap.getEnvelope());
    }

    /**
     * The enum follows the documentation on
     * https://usgs.github.io/shakemap/manual3_5/tg_intensity.html
     *
     *
     * The classification of the pga value into intensity values
     * is mostly straight forward,
     * however it starts with intensity ONE for all values below 0.05.
     * Because ZERO and ONE have the same color it is okay to not
     * care about the difference here.
     *
     * Also there is a classification for the values between
     * 0.05 up to 0.3 in level two or three.
     *
     * In order to use the full spectrum of the colors defined here,
     * we created a level border at 0.18.
     *
     * The rest is good documented.
     *
     */
    private enum IntensityLevel {
        /**
         * Level Zero.
         */
        ZERO(255, 255, 255, 0.0),
        /**
         * Level One.
         */
        ONE(255, 255, 255, 0.05),
        /**
         * Level Two.
         */
        TWO(191, 204, 255, 0.18),
        /**
         * Level Three.
         */
        THREE(160, 230, 255, 0.3),
        /**
         * Level Four.
         */
        FOUR(128, 255, 255, 2.8),
        /**
         * Level Five.
         */
        FIVE(122, 255, 147, 6.2),
        /**
         * Level Six.
         */
        SIX(255, 255, 0, 12),
        /**
         * Level Seven.
         */
        SEVEN(255, 200, 0, 22),
        /**
         * Level Eight.
         */
        EIGHT(255, 145, 0, 40),
        /**
         * Level Nine.
         */
        NINE(255, 0, 0, 139),
        /**
         * Level Ten.
         */
        TEN(200, 0, 0, Double.POSITIVE_INFINITY);

        /**
         * This variable stores the value for the red band
         * on a rgb visualization.
         */
        private final int red;

        /**
         * This variable stores the value for the green band
         * on a rgb visualization.
         */
        private final int green;
        /**
         * This variable stores the value for the blue band
         * on a rgb visualization.
         */
        private final int blue;
        /**
         * This is for storing the pga value that is the limit
         * up to this level is used for the pga.
         * The ordering is defined by the order in the enum.
         */
        private final double upperLimitForPga;

        /**
         * Constructor with red, green, blue and a limit for the pga.
         * @param aRed red value for rgb
         * @param aGreen green value for rgb
         * @param aBlue blue value of rgb
         * @param aUpperLimitForPga upper limit for the pga classification
         */
        IntensityLevel(
                final int aRed,
                final int aGreen,
                final int aBlue,
                final double aUpperLimitForPga) {
            this.red = aRed;
            this.green = aGreen;
            this.blue = aBlue;
            this.upperLimitForPga = aUpperLimitForPga;
        }

        /**
         * Static method to classify the pga value in
         * IntensityLevel.
         * @param pga pga value of a shakemap
         * @return intensity level
         */
        public static IntensityLevel classifyPga(final double pga) {

            for (final IntensityLevel intensityLevel : values()) {
                if (pga < intensityLevel.upperLimitForPga) {
                    return intensityLevel;
                }
            }

            return getLast();
        }

        /**
         *
         * @return the last element of the
         */
        private static IntensityLevel getLast() {
            final IntensityLevel[] vals = values();
            return vals[vals.length - 1];
        }
    }
}
