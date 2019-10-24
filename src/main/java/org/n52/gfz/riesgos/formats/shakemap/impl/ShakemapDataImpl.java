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

import org.n52.gfz.riesgos.formats.shakemap.IShakemapData;

import java.util.Map;

/**
 * This class represents a row in the grid_data section
 * of the shakemap.
 *
 * It contains the lat, the lon and a undefined number of
 * additional values (that are defined as Fields).
 */
public class ShakemapDataImpl implements IShakemapData {
    /**
     * Longitude value of the point.
     */
    private final double lon;
    /**
     * Latitude value of the point.
     */
    private final double lat;

    /**
     * Map with all the custom values on the point.
     */
    private final Map<String, Double> customValues;

    /**
     * Creates the ShakemapDataImpl.
     * @param aLon the longitude value of the point
     * @param aLat the latitude value of the point
     * @param aCustomValues a map with custom values (*that will not be copied*)
     */
    public ShakemapDataImpl(
            final double aLon,
            final double aLat,
            final Map<String, Double> aCustomValues) {
        this.lon = aLon;
        this.lat = aLat;
        this.customValues = aCustomValues;
    }

    /**
     * Returns the latitude value of the point.
     * @return lat value of the point
     */
    @Override
    public double getLat() {
        return lat;
    }

    /**
     * Returns the longitude value of the point.
     * @return lon value of the point
     */
    @Override
    public double getLon() {
        return lon;
    }

    /**
     * Returns the map with the custom values.
     * @return custom values of the point.
     */
    @Override
    public Map<String, Double> getCustomValues() {
        return customValues;
    }
}
