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

package org.n52.gfz.riesgos.util;

import org.geotools.coverage.grid.GridCoverage2D;
import org.opengis.referencing.ReferenceIdentifier;

import java.awt.geom.Rectangle2D;

/**
 * Helper class to extract information from coverages.
 */
public enum CoverageUtils {
    /**
     * Singleton.
     */
    INSTANCE;

    /**
     * Constructor for the coverage utils.
     */
    CoverageUtils() {
        // empty.
    }

    /**
     * Extract a bounding box string (minX, minY, maxX, maxY).
     * @param gridCoverage2D raster to extract the bbox
     * @return bounding box string (minX, minY, maxX, maxY).
     */
    public static String extractBBoxString(
            final GridCoverage2D gridCoverage2D
    ) {
        final Rectangle2D bounds = gridCoverage2D.getEnvelope2D().getBounds();

        final double minX = bounds.getMinX();
        final double minY = bounds.getMinY();

        final double maxX = bounds.getMaxX();
        final double maxY = bounds.getMaxY();

        return minX + "," + minY + "," + maxX + "," + maxY;
    }

    /**
     * Extract the srs string.
     * @param gridCoverage2D raster to extract the srs
     * @return something ala epsg:4326
     */
    public static String extractSRSString(final GridCoverage2D gridCoverage2D) {
        final ReferenceIdentifier srsIdentifier = gridCoverage2D.getEnvelope2D()
                .getCoordinateReferenceSystem()
                .getCoordinateSystem()
                .getIdentifiers()
                .iterator()
                .next();

        return srsIdentifier.getCodeSpace() + ":" + srsIdentifier.getCode();
    }
}
