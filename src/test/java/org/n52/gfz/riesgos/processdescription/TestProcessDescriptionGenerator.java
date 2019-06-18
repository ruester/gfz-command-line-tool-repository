package org.n52.gfz.riesgos.processdescription;

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

import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.junit.Test;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.configuration.InputParameterFactory;
import org.n52.gfz.riesgos.configuration.OutputParameterFactory;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.processdescription.impl.ProcessDescriptionGeneratorImpl;
import org.n52.wps.io.IGenerator;
import org.n52.wps.io.IParser;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.webapp.api.FormatEntry;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

/**
 * This is a test class for the generation of the process description
 */
public class TestProcessDescriptionGenerator {

    @Test
    public void testQuakeledgerConfig() {

        // because of the test case
        // because of the test case
        // there is no direct access to the ParserFactory and GeneratorFactory classes
        // it only uses a Supplier to get the the parsers and generators
        final IConfiguration configuration = new QuakeledgerConfig();
        final IProcessDescriptionGenerator generator = new ProcessDescriptionGeneratorImpl(configuration, createParserSupplier(), createGeneratorSupplier());

        final ProcessDescriptionsDocument processDescription = generator.generateProcessDescription();

        final String expectedProcessDescriptionString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<wps:ProcessDescriptions xml:lang=\"en-US\" service=\"WPS\" version=\"1.0.0\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\">\n" +
                "  <ProcessDescription statusSupported=\"true\" storeSupported=\"true\" wps:processVersion=\"1.0.0\">\n" +
                "    <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.QuakeledgerTest</ows:Identifier>\n" +
                "    <ows:Title>QuakeledgerTest</ows:Title>\n" +
                "    <ows:Abstract>This is the description of the test quakeledger process</ows:Abstract>\n" +
                "    <DataInputs>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>lonmin</ows:Identifier>\n" +
                "        <ows:Title>lonmin</ows:Title>\n" +
                "        <ows:Abstract>the minimum longitude to search for</ows:Abstract>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                "          <ows:AnyValue/>\n" +
                "          <DefaultValue>288.0</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>lonmax</ows:Identifier>\n" +
                "        <ows:Title>lonmax</ows:Title>\n" +
                "        <ows:Abstract>the maximum longitude to search for</ows:Abstract>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                "          <ows:AnyValue/>\n" +
                "          <DefaultValue>292.0</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>latmin</ows:Identifier>\n" +
                "        <ows:Title>latmin</ows:Title>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                "          <ows:AnyValue/>\n" +
                "          <DefaultValue>-70.0</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>latmax</ows:Identifier>\n" +
                "        <ows:Title>latmax</ows:Title>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                "          <ows:AnyValue/>\n" +
                "          <DefaultValue>-10.0</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>mmin</ows:Identifier>\n" +
                "        <ows:Title>mmin</ows:Title>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                "          <ows:AnyValue/>\n" +
                "          <DefaultValue>6.6</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>mmax</ows:Identifier>\n" +
                "        <ows:Title>mmax</ows:Title>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                "          <ows:AnyValue/>\n" +
                "          <DefaultValue>8.5</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>zmin</ows:Identifier>\n" +
                "        <ows:Title>zmin</ows:Title>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                "          <ows:AnyValue/>\n" +
                "          <DefaultValue>5.0</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>zmax</ows:Identifier>\n" +
                "        <ows:Title>zmax</ows:Title>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                "          <ows:AnyValue/>\n" +
                "          <DefaultValue>140.0</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>p</ows:Identifier>\n" +
                "        <ows:Title>p</ows:Title>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                "          <ows:AnyValue/>\n" +
                "          <DefaultValue>0.1</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>etype</ows:Identifier>\n" +
                "        <ows:Title>etype</ows:Title>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:string\"/>\n" +
                "          <ows:AllowedValues>\n" +
                "            <ows:Value>observed</ows:Value>\n" +
                "            <ows:Value>deaggregation</ows:Value>\n" +
                "            <ows:Value>stochastic</ows:Value>\n" +
                "            <ows:Value>expert</ows:Value>\n" +
                "          </ows:AllowedValues>\n" +
                "          <DefaultValue>deaggregation</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>tlon</ows:Identifier>\n" +
                "        <ows:Title>tlon</ows:Title>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                "          <ows:AnyValue/>\n" +
                "          <DefaultValue>-71.5730623712764</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>tlat</ows:Identifier>\n" +
                "        <ows:Title>tlat</ows:Title>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                "          <ows:AnyValue/>\n" +
                "          <DefaultValue>-33.1299174879672</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "    </DataInputs>\n" +
                "    <ProcessOutputs>\n" +
                "      <Output>\n" +
                "        <ows:Identifier>selectedRows</ows:Identifier>\n" +
                "        <ows:Title>selectedRows</ows:Title>\n" +
                "        <ows:Abstract>the resulting quakeml</ows:Abstract>\n" +
                "        <ComplexOutput>\n" +
                "          <Default>\n" +
                "            <Format>\n" +
                "              <MimeType>text/xml</MimeType>\n" +
                "            </Format>\n" +
                "          </Default>\n" +
                "          <Supported>\n" +
                "            <Format>\n" +
                "              <MimeType>text/xml</MimeType>\n" +
                "            </Format>\n" +
                "          </Supported>\n" +
                "        </ComplexOutput>\n" +
                "      </Output>\n" +
                "    </ProcessOutputs>\n" +
                "  </ProcessDescription>\n" +
                "</wps:ProcessDescriptions>";

        try {
            final XmlObject expectedProcessDescriptionXml = XmlObject.Factory.parse(expectedProcessDescriptionString);

            assertEquals("The xml output should be equal", expectedProcessDescriptionXml.xmlText(XML_OPTIONS), processDescription.xmlText(XML_OPTIONS));
        } catch(final XmlException xmlException) {
            fail("There is an xml exception");
        }
    }

