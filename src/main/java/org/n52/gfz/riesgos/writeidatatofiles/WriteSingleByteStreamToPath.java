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
 *
 *
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
 */
public class WriteSingleByteStreamToPath implements IWriteIDataToFiles {

    public IConvertIDataToByteArray converter;

    /**
     *
     * @param converter Function to convert the iData to byte[]
     */
    public WriteSingleByteStreamToPath(final IConvertIDataToByteArray converter) {
        this.converter = converter;
    }

    @Override
    public void writeToFiles(IData iData, IExecutionContext context, String workingDirectory, String path) throws ConvertToBytesException, IOException {
        final byte[] content = converter.convertToBytes(iData);
        context.writeToFile(content, workingDirectory, path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WriteSingleByteStreamToPath that = (WriteSingleByteStreamToPath) o;
        return Objects.equals(converter, that.converter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(converter);
    }
}
