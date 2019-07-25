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

package org.n52.gfz.riesgos.processdescription.impl.addformats;

import net.opengis.wps.x100.OutputDescriptionType;

/**
 * Interface to add the format parts for the output class.
 */
public interface IAddTypeDataForOutput {
    /**
     * Adds the type data to the xml.
     * @param outputDescriptionType xml with the data for the output
     */
    void addTypeData(OutputDescriptionType outputDescriptionType);
}
