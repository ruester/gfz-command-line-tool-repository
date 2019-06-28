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

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.raster.ContourProcess;
import org.geotools.util.DefaultProgressListener;
import org.n52.gfz.riesgos.formats.shakemap.IShakemap;
import org.opengis.util.ProgressListener;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Function to transform the shakemap to an feature collection with isolines.
 */
public class ShakemapToIsolines
        implements Function<IShakemap, SimpleFeatureCollection> {

    /**
     * Transformer to convert the shakemap to a grid before.
     */
    private static final Function<IShakemap, GridCoverage2D> TO_GRID =
            new ShakemapToGridCoverageForRegularGrid();

    /**
     * Band for which the conversion should happen.
     */
    private static final int PGA_BAND = 0;

    /**
     * Function to convert the shakemap to isolines.
     * @param shakemap shakemap with a pga band to convert
     * @return feature collection with the iso lines
     */
    @Override
    public SimpleFeatureCollection apply(final IShakemap shakemap) {

        final GridCoverage2D grid = TO_GRID.apply(shakemap);
        final ContourProcess p = new ContourProcess();

        final boolean simplify = true;
        final boolean smooth = false;
        final Geometry roi = null;

        final double[] levels = getLevels(grid);

        // -> use the levels instead
        final Double interval = null;

        final ProgressListener processListener = new DefaultProgressListener();

        return p.execute(
                grid,
                PGA_BAND,
                levels,
                interval,
                simplify,
                smooth,
                roi,
                processListener);
    }

    /**
     * Method to provide the values for the iso lines.
     * @param grid grid with the data
     * @return double array with the levels
     */
    private double[] getLevels(final GridCoverage2D grid) {
        return useLevelsFromPgaLevels();
    }

    /**
     * Method to get the levels from the pga classification.
     * @return levels from pga classification
     */
    private double[] useLevelsFromPgaLevels() {
        return Stream.of(PgaIntensityLevel.values())
                .mapToDouble(PgaIntensityLevel::getUpperLimitForPga)
                .toArray();
    }

}
