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

package org.n52.gfz.riesgos.util;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests for the StreamUtils class.
 */
public class TestStreamUtils {

    /**
     * Test the combineInputStreamsAsJsonObject with an empty map.
     * @throws IOException IOUtils.toString in the text may fail
     */
    @Test
    public void testCombiningStreamsEmpty() throws IOException {
        final Map<String, InputStream> input = new HashMap<>();
        final InputStream result = StreamUtils.INSTANCE.combineInputStreamsAsJsonObject(input);

        final String stringResult = IOUtils.toString(result);
        final String expected = "{}";
        assertEquals(
                "The output should be an empty object",
                expected,
                stringResult
        );
    }

    /**
     * Test the combineInputStreamsAsJsonObject method with a map with one
     * entry.
     * @throws IOException IOUtils.toString in the text may fail
     */
    @Test
    public void testCombiningStreamsOneElement () throws IOException {
        final Map<String, InputStream> input = new HashMap<>();
        input.put("foo", IOUtils.toInputStream("bar"));
        final InputStream result = StreamUtils.INSTANCE.combineInputStreamsAsJsonObject(input);

        final String stringResult = IOUtils.toString(result);
        final String expected = "{\"foo\":\"bar\"}";
        assertEquals(
                "The output should be an object with one entry",
                expected,
                stringResult
        );
    }

    /**
     * Test the combineInputStreamsAsJsonObject method with a map with two
     * entries.
     * @throws IOException IOUtils.toString in the text may fail
     */
    @Test
    public void testCombiningStreamsTwoElements () throws IOException {
        final Map<String, InputStream> input = new HashMap<>();
        input.put("foo", IOUtils.toInputStream("bar"));
        input.put("project", IOUtils.toInputStream("riesgos"));
        final InputStream result = StreamUtils.INSTANCE.combineInputStreamsAsJsonObject(input);

        final String stringResult = IOUtils.toString(result);
        // The ordering can be implementation dependent.
        final String expected = "{\"foo\":\"bar\", \"project\":\"riesgos\"}";
        assertEquals(
                "The output should be an object with two entries",
                expected,
                stringResult
        );
    }

    /**
     * Test the combineInputStreamsAsJsonObject method with a map with three
     * entries.
     * @throws IOException IOUtils.toString in the text may fail
     */
    @Test
    public void testCombiningStreamsThreeElements () throws IOException {
        final Map<String, InputStream> input = new HashMap<>();
        input.put("foo", IOUtils.toInputStream("bar"));
        input.put("project", IOUtils.toInputStream("riesgos"));
        input.put("test", IOUtils.toInputStream("pass"));
        final InputStream result = StreamUtils.INSTANCE.combineInputStreamsAsJsonObject(input);

        final String stringResult = IOUtils.toString(result);
        // The ordering can be implementation dependent.
        final String expected = "{\"test\":\"pass\", \"foo\":\"bar\", \"project\":\"riesgos\"}";
        assertEquals(
                "The output should be an object with 3 entries",
                expected,
                stringResult
        );
    }
}
