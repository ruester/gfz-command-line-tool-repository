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

package org.n52.gfz.riesgos.cache.hash;

import org.junit.Test;
import org.n52.gfz.riesgos.cache.dockerimagehandling.NoDockerImageIdLookup;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.InputParameterFactory;
import org.n52.gfz.riesgos.configuration.impl.ConfigurationImpl;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * This is the test class to test the hashing.
 */
public class TestHasherImpl {


    @Test
    public void testHashing() {
        final IConfiguration configuration1 = new ConfigurationImpl.Builder(
                "example",
                null,
                "exampleimage",
                "/tmp",
                Arrays.asList("echo", "Hello World"))
                .withAddedInputIdentifier(InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                        "times", false, null, null, null, null)
                ).build();

        final IConfiguration configurationSameAs1 = new ConfigurationImpl.Builder(
                "example",
                null,
                "exampleimage",
                "/tmp",
                Arrays.asList("echo", "Hello World"))
                .withAddedInputIdentifier(InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                        "times", false, null, null, null, null)
                ).build();

        final Map<String, List<IData>> inputData1 = new HashMap<>();
        inputData1.put("times", Collections.singletonList(new LiteralIntBinding(3)));

        final Map<String, List<IData>> inputDataSameAs1 = new HashMap<>();
        inputDataSameAs1.put("times", Collections.singletonList(new LiteralIntBinding(3)));


        final IHasher hasher = new HasherImpl(new NoDockerImageIdLookup());

        final String hash1 = hasher.hash(configuration1, inputData1);
        final String hashSameAs1 = hasher.hash(configurationSameAs1, inputDataSameAs1);

        assertEquals("Both hashes are the same", hash1, hashSameAs1);

        final IConfiguration configuration2 = new ConfigurationImpl.Builder(
                "example2",
                null,
                "exampleimage",
                "/tmp",
                Arrays.asList("echo", "Hello World"))
                .withAddedInputIdentifier(InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                        "times", false, null, null, null, null)
                ).build();

        final String hash2 = hasher.hash(configuration2, inputData1);

        assertNotEquals("The hashes are different", hash1, hash2);

        final Map<String, List<IData>> inputData2 = new HashMap<>();
        inputData2.put("times", Collections.singletonList(new LiteralIntBinding(4)));

        final String hash3 = hasher.hash(configuration1, inputData2);

        assertNotEquals("The hashes are different", hash1, hash3);
    }
}
