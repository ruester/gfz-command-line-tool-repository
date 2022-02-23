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

package org.n52.gfz.riesgos.util.geoserver;

import org.n52.gfz.riesgos.util.geoserver.exceptions.UnableToCreateCoverageException;
import org.n52.gfz.riesgos.util.geoserver.exceptions.UnableToCreateWorkspaceException;

import java.io.File;

/**
 * Interface for a client to work with a geoserver.
 */
public interface IGeoserverClient {

    /**
     * Create a workspace on the geoserver.
     * @param workspace name of the workspace
     *
     * @throws UnableToCreateWorkspaceException In case we can't create the
     * workspace (for whatever reason), we throw that exception.
     * Also possible WorkspaceAlreadyExistsException: Very specific exception
     * in case the workspace exists already. Client can decide what to do then.
     */
    void createWorkspace(String workspace)
            throws
            UnableToCreateWorkspaceException;

    /**
     * Create the coverage on the geoserver.
     * @param file Raster file that we want to send.
     * @param workspace workspace name in that the file should be added
     * @param layerName name of the layer to add.
     * @throws UnableToCreateCoverageException In case something goes
     * wrong when creating this coverage we throw this exception.
     */
    void createCoverage(
            File file, String workspace, String layerName
    ) throws UnableToCreateCoverageException;
}
