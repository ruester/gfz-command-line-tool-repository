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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.gfz.riesgos.cache.dockerimagehandling.NoDockerImageIdLookup;
import org.n52.gfz.riesgos.cache.wpsversionhandling.NoWpsVersionHandler;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.InputParameterFactory;
import org.n52.gfz.riesgos.configuration.OutputParameterFactory;
import org.n52.gfz.riesgos.configuration.impl.ConfigurationImpl;
import org.n52.gfz.riesgos.formats.quakeml.binding.QuakeMLXmlDataBinding;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.bbox.BoundingBoxData;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

/**
 * This is the test class to test the hashing.
 */
public class TestHasherImpl {
    /**
     * This is a simple test that cares about the configurations
     * and a integer value that will be given to the program.
     */
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


        final IHasher hasher = new HasherImpl(new NoDockerImageIdLookup(), new NoWpsVersionHandler());

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

    @Test
    public void otherInputDataMechanism() {
        final IHasher hasher = new HasherImpl(new NoDockerImageIdLookup(), new NoWpsVersionHandler());
        final Map<String, List<IData>> exampleInputData = new HashMap<>();
        try {
            exampleInputData.put("dummy", Collections.singletonList(new GenericXMLDataBinding(XmlObject.Factory.parse("<a><b>test</b></a>"))));

            final IConfiguration conf1 = new ConfigurationImpl.Builder("a", null, "b", "/tmp", Arrays.asList("ls"))
                    .withAddedInputIdentifier(
                            InputParameterFactory
                                    .INSTANCE
                                    .createCommandLineArgumentNrmlFile
                                            ("dummy",
                                                    false,
                                                    null,
                                                    null,
                                                    null))
                    .build();

            final String hash1 = hasher.hash(conf1, exampleInputData);

            final IConfiguration conf2 = new ConfigurationImpl.Builder("a", null, "b", "/tmp", Arrays.asList("ls"))
                    .withAddedInputIdentifier(
                            InputParameterFactory
                                    .INSTANCE
                                    .createFileInNrml(
                                            "dummy",
                                            false,
                                            null,
                                            null,
                                            "a.xml"))
                    .build();

            final String hash2 = hasher.hash(conf2, exampleInputData);

            assertNotEquals("Both should not be equal", hash1, hash2);

            final IConfiguration conf3 = new ConfigurationImpl.Builder("a", null, "b", "/tmp", Arrays.asList("ls"))
                    .withAddedInputIdentifier(
                            InputParameterFactory
                                    .INSTANCE
                                    .createFileInNrml(
                                            "dummy",
                                            false,
                                            null,
                                            null,
                                            "b.xml"))
                    .build();

            final String hash3 = hasher.hash(conf3, exampleInputData);

            assertNotEquals("Both should not be equal", hash2, hash3);

            final IConfiguration conf4 = new ConfigurationImpl.Builder("a", null, "b", "/tmp", Arrays.asList("ls"))
                    .withAddedInputIdentifier(
                            InputParameterFactory
                                    .INSTANCE
                                    .createFileInNrml(
                                            "dummy",
                                            true,
                                            null,
                                            null,
                                            "a.xml"))
                    .build();

            final String hash4 = hasher.hash(conf4, exampleInputData);

            assertNotEquals("Both should not be equal", hash2, hash4);

            final IConfiguration conf5 = new ConfigurationImpl.Builder("a", null, "b", "/tmp", Arrays.asList("ls"))
                    .withAddedInputIdentifier(
                            InputParameterFactory
                                    .INSTANCE
                                    .createFileInQuakeML(
                                            "dummy",
                                            true,
                                            null,
                                            null,
                                            "a.xml"))
                    .build();

            final String hash5 = hasher.hash(conf5, exampleInputData);

            assertNotEquals("Both should not be equal", hash2, hash5);

            final IConfiguration conf6 = new ConfigurationImpl.Builder("a", null, "b", "/tmp", Arrays.asList("ls"))
                    .withAddedInputIdentifier(
                            InputParameterFactory
                                    .INSTANCE
                                    .createFileInQuakeML(
                                            "dummy",
                                            true,
                                            null,
                                            null,
                                            "a.xml"))
                    .build();

            final String hash6 = hasher.hash(conf6, exampleInputData);

            assertEquals("Both should be equal", hash6, hash5);
        } catch (final XmlException xmlException) {
            xmlException.printStackTrace();
            fail("There should be no xml exception");
        }
    }

