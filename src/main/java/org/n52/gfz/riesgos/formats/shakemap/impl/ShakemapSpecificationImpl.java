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

import org.n52.gfz.riesgos.formats.shakemap.IShakemapSpecification;

public class ShakemapSpecificationImpl implements IShakemapSpecification {

    private final double latMax;
    private final double latMin;
    private final double lonMax;
    private final double lonMin;
    private final int nLat;
    private final int nLon;
    private final double nominalLatSpacing;
    private final double nominalLonSpacing;
    private final boolean regularGrid;

    public ShakemapSpecificationImpl(
            final double latMax,
            final double latMin,
            final double lonMax,
            final double lonMin,
            final int nLat,
            final int nLon,
            final double nominalLatSpacing,
            final double nominalLonSpacing,
            final boolean regularGrid) {
        this.latMax = latMax;
        this.latMin = latMin;
        this.lonMax = lonMax;
        this.lonMin = lonMin;
        this.nLat = nLat;
        this.nLon = nLon;
        this.nominalLatSpacing = nominalLatSpacing;
        this.nominalLonSpacing = nominalLonSpacing;
        this.regularGrid = regularGrid;
    }

    @Override
    public double getLatMax() {
        return latMax;
    }

    @Override
    public double getLatMin() {
        return latMin;
    }

    @Override
    public double getLonMax() {
        return lonMax;
    }

    @Override
    public double getLonMin() {
        return lonMin;
    }

    @Override
    public int getNLat() {
        return nLat;
    }

    @Override
    public int getNLon() {
        return nLon;
    }

    @Override
    public double getNominalLatSpacing() {
        return nominalLatSpacing;
    }

    @Override
    public double getNominalLonSpacing() {
        return nominalLonSpacing;
    }

    @Override
    public boolean isRegular() {
        return regularGrid;
    }
}
