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
import org.n52.gfz.riesgos.exceptions.ConvertToBytesException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;

/**
 * Is the same as ConvertGenericXMLDataBindingToBytes,
 * but here an addional fitlering applies.
 * There should be no header for the xml specifying the encoding.
 * This is a very specific adjustment, that I hope I can remove in
 * the next time
 */
public class ConvertGenericXMLDataBindingToBytesWithoutHeader implements IConvertIDataToByteArray {

    private static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    @Override
    public byte[] convertToBytes(IData iData) throws ConvertToBytesException {
        if(iData instanceof GenericXMLDataBinding) {
            final GenericXMLDataBinding binding = (GenericXMLDataBinding) iData;
            final XmlObject xmlObject = binding.getPayload();
            final String strContent = removeHeader(xmlObject.xmlText());
            return strContent.getBytes();
        } else {
            throw new ConvertToBytesException("Wrong binding class");
        }
    }

    private String removeHeader(final String text) {
        return text.replace(HEADER, "");

    }
}
