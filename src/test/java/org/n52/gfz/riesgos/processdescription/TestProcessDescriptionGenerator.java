package org.n52.gfz.riesgos.processdescription;

import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.junit.Test;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.commonimpl.CommandLineArgumentDoubleWithDefaultValueImpl;
import org.n52.gfz.riesgos.configuration.commonimpl.CommandLineArgumentStringWithDefaultValueAndAllowedValuesImpl;
import org.n52.gfz.riesgos.configuration.commonimpl.FileOutXmlWithSchemaImpl;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.processdescription.impl.ProcessDescriptionGeneratorImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

/**
 * This is a test class for the generation of the process description
 */
public class TestProcessDescriptionGenerator {

    @Test
    public void testQuakeledgerConfig() {

        final IProcessDescriptionGenerator generator = new ProcessDescriptionGeneratorImpl();

        final IConfiguration configuration = new QuakeledgerConfig();

        final ProcessDescriptionsDocument processDescription = generator.generateProcessDescription(configuration);

        // TODO
        // the link to xsi and the schema location is not necessary
        // if it is removed, the test will pass
        final String expecedProcessDescriptionString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<wps:ProcessDescriptions xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsDescribeProcess_response.xsd\" xml:lang=\"en-US\" service=\"WPS\" version=\"1.0.0\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ows=\"http://www.opengis.net/ows/1.1\">\n" +
                "  <ProcessDescription statusSupported=\"true\" storeSupported=\"true\" wps:processVersion=\"1.0.0\">\n" +
                "    <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.QuakeledgerTest</ows:Identifier>\n" +
                "    <ows:Title>QuakeledgerTest</ows:Title>\n" +
                "    <DataInputs>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>lonmin</ows:Identifier>\n" +
                "        <ows:Title>lonmin</ows:Title>\n" +
                "        <LiteralData>\n" +
                "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                "          <ows:AnyValue/>\n" +
                "          <DefaultValue>288.0</DefaultValue>\n" +
                "        </LiteralData>\n" +
                "      </Input>\n" +
                "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                "        <ows:Identifier>lonmax</ows:Identifier>\n" +
                "        <ows:Title>lonmax</ows:Title>\n" +
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
                "        <ComplexOutput>\n" +
                "          <Default>\n" +
                "            <Format>\n" +
                "              <MimeType>text/xml</MimeType>\n" +
                "              <Schema>http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd</Schema>\n" +
                "            </Format>\n" +
                "          </Default>\n" +
                "          <Supported>\n" +
                "            <Format>\n" +
                "              <MimeType>text/xml</MimeType>\n" +
                "              <Schema>http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd</Schema>\n" +
                "            </Format>\n" +
                "          </Supported>\n" +
                "        </ComplexOutput>\n" +
                "      </Output>\n" +
                "    </ProcessOutputs>\n" +
                "  </ProcessDescription>\n" +
                "</wps:ProcessDescriptions>";

        try {
            final XmlObject expecedProcessDescriptionXml = XmlObject.Factory.parse(expecedProcessDescriptionString);

            assertEquals("The xml output should be equal", expecedProcessDescriptionXml.xmlText(XML_OPTIONS), processDescription.xmlText(XML_OPTIONS));
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
        public List<IIdentifierWithBinding> getInputIdentifiers() {
            return Arrays.asList(
                    new CommandLineArgumentDoubleWithDefaultValueImpl("lonmin", 288),
                    new CommandLineArgumentDoubleWithDefaultValueImpl("lonmax", 292),
                    new CommandLineArgumentDoubleWithDefaultValueImpl("latmin", -70),
                    new CommandLineArgumentDoubleWithDefaultValueImpl("latmax", -10),
                    new CommandLineArgumentDoubleWithDefaultValueImpl("mmin", 6.6),
                    new CommandLineArgumentDoubleWithDefaultValueImpl("mmax", 8.5),
                    new CommandLineArgumentDoubleWithDefaultValueImpl("zmin", 5),
                    new CommandLineArgumentDoubleWithDefaultValueImpl("zmax", 140),
                    new CommandLineArgumentDoubleWithDefaultValueImpl("p", 0.1),
                    new CommandLineArgumentStringWithDefaultValueAndAllowedValuesImpl("etype", "deaggregation",
                            Arrays.asList("observed", "deaggregation", "stochastic", "expert")),
                    new CommandLineArgumentDoubleWithDefaultValueImpl("tlon", -71.5730623712764),
                    new CommandLineArgumentDoubleWithDefaultValueImpl("tlat", -33.1299174879672)
            );
        }

        @Override
        public List<IIdentifierWithBinding> getOutputIdentifiers() {
            return Arrays.asList(
                    new FileOutXmlWithSchemaImpl("selectedRows","test.xml", "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd")
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


    public static final XmlOptions XML_OPTIONS = createXmlOptions();

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
}
