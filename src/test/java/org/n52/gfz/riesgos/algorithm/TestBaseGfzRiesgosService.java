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

package org.n52.gfz.riesgos.algorithm;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.n52.gfz.riesgos.cache.ICacher;
import org.n52.gfz.riesgos.cache.dockerimagehandling.DockerImageIdLookup;
import org.n52.gfz.riesgos.cache.hash.HasherImpl;
import org.n52.gfz.riesgos.cache.hash.IHasher;
import org.n52.gfz.riesgos.cache.impl.CacheImpl;
import org.n52.gfz.riesgos.cmdexecution.common.ExecutionRunImpl;
import org.n52.gfz.riesgos.cmdexecution.common.ExecutionRunResultImpl;
import org.n52.gfz.riesgos.cmdexecution.docker.DockerContainerExecutionContextManagerImpl;
import org.n52.gfz.riesgos.cmdexecution.docker.DockerExecutionContextImpl;
import org.n52.gfz.riesgos.cmdexecution.util.IExecutionContextManagerFactory;
import org.n52.gfz.riesgos.configuration.ConfigurationFactory;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.formats.quakeml.binding.QuakeMLXmlDataBinding;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.bbox.BoundingBoxData;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.server.ExceptionReport;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * This is the test class for our gfz base riesgos skeleton.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = {
        "org.n52.gfz.riesgos.cmdexecution.docker.DockerContainerExecutionContextManagerImpl",
        "org.n52.gfz.riesgos.cmdexecution.docker.DockerExecutionContextImpl",
        "org.n52.gfz.riesgos.cmdexecution.ExecutionRunImpl",
        "org.n52.gfz.riesgos.cmdexecution.ExecutionRunResultImpl",
        "org.n52.gfz.riesgos.cache.dockerimagehandling.DockerImageIdLookup"
})
public class TestBaseGfzRiesgosService {

