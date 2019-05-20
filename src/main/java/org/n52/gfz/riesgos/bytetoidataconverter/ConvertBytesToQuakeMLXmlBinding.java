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

package org.n52.gfz.riesgos.bytetoidataconverter;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.formats.quakeml.binding.QuakeMLXmlDataBinding;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.IData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * This class works as ConvertBytesToGenericXMLDataBindung but it returns a QuakeMLXmlDataBinding
 * It reads the bytes to an xml object and wraps it with a QuakeMLXmlDataBinding
 */
public class ConvertBytesToQuakeMLXmlBinding implements IConvertByteArrayToIData {

    @Override
    public IData convertToIData(final byte[] content) throws ConvertToIDataException {
        try(final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            return QuakeMLXmlDataBinding.fromValidatedXml(XmlObject.Factory.parse(byteArrayInputStream));
        } catch(final XmlException | IOException exception) {
            throw new ConvertToIDataException(exception);
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