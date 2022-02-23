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

package org.n52.gfz.riesgos.formats.shakemap.generators;

import org.apache.commons.io.IOUtils;
import org.geotools.coverage.grid.GridCoverage2D;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.IMimeTypeAndSchemaConstants;
import org.n52.gfz.riesgos.formats.shakemap.IShakemap;
import org.n52.gfz.riesgos.formats.shakemap.binding.ShakemapXmlDataBinding;
import org.n52.gfz.riesgos.formats.shakemap.functions.DoubleGridToFloat;
import org.n52.gfz.riesgos.formats.shakemap.functions.ShakemapToGridCoverageForRegularGrid;
import org.n52.gfz.riesgos.formats.wms.generators.RiesgosWmsGenerator;
import org.n52.gfz.riesgos.util.geoserver.exceptions.GeoserverClientException;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * This is the generator for the WMS output for shakemaps.
 * Most of the work is done with transformer functions and
 * a wrapped general wms output generator.
 */
public class ShakemapWMSGenerator extends AbstractGenerator {

    /**
     * Logger for the Generator.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ShakemapWMSGenerator.class);
    /**
     * Function to convert the shakemap to a grid.
     * In this case this first creates a grid with several bands
     * (one for pga, one for the stdpga) all with doubles.
     *
     * However having this double values is not meaningful for
     * the WMS visualization.
     *
     * The usgs has done some documentation for the intensities
     * (based on the pga) and the colors.
     * This is done in the PgaShakemapToIntensityRgbGrid function.
     */
    private static final Function<IShakemap, GridCoverage2D> TO_GRID =
            new ShakemapToGridCoverageForRegularGrid().andThen(
                    new DoubleGridToFloat());

    /**
     * The basic wms format.
     */
    private static final FormatEntry WMS =
            DefaultFormatOption.WMS.getFormat();

    /**
     * Default constructor.
     */
    public ShakemapWMSGenerator() {
        super();

        supportedIDataTypes.add(ShakemapXmlDataBinding.class);
        supportedFormats.add(WMS.getMimeType());
        supportedEncodings.add(IMimeTypeAndSchemaConstants.DEFAULT_ENCODING);

        formats.add(WMS);
    }


    /**
     *
     * @param data the data
     * @param mimeType the mime type of the data
     * @param schema the schema of the data
     * @return an <code>InputStream</code> containing the data
     * @throws IOException if the <code>InputStream</code> cannot be created
     *
     * generates final output data produced by an IAlgorithm
     * and returns an InputStream for subsequent access.
     *
     */
    @Override
    public InputStream generateStream(
            final IData data,
            final String mimeType,
            final String schema)

            throws IOException {

        if (data instanceof ShakemapXmlDataBinding) {
            final ShakemapXmlDataBinding binding =
                    (ShakemapXmlDataBinding) data;
            final IShakemap shakemap = binding.getPayloadShakemap();

            final GridCoverage2D gridCoverage = TO_GRID.apply(shakemap);

            try {
                return IOUtils.toInputStream(
                        new RiesgosWmsGenerator().storeGridAndReturnGetMapUrl(
                                gridCoverage
                        )
                );
            } catch (GeoserverClientException geoserverClientException) {
                throw new IOException(geoserverClientException);
            }
        } else {
            LOGGER.error(
                    "Can't convert another data binding "
                            + "as ShakemapXmlDataBinding");
        }
        return null;
    }
}
