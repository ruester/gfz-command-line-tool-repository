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

package org.n52.gfz.riesgos.formats.shakemap;

/**
 * Interface for a field in the shakemap grid_field tags
 */
public interface IShakemapField {

    /**
     *
     * @return index of the field (one based)
     */
    int getIndex();

    /**
     *
     * @return name of the field
     */
    String getName();

    /**
     *
     * @return units of the field (for example dd for lat and lon, m for height)
     */
    String getUnits();

    /**
     *
     * @return true, if it is field for the longitude
     */
    boolean isLon();

    /**
     *
     * @return true, if it is the field for the latitude
     */
    boolean isLat();

    /**
     *
     * @return true, if it is a custom field (not lat and not lon)
     */
    boolean isCustom();
}
