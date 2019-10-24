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

import java.util.List;

/**
 * This is the interface for all shakemap implementations.
 *
 * All shakemaps are a regular grid
 * (there are plans to provide irregular grids,
 * but this is future development).
 */
public interface IShakemap {

    /**
     *
     * @return list with fields (lon, lat, val1, val2, ...)
     */
    List<IShakemapField> getFields();

    /**
     *
     * @return list with the data for the fields (52.5, 12.5, -3.54, 4.54, ...)
     */
    List<IShakemapData> getData();

    /**
     *
     * @return specification of the grid
     */
    IShakemapSpecification getSpecification();
}
