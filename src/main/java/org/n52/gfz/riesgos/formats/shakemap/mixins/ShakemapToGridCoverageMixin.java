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

package org.n52.gfz.riesgos.formats.shakemap.mixins;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Mixin class to provide some common functionality for
 * creating grid coverage instances.
 */
public class ShakemapToGridCoverageMixin {
    /**
     *
     * @param lon longitude of the point
     * @param lonMin minimum lon of the grid
     * @param lonMax maximum lon of the grid
     * @param width width of the grid
     * @return image coordinage (x)
     */
    public int transformLonToImageCoordinate(
            final double lon,
            final double lonMin,
            final double lonMax,
            final int width) {

        final double diff = lonMax - lonMin;

        final double normed01 = (lon - lonMin) / diff;
        final double normed0x = normed01 * (width - 1);
        return (int) Math.round(normed0x);
    }

    /**
     *
     * @param lat latitude for the point
     * @param latMin minimum lat of the grid
     * @param latMax maximum lat of the grid
     * @param height height of the grid
     * @return image coordinate (y)
     */
    public int transformLatToImageCoordinate(
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

    /**
     *
     * @return CRS with WGS84
     */
    public CoordinateReferenceSystem findWgs84() {

        try {
            return CRS.decode("EPSG:4326");
        } catch (FactoryException exception) {
            throw new EgspCodeNotFoundException(exception);
        }
    }

    /**
     * Special EgspCodeNotFoundException class.
     * Used instead of a normal RuntimeException (just to be more specific).
     */
    class EgspCodeNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 680153609684699644L;

        /**
         * Construct the exception instance.
         *
         * Basically use the parent constructor.
         * @param baseException Exception that caused the
         *                      EgspCodeNotFoundException
         */
        EgspCodeNotFoundException(final FactoryException baseException) {
            super(baseException);
        }
    }
}
