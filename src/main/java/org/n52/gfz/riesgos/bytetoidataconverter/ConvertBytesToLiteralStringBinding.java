package org.n52.gfz.riesgos.bytetoidataconverter;

/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 */

import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.Objects;

/**
 * Function to convert bytes to a literal string binding.
 */
public class ConvertBytesToLiteralStringBinding
        implements IConvertByteArrayToIData<LiteralStringBinding> {

    private static final long serialVersionUID = 8298315810379102433L;

    /**
     * Returns an LiteralStringBinding from the byte array.
     * @param content byte array to convert
     * @return LiteralStringBinding
     */
    @Override
    public LiteralStringBinding convertToIData(final byte[] content) {
        return new LiteralStringBinding(new String(content));
    }

    /**
     * Tests for equality.
     * @param o other object
     * @return true if both are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    /**
     *
     * @return hashcode of the instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName());
    }
}
