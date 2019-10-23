package org.n52.gfz.riesgos.idatatobyteconverter;

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
 *
 *
 */


import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;

import java.util.Objects;

/**
 * Implementation to convert a GenericXMLDataBinding to a byte array
 */
public class ConvertGenericXMLDataBindingToBytes
    <T extends GenericXMLDataBinding>
    implements IConvertIDataToByteArray<T> {

    @Override
    public byte[] convertToBytes(final T binding) {
        final XmlObject xmlObject = binding.getPayload();
        final String strContent = xmlObject.xmlText();

        return strContent.getBytes();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName());
    }
}