    /**
     * This is the test for the quakeledger process.
     */
    @Test
    public void testQuakeledger() {

        final IConfiguration configuration = ConfigurationFactory.INSTANCE.createQuakeledger();

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


        final DockerContainerExecutionContextManagerImpl mockContextManager = mock(DockerContainerExecutionContextManagerImpl.class);
        final DockerExecutionContextImpl mockContext = mock(DockerExecutionContextImpl.class);
        final ExecutionRunImpl mockRun = mock(ExecutionRunImpl.class);
        final ExecutionRunResultImpl mockRunResult = mock(ExecutionRunResultImpl.class);
        final DockerImageIdLookup mockImageIdLookup = mock(DockerImageIdLookup.class);
        try {
            whenNew(DockerContainerExecutionContextManagerImpl.class).withAnyArguments().thenReturn(mockContextManager);
            whenNew(DockerExecutionContextImpl.class).withAnyArguments().thenReturn(mockContext);
            whenNew(ExecutionRunImpl.class).withAnyArguments().thenReturn(mockRun);
            whenNew(ExecutionRunResultImpl.class).withAnyArguments().thenReturn(mockRunResult);
            whenNew(DockerImageIdLookup.class).withAnyArguments().thenReturn(mockImageIdLookup);


            when(mockImageIdLookup.lookUpImageId(Mockito.anyString())).thenReturn("quakeledger:latest");
            when(mockContextManager.createExecutionContext(Mockito.anyString(), Mockito.anyList())).thenReturn(mockContext);

            doNothing().when(mockContext).close();
            // nothing to write
            doNothing().when(mockContext).writeToFile(Mockito.any(byte[].class), Mockito.anyString(), Mockito.anyString());

            final String txtQuakeML = "<eventParameters publicID=\"quakeml:quakeledger/0\" xmlns=\"http://quakeml.org/xmlns/bed/1.2\">\n" +
                    "  <event publicID=\"quakeml:quakeledger/84945\">\n" +
                    "    <preferredOriginID>quakeml:quakeledger/84945</preferredOriginID>\n" +
                    "    <preferredMagnitudeID>quakeml:quakeledger/84945</preferredMagnitudeID>\n" +
                    "    <type>earthquake</type>\n" +
                    "    <description>\n" +
                    "      <text>stochastic</text>\n" +
                    "    </description>\n" +
                    "    <origin publicID=\"quakeml:quakeledger/84945\">\n" +
                    "      <time>\n" +
                    "        <value>16773-01-01T00:00:00.000000Z</value>\n" +
                    "        <uncertainty>NaN</uncertainty>\n" +
                    "      </time>\n" +
                    "      <latitude>\n" +
                    "        <value>-30.9227</value>\n" +
                    "        <uncertainty>NaN</uncertainty>\n" +
                    "      </latitude>\n" +
                    "      <longitude>\n" +
                    "        <value>-71.49875</value>\n" +
                    "        <uncertainty>NaN</uncertainty>\n" +
                    "      </longitude>\n" +
                    "      <depth>\n" +
                    "        <value>34.75117</value>\n" +
                    "        <uncertainty>NaN</uncertainty>\n" +
                    "      </depth>\n" +
                    "      <creationInfo>\n" +
                    "        <author>GFZ</author>\n" +
                    "      </creationInfo>\n" +
                    "      <originUncertainty>\n" +
                    "        <horizontalUncertainty>NaN</horizontalUncertainty>\n" +
                    "        <minHorizontalUncertainty>NaN</minHorizontalUncertainty>\n" +
                    "        <maxHorizontalUncertainty>NaN</maxHorizontalUncertainty>\n" +
                    "        <azimuthMaxHorizontalUncertainty>NaN</azimuthMaxHorizontalUncertainty>\n" +
                    "      </originUncertainty>\n" +
                    "    </origin>\n" +
                    "    <magnitude publicID=\"quakeml:quakeledger/84945\">\n" +
                    "      <mag>\n" +
                    "        <value>8.35</value>\n" +
                    "        <uncertainty>NaN</uncertainty>\n" +
                    "      </mag>\n" +
                    "      <type>MW</type>\n" +
                    "      <creationInfo>\n" +
                    "        <author>GFZ</author>\n" +
                    "      </creationInfo>\n" +
                    "    </magnitude>\n" +
                    "    <focalMechanism publicID=\"quakeml:quakeledger/84945\">\n" +
                    "      <nodalPlanes preferredPlane=\"1\">\n" +
                    "        <nodalPlane1>\n" +
                    "          <strike>\n" +
                    "            <value>7.310981</value>\n" +
                    "            <uncertainty>NaN</uncertainty>\n" +
                    "          </strike>\n" +
                    "          <dip>\n" +
                    "            <value>16.352970000000003</value>\n" +
                    "            <uncertainty>NaN</uncertainty>\n" +
                    "          </dip>\n" +
                    "          <rake>\n" +
                    "            <value>90.0</value>\n" +
                    "            <uncertainty>NaN</uncertainty>\n" +
                    "          </rake>\n" +
                    "        </nodalPlane1>\n" +
                    "      </nodalPlanes>\n" +
                    "    </focalMechanism>\n" +
                    "  </event>\n" +
                    "</eventParameters>\n";

            when(mockContext.readFromFile(Mockito.anyString())).thenReturn(txtQuakeML.getBytes());

            when(mockContext.run()).thenReturn(mockRun);

            final PrintStream stdin = new PrintStream(new ByteArrayOutputStream());

            when(mockRun.getStdin()).thenReturn(stdin);
            when(mockRun.waitForCompletion()).thenReturn(mockRunResult);

            when(mockRunResult.getExitValue()).thenReturn(0);
            when(mockRunResult.getStderrResult()).thenReturn("");
            when(mockRunResult.getStdoutResult()).thenReturn("");

            final IHasher hasher = new HasherImpl(mockImageIdLookup);
            final ICacher cache = new CacheImpl();

            final IExecutionContextManagerFactory factory = (conf) -> mockContextManager;

            final BaseGfzRiesgosService service = new BaseGfzRiesgosService(
                    configuration, LoggerFactory.getLogger(TestBaseGfzRiesgosService.class),
                    hasher, cache, factory);

            try {
                final Map<String, IData> result = service.run(inputData);

                final String resultKey = "selectedRows";
                assertTrue("The result data is in the map", result.containsKey(resultKey));

                final IData iData = result.get(resultKey);

                assertTrue("The result is a QuakeMLXml binding", iData instanceof QuakeMLXmlDataBinding);

                final XmlObject validatedXml = ((QuakeMLXmlDataBinding) iData).getPayloadValidatedXml();

                final XmlObject xmlTxtQuakeML = XmlObject.Factory.parse(txtQuakeML);

                assertEquals("The resulting xml is the same", xmlTxtQuakeML.xmlText(), validatedXml.xmlText());
            } catch (final ExceptionReport exceptionReport) {
                fail("There should be no exception report");
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
            fail("There should be no exception on mocking");
        }



    }
}
