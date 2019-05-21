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
 *
 *
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
    private final double lon;
    private final double lat;

    private final Map<String, Double> customValues;

    /**
     * Creates the ShakemapDataImpl
     * @param lon the longitude value of the point
     * @param lat the latitude value of the point
     * @param customValues a map with custom values (*that will not be copied*)
     */
    public ShakemapDataImpl(
            final double lon,
            final double lat,
            final Map<String, Double> customValues) {
        this.lon = lon;
        this.lat = lat;
        this.customValues = customValues;
    }

    @Override
    public double getLat() {
        return lat;
    }

    @Override
    public double getLon() {
        return lon;
    }

    @Override
    public Map<String, Double> getCustomValues() {
        return customValues;
    }
}
