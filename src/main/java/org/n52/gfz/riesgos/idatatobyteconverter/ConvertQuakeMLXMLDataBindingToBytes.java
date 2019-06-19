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

package org.n52.gfz.riesgos.idatatobyteconverter;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.gfz.riesgos.formats.quakeml.binding.QuakeMLXmlDataBinding;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ConvertQuakeMLXMLDataBindingToBytes implements IConvertIDataToByteArray<QuakeMLXmlDataBinding> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertQuakeMLXMLDataBindingToBytes.class);

    @Override
    public byte[] convertToBytes(final QuakeMLXmlDataBinding binding) {
        final XmlObject xmlObject = binding.getPayload();

        final XmlOptions options = new XmlOptions();
        options.setUseDefaultNamespace();

        final String strContent = xmlObject.xmlText(options);

        return strContent.getBytes();
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
