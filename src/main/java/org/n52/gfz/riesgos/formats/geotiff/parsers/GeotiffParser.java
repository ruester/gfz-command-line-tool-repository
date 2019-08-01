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

package org.n52.gfz.riesgos.formats.geotiff.parsers;

import org.apache.commons.io.IOUtils;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;
import org.n52.wps.io.datahandler.parser.AbstractParser;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Very simple implementation for parsing a file to
 * a Geotiff binding.
 */
public class GeotiffParser extends AbstractParser {

    /**
     * Logger for logging unexpected behaviour.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GeotiffParser.class);

    /**
     * Default constructor.
     */
    public GeotiffParser() {
        super();

        final FormatEntry geotiff = DefaultFormatOption.GEOTIFF.getFormat();
        supportedIDataTypes.add(GeotiffBinding.class);
        supportedFormats.add(geotiff.getMimeType());
        supportedEncodings.add(geotiff.getEncoding());
        formats.add(geotiff);
    }

    /**
     * Parses the input stream to a IData binding class.
     * @param input stream with the input data
     * @param mimeType mimetype of the data
     * @param schema schema of the data
     * @return GeotiffBinding
     */
    @Override
    public IData parse(
            final InputStream input,
            final String mimeType,
            final String schema) {

        try {
        final File tempFile = File.createTempFile(
                "parseGeotiff", ".tiff");
            tempFile.deleteOnExit();

            try (FileOutputStream fileOutputStream =
                         new FileOutputStream(tempFile)) {
                IOUtils.copy(input, fileOutputStream);
            }

            return new GeotiffBinding(tempFile);

        } catch (IOException exception) {
            LOGGER.error("It is not possible to parse the geotiff", exception);
        }

        return null;
    }
}