    private static class QuakeledgerConfig implements IConfiguration {

        @Override
        public String getIdentifier() {
            return "QuakeledgerTest";
        }

        @Override
        public Optional<String> getAbstract() {
            return Optional.of("This is the description of the test quakeledger process");
        }

        @Override
        public List<IInputParameter> getInputIdentifiers() {
            return Arrays.asList(
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("lonmin", false, "the minimum longitude to search for", null, "288.0", null),
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("lonmax", false, "the maximum longitude to search for", null,  "292.0", null),
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("latmin", false, null, null, "-70.0", null),
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("latmax", false, null, null,  "-10.0", null),
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("mmin", false, null, null, "6.6", null),
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("mmax", false, null, null, "8.5", null),
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("zmin", false, null, null,  "5.0", null),
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("zmax", false, null, null,  "140.0", null),
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("p", false, null, null, "0.1", null),
                    InputParameterFactory.INSTANCE.createCommandLineArgumentString(
                            "etype", false, null,null,  "deaggregation",
                            Arrays.asList("observed", "deaggregation", "stochastic", "expert")),
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("tlon", false, null, null, "-71.5730623712764", null),
                    InputParameterFactory.INSTANCE.createCommandLineArgumentDouble("tlat", false, null ,null, "-33.1299174879672", null)
            );
        }

        @Override
        public List<IOutputParameter> getOutputIdentifiers() {
            return Collections.singletonList(
                    OutputParameterFactory.INSTANCE.createFileOutXmlWithSchema(
                            "selectedRows", false, "the resulting quakeml", "test.xml", "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd")
            );
        }

        @Override
        public String getImageId() {
            return "sha256:71b93ade61bf41da8d68419bec12ec1e274eae28b36bc64cc156e1be33294821";
        }

        @Override
        public String getWorkingDirectory() {
            return "/usr/share/git/quakeledger";
        }

        @Override
        public List<String> getCommandToExecute() {
            return Arrays.asList("python3", "eventquery.py");
        }

        @Override
        public List<String> getDefaultCommandLineFlags() {
            return Collections.emptyList();
        }

        @Override
        public Optional<IStderrHandler> getStderrHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<IExitValueHandler> getExitValueHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<IStdoutHandler> getStdoutHandler() {
            return Optional.empty();
        }
    }


    // xml options for comparing the xml results
    private static final XmlOptions XML_OPTIONS = createXmlOptions();

    private static XmlOptions createXmlOptions() {
        final XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setSavePrettyPrint();
        xmlOptions.setSaveAggressiveNamespaces();
        final Map<String, String> nsmap = new HashMap<>();
        nsmap.put("http://www.opengis.net/ows/1.1", "ows");
        nsmap.put("http://www.opengis.net/wps/1.0.0", "wps");
        xmlOptions.setSaveSuggestedPrefixes(nsmap);

        return xmlOptions;
    }

    private Supplier<List<IParser>> createParserSupplier() {
        final IParser parser = new IParser() {
            @Override
            public IData parse(InputStream inputStream, String s, String s1) {
                return null;
            }

            @Override
            public IData parseBase64(InputStream inputStream, String s, String s1) {
                return null;
            }

            @Override
            public boolean isSupportedSchema(String s) {
                return true;
            }

            @Override
            public boolean isSupportedFormat(String s) {
                return false;
            }

            @Override
            public boolean isSupportedEncoding(String s) {
                return false;
            }

            @Override
            public boolean isSupportedDataBinding(Class<?> aClass) {
                return false;
            }

            @Override
            public String[] getSupportedSchemas() {
                return new String[0];
            }

            @Override
            public String[] getSupportedFormats() {
                return new String[]{ "text/xml"};
            }

            @Override
            public String[] getSupportedEncodings() {
                return new String[0];
            }

            @Override
            public List<FormatEntry> getSupportedFullFormats() {
                final FormatEntry entry = new FormatEntry("text/xml", null, null, true);
                return Collections.singletonList(entry);
            }

            @Override
            public Class<?>[] getSupportedDataBindings() {
                return new Class<?>[] {GenericXMLDataBinding.class};
            }
        };

        return () -> Collections.singletonList(parser);
    }


    private Supplier<List<IGenerator>> createGeneratorSupplier() {
        final IGenerator generator = new IGenerator() {
            @Override
            public InputStream generateStream(IData iData, String s, String s1) {
                return null;
            }

            @Override
            public InputStream generateBase64Stream(IData iData, String s, String s1) {
                return null;
            }

            @Override
            public boolean isSupportedSchema(String s) {
                return false;
            }

            @Override
            public boolean isSupportedFormat(String s) {
                return false;
            }

            @Override
            public boolean isSupportedEncoding(String s) {
                return false;
            }

            @Override
            public boolean isSupportedDataBinding(Class<?> aClass) {
                return false;
            }

            @Override
            public String[] getSupportedSchemas() {
                return new String[0];
            }

            @Override
            public String[] getSupportedFormats() {
                return new String[0];
            }

            @Override
            public String[] getSupportedEncodings() {
                return new String[0];
            }

            @Override
            public List<FormatEntry> getSupportedFullFormats() {
                final FormatEntry entry = new FormatEntry("text/xml", null, null, true);
                return Collections.singletonList(entry);
            }

            @Override
            public Class<?>[] getSupportedDataBindings() {
                return new Class<?>[]{ GenericXMLDataBinding.class };
            }
        };
        return () -> Collections.singletonList(generator);
    }
}
