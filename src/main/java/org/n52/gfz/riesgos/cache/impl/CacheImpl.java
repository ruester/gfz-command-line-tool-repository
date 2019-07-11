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


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.n52.gfz.riesgos.cache.ICacher;
import org.n52.gfz.riesgos.cache.IDataRecreator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the caching mechanism.
 *
 */
public class CacheImpl implements ICacher {

    private static final long MAX_SIZE = 50_000L;
    private static final long MAX_DURATION_DAYS = 60;

    /**
     * Map to save the data.
     */
    private final Cache<String, Map<String, IDataRecreator>> cache;


    /**
     * Constructor without parameters.
     */
    public CacheImpl() {
        cache = CacheBuilder.newBuilder().maximumSize(MAX_SIZE).expireAfterAccess(MAX_DURATION_DAYS, TimeUnit.DAYS).build();
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
    public Optional<Map<String, IDataRecreator>> getCachedResult(
            final String hash) {


        if (cache.asMap().containsKey(hash)) {
            return Optional.ofNullable(cache.getIfPresent(hash));
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
            final String hash,
            final Map<String, IDataRecreator> outputData) {

        cache.put(hash, outputData);
    }


}
