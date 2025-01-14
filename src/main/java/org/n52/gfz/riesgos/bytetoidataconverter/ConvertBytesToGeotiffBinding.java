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

import org.apache.commons.io.IOUtils;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Function to create a GeotiffBinding from a byte array.
 */
public class ConvertBytesToGeotiffBinding
        implements IConvertByteArrayToIData<GeotiffBinding> {

    private static final long serialVersionUID = 5174079061385019321L;

    /**
     * Creates an GeotiffBinding instance from the byte array.
     * @param content byte array to convert
     * @return GeotiffBinding
     * @throws ConvertToIDataException exception on an io exception
     */
    @Override
    public GeotiffBinding convertToIData(
            final byte[] content) throws ConvertToIDataException {

        try {
            final File tempFile = File.createTempFile(
                    "convertBytesToIData", ".tiff");
            tempFile.deleteOnExit();

            try (FileOutputStream fileOutputStream =
                         new FileOutputStream(tempFile)) {
                IOUtils.write(content, fileOutputStream);
            }

            return new GeotiffBinding(tempFile);
        } catch (final IOException ioException) {
            throw new ConvertToIDataException(ioException);
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
