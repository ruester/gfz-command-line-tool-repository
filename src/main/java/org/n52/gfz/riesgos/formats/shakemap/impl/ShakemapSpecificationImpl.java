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

package org.n52.gfz.riesgos.formats.shakemap.impl;

import org.n52.gfz.riesgos.formats.shakemap.IShakemapSpecification;

/**
 * Implementation of the IShakemapSpecification interface.
 */
public class ShakemapSpecificationImpl implements IShakemapSpecification {

    /**
     * Range for the latitude.
     */
    private final LatLonRange latRange;
    /**
     * Range for the longitude.
     */
    private final LatLonRange lonRange;

    /**
     * Number of the different latitude values.
     */
    private final int nLat;
    /**
     * Number of the different longitude values.
     */
    private final int nLon;
    /**
     * Spacing between the latitude values.
     */
    private final double nominalLatSpacing;
    /**
     * Spacing between the longitude values.
     */
    private final double nominalLonSpacing;
    /**
     * Value to hold if the grid is regular.
     */
    private final boolean regularGrid;


    /**
     * Default constructor.
     * @param aLatRange range for the latitude
     * @param aLonRange range for the longitude
     * @param aNLat count of different lat values
     * @param aNLon count of different lon values
     * @param aNominalLatSpacing spacing between lat values
     * @param aNominalLonSpacing spacing between lon values
     * @param aRegularGrid boolean if regular grid
     */
    public ShakemapSpecificationImpl(
            final LatLonRange aLatRange,
            final LatLonRange aLonRange,
            final int aNLat,
            final int aNLon,
            final double aNominalLatSpacing,
            final double aNominalLonSpacing,
            final boolean aRegularGrid) {
        this.latRange = aLatRange;
        this.lonRange = aLonRange;
        this.nLat = aNLat;
        this.nLon = aNLon;
        this.nominalLatSpacing = aNominalLatSpacing;
        this.nominalLonSpacing = aNominalLonSpacing;
        this.regularGrid = aRegularGrid;
    }

    /**
     *
     * @return maximum latitude
     */
    @Override
    public double getLatMax() {
        return latRange.getMax();
    }

    /**
     *
     * @return minimum latitude
     */
    @Override
    public double getLatMin() {
        return latRange.getMin();
    }

    /**
     *
     * @return maximum longitude
     */
    @Override
    public double getLonMax() {
        return lonRange.getMax();
    }

    /**
     *
     * @return minimum longitude
     */
    @Override
    public double getLonMin() {
        return lonRange.getMin();
    }

    /**
     *
     * @return number of different latitude values
     */
    @Override
    public int getNLat() {
        return nLat;
    }

    /**
     *
     * @return number of different longitude values
     */
    @Override
    public int getNLon() {
        return nLon;
    }

    /**
     *
     * @return spacing between the latitude values
     */
    @Override
    public double getNominalLatSpacing() {
        return nominalLatSpacing;
    }

    /**
     *
     * @return spacing between the longitude values
     */
    @Override
    public double getNominalLonSpacing() {
        return nominalLonSpacing;
    }

    /**
     *
     * @return true if the grid is regular
     */
    @Override
    public boolean isRegular() {
        return regularGrid;
    }
}
