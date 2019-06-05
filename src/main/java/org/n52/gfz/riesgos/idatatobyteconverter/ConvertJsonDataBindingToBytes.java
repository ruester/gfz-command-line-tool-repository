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

package org.n52.gfz.riesgos.idatatobyteconverter;

import org.json.simple.JSONObject;
import org.n52.gfz.riesgos.exceptions.ConvertToBytesException;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.wps.io.data.IData;

import java.util.Objects;

/**
 * Function to convert a json data binding to a byte array
 */
public class ConvertJsonDataBindingToBytes implements IConvertIDataToByteArray {

    @Override
    public byte[] convertToBytes(final IData data) throws ConvertToBytesException {
        if(data instanceof JsonDataBinding) {
            final JsonDataBinding binding = (JsonDataBinding) data;
            final JSONObject jsonObject = binding.getPayload();
            final String content = jsonObject.toJSONString();
            return content.getBytes();
        } else {
            throw new ConvertToBytesException("Wrong binding class");
        }
    }

    @Override
    public boolean equals(Object o) {
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
