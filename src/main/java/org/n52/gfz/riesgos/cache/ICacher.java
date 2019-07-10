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

package org.n52.gfz.riesgos.cache;

import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.wps.io.data.IData;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This is the interface for caching the results for the base skeleton.
 */
public interface ICacher {

    /**
     * This is the method to ask the caching system about if it has data in it.
     * If there is no data for in the caching system, than it will just give back
     * Optional.empty
     * @param hash hash that is computed from the configuration and the
     *             input data
     * @return optional map with output data
     */
    Optional<Map<String, IData>> getCachedResult(final Object hash);

    /**
     * This is the method to call once the algorithm is done and the
     * result should be included in the caching system.
     * @param hash hash that is computed from the configuration and
     *             the input data
     * @param outputData resulting data to store
     */
    void insertResultIntoCache(final Object hash,
            Map<String, IData> outputData);
}
