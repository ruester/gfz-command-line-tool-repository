/*
 * Copyright (C) 2019-2022 GFZ German Research Centre for Geosciences
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
import org.n52.gfz.riesgos.util.geoserver.exceptions.UnableToUploadShpException;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    /**
     * Upload a shape file to the geoserver.
     * @param file Shapefile that will be send
     * @param workspace workspace name in that the file should be added
     * @param storeName name of the store to add
     * @return response of the request
     * @throws UnableToUploadShpException if there is an error in the
     * communication
     * @throws IOException if writing data to disk failed
     */
    String uploadShp(
            File file,
            String workspace,
            String storeName
    ) throws UnableToUploadShpException, IOException;

    /**
     * Get a list of layers for a data store.
     * @param workspace name of the workspace
     * @param storeName name of the data store
     * @return list with the names (strings) of the layers in that store
     * @throws IOException IOException if we have trouble interacting with the
     * geoserver or parsing its responses
     */
    List<String> getLayerNamesForDataStore(
            String workspace,
            String storeName
    ) throws IOException;
}
