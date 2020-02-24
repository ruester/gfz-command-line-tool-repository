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

package org.n52.gfz.riesgos.formats.json.parsers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.formats.json.binding.JsonObjectOrArray;
import org.n52.wps.io.data.IData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This is the class that captures all the logic for the JsonParser.
 * This is an additional class to make it easier to test it
 * (since the JsonParser needs some interaction with the WPS Server).
 */
public class JsonParserLogic {

    /**
     * Parses the stream to an JsonDataBinding.
     * @param stream stream with json
     * @return JsonDataBinding
     */
    public IData parse(
            final InputStream stream) {
        try {
            final JSONParser parser = new JSONParser();
            final Object parsed = parser.parse(new InputStreamReader(stream));
            if (parsed instanceof JSONObject) {
                final JSONObject jsonObject = (JSONObject) parsed;
                return new JsonDataBinding(new JsonObjectOrArray(jsonObject));
            } else if (parsed instanceof JSONArray) {
                final JSONArray jsonArray = (JSONArray) parsed;
                return new JsonDataBinding(new JsonObjectOrArray(jsonArray));
            }
            throw new RuntimeException(
                    "Can't parse the content to an json object");
        } catch (final IOException | ParseException exception) {
            throw new RuntimeException(exception);
        }
    }

}
