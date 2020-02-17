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

package org.n52.gfz.riesgos.bytetoidataconverter;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.formats.json.binding.JsonObjectOrArray;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;

public class TestConvertBytesToJsonDataBinding {

    @Test
    public void testConvertObject() {
        final String inputText = "{\"keyone\": \"value1\"}";

        final byte[] inputBytes = inputText.getBytes();

        final ConvertBytesToJsonDataBinding converter = new ConvertBytesToJsonDataBinding();

        try {
            final JsonDataBinding result = converter.convertToIData(inputBytes);
            final JsonObjectOrArray payload = result.getPayload();
            assertFalse("There is no array", payload.getJsonArray().isPresent());
            assertTrue("There is an object", payload.getJsonObject().isPresent());

            final JSONObject jsonObject = payload.getJsonObject().get();
            assertTrue("There is the expected key", jsonObject.containsKey("keyone"));
            final Object innerObject = jsonObject.get("keyone");
            assertTrue("It is a string", innerObject instanceof String);

            final String innerValue = (String) innerObject;
            assertEquals("And it is the expected string", "value1", innerValue);

        } catch(final ConvertToIDataException exception) {
            fail("There should be no exception");
        }
    }

    @Test
    public void testConvertArray() {
        final String inputText = "[\"value1\", \"value2\"]";

        final byte[] inputBytes = inputText.getBytes();

        final ConvertBytesToJsonDataBinding converter = new ConvertBytesToJsonDataBinding();

        try {
            final JsonDataBinding result = converter.convertToIData(inputBytes);
            final JsonObjectOrArray payload = result.getPayload();
            assertFalse("There is no object", payload.getJsonObject().isPresent());
            assertTrue("But it is an array", payload.getJsonArray().isPresent());

            final JSONArray jsonArray = payload.getJsonArray().get();
            assertEquals("There are two elements", 2, jsonArray.size());

            final Object object1 = jsonArray.get(0);
            final Object object2 = jsonArray.get(1);

            assertTrue("object1 is a string", object1 instanceof String);
            assertTrue("object2 is a string", object2 instanceof String);

            final String string1 = (String) object1;
            final String string2 = (String) object2;

            assertEquals("The first string matches", "value1", string1);
            assertEquals("The second string matches", "value2", string2);

        } catch(final ConvertToIDataException exception) {
            fail("There should be no exception");
        }
    }
}
