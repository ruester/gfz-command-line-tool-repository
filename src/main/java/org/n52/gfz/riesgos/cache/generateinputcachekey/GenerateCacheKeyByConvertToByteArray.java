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

package org.n52.gfz.riesgos.cache.generateinputcachekey;

import org.n52.gfz.riesgos.cache.IFunctionToGenerateCacheKey;
import org.n52.gfz.riesgos.cache.inputparametercachekey.InputParameterCacheKeyByByteArray;
import org.n52.gfz.riesgos.cache.inputparametercachekey.InputParameterCacheKeyByException;
import org.n52.gfz.riesgos.cache.IInputParameterCacheKey;
import org.n52.gfz.riesgos.exceptions.ConvertToBytesException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.wps.io.data.IData;

/**
 * Implementation of a cache key generator for cases in which
 * the input data is given to stdin.
 * @param <T> class that extends IData
 */
public class GenerateCacheKeyByConvertToByteArray<T extends IData>
        implements IFunctionToGenerateCacheKey<T> {


    /**
     * Function to convert the data to a byte array.
     */
    private final IConvertIDataToByteArray<T> functionToConvertToByteArray;

    /**
     * Constructor that takes a function to convert the data to
     * a byte array.
     * @param aFunctionToConvertToByteArray function to convert the data
     */
    public GenerateCacheKeyByConvertToByteArray(
            final IConvertIDataToByteArray<T> aFunctionToConvertToByteArray) {
        this.functionToConvertToByteArray = aFunctionToConvertToByteArray;
    }

    /**
     * Generates the cache key for the input data.
     * @param idata data to compute a cache key for
     * @return InputParameterCacheKeyByByteArray
     */
    @Override
    public IInputParameterCacheKey generateCacheKey(final T idata) {

        try {
            final byte[] content =
                    functionToConvertToByteArray.convertToBytes(idata);
            return new InputParameterCacheKeyByByteArray(content);
        } catch (final ConvertToBytesException exception) {
            return new InputParameterCacheKeyByException(exception);
        }
    }
}
