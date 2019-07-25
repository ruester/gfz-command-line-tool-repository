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

import org.n52.wps.io.data.IData;

/**
 * Functional interface to get a InputParameterCacheKey
 * for the input data.
 * @param <T> Type that extends IData
 */
@FunctionalInterface
public interface IFunctionToGenerateCacheKey<T extends IData> {

    /**
     * Generates a cache key for the input data
     * depending on how to process this data.
     * @param idata data to compute a cache key for
     * @return IInputParameterCacheKey for the input
     */
    IInputParameterCacheKey generateCacheKey(T idata);
}
