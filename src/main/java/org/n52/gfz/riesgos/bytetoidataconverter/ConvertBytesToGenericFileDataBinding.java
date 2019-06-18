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
 *
 *
 */

import org.apache.commons.io.IOUtils;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * Function to create a GenericFileDataBinding from a byte array
 */
public class ConvertBytesToGenericFileDataBinding implements IConvertByteArrayToIData<GenericFileDataBinding> {

    private static final String MOST_GENERIC_MIME_TYPE = "application/octet-stream";

    private final String mimeType;

    /**
     * Constructor with a mime type
     * @param mimeType mime type of the file that should be read
     */
    public ConvertBytesToGenericFileDataBinding(final String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Constructor with most generic mime type
     */
    public ConvertBytesToGenericFileDataBinding() {
        this(MOST_GENERIC_MIME_TYPE);
    }

    @Override
    public GenericFileDataBinding convertToIData(final byte[] content) throws ConvertToIDataException {

        try {
            final File tempFile = File.createTempFile("convertBytesToIData", ".dat");
            tempFile.deleteOnExit();

            try (final FileWriter writer = new FileWriter(tempFile)) {
                IOUtils.write(content, writer);
            }

            return new GenericFileDataBinding(new GenericFileData(tempFile, mimeType));

        } catch(final IOException ioException) {
            throw new ConvertToIDataException(ioException);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConvertBytesToGenericFileDataBinding that = (ConvertBytesToGenericFileDataBinding) o;
        return Objects.equals(mimeType, that.mimeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mimeType);
    }
}
