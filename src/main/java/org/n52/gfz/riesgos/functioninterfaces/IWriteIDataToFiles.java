package org.n52.gfz.riesgos.functioninterfaces;

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
import org.n52.wps.io.data.IData;

import java.io.IOException;

/**
 * Interface to write the iData content to one or more files
 * (maybe on a container)
 */
@FunctionalInterface
public interface IWriteIDataToFiles<T extends IData> {

    /**
     * Writes the content of the idata to the filesystem (maybe multiple files)
     * and maybe in a container
     * @param iData Binding class to write
     * @param context context (maybe a container)
     * @param workingDirectory directory to copy the f
     * @param path destination of the iData
     * @throws ConvertToBytesException Exception on converting the IData to
     *                                 byte[]
     * @throws IOException a normal IOException that may happen on writing the
     *                     files
     */
    void writeToFiles(
            T iData,
            IExecutionContext context,
            String workingDirectory,
            String path) throws ConvertToBytesException, IOException;
}
