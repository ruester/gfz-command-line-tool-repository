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
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.formats.shakemap.binding.ShakemapXmlDataBinding;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Function to convert the content of a byte array to a ShakemapXmlBinding.
 */
public class ConvertBytesToShakemapXmlBinding
        implements IConvertByteArrayToIData<ShakemapXmlDataBinding> {

    private static final long serialVersionUID = 2545645993592527378L;

    /**
     * Creates a ShakemapXmlDataBinding from the byte array.
     * @param content byte array to convert
     * @return ShakemapXmlDataBinding
     * @throws ConvertToIDataException exception an an io or xml
     * exception
     */
    @Override
    public ShakemapXmlDataBinding convertToIData(
            final byte[] content) throws ConvertToIDataException {
        try (ByteArrayInputStream byteArrayInputStream =
                     new ByteArrayInputStream(content)) {
            return ShakemapXmlDataBinding.fromXml(
                    XmlObject.Factory.parse(byteArrayInputStream));
        } catch (final XmlException | IOException exception) {
            throw new ConvertToIDataException(exception);
        }
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
