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
 */

package org.n52.gfz.riesgos.formats.json.parsers;

import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.parser.AbstractParser;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Parser for json input.
 */
public class JsonParser
        extends AbstractParser {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JsonParser.class);


    /**
     * Helper class that contains all the logic for the json parser.
     */
    private final JsonParserLogic logic;

    /**
     * This is the default constructor for the JsonParser.
     */
    public JsonParser() {
        super();

        final FormatEntry json = DefaultFormatOption.JSON.getFormat();
        supportedIDataTypes.add(JsonDataBinding.class);
        supportedFormats.add(json.getMimeType());
        supportedEncodings.add(json.getEncoding());
        formats.add(json);

        logic = new JsonParserLogic();
    }

    /**
     * Parses the stream to a JsonDataBinding.
     * @param stream stream with the content
     * @param mimeType mimeType of the content
     * @param schema schema of the content
     * @return JsonDataBinding
     */
    @Override
    public IData parse(
            final InputStream stream,
            final String mimeType,
            final String schema) {
        return logic.parse(stream);
    }
}
