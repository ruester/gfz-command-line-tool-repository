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

package org.n52.gfz.riesgos.cache;

import org.junit.Test;
import org.n52.gfz.riesgos.cache.inputparametercachekey.InputParameterCacheKeyByByteArrayList;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

public class TestCacheKeyByByteArrayList {

    @Test
    public void testEqual() {
        final IInputParameterCacheKey key1 = new InputParameterCacheKeyByByteArrayList(Arrays.asList("first try".getBytes(), "second one".getBytes()));

        final IInputParameterCacheKey key2 = new InputParameterCacheKeyByByteArrayList(Arrays.asList("first try".getBytes(), "second one".getBytes()));

        assertEquals("Both must be equal", key1, key2);
    }

    @Test
    public void testNotEqual() {
        final IInputParameterCacheKey key1 = new InputParameterCacheKeyByByteArrayList(Arrays.asList("first try".getBytes(), "second one".getBytes()));

        final IInputParameterCacheKey key2 = new InputParameterCacheKeyByByteArrayList(Arrays.asList("first try".getBytes(), "second one".getBytes(), "third one".getBytes()));

        assertNotEquals("Both must not be equal", key1, key2);
    }

    @Test
    public void testEqual2() {

        final ByteArrayOutputStream byteOut1 = new ByteArrayOutputStream();
        for(final byte b : "first try".getBytes()) {
            byteOut1.write(b);
        }

        final ByteArrayOutputStream byteOut2 = new ByteArrayOutputStream();
        for(final byte b : "second one".getBytes()) {
            byteOut2.write(b);
        }

        final IInputParameterCacheKey key1 = new InputParameterCacheKeyByByteArrayList(Arrays.asList("first try".getBytes(), "second one".getBytes()));

        final IInputParameterCacheKey key2 = new InputParameterCacheKeyByByteArrayList(Arrays.asList(byteOut1.toByteArray(), byteOut2.toByteArray()));

        assertEquals("Both must be equal", key1, key2);
    }
}
