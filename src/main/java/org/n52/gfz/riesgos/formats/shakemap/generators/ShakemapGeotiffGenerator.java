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

package org.n52.gfz.riesgos.formats.shakemap.generators;

import org.geotools.coverage.grid.GridCoverage2D;
import org.n52.gfz.riesgos.formats.IMimeTypeAndSchemaConstants;
import org.n52.gfz.riesgos.formats.shakemap.IShakemap;
import org.n52.gfz.riesgos.formats.shakemap.binding.ShakemapXmlDataBinding;
import org.n52.gfz.riesgos.formats.shakemap.functions.ShakemapToGridCoverage;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTRasterDataBinding;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.io.datahandler.generator.GeotiffGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * Generator to transform shakemaps to geotiffs
 */
public class ShakemapGeotiffGenerator extends AbstractGenerator implements IMimeTypeAndSchemaConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShakemapGeotiffGenerator.class);
    private static final Function<IShakemap, GridCoverage2D> TO_GRID =
            new ShakemapToGridCoverage();

    /**
     * Default constructor
     */
    public ShakemapGeotiffGenerator() {
        super();

        supportedIDataTypes.add(ShakemapXmlDataBinding.class);
        supportedEncodings.add(DEFAULT_ENCODING);
        supportedEncodings.add(ENCODING_BASE64);
        supportedFormats.add(MIME_TYPE_GEOTIFF);
        formats.add(new FormatEntry(MIME_TYPE_GEOTIFF, null, DEFAULT_ENCODING, true));
        formats.add(new FormatEntry(MIME_TYPE_GEOTIFF, null, ENCODING_BASE64, true));
    }

    @Override
    public InputStream generateStream(final IData data, final String mimeType, final String schema) throws IOException {
        if(data instanceof ShakemapXmlDataBinding) {
            final ShakemapXmlDataBinding binding = (ShakemapXmlDataBinding) data;
            final IShakemap shakemap = binding.getPayloadShakemap();

            final GridCoverage2D gridCoverage = TO_GRID.apply(shakemap);

            return new GeotiffGenerator().generateStream(new GTRasterDataBinding(gridCoverage), mimeType, schema);
        } else {
            LOGGER.error("Can't convert another data binding as ShakemapXmlDataBinding");
        }
        return null;
    }
}
