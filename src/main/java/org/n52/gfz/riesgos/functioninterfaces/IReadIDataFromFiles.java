package org.n52.gfz.riesgos.functioninterfaces;

import java.io.IOException;
import java.io.Serializable;

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

import org.n52.gfz.riesgos.cache.DataWithRecreatorTuple;
import org.n52.gfz.riesgos.cmdexecution.IExecutionContext;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.wps.io.data.IData;

/**
 * Interface to read the idata from one or moore files
 * (maybe on a container).
 * @param <T> Type of data
 */
public interface IReadIDataFromFiles<T extends IData> extends Serializable {

    /**
     * Reads the idata from the filesystem (maybe multiple files, maybe from a
     * container).
     * @param context context (maybe the container)
     * @param workingDirectory the working directory in which the file is /
     *                         the files are
     * @param path basic path of a single file / the main file if there are
     *             several
     * @return IData with the recreator (for a caching mechanism)
     * @throws ConvertToIDataException if the conversion is not possible this
     *                                 exception will be thrown
     * @throws IOException will be thrown if there is problem on the IO
     *                     mechanism on java
     */
    DataWithRecreatorTuple<T> readFromFiles(
        IExecutionContext context,
        String workingDirectory,
        String path
    ) throws ConvertToIDataException, IOException;
}
