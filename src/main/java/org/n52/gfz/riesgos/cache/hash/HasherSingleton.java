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

package org.n52.gfz.riesgos.cache.hash;

import org.n52.gfz.riesgos.cache.dockerimagehandling.DockerImageIdLookup;
import org.n52.gfz.riesgos.cache.wpsversionhandling.StaticWpsVersionHandler;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.wps.io.data.IData;

import java.util.List;
import java.util.Map;

/**
 * Singleton implementation of the hasher.
 */
public enum HasherSingleton implements IHasher {

    /**
     * Singleton implementation.
     * Cares about the docker image ids.
     */
    INSTANCE(new HasherImpl(
            new DockerImageIdLookup(),
            new StaticWpsVersionHandler()));

    /**
     * Inner hasher instance.
     */
    private final IHasher wrappedHasher;

    /**
     * Constructor with a hasher that is used for all the tasks.
     * @param aWrappedHasher hasher implementation to use
     */
    HasherSingleton(final IHasher aWrappedHasher) {
        this.wrappedHasher = aWrappedHasher;
    }

    /**
     * Computes the hash for the configuration and the
     * input data.
     * @param configuration configuration of the process
     * @param inputData input data for the query
     * @return hash
     */
    @Override
    public String hash(
            final IConfiguration configuration,
            final Map<String, List<IData>> inputData) {
        return wrappedHasher.hash(configuration, inputData);
    }
}
