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

package org.n52.gfz.riesgos.formats.wms.generators;

import org.geotools.coverage.grid.GridCoverage2D;
import org.n52.gfz.riesgos.settings.RiesgosWpsSettings;
import org.n52.gfz.riesgos.util.CoverageUtils;
import org.n52.gfz.riesgos.util.StringUtils;
import org.n52.gfz.riesgos.util.geoserver.exceptions.UnableToCreateCoverageException;
import org.n52.gfz.riesgos.util.geoserver.exceptions.UnableToCreateWorkspaceException;
import org.n52.gfz.riesgos.util.geoserver.exceptions.WorkspaceAlreadyExistsException;
import org.n52.gfz.riesgos.util.geoserver.impl.GeoserverRestApiClient;
import org.n52.wps.io.data.GenericFileDataWithGT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Alternative to the
 * org.n52.wps.io.datahandler.generator.GeoserverWMSGenerator
 *
 * that uses our riesgos config instead.
 */
public class RiesgosWmsGenerator {

    /**
     * The logger to log if bad things happen.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(RiesgosWmsGenerator.class);


    /**
     * Upload the grid to a geoserver & return the get map url.
     * @param gridCoverage2D grid to upload as tiff.
     * @return get map url
     * @throws UnableToCreateWorkspaceException if something bad happens on
     *                                          creating the workspace
     * @throws UnableToCreateCoverageException if something bas happens on
     *                                         creating the coverage
     */
    public String storeGridAndReturnGetMapUrl(
            final GridCoverage2D gridCoverage2D
    ) throws UnableToCreateWorkspaceException, UnableToCreateCoverageException {
        LOGGER.debug("Start with sending a coverage to the geoserver");
        final GenericFileDataWithGT fileData = new GenericFileDataWithGT(
                gridCoverage2D, null
        );
        final File file = fileData.getBaseFile(true);

        final String layerName = StringUtils.makeUniqueFileName(file.getName());
        LOGGER.debug("Prepared file with unique filename");

        final String workspace = RiesgosWpsSettings
                .INSTANCE
                .getGeoserverWorkspace();

        final GeoserverRestApiClient geoserverClient =
                new GeoserverRestApiClient(
                 RiesgosWpsSettings.INSTANCE.getGeoserverSendBaseUrl(),
                 RiesgosWpsSettings.INSTANCE.getGeoserverUsername(),
                 RiesgosWpsSettings.INSTANCE.getGeoserverPassword()
                );

        LOGGER.debug("Start to create workspace");
        LOGGER.debug(workspace);

        try {
            geoserverClient.createWorkspace(workspace);
        } catch (
                WorkspaceAlreadyExistsException
                        alreadyExistingWorkspaceException
        ) {
            LOGGER.debug("Workspace exists already - fine as well");
        }
        LOGGER.debug("Sure that we have the workspace");

        LOGGER.debug("Start sending the coverage");
        geoserverClient.createCoverage(file, workspace, layerName);
        LOGGER.debug("Finished sending the coverage");

        final String bboxString =
                CoverageUtils.extractBBoxString(gridCoverage2D);


        final String srsString = CoverageUtils.extractSRSString(gridCoverage2D);

        final int width = gridCoverage2D.getRenderedImage().getWidth();
        final int height = gridCoverage2D.getRenderedImage().getHeight();

        return
                RiesgosWpsSettings.INSTANCE.getGeoserverAccessBaseUrl()
                + "/wms?Service=WMS&Request=GetMap&Version=1.1.1&layers="
                + workspace + ":" + layerName
                + "&width=" + width + "&height=" + height
                + "&format=image/png"
                + "&bbox=" + bboxString + "&srs=" + srsString;
    }
}
