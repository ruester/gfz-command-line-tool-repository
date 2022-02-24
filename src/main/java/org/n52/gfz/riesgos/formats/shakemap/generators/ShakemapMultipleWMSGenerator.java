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
import org.n52.gfz.riesgos.formats.shakemap.functions.ShakemapToGridCoverageForMultipleRegularGrids;
import org.n52.gfz.riesgos.formats.wms.generators.RiesgosWmsGenerator;
import org.n52.gfz.riesgos.util.StreamUtils;
import org.n52.gfz.riesgos.util.geoserver.exceptions.GeoserverClientException;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.DataBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This is the generator for the WMS output for shakemaps.
 * The idea here is to generate an wms for each of the intensity measurements
 * included in the shakemap.
 * Most of the work is done with transformer functions and
 * a wrapped general wms output generator.
 */
public class ShakemapMultipleWMSGenerator extends AbstractGenerator {

    /**
     * Logger for the Generator.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ShakemapMultipleWMSGenerator.class);

    /**
     * The listed intensity measurements are the one that this parser
     * supports.
     * It will return the urls for those in a comma separated list.
     */
    private static final List<String> INTENSITY_MEASUREMENTS =
            Arrays.asList("PGA", "SA(0.3)", "SA(1.0)");

    /**
     * Function to convert the shakemap to a map of grids (
     * all with double numbers, all have hteir own grid).
     *
     */
    private static final Function<IShakemap, Map<String, GridCoverage2D>>
            TO_GRIDS = new ShakemapToGridCoverageForMultipleRegularGrids(
                    DataBuffer.TYPE_FLOAT);

    /**
     * The basic wms format.
     */
    private static final FormatEntry JSON =
            DefaultFormatOption.JSON.getFormat();

    /**
     * Default constructor.
     */
    public ShakemapMultipleWMSGenerator() {
        super();

        supportedIDataTypes.add(ShakemapXmlDataBinding.class);
        supportedFormats.add(JSON.getMimeType());
        supportedEncodings.add(IMimeTypeAndSchemaConstants.DEFAULT_ENCODING);

        formats.add(JSON);
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
     * The input stream will be a json style string.
     * For example:
     * {
     *     "PGA": "http://mapserver:9999/parameter=value",
     *     "SA(0.3)": "http://mapserver:9999/other=paramater"
     * }
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

            final Map<String, GridCoverage2D> grids = TO_GRIDS.apply(shakemap);

            final Map<String, InputStream> streams = new HashMap<>();
            try {
                for (final Map.Entry<String, GridCoverage2D> entry
                        : grids.entrySet()) {
                    final String imt = entry.getKey();
                    if (INTENSITY_MEASUREMENTS.contains(imt)) {
                        final GridCoverage2D floatGrid = entry.getValue();
                        final InputStream innerInputStream =
                                IOUtils.toInputStream(
                                        new RiesgosWmsGenerator()
                                                .storeGridAndReturnGetMapUrl(
                                                        floatGrid
                                                )
                                );
                        streams.put(imt, innerInputStream);
                    }
                }

                return StreamUtils.INSTANCE.combineInputStreamsAsJsonObject(
                        streams
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
