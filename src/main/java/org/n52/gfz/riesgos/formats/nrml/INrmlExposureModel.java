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
 * This is the interface for the sub elements
 * of the exposure model.
 */
public interface INrmlExposureModel {

    /**
     *
     * @return element with the description
     */
    INrmlDescription getDescription();

    /**
     *
     * @return element with the conversions
     */
    INrmlConversions getConversions();

    /**
     *
     * @return element with the assets
     */
    INrmlAssets getAssets();

    /**
     *
     * @return string with the id
     */
    String getId();

    /**
     *
     * @return string with the category
     */
    String getCategory();

    /**
     *
     * @return string with the taxonomy source
     */
    String getTaxonomySource();
}
