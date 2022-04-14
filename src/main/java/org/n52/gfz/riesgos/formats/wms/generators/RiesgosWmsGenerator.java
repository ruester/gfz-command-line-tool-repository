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

package org.n52.gfz.riesgos.formats.wms.generators;

import org.geotools.coverage.grid.GridCoverage2D;
import org.n52.gfz.riesgos.settings.RiesgosWpsSettings;
import org.n52.gfz.riesgos.util.CoverageUtils;
import org.n52.gfz.riesgos.util.StringUtils;
import org.n52.gfz.riesgos.util.geoserver.exceptions.UnableToCreateCoverageException;
import org.n52.gfz.riesgos.util.geoserver.exceptions.UnableToCreateWorkspaceException;
import org.n52.gfz.riesgos.util.geoserver.exceptions.UnableToUploadShpException;
import org.n52.gfz.riesgos.util.geoserver.exceptions.WorkspaceAlreadyExistsException;
import org.n52.gfz.riesgos.util.geoserver.impl.GeoserverRestApiClient;
import org.n52.wps.io.IOUtils;
import org.n52.wps.io.data.GenericFileDataWithGT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

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
     * Store a shapefile on the geoserver and return a url that
     * can be used as WMS client.
     * @param shp Shapefile (explicitly the .shp file)
     *            The geoserver currently only supports to upload shapefiles
     *            for vector data (no support for geojson for example).
     * @return a string to access the wms on our geoserver
     * @throws UnableToCreateWorkspaceException exception if we can't create
     * the workspace on the geoserver
     * @throws UnableToUploadShpException exception if we can't upload our
     * shapefile to the geoserver
     */
    public String storeVectorAndReturnGetMapUrl(
            final File shp
    ) throws UnableToCreateWorkspaceException, UnableToUploadShpException {
        final String path = shp.getAbsolutePath();
        final String baseName = path.substring(
                0,
                path.length() - ".shp".length()
        );
        final File shx = new File(baseName + ".shx");
        final File dbf = new File(baseName + ".dbf");
        final File prj = new File(baseName + ".prj");
        File zipped = null;

        try {

            zipped = IOUtils.zip(shp, shx, dbf, prj);

            final String storeName = StringUtils.makeUniqueFileName(
                    zipped.getName()
            );

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

            tryToCreateTheWorkspaceAndBeFineIfItAlreadyExists(
                    geoserverClient, workspace
            );

            // First we upload our file (but the answer by the geoserver is
            // not that useful. It only says it created it.
            geoserverClient.uploadShp(zipped, workspace, storeName);

            LOGGER.debug("File stored on the geoserver");
            // We need to ask explicitly for the layer names
            final List<String> layerNames =
                    geoserverClient.getLayerNamesForDataStore(
                            workspace, storeName);
            // And we only have one in this new store.
            final String layerName = layerNames.get(0);

            LOGGER.debug("Extracted the layerName");
            LOGGER.debug(layerName);

            return RiesgosWpsSettings.INSTANCE.getGeoserverAccessBaseUrl()
                    + "/wms?Service=WMS&Request=GetMap&Version=1.1.1&layers="
                    + workspace + ":" + layerName
                    + "&format=image/png";
        } catch (final IOException ioException) {
            throw new UnableToUploadShpException(ioException);
        } finally {
            // After we uploaded it, we don't need the file anymore.
            Stream.of(zipped, shp, shx, dbf, prj)
                    .filter(this::notNull)
                    .forEach(this::cleanUpFile);
        }
    }

    /**
     * Helper method to check if a value is not null.
     * @param t object to check
     * @param <T> any kind of input type
     * @return true if the object is not null, false otherwise
     */
    private <T> boolean  notNull(final T t) {
        return t != null;
    }

    /**
     * Helper method to cleanup (delete) a file.
     *
     * It will only try to delete. When an exception happens we warn about it,
     * but we don't raise other exceptions to make the rest of the geoserver
     * code go on.
     * @param file file to delete
     */
    private void cleanUpFile(final File file) {
        try {
            final boolean deleteSuccess = Files.deleteIfExists(file.toPath());
            if (!deleteSuccess) {
                LOGGER.warn("Could not delete the file {}", file.getPath());
            }
        } catch (IOException ioException) {
            LOGGER.warn("Could not delete the file {}", file.getPath());
            LOGGER.warn(ioException.getMessage());
        }
    }

    /**
     * Helper method to try to create the workspace.
     * If the workspace already exists then it is fine.
     * @param geoserverClient geoserver client
     * @param workspace name of the workspace
     * @throws UnableToCreateWorkspaceException thrown if we have other trouble
     * creating the workspace (other then that it already exists).
     */
    private void tryToCreateTheWorkspaceAndBeFineIfItAlreadyExists(
            final GeoserverRestApiClient geoserverClient,
            final String workspace
    ) throws UnableToCreateWorkspaceException {
        try {
            geoserverClient.createWorkspace(workspace);
        } catch (
                WorkspaceAlreadyExistsException
                        alreadyExistingWorkspaceException
        ) {
            LOGGER.debug("Workspace exists already - fine as well");
        }
    }

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

        tryToCreateTheWorkspaceAndBeFineIfItAlreadyExists(
                geoserverClient, workspace
        );

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
