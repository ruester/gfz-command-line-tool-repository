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

package org.n52.gfz.riesgos.cache.impl;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.gfz.riesgos.cache.ICacher;
import org.n52.gfz.riesgos.cache.IDataRecreator;
import org.n52.gfz.riesgos.cache.RecreateFromBindingClass;
import org.n52.gfz.riesgos.cache.dockerimagehandling.IDockerImageIdLookup;
import org.n52.gfz.riesgos.cache.dockerimagehandling.NoDockerImageIdLookup;
import org.n52.gfz.riesgos.cache.hash.HasherImpl;
import org.n52.gfz.riesgos.cache.hash.IHasher;
import org.n52.gfz.riesgos.cache.wpsversionhandling.NoWpsVersionHandler;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.configuration.InputParameterFactory;
import org.n52.gfz.riesgos.configuration.OutputParameterFactory;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.nrml.binding.NrmlXmlDataBinding;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.bbox.BoundingBoxData;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;

/**
 * Test for the caching mechanism
 */
public class TestCacheImpl {

    @Test
    public void testCacheInt() {

        final IHasher hasher = new HasherImpl(new NoDockerImageIdLookup(), new NoWpsVersionHandler());

        final ICacher cache = new CacheImpl();


        final IConfiguration configuration = new TestConfiguration("TestconfigurationInt",
                InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                        "times",
                        false,
                        "A dummy value that is not used",
                        null,
                        "3",
                        null));

        final IData literalIntInput = new LiteralIntBinding(3);

        final Map<String, List<IData>> inputData = new HashMap<>();
        inputData.put(configuration.getInputIdentifiers().get(0).getIdentifier(), Collections.singletonList(literalIntInput));

        final String hash = hasher.hash(configuration, inputData);

        final Optional<Map<String, IDataRecreator>> optionalCacheResult = cache.getCachedResult(hash);

        assertFalse("There is no data at the beginning", optionalCacheResult.isPresent());

        final String outputValue = "Hello World";
        final LiteralStringBinding strOutput = new LiteralStringBinding(outputValue);

        final Map<String, IDataRecreator> outputData = new HashMap<>();
        outputData.put(configuration.getOutputIdentifiers().get(0).getIdentifier(), new RecreateFromBindingClass(strOutput));

        cache.insertResultIntoCache(hash, outputData);

        final Optional<Map<String, IDataRecreator>> cacheResult = cache.getCachedResult(hash);

        assertTrue("There is a result in the cache", cacheResult.isPresent());

        assertTrue("The cached result has the entry for the output", cacheResult.get().containsKey(configuration.getOutputIdentifiers().get(0).getIdentifier()));

        final IDataRecreator innerCachedResultCreator = cacheResult.get().get(configuration.getOutputIdentifiers().get(0).getIdentifier());
        final IData innerCachedResult = innerCachedResultCreator.recreate();

        assertTrue("The cached result contains a string", innerCachedResult instanceof LiteralStringBinding);

        final LiteralStringBinding innerCachedResultStr = (LiteralStringBinding) innerCachedResult;

        assertEquals("The payload is as expected", outputValue, innerCachedResultStr.getPayload());


        // and we can query it with new created data as well

