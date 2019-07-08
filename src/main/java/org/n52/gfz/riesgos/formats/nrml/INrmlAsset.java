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

package org.n52.gfz.riesgos.formats.nrml;

/**
 * Interface for the asset.
 */
public interface INrmlAsset {

    /**
     *
     * @return id of the asset
     */
    String getId();

    /**
     *
     * @return how many entries
     */
    int getNumber();

    /**
     *
     * @return taxonomy of the asset
     */
    String getTaxonomy();

    /**
     *
     * @return sub element with the location
     */
    INrmlLocation getLocation();

    /**
     *
     * @return sub element with the costs
     */
    INrmlCosts getCosts();

    /**
     *
     * @return sub element with the occupancies
     */
    INrmlOccupancies getOccupancies();
}
