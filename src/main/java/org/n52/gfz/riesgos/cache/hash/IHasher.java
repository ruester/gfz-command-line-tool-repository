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

import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.wps.io.data.IData;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the interface for the computation of the
 * hash from the configuration and the input data
 * for a process.
 * The key that is computed here should be used as
 * keys for the caching.
 */
public interface IHasher {

    /**
     * Computes the hash for the configuration and the input data.
     * @param configuration configuration of the process that should be cached
     * @param inputData input data for the process
     * @param requestedParameters the ids of the output that the user requested
     * @return hash (unique for the combination of configuration and input data)
     */
    String hash(
            IConfiguration configuration,
            Map<String, List<IData>> inputData,
            Set<String> requestedParameters);

}
