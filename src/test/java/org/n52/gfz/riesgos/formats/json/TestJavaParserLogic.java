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

package org.n52.gfz.riesgos.formats.json;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.formats.json.binding.JsonObjectOrArray;
import org.n52.gfz.riesgos.formats.json.parsers.JsonParserLogic;
import org.n52.wps.io.data.IData;

import java.io.ByteArrayInputStream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestJavaParserLogic {

    @Test
    public void testParse() {
        final String inputText = "{\"keyone\": \"value1\"}";

        final JsonParserLogic jsonParser = new JsonParserLogic();

        final IData result = jsonParser.parse(
                new ByteArrayInputStream(inputText.getBytes())
        );

        assertTrue("The result is an instance of JsonDataBinding", result instanceof JsonDataBinding);

        final JsonObjectOrArray payload = ((JsonDataBinding) result).getPayload();

        assertFalse("It is no array", payload.getJsonArray().isPresent());
        assertTrue("It is an object", payload.getJsonObject().isPresent());

        final JSONObject jsonObject = payload.getJsonObject().get();

        assertTrue("The key is inside the object", jsonObject.containsKey("keyone"));

        final Object value = jsonObject.get("keyone");
        assertTrue("The value is a string", value instanceof String);
        final String strValue = (String) value;
        assertEquals("And it has the expected value", "value1", strValue);


    }
}
