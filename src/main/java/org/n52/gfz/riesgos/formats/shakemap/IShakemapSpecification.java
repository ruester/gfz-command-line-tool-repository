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
 */

package org.n52.gfz.riesgos.formats.shakemap;

/**
 * Interface for the shakemap specification.
 * This includes data about the bounding box
 * and the spacing for the raster.
 */
public interface IShakemapSpecification {

    /**
     *
     * @return maximum latitude
     */
    double getLatMax();

    /**
     *
     * @return minimum latitude
     */
    double getLatMin();

    /**
     *
     * @return maximum longitude
     */
    double getLonMax();

    /**
     *
     * @return minimum longitude
     */
    double getLonMin();

    /**
     * Returns the number of different values for the latitude.
     * So for the values 0, 5, 10 the n is 3.
     *
     * @return number of different values for the latitude
     */
    int getNLat();

    /**
     * Returns the number of different values for the longitude.
     * So for the values 0, 2, 4, 6, 8 the n is 5
     *
     * @return number of different values for the longitude
     */
    int getNLon();

    /**
     *
     * @return spacing between each latitude value
     */
    double getNominalLatSpacing();

    /**
     *
     * @return spacing between each longitude value
     */
    double getNominalLonSpacing();

    /**
     *
     * @return true if the grid is regular
     */
    boolean isRegular();

}
