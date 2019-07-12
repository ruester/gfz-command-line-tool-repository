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

package org.n52.gfz.riesgos.algorithm;

import org.n52.gfz.riesgos.cache.ICacher;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorOutputData;

import java.util.List;

/**
 * Interface for creating a process that gives
 * back all the same output data
 * but only reads the content from the cache.
 */
public interface ICachableProcess {

    /**
     * Returns the cache that is used to store
     * the normal results of the inner process.
     * @return cache
     */
    ICacher getCache();

    /**
     * Returns a list with the output identifiers
     * of the inner process.
     * @return List with the output identifiers.
     */
    List<String> getOutputIdentifiers();

    /**
     * Returns a map with the class of the outputs
     * by their identifiers.
     * @param id identifier of the output parameter
     * @return binding class of the id
     */
    Class<?> getOutputDataType(String id);

    /**
     * Returns a list of the output parameters
     * for the generation of the process description.
     * @return list with the names and formats of the output data
     */
    List<IProcessDescriptionGeneratorOutputData>
    getOutputDataForProcessGeneration();
}
