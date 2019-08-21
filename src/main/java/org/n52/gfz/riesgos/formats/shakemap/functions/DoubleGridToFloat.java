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
 * Converts a grid with double numbers to one with float numbers.
 * This is necessary because of the geoserver (which is responsible
 * for giving wms output) does not work with double values in a geotiff.
 */
public class DoubleGridToFloat
        implements Function<GridCoverage2D, GridCoverage2D> {
    /**
     * Applies this function to the given argument.
     *
     * @param gridCoverage2D the function argument
     * @return the function result
     */
    @Override
    public GridCoverage2D apply(final GridCoverage2D gridCoverage2D) {
        final int width = gridCoverage2D.getRenderedImage().getWidth();
        final int height = gridCoverage2D.getRenderedImage().getHeight();

        final WritableRaster raster = RasterFactory.createBandedRaster(
                DataBuffer.TYPE_FLOAT,
                width,
                height,
                1,
                null);

        final Raster baseRaster = gridCoverage2D.getRenderedImage().getData();

        for (int indexWidth=0; indexWidth < width; indexWidth += 1) {
            for (int indexHeight=0; indexHeight < height; indexHeight += 1) {
                final double value = baseRaster.getSampleDouble(
                        indexWidth, indexHeight, 0);

                raster.setSample(indexWidth, indexHeight, 0, value);
            }
        }

        return CoverageFactoryFinder.getGridCoverageFactory(null)
                .create(gridCoverage2D.getName(),
                    raster, gridCoverage2D.getEnvelope());
    }
}
