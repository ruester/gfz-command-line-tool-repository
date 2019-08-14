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

package org.n52.gfz.riesgos.formats.shakemap.impl;

/**
 * Tuple class with the range (min and max values).
 */
public class LatLonRange {

    /**
     * Minimum value.
     */
    private final double min;
    /**
     * Maximum value.
     */
    private final double max;

    /**
     * Default constructor.
     * @param aMin min value
     * @param aMax max value
     */
    public LatLonRange(
            final double aMin,
            final double aMax
    ) {
        this.min = aMin;
        this.max = aMax;
    }

    /**
     *
     * @return min value
     */
    public double getMin() {
        return min;
    }

    /**
     *
     * @return max value
     */
    public double getMax() {
        return max;
    }
}
