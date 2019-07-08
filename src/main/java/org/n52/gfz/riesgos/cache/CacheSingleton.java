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
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.wps.io.data.IData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public enum CacheSingleton implements ICacher {

    INSTANCE;


    private final Map<Object, Map<String, IData>> cache;


    CacheSingleton() {
        cache = new HashMap<>();
    }

    /**
     * This is the method to ask the caching system about if it has data in it.
     * If there is no data for in the caching system, than it will just give back
     * Optional.empty
     *
     * @param configuration configuration that is used to process the data
     * @param inputData     input data
     * @return optional map with output data
     */
    @Override
    public Optional<Map<String, IData>> getCachedResult(IConfiguration configuration, Map<String, List<IData>> inputData) {

        // TODO
        // how to compute a key from the data?

        final Object key = generateCacheKey(configuration, inputData);

        if(cache.containsKey(key)) {
            return Optional.ofNullable(cache.get(key));
        }
        return Optional.empty();
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
    public void insertResultIntoCache(IConfiguration configuration, Map<String, List<IData>> inputData, Map<String, IData> outputData) {

        final Object key = generateCacheKey(configuration, inputData);
        cache.put(key, outputData);
    }

    private Object generateCacheKey(final IConfiguration configuration, final Map<String, List<IData>> inputData) {

        /*
          Ok, this is one of the most important functions here.
          It must take all the elements into account that matters here.
          And all the contents of cause.

         */

        // TODO
        // byte contents must be saved not the payloads
        // example: Shapefiles (that just point to a file
        // that maybe don't exist anymore)

        return new CacheKey(configuration, inputData);
    }

    private static class CacheKey {


        /*
        What must be taken into account?
        - the content of the input data
         */
        // TODO
        // include much more data
        // but it should already be usable in the example client
        private final String fullQualifiedIdentifier;
        private final Map<String, List<Object>> payloads;


        CacheKey(final IConfiguration configuration, final Map<String, List<IData>> inputData) {

            fullQualifiedIdentifier = configuration.getFullQualifiedIdentifier();
            payloads = inputData.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> getPayloads(entry.getValue())));
        }

        private List<Object> getPayloads(final List<IData> idatas) {
            return idatas.stream().map(IData::getPayload).collect(Collectors.toList());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CacheKey cacheKey = (CacheKey) o;
            return Objects.equals(fullQualifiedIdentifier, cacheKey.fullQualifiedIdentifier) &&
                    Objects.equals(payloads, cacheKey.payloads);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fullQualifiedIdentifier, payloads);
        }
    }
}
