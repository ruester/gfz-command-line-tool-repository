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

package org.n52.gfz.riesgos.formats.jsonfile.parsers;

import org.apache.commons.io.IOUtils;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.jsonfile.binding.JsonFileBinding;
import org.n52.gfz.riesgos.formats.jsonfile.binding.JsonFileData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.parser.AbstractParser;
import org.n52.wps.webapp.api.FormatEntry;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser for json file input.
 */
public class JsonFileParser extends AbstractParser {

    /**
     * This is the default constructor for the JsonFileParser.
     */
    public JsonFileParser() {
        super();

        final FormatEntry json = DefaultFormatOption.JSON.getFormat();
        supportedIDataTypes.add(JsonFileBinding.class);
        supportedFormats.add(json.getMimeType());
        supportedEncodings.add(json.getEncoding());
        formats.add(json);
    }

    /**
     * Parses the stream to a JsonFileBinding.
     * @param stream stream with the content
     * @param mimeType mimeType of the content
     * @param schema schema of the content
     * @return JsonFileBinding
     */
    @Override
    public IData parse(
            final InputStream stream,
            final String mimeType,
            final String schema
    ) {
        try {
            final byte[] content = IOUtils.toByteArray(stream);
            return new JsonFileBinding(
                JsonFileData.fromUncompressedBytes(content)
            );
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
