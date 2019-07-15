package org.n52.gfz.riesgos.cmdexecution;

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

import java.io.IOException;

/**
 * Interface for an context to run an executable
 * Examples: a docker container or no special context (just a normal process)
 */
public interface IExecutionContext extends AutoCloseable {

    /**
     * Auto closable to maybe remove a created container
     */
    void close();

    /**
     * runs the executable
     * @return interface for the run (to provide access to stdin)
     * @throws IOException there may be an IOException on starting the run
     */
    IExecutionRun run() throws IOException;

    /**
     * Reads a file from the path (maybe out of a docker container)
     * @param path path of a file
     * @return byte array content
     * @throws IOException there may be an IOException on reading the file
     */
    byte[] readFromFile(String path) throws IOException;

    /**
     * Write the contents of a byte array to a path (maybe in a docker container)
     * @param content byte array with the data
     * @param workingDir working directory to write to
     * @param fileName filename in the working directory
     * @throws IOException there may be an IOException on writing the file
     */
    void writeToFile(byte[] content, String workingDir, String fileName) throws IOException;
}
