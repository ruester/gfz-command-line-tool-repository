package org.n52.gfz.riesgos.writeidatatofiles;

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

import org.n52.gfz.riesgos.cmdexecution.IExecutionContext;
import org.n52.gfz.riesgos.exceptions.ConvertToBytesException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.gfz.riesgos.functioninterfaces.IWriteIDataToFiles;
import org.n52.wps.io.data.IData;

import java.io.IOException;
import java.util.Objects;

/**
 * Implementation that just uses a function to convert the
 * IData to an byte[] and just writes that to the single given file.
 *
 * Can be used in all situations where the iData just produces one file.
 *
 * @param <T> binding class that extends the IData interface
 */
public class WriteSingleByteStreamToPath<T extends IData>
        implements IWriteIDataToFiles<T> {

    /**
     * Inner converter to convert the idata to a byte array.
     */
    private final IConvertIDataToByteArray<T> converter;

    /**
     * Default constructor with an inner converter.
     * @param aConverter Function to convert the iData to byte[]
     */
    public WriteSingleByteStreamToPath(
            final IConvertIDataToByteArray<T> aConverter) {
        this.converter = aConverter;
    }

    /**
     * Writes the content of the idata to the filesystem (maybe multiple files)
     * and maybe in a container.
     * @param iData Binding class to write
     * @param context context (maybe a container)
     * @param workingDirectory directory to copy the f
     * @param path destination of the iData
     * @throws ConvertToBytesException Exception on converting
     * the IData to byte[]
     * @throws IOException a normal IOException that may happen
     * on writing the files
     */
    @Override
    public void writeToFiles(
            final T iData,
            final IExecutionContext context,
            final String workingDirectory,
            final String path)

            throws ConvertToBytesException, IOException {

        final byte[] content = converter.convertToBytes(iData);
        context.writeToFile(content, workingDirectory, path);
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
        WriteSingleByteStreamToPath that = (WriteSingleByteStreamToPath) o;
        return Objects.equals(converter, that.converter);
    }

    /**
     *
     * @return hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(converter);
    }
}
