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

package org.n52.gfz.riesgos.formats.json.generators;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.formats.json.binding.JsonObjectOrArray;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

/**
 * Generator for json data.
 */
public class JsonGenerator
        extends AbstractGenerator {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JsonGenerator.class);

    /**
     * Default constructor.
     */
    public JsonGenerator() {
        super();

        final FormatEntry json = DefaultFormatOption.JSON.getFormat();
        supportedIDataTypes.add(JsonDataBinding.class);
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
            final String schema) {
        if (data instanceof JsonDataBinding) {
            final JsonDataBinding binding = (JsonDataBinding) data;
            final JsonObjectOrArray jsonObject = binding.getPayload();

            final Optional<JSONObject> asJsonObject =
                    jsonObject.getJsonObject();

            if (asJsonObject.isPresent()) {
                return new ByteArrayInputStream(
                        asJsonObject.get().toJSONString().getBytes());
            }

            final Optional<JSONArray> asJsonArray = jsonObject.getJsonArray();

            if (asJsonArray.isPresent()) {
                return new ByteArrayInputStream(
                        asJsonArray.get().toJSONString().getBytes());
            }

            LOGGER.error("JSON not an object nor an array");

        } else {
            LOGGER.error(
                    "Can't convert another data binding as JsonDataBinding");
        }
        return null;
    }
}
