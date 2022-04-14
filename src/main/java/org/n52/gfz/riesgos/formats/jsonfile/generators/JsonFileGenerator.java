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

package org.n52.gfz.riesgos.formats.jsonfile.generators;

import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.jsonfile.binding.JsonFileBinding;
import org.n52.gfz.riesgos.formats.jsonfile.binding.JsonFileData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Generator for json file data.
 */
public class JsonFileGenerator extends AbstractGenerator {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JsonFileGenerator.class);

    /**
     * Default constructor.
     */
    public JsonFileGenerator() {
        super();

        final FormatEntry json = DefaultFormatOption.JSON.getFormat();
        supportedIDataTypes.add(JsonFileBinding.class);
        supportedFormats.add(json.getMimeType());
        supportedEncodings.add(json.getEncoding());
        formats.add(json);
    }

    /**
     * Generates an input stream with the content of the data.
     * @param data data binding with information to put in the stream
     * @param mimeType mime type to generate
     * @param schema schema to generate
     * @return input stream with the data
     */
    @Override
    public InputStream generateStream(
            final IData data,
            final String mimeType,
            final String schema
    ) throws IOException {
        if (data instanceof JsonFileBinding) {
            final JsonFileBinding binding = (JsonFileBinding) data;
            final JsonFileData jsonFileData = binding.getPayload();
            final byte[] content = jsonFileData.getContent();
            return new ByteArrayInputStream(content);
        } else {
            LOGGER.error(
                "Can't convert another data binding as JsonFileBinding"
            );
        }
        return null;

    }
}
