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

import org.n52.gfz.riesgos.formats.shakemap.IShakemapField;

/**
 * Implementation for a field entry.
 */
public class ShakemapFieldImpl implements IShakemapField {

    /**
     * String with LON to check if the data is
     * the longitude column.
     */
    private static final String LON = "LON";
    /**
     * String with LAT to check if the data
     * is the latitude column.
     */
    private static final String LAT = "LAT";

    /**
     * Stores the index of the column (one-based).
     */
    private final int index;
    /**
     * Stores the name of the column.
     */
    private final String name;
    /**
     * Stores the unit of the column.
     */
    private final String units;

    /**
     * Creates a new field-record.
     * @param aIndex index of the field (one-based)
     * @param aName name of the field
     * @param aUnits units of the field
     */
    public ShakemapFieldImpl(
            final int aIndex,
            final String aName,
            final String aUnits) {
        this.index = aIndex;
        this.name = aName;
        this.units = aUnits;
    }

    /**
     * Returns the index (one based).
     * @return index of the column (one based)
     */
    @Override
    public int getIndex() {
        return index;
    }

    /**
     * Returns the name of the column.
     * @return name of the column
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the unit of the column.
     * @return unit of the column
     */
    @Override
    public String getUnit() {
        return units;
    }

    /**
     *
     * @return true if it is the longitude column
     */
    @Override
    public boolean isLon() {
        return LON.equals(name);
    }

    /**
     *
     * @return true if it is the latitude column
     */
    @Override
    public boolean isLat() {
        return LAT.equals(name);
    }

    /**
     *
     * @return true if it is neither a longitude nor
     * a latitude column
     */
    @Override
    public boolean isCustom() {
        return (!isLat() && (!isLon()));
    }
}
