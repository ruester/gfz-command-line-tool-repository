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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * IInputParameterCacheKey that works for a list of contents from byte arrays.
 */
public class InputParameterCacheKeyByByteArrayList
        implements IInputParameterCacheKey {

    private static final long serialVersionUID = 8005087896116912457L;

    /**
     * List of keys for all the byte arrays.
     */
    private final List<IInputParameterCacheKey> keys;

    /**
     * Constructor with a list of byte arrays.
     * @param aContents list of byte arrays (maybe several files).
     */
    public InputParameterCacheKeyByByteArrayList(final List<byte[]> aContents) {
        this.keys = aContents.stream()
                .map(InputParameterCacheKeyByByteArray::new)
                .collect(Collectors.toList());
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
        InputParameterCacheKeyByByteArrayList that =
                (InputParameterCacheKeyByByteArrayList) o;
        return Objects.equals(keys, that.keys);
    }

    /**
     *
     * @return hashcode of the object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(keys);
    }
}
