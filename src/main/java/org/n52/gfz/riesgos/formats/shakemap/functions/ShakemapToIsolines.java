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

public class ShakemapToIsolines implements Function<IShakemap, SimpleFeatureCollection> {

    private static final Function<IShakemap, GridCoverage2D> TO_GRID = new ShakemapToGridCoverage();

    @Override
    public SimpleFeatureCollection apply(final IShakemap shakemap) {

        final GridCoverage2D grid = TO_GRID.apply(shakemap);
        final ContourProcess p = new ContourProcess();


        final int band = 0;
        final boolean simplify = true;
        final boolean smooth = false;
        final Geometry roi = null;

        final double[] levels = null;
        final double interval = 0.1;

        final ProgressListener processListener = new DefaultProgressListener();

        // you need to add the gt-process-raster jar
        // and also the gt-process jar
        // and also jt-contour-1.3.1.jar
        // and jt-attributeop-1.3.1.jar

        return p.execute(grid, band, levels, interval, simplify, smooth, roi, processListener);

    }
}