    @Test
    public void testDifferentXmlInput() {
        final IConfiguration shakygroundConfig = new ConfigurationImpl.Builder(
                "a",
                null,
                "b",
                "/tmp",
                Arrays.asList("ls")
                ).withAddedInputIdentifier(
                    InputParameterFactory
                        .INSTANCE
                        .createCommandLineArgumentQuakeML(
                            "dummy",
                            false,
                            null,
                            null,
                            null
                        )
                ).build();

        try {
            final String dummyXml = "<eventParameters publicID=\"quakeml:"
                + "quakeledger/0\" xmlns="
                + "\"http://quakeml.org/xmlns/bed/1.2\">\n"
                + "  <event publicID=\"quakeml:quakeledger/84945\">\n"
                + "    <preferredOriginID>quakeml:quakeledger/"
                + "84945</preferredOriginID>\n"
                + "    <preferredMagnitudeID>quakeml:quakeledger/"
                + "84945</preferredMagnitudeID>\n"
                + "    <type>earthquake</type>\n"
                + "    <description>\n"
                + "      <text>stochastic</text>\n"
                + "    </description>\n"
                + "    <origin publicID=\"quakeml:quakeledger/84945\">\n"
                + "      <time>\n"
                + "        <value>16773-01-01T00:00:00.000000Z</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </time>\n"
                + "      <latitude>\n"
                + "        <value>-30.9227</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </latitude>\n"
                + "      <longitude>\n"
                + "        <value>-71.49875</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </longitude>\n"
                + "      <depth>\n"
                + "        <value>34.75117</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </depth>\n"
                + "      <creationInfo>\n"
                + "        <author>GFZ</author>\n"
                + "      </creationInfo>\n"
                + "      <originUncertainty>\n"
                + "        <horizontalUncertainty>NaN</horizontalUncertainty>\n"
                + "        <minHorizontalUncertainty>NaN"
                + "</minHorizontalUncertainty>\n"
                + "        <maxHorizontalUncertainty>NaN"
                + "</maxHorizontalUncertainty>\n"
                + "        <azimuthMaxHorizontalUncertainty>NaN"
                + "</azimuthMaxHorizontalUncertainty>\n"
                + "      </originUncertainty>\n"
                + "    </origin>\n"
                + "    <magnitude publicID=\"quakeml:quakeledger/84945\">\n"
                + "      <mag>\n"
                + "        <value>8.35</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </mag>\n"
                + "      <type>MW</type>\n"
                + "      <creationInfo>\n"
                + "        <author>GFZ</author>\n"
                + "      </creationInfo>\n"
                + "    </magnitude>\n"
                + "    <focalMechanism publicID=\"quakeml:quakeledger/"
                + "84945\">\n"
                + "      <nodalPlanes preferredPlane=\"1\">\n"
                + "        <nodalPlane1>\n"
                + "          <strike>\n"
                + "            <value>7.310981</value>\n"
                + "            <uncertainty>NaN</uncertainty>\n"
                + "          </strike>\n"
                + "          <dip>\n"
                + "            <value>16.352970000000003</value>\n"
                + "            <uncertainty>NaN</uncertainty>\n"
                + "          </dip>\n"
                + "          <rake>\n"
                + "            <value>90.0</value>\n"
                + "            <uncertainty>NaN</uncertainty>\n"
                + "          </rake>\n"
                + "        </nodalPlane1>\n"
                + "      </nodalPlanes>\n"
                + "    </focalMechanism>\n"
                + "  </event>\n"
                + "</eventParameters>\n";

            final Map<String, List<IData>> inputData1 = new HashMap<>();
            inputData1.put(
                "dummy",
                Collections.singletonList(
                    QuakeMLXmlDataBinding.fromValidatedXml(
                        XmlObject.Factory.parse(dummyXml)
                    )
                )
            );
            final IHasher hasher = new HasherImpl(
                new NoDockerImageIdLookup(),
                new NoWpsVersionHandler()
            );

            final String hash = hasher.hash(shakygroundConfig, inputData1);

            final String dummyXml2 = "<eventParameters publicID=\"quakeml:"
                + "quakeledger/0\" xmlns="
                + "\"http://quakeml.org/xmlns/bed/1.2\">\n"
                + "  <event publicID=\"quakeml:quakeledger/84945\">\n"
                + "    <preferredOriginID>quakeml:quakeledger/84945"
                + "</preferredOriginID>\n"
                + "    <preferredMagnitudeID>quakeml:quakeledger/84945"
                + "</preferredMagnitudeID>\n"
                + "    <type>earthquake</type>\n"
                + "    <description>\n"
                + "      <text>stochastic</text>\n"
                + "    </description>\n"
                + "    <origin publicID=\"quakeml:quakeledger/84945\">\n"
                + "      <time>\n"
                + "        <value>16773-01-01T00:00:00.000000Z</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </time>\n"
                + "      <latitude>\n"
                + "        <value>-31.9227</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </latitude>\n"
                + "      <longitude>\n"
                + "        <value>-71.49875</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </longitude>\n"
                + "      <depth>\n"
                + "        <value>34.75117</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </depth>\n"
                + "      <creationInfo>\n"
                + "        <author>GFZ</author>\n"
                + "      </creationInfo>\n"
                + "      <originUncertainty>\n"
                + "        <horizontalUncertainty>NaN"
                + "</horizontalUncertainty>\n"
                + "        <minHorizontalUncertainty>NaN"
                + "</minHorizontalUncertainty>\n"
                + "        <maxHorizontalUncertainty>NaN"
                + "</maxHorizontalUncertainty>\n"
                + "        <azimuthMaxHorizontalUncertainty>NaN"
                + "</azimuthMaxHorizontalUncertainty>\n"
                + "      </originUncertainty>\n"
                + "    </origin>\n"
                + "    <magnitude publicID=\"quakeml:quakeledger/84945\">\n"
                + "      <mag>\n"
                + "        <value>8.35</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </mag>\n"
                + "      <type>MW</type>\n"
                + "      <creationInfo>\n"
                + "        <author>GFZ</author>\n"
                + "      </creationInfo>\n"
                + "    </magnitude>\n"
                + "    <focalMechanism publicID=\"quakeml:quakeledger/"
                + "84945\">\n"
                + "      <nodalPlanes preferredPlane=\"1\">\n"
                + "        <nodalPlane1>\n"
                + "          <strike>\n"
                + "            <value>7.310981</value>\n"
                + "            <uncertainty>NaN</uncertainty>\n"
                + "          </strike>\n"
                + "          <dip>\n"
                + "            <value>16.352970000000003</value>\n"
                + "            <uncertainty>NaN</uncertainty>\n"
                + "          </dip>\n"
                + "          <rake>\n"
                + "            <value>90.0</value>\n"
                + "            <uncertainty>NaN</uncertainty>\n"
                + "          </rake>\n"
                + "        </nodalPlane1>\n"
                + "      </nodalPlanes>\n"
                + "    </focalMechanism>\n"
                + "  </event>\n"
                + "</eventParameters>\n";

            // other latitude value
            final Map<String, List<IData>> inputData2 = new HashMap<>();
            inputData2.put(
                "dummy",
                Collections.singletonList(
                    QuakeMLXmlDataBinding.fromValidatedXml(
                        XmlObject.Factory.parse(dummyXml2)
                    )
                )
            );

            final String hash2 = hasher.hash(shakygroundConfig, inputData2);

            assertNotEquals("The hashes must be different", hash, hash2);

            final String dummyXml3 = "<eventParameters publicID="
                + "\"quakeml:quakeledger/0\" xmlns="
                + "\"http://quakeml.org/xmlns/bed/1.2\">\n"
                + "  <event publicID=\"quakeml:quakeledger/84945\">\n"
                + "    <preferredOriginID>quakeml:quakeledger/"
                + "84945</preferredOriginID>\n"
                + "    <preferredMagnitudeID>quakeml:quakeledger/"
                + "84945</preferredMagnitudeID>\n"
                + "    <type>earthquake</type>\n"
                + "    <description>\n"
                + "      <text>stochastic</text>\n"
                + "    </description>\n"
                + "    <origin publicID=\"quakeml:quakeledger/84945\">\n"
                + "      <time>\n"
                + "        <value>16773-01-01T00:00:00.000000Z</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </time>\n"
                + "      <latitude>\n"
                + "        <value>-30.9227</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </latitude>\n"
                + "      <longitude>\n"
                + "        <value>-71.49875</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </longitude>\n"
                + "      <depth>\n"
                + "        <value>34.75117</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </depth>\n"
                + "      <creationInfo>\n"
                + "        <author>GFZ</author>\n"
                + "      </creationInfo>\n"
                + "      <originUncertainty>\n"
                + "        <horizontalUncertainty>NaN</horizontalUncertainty>\n"
                + "        <minHorizontalUncertainty>NaN"
                + "</minHorizontalUncertainty>\n"
                + "        <maxHorizontalUncertainty>NaN"
                + "</maxHorizontalUncertainty>\n"
                + "        <azimuthMaxHorizontalUncertainty>NaN"
                + "</azimuthMaxHorizontalUncertainty>\n"
                + "      </originUncertainty>\n"
                + "    </origin>\n"
                + "    <magnitude publicID=\"quakeml:quakeledger/84945\">\n"
                + "      <mag>\n"
                + "        <value>8.35</value>\n"
                + "        <uncertainty>NaN</uncertainty>\n"
                + "      </mag>\n"
                + "      <type>MW</type>\n"
                + "      <creationInfo>\n"
                + "        <author>GFZ</author>\n"
                + "      </creationInfo>\n"
                + "    </magnitude>\n"
                + "    <focalMechanism publicID=\"quakeml:"
                + "quakeledger/84945\">\n"
                + "      <nodalPlanes preferredPlane=\"1\">\n"
                + "        <nodalPlane1>\n"
                + "          <strike>\n"
                + "            <value>7.310981</value>\n"
                + "            <uncertainty>NaN</uncertainty>\n"
                + "          </strike>\n"
                + "          <dip>\n"
                + "            <value>16.352970000000003</value>\n"
                + "            <uncertainty>NaN</uncertainty>\n"
                + "          </dip>\n"
                + "          <rake>\n"
                + "            <value>90.0</value>\n"
                + "            <uncertainty>NaN</uncertainty>\n"
                + "          </rake>\n"
                + "        </nodalPlane1>\n"
                + "      </nodalPlanes>\n"
                + "    </focalMechanism>\n"
                + "  </event>\n"
                + "</eventParameters>\n";

            final Map<String, List<IData>> inputData3 = new HashMap<>();
            inputData3.put(
                "dummy",
                Collections.singletonList(
                    QuakeMLXmlDataBinding.fromValidatedXml(
                        XmlObject.Factory.parse(dummyXml3)
                    )
                )
            );

            final String hash3 = hasher.hash(shakygroundConfig, inputData3);

            assertEquals("hash and hash3 must be equal", hash, hash3);

            final IHasher otherHasher = new HasherImpl(
                new NoDockerImageIdLookup(),
                new NoWpsVersionHandler()
            );

            final String hash4 = otherHasher.hash(
                shakygroundConfig,
                inputData3
            );

            assertEquals(
                "Hashes created with another instance should be consistent",
                hash3,
                hash4
            );

            // and it should be consistent if I recreate the configuration
            // the tricky point here is that the quakeml argument gets a
            // temporary file path
            // --> this temporary file path should not be taken into account
            final IConfiguration shakygroundConf2 =
                new ConfigurationImpl.Builder(
                    "a",
                    null,
                    "b",
                    "/tmp",
                    Arrays.asList("ls")
                ).withAddedInputIdentifier(
                    InputParameterFactory
                        .INSTANCE
                        .createCommandLineArgumentQuakeML(
                            "dummy",
                            false,
                            null,
                            null,
                            null
                        )
                ).build();

            final String hash5 = otherHasher.hash(shakygroundConf2, inputData3);

            assertEquals(
                "Hashes for recreated configurations (the same ones) "
                + "should be equal",
                hash3,
                hash5
            );
        } catch (final XmlException xmlException) {
            xmlException.printStackTrace();
            fail("There should be no xml exception");
        }
    }