        final IConfiguration configuration2 = new TestConfiguration("TestconfigurationInt",
                InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                        "times",
                        false,
                        "A dummy value that is not used",
                        null,
                        "3",
                        null));
        final IData literalIntInput2 = new LiteralIntBinding(3);

        final Map<String, List<IData>> inputData2 = new HashMap<>();
        inputData2.put(configuration2.getInputIdentifiers().get(0).getIdentifier(), Collections.singletonList(literalIntInput2));

        final String hash2 = hasher.hash(configuration2, inputData2);
        final Optional<Map<String, IDataRecreator>> cachedResult2 = cache.getCachedResult(hash2);

        // I think it is a problem regarding to identities and equality
        assertTrue("Also here the data is present", cachedResult2.isPresent());
    }

    @Test
    public void testCacheBBox() {
        final IHasher hasher = new HasherImpl(new NoDockerImageIdLookup(), new NoWpsVersionHandler());
        final ICacher cache = new CacheImpl();

        final IConfiguration configuration = new TestConfiguration("TestConfigurationBBox", InputParameterFactory.INSTANCE.createCommandLineArgumentBBox(
                "bbox", false, null, Collections.singletonList("EPSG:4328")));

        final IData bbox= new BoundingBoxData(new double[]{-33.0, -71.0}, new double[]{-32.0, -70.0}, "EPSG:4328");

        final Map<String, List<IData>> inputData = new HashMap<>();
        inputData.put(configuration.getInputIdentifiers().get(0).getIdentifier(), Collections.singletonList(bbox));

        final String hash = hasher.hash(configuration, inputData);
        final Optional<Map<String, IDataRecreator>> optionalCacheResult = cache.getCachedResult(hash);

        assertFalse("There is no data at the beginning", optionalCacheResult.isPresent());

        final String outputValue = "Hello World";
        final LiteralStringBinding strOutput = new LiteralStringBinding(outputValue);

        final Map<String, IDataRecreator> outputData = new HashMap<>();
        outputData.put(configuration.getOutputIdentifiers().get(0).getIdentifier(), new RecreateFromBindingClass(strOutput));

        cache.insertResultIntoCache(hash, outputData);

        final Optional<Map<String, IDataRecreator>> cacheResult = cache.getCachedResult(hash);

        assertTrue("There is a result in the cache", cacheResult.isPresent());

        assertTrue("The cached result has the entry for the output", cacheResult.get().containsKey(configuration.getOutputIdentifiers().get(0).getIdentifier()));

        final IDataRecreator innerCacheResultCreator = cacheResult.get().get(configuration.getOutputIdentifiers().get(0).getIdentifier());
        final IData innerCachedResult = innerCacheResultCreator.recreate();

        assertTrue("The cached result contains a string", innerCachedResult instanceof LiteralStringBinding);

        final LiteralStringBinding innerCachedResultStr = (LiteralStringBinding) innerCachedResult;

        assertEquals("The payload is as expected", outputValue, innerCachedResultStr.getPayload());


        // and we can query it with new created data as well


        final IConfiguration configuration2 = new TestConfiguration("TestConfigurationBBox", InputParameterFactory.INSTANCE.createCommandLineArgumentBBox(
                "bbox", false, null, Collections.singletonList("EPSG:4328")));
        final IData bbox2 = new BoundingBoxData(new double[]{-33.0, -71.0}, new double[]{-32.0, -70.0}, "EPSG:4328");

        final Map<String, List<IData>> inputData2 = new HashMap<>();
        inputData2.put(configuration2.getInputIdentifiers().get(0).getIdentifier(), Collections.singletonList(bbox2));

        final String hash2 = hasher.hash(configuration2, inputData2);
        final Optional<Map<String, IDataRecreator>> cachedResult2 = cache.getCachedResult(hash2);

        // I think it is a problem regarding to identities and equality
        assertTrue("Also here the data is present", cachedResult2.isPresent());
    }

    @Test
    public void testCacheXml() {
        final IHasher hasher = new HasherImpl(new NoDockerImageIdLookup(), new NoWpsVersionHandler());
        final ICacher cache = new CacheImpl();

        try {
            final IConfiguration configuration = new TestConfiguration("TestConfigurationXml", InputParameterFactory.INSTANCE.createFileInNrml("nrm",
                    false, null, DefaultFormatOption.NRML.getFormat(), "file-in.xml"));

            final IData xml = NrmlXmlDataBinding.fromXml(XmlObject.Factory.parse("<a><b><c>Element1</c></b></a>"));

            final Map<String, List<IData>> inputData = new HashMap<>();
            inputData.put(configuration.getInputIdentifiers().get(0).getIdentifier(), Collections.singletonList(xml));

            final String hash = hasher.hash(configuration, inputData);
            final Optional<Map<String, IDataRecreator>> optionalCacheResult = cache.getCachedResult(hash);

            assertFalse("There is no data at the beginning", optionalCacheResult.isPresent());

            final String outputValue = "Hello World";
            final LiteralStringBinding strOutput = new LiteralStringBinding(outputValue);

            final Map<String, IDataRecreator> outputData = new HashMap<>();
            outputData.put(configuration.getOutputIdentifiers().get(0).getIdentifier(), new RecreateFromBindingClass(strOutput));

            cache.insertResultIntoCache(hash, outputData);

            final Optional<Map<String, IDataRecreator>> cacheResult = cache.getCachedResult(hash);

            assertTrue("There is a result in the cache", cacheResult.isPresent());

            assertTrue("The cached result has the entry for the output", cacheResult.get().containsKey(configuration.getOutputIdentifiers().get(0).getIdentifier()));

            final IDataRecreator innerCachedResultCreator = cacheResult.get().get(configuration.getOutputIdentifiers().get(0).getIdentifier());
            final IData innerCachedResult = innerCachedResultCreator.recreate();

            assertTrue("The cached result contains a string", innerCachedResult instanceof LiteralStringBinding);

            final LiteralStringBinding innerCachedResultStr = (LiteralStringBinding) innerCachedResult;

            assertEquals("The payload is as expected", outputValue, innerCachedResultStr.getPayload());


            // and we can query it with new created data as well


            final IConfiguration configuration2 = new TestConfiguration("TestConfigurationXml", InputParameterFactory.INSTANCE.createFileInNrml("nrm",
                    false, null, DefaultFormatOption.NRML.getFormat(), "file-in.xml"));

            final IData xml2 = NrmlXmlDataBinding.fromXml(XmlObject.Factory.parse("<a><b><c>Element1</c></b></a>"));

            final Map<String, List<IData>> inputData2 = new HashMap<>();
            inputData2.put(configuration2.getInputIdentifiers().get(0).getIdentifier(), Collections.singletonList(xml2));

            final String hash2 = hasher.hash(configuration2, inputData2);
            final Optional<Map<String, IDataRecreator>> cachedResult2 = cache.getCachedResult(hash2);

            // I think it is a problem regarding to identities and equality
            assertTrue("Also here the data is present", cachedResult2.isPresent());
        } catch (XmlException xmlException) {
            fail("There should be no xml exception");
        }
    }

    private class TestConfiguration implements IConfiguration {

        private final String identifier;
        private final IInputParameter inputParameter;

        public TestConfiguration(
                final String identifier,
                final IInputParameter inputParameter
        ) {
            this.identifier = identifier;
            this.inputParameter = inputParameter;
        }

        /**
         * @return Identifier that will be displayed as the title of the process
         */
        @Override
        public String getIdentifier() {
            return identifier;
        }

        /**
         * @return optional description text for the process of this configuration
         */
        @Override
        public Optional<String> getAbstract() {
            return Optional.empty();
        }

        /**
         * @return ID of the docker imaged used to create a container for running the process
         */
        @Override
        public String getImageId() {
            // TODO
            return "testconfiguration:latest";
        }

        /**
         * @return working directory to run the executable inside of the container
         */
        @Override
        public String getWorkingDirectory() {
            return "/usr/share";
        }

        /**
         * @return list with the command line command (for example ["python3", "script.py"]
         */
        @Override
        public List<String> getCommandToExecute() {
            return Arrays.asList("echo", "hello world");
        }

        /**
         * @return list with default command line flags that should always be provided
         */
        @Override
        public List<String> getDefaultCommandLineFlags() {
            return Collections.emptyList();
        }

        /**
         * @return list with the configuration of all the input parameters
         */
        @Override
        public List<IInputParameter> getInputIdentifiers() {
            return Collections.singletonList(inputParameter);
        }

        /**
         * @return list with the configuration of all the output parameters
         */
        @Override
        public List<IOutputParameter> getOutputIdentifiers() {
            return Collections.singletonList(
                    OutputParameterFactory.INSTANCE.createStdoutString("std-out", false, null));
        }

        /**
         * @return handler for stderr (indicating errors, logging, ...)
         */
        @Override
        public Optional<IStderrHandler> getStderrHandler() {
            return Optional.empty();
        }

        /**
         * @return handler for exit value (indicating errors, logging, ...)
         */
        @Override
        public Optional<IExitValueHandler> getExitValueHandler() {
            return Optional.empty();
        }

        /**
         * @return handler for stdout (logging, ...)
         */
        @Override
        public Optional<IStdoutHandler> getStdoutHandler() {
            return Optional.empty();
        }
    }

}
