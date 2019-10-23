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
import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Function to create a GenericFileDataBinding from a byte array.
 */
public class ConvertBytesToGenericFileDataBinding
        implements IConvertByteArrayToIData<GenericFileDataBinding> {

    private static final long serialVersionUID = 3394953481818065813L;

    /**
     * The most generic mime type for any kind of file.
     */
    private static final String MOST_GENERIC_MIME_TYPE =
            "application/octet-stream";

    /**
     * Mime-Type of the instance.
     */
    private final String mimeType;

    /**
     * Constructor with a mime type.
     * @param aMimeType mime type of the file that should be read
     */
    public ConvertBytesToGenericFileDataBinding(final String aMimeType) {
        this.mimeType = aMimeType;
    }

    /**
     * Constructor with most generic mime type.
     */
    public ConvertBytesToGenericFileDataBinding() {
        this(MOST_GENERIC_MIME_TYPE);
    }

    /**
     * Generates the GenericFileDataBinding from the byte array.
     * @param content byte array to convert
     * @return GenericFileDataBinding
     * @throws ConvertToIDataException exception that is thrown in case of an
     * io exception
     */
    @Override
    public GenericFileDataBinding convertToIData(
            final byte[] content) throws ConvertToIDataException {

        try {
            final File tempFile = File.createTempFile(
                    "convertBytesToIData", ".dat");
            tempFile.deleteOnExit();

            try (FileOutputStream fileOutputStream =
                         new FileOutputStream(tempFile)) {
                IOUtils.write(content, fileOutputStream);
            }

            return new GenericFileDataBinding(
                    new GenericFileData(tempFile, mimeType));

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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConvertBytesToGenericFileDataBinding that =
                (ConvertBytesToGenericFileDataBinding) o;
        return Objects.equals(mimeType, that.mimeType);
    }

    /**
     *
     * @return hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(mimeType);
    }
}
