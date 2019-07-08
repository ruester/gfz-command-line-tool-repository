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
import org.n52.gfz.riesgos.cache.dockerimagehandling.DockerImageIdLookup;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.wps.io.data.IData;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This is the singleton implementation to
 * store all the data of the cache in on
 * on memory "database".
 */
public enum CacheSingleton implements ICacher {

    /**
     * Singleton instance.
     */
    INSTANCE;

    /**
     * The cache implementation that is used for all the work.
     */
    private final ICacher innerCacher;


    /**
     * Constructor for assigning the internal cacher.
     */
    CacheSingleton() {
        innerCacher = new CacheImpl(new DockerImageIdLookup());
    }

    /**
     * This is the method to ask the caching system about if it has data in it.
     * If there is no data for in the caching system, than it will just
     * give back Optional.empty
     *
     * @param configuration configuration that is used to process the data
     * @param inputData     input data
     * @return optional map with output data
     */
    @Override
    public Optional<Map<String, IData>> getCachedResult(
            final IConfiguration configuration,
            final Map<String, List<IData>> inputData) {
        return innerCacher.getCachedResult(configuration, inputData);
    }

    /**
     * This is the method to call once the algorithm is done and the
     * result should be included in the caching system.
     *
     * @param configuration configuration used to process the data
     * @param inputData     input data
     * @param outputData    resulting data to store
     */
    @Override
    public void insertResultIntoCache(
            final IConfiguration configuration,
            final Map<String, List<IData>> inputData,
            final Map<String, IData> outputData) {

        innerCacher.insertResultIntoCache(configuration, inputData, outputData);

    }
}
