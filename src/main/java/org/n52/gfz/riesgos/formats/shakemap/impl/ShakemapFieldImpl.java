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
 * Implementation for a field entry
 */
public class ShakemapFieldImpl implements IShakemapField {

    private static final String LON = "LON";
    private static final String LAT = "LAT";

    private final int index;
    private final String name;
    private final String units;

    /**
     * Creates a new field-record
     * @param index index of the field (one-based)
     * @param name name of the field
     * @param units units of the field
     */
    public ShakemapFieldImpl(
            final int index,
            final String name,
            final String units) {
        this.index = index;
        this.name = name;
        this.units = units;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUnits() {
        return units;
    }

    @Override
    public boolean isLon() {
        return LON.equals(name);
    }

    @Override
    public boolean isLat() {
        return LAT.equals(name);
    }

    @Override
    public boolean isCustom() {
        return (! isLat() && ! isLon());
    }
}
