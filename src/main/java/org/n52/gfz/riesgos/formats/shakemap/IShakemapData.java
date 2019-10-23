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

import java.util.Map;

/**
 * Interface for a row in the shakemap grid_data section.
 */
public interface IShakemapData {
    /**
     *
     * @return latitude of the point
     */
    double getLat();

    /**
     *
     * @return longitude of the point
     */
    double getLon();

    /**
     *
     * @return Map with all additional values
     */
    Map<String, Double> getCustomValues();
}