    @Test
    public void testDifferentOutputFormats() {

        final Map<String, List<IData>> inputData = new HashMap<>();
        inputData.put("times", Collections.singletonList(new LiteralIntBinding(3)));

        final IConfiguration configuration1 = new ConfigurationImpl.Builder(
                "example",
                null,
                "exampleimage",
                "/tmp",
                Arrays.asList("echo", "Hello World"))
                .withAddedInputIdentifier(InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                        "times", false, null, null, null, null))
                .build();

        final IConfiguration configuration2 = new ConfigurationImpl.Builder(
                "example",
                null,
                "exampleimage",
                "/tmp",
                Arrays.asList("echo", "Hello World"))
                .withAddedInputIdentifier(InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                        "times", false, null, null, null, null))
                .withAddedOutputIdentifier(OutputParameterFactory.INSTANCE.createStdoutString("stdout", false, null))
                .build();

        final IHasher hasher = new HasherImpl(new NoDockerImageIdLookup(), new NoWpsVersionHandler());

        final String hash1 = hasher.hash(configuration1, inputData);
        final String hash2 = hasher.hash(configuration2, inputData);

        assertNotEquals("Both hashes are different", hash1, hash2);


        final IConfiguration configuration3 = new ConfigurationImpl.Builder(
                "example",
                null,
                "exampleimage",
                "/tmp",
                Arrays.asList("echo", "Hello World"))
                .withAddedInputIdentifier(InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                        "times", false, null, null, null, null))
                .withAddedOutputIdentifier(OutputParameterFactory.INSTANCE.createStdoutString("stdout", false, null))
                .build();

        final String hash3 = hasher.hash(configuration3, inputData);

        assertEquals("2 and 3 are equal", hash2, hash3);

        final IConfiguration configuration4 = new ConfigurationImpl.Builder(
                "example",
                null,
                "exampleimage",
                "/tmp",
                Arrays.asList("echo", "Hello World"))
                .withAddedInputIdentifier(InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                        "times", false, null, null, null, null))
                .withAddedOutputIdentifier(OutputParameterFactory.INSTANCE.createFileOutGeneric("stdout", false, null, null, "a.txt"))
                .build();

        final String hash4 = hasher.hash(configuration4, inputData);

        assertNotEquals("the hashes are different", hash2, hash4);
    }

    @Test
    public void testDifferentInputOrderings() {
        final IConfiguration conf1 = new ConfigurationImpl.Builder("conf", null, "a", "/tmp", Arrays.asList("ls"))
                .withAddedInputIdentifier(
                        InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                                "timesA",
                                false,
                                null,
                                null,
                                null,
                                null))
                .withAddedInputIdentifier(
                        InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                                "timesB",
                                false,
                                null,
                                null,
                                null,
                                null))
                .build();

        final IConfiguration conf2 = new ConfigurationImpl.Builder("conf", null, "a", "/tmp", Arrays.asList("ls"))
                .withAddedInputIdentifier(
                        InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                                "timesB",
                                false,
                                null,
                                null,
                                null,
                                null))
                .withAddedInputIdentifier(
                        InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                                "timesA",
                                false,
                                null,
                                null,
                                null,
                                null))
                .build();

        final Map<String, List<IData>> inputValues = new HashMap<>();
        inputValues.put("timesA", Collections.singletonList(new LiteralIntBinding(1)));
        inputValues.put("timesB", Collections.singletonList(new LiteralIntBinding(2)));

        final IHasher hasher = new HasherImpl(new NoDockerImageIdLookup(), new NoWpsVersionHandler());

        final String hash1 = hasher.hash(conf1, inputValues);
        final String hash2 = hasher.hash(conf2, inputValues);

        assertNotEquals("The ordering is different -> not equals", hash1, hash2);
    }

    private Map<String, List<IData>> createQuakeledgerInputData() {
        final Map<String, List<IData>> inputData = new HashMap<>();
        inputData.put("input-boundingbox", Collections.singletonList(new BoundingBoxData(new double[]{-71.8, -33.2}, new double[]{-71.4, -33.0}, "EPSG:4328")));
        inputData.put("mmin", Collections.singletonList(new LiteralDoubleBinding(6.6)));
        inputData.put("mmax", Collections.singletonList(new LiteralDoubleBinding(8.5)));
        inputData.put("zmin", Collections.singletonList(new LiteralDoubleBinding(5.0)));
        inputData.put("zmax", Collections.singletonList(new LiteralDoubleBinding(140.0)));
        inputData.put("p", Collections.singletonList(new LiteralDoubleBinding(0.1)));
        inputData.put("etype", Collections.singletonList(new LiteralStringBinding("deaggregation")));
        inputData.put("tlon", Collections.singletonList(new LiteralDoubleBinding(-71.5730623712764)));
        inputData.put("tlat", Collections.singletonList(new LiteralDoubleBinding(-33.1299174879672)));
        return inputData;
    }
}
