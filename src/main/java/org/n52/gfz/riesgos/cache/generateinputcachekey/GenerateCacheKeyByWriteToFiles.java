/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.n52.gfz.riesgos.cache.generateinputcachekey;

import org.n52.gfz.riesgos.cache.IFunctionToGenerateCacheKey;
import org.n52.gfz.riesgos.cache.inputparametercachekey.InputParameterCacheKeyByByteArrayList;
import org.n52.gfz.riesgos.cache.inputparametercachekey.InputParameterCacheKeyByException;
import org.n52.gfz.riesgos.cache.IInputParameterCacheKey;
import org.n52.gfz.riesgos.cmdexecution.IExecutionContext;
import org.n52.gfz.riesgos.cmdexecution.IExecutionRun;
import org.n52.gfz.riesgos.exceptions.ConvertToBytesException;
import org.n52.gfz.riesgos.functioninterfaces.IWriteIDataToFiles;
import org.n52.wps.io.data.IData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation to generate a cache key for input parameters
 * that are given to the program via input files.
 *
 * @param <T> class that extends IData
 */
public class GenerateCacheKeyByWriteToFiles<T extends IData>
        implements IFunctionToGenerateCacheKey<T> {

    /**
     * Pseudo working directory that should not be used.
     */
    private static final String PSEUDO_WORK_DIR = "/tmp";
    /**
     * Pseudo file path that should not be used.
     */
    private static final String PSEUDO_PATH = "file";

    /**
     * Function to write IData to files.
     * Used to write to an internal byte array
     * manager.
     */
    private final IWriteIDataToFiles<T> writeIDataToFiles;

    /**
     * Constructor with a IWriteIDataToFiles.
     * @param aWriteIDataToFiles function to write the idata to files
     */
    public GenerateCacheKeyByWriteToFiles(
            final IWriteIDataToFiles<T> aWriteIDataToFiles) {
        this.writeIDataToFiles = aWriteIDataToFiles;
    }

    /**
     *
     * @param idata data to compute a cache key for
     * @return InputParameterCacheKeyByByteArrayList
     */
    @Override
    public IInputParameterCacheKey generateCacheKey(final T idata) {

        final CollectorExecutionContext context =
                new CollectorExecutionContext();
        try {
            writeIDataToFiles.writeToFiles(
                    idata,
                    context,
                    PSEUDO_WORK_DIR,
                    PSEUDO_PATH);
            return new InputParameterCacheKeyByByteArrayList(context.contents);
        } catch (final IOException | ConvertToBytesException exception) {
            return new InputParameterCacheKeyByException(exception);
        }
    }


    /**
     * Inner class that is used as IExecutionContext implementation.
     * It is just used to read all the files in and store it in byte
     * arrays.
     */
    private class CollectorExecutionContext implements IExecutionContext {

        /**
         * List with the contents of the byte arrays.
         */
        private final List<byte[]> contents;

        /**
         * Constructor without arguments.
         */
        CollectorExecutionContext() {
            contents = new ArrayList<>();
        }

        /**
         * It will not close any resource.
         */
        @Override
        public void close() {
            // nothing
        }

        /**
         * This will not run any executable.
         *
         * @return null
         */
        @Override
        public IExecutionRun run() {
            // nothing to return
            return null;
        }

        /**
         * Function will not be used.
         *
         * @param path path of a file
         * @return useless empty byte array
         */
        @Override
        public byte[] readFromFile(final String path) {
            // nothing to read here
            return new byte[0];
        }

        /**
         * This is the only method in that interface that is used.
         * It will just add the byte array to a list.
         *
         * @param content    byte array with the data
         * @param workingDir working directory to write to
         * @param fileName   filename in the working directory
         */
        @Override
        public void writeToFile(
                final byte[] content,
                final String workingDir,
                final String fileName) {
            contents.add(content);
        }
    }
}
