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

package org.n52.gfz.riesgos.cache.impl;


import org.n52.gfz.riesgos.cache.ICacher;
import org.n52.wps.io.data.IData;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the caching mechanism.
 *
 * TODO: Things that are not supported at the moment:
 * - The full mechanism of how to read the data (at the moment
 *   the mechanism is the same for elements that are read from files
 *   and elements that are read from files but given to the program
 *   as command line arguments)
 * - The output data mechanism can't handle temporary files at the moment.
 *   This is especially true for the current handling of shapefiles.
 * - The whole mechanism should be permanent (so store the data in a database).
 * - Test with optional input parameters must be written.
 * - The wohle cache fills over the time and no key is forgotten. This may
 *   lead to OutOfMemory Errors.
 */
public class CacheImpl implements ICacher {

    /**
     * Map to save the data.
     */
    private final Map<Object, Map<String, IData>> cache;


    /**
     * Constructor without parameters.
     */
    public CacheImpl() {

        cache = new HashMap<>();
    }

    /**
     * This is the method to ask the caching system about if it has data in it.
     * If there is no data for in the caching system, than it will just give
     * back Optional.empty
     *
     * @param hash hash that is computed from the configuration and
     *             the input data
     * @return optional map with output data
     */
    @Override
    public Optional<Map<String, IData>> getCachedResult(
            final Object hash) {

        if (cache.containsKey(hash)) {
            return Optional.ofNullable(cache.get(hash));
        }
        return Optional.empty();
    }

    /**
     * This is the method to call once the algorithm is done and the
     * result should be included in the caching system.
     *
     * @param hash hash that is computed from the configuration and the
     *             input data
     * @param outputData    resulting data to store
     */
    @Override
    public void insertResultIntoCache(
            final Object hash,
            final Map<String, IData> outputData) {

        cache.put(hash, outputData);
    }


}
