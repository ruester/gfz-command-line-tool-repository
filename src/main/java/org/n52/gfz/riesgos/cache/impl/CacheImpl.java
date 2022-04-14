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
import com.google.common.cache.Weigher;
import org.n52.gfz.riesgos.cache.ICacher;
import org.n52.gfz.riesgos.cache.IDataRecreator;
import org.n52.gfz.riesgos.settings.RiesgosWpsSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the caching mechanism.
 *
 */
public class CacheImpl implements ICacher {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(CacheImpl.class);

    /**
     * Convert number between bytes and kilobytes.
     */
    private static final int CONVERT_BYTES = 1024;

    /**
     * Maximum duration to store the
     * data in the cache.
     */
    private static final long MAX_DURATION_DAYS = 60;

    /**
     * Map to save the data.
     */
    private final Cache<String, Map<String, IDataRecreator>> cache;

    /**
     * Constructor without parameters.
     */
    public CacheImpl() {
        cache = CacheBuilder.newBuilder()
            .maximumWeight(
                RiesgosWpsSettings.INSTANCE.getMaxCacheSizeMb()
            )
            .weigher(new Weigher<String, Map<String, IDataRecreator>>() {
                public int weigh(
                        final String key,
                        final Map<String, IDataRecreator> cacheOne
                ) {
                    int weight = 0;

                    /* The weigh function will only be called when adding a
                     * new entry to the cache and the cacheOne variable only
                     * contains this one new entry. */
                    for (IDataRecreator entry : cacheOne.values()) {
                        int mb = entry.getSizeInBytes()
                            / CONVERT_BYTES / CONVERT_BYTES;

                        if (mb == 0) {
                            mb = 1;
                        }

                        weight += mb;

                        LOGGER.debug(
                            "Weight of new entry for cache: "
                            + String.valueOf(weight)
                        );
                    }

                    return weight;
                }
            })
            .expireAfterAccess(MAX_DURATION_DAYS, TimeUnit.DAYS)
            .build();
    }

    /**
     * Helper function to get the current memory usage of the cache in MB.
     * @return current memory usage of the cache in MB
     */
    public int getCacheSizeMb() {
        int sum = 0;

        for (Map<String, IDataRecreator> m : cache.asMap().values()) {
            for (IDataRecreator i : m.values()) {
                int one = i.getSizeInBytes() / CONVERT_BYTES / CONVERT_BYTES;
                if (one == 0) {
                    one = 1;
                }
                sum += one;
            }
        }

        return sum;
    }

    /**
     * Helper function to log the current entries of the cache and their sizes.
     */
    public void logCacheEntries() {
        for (Map<String, IDataRecreator> m : cache.asMap().values()) {
            for (IDataRecreator i : m.values()) {
                int one = i.getSizeInBytes() / CONVERT_BYTES / CONVERT_BYTES;
                if (one == 0) {
                    one = 1;
                }
                LOGGER.info(one + " MB for entry: " + m.toString());
            }
        }

        LOGGER.info("Total MB used by cache: " + getCacheSizeMb());
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
            final Map<String, IDataRecreator> outputData
    ) {
        // cleanup cache before adding the new entry
        // (removing old entries if cache is quite full)
        cache.cleanUp();
        cache.put(hash, outputData);
    }
}
