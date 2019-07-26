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

package org.n52.gfz.riesgos.cache.inputparametercachekey;

import org.n52.gfz.riesgos.cache.IInputParameterCacheKey;

import java.util.Objects;

/**
 * Implementation of the input parameter cache key.
 * This is a fallback mode only for cases where
 * there is a problem on reading the content from the idata.
 *
 * (Also used for optional parameters).
 */
public class InputParameterCacheKeyByException
        implements IInputParameterCacheKey {

    private static final long serialVersionUID = 4303266509490539979L;

    /**
     * Exception that is wrapped.
     */
    private final Exception exception;
    /**
     * Path of the input.
     */
    private final String path;
    /**
     * Value if the input is optional.
     */
    private final boolean isOptional;

    /**
     * Constructor with the exception.
     * @param aException exception to wrap
     * @param aPath path of the input
     * @param aIsOptional value if the input is optional
     */
    public InputParameterCacheKeyByException(
            final Exception aException,
            final String aPath,
            final boolean aIsOptional) {

        this.exception = aException;
        this.path = aPath;
        this.isOptional = aIsOptional;
    }

    /**
     * Tests equality.
     * @param o other object
     * @return true if both are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InputParameterCacheKeyByException that =
                (InputParameterCacheKeyByException) o;
        return Objects.equals(exception, that.exception)
                && Objects.equals(path, that.path)
                && isOptional == that.isOptional;
    }

    /**
     *
     * @return hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(exception, path, isOptional);
    }
}
