package org.n52.gfz.riesgos.algorithm.impl;

import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralDoubleBindingToStringCmd;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.commonimpl.CommandLineArgumentDoubleImpl;
import org.n52.gfz.riesgos.configuration.commonimpl.CommandLineArgumentStringWithAllowedValuesImpl;
import org.n52.gfz.riesgos.configuration.commonimpl.FileOutXmlImpl;
import org.n52.gfz.riesgos.exitvaluehandler.LogExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.stderrhandler.LogStderrHandler;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Quakeledger is one implementation of a abstract riesgos service.
 * It is used to query a python script to provide a list of
 * earth quake events using quakeml
 *
 */
public class Quakeledger extends BaseGfzRiesgosService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Quakeledger.class);

    /**
     * Default constructor
     */
    public Quakeledger() {
        super(createQuakeledgerConfig(), LOGGER);
    }

    private static IConfiguration createQuakeledgerConfig() {
        return new QuakeledgerConfig();
    }

    private static class QuakeledgerConfig implements IConfiguration {
        @Override
        public List<IIdentifierWithBinding> getInputIdentifiers() {
            return Arrays.asList(
                    new CommandLineArgumentDoubleImpl("lonmin"),
                    new CommandLineArgumentDoubleImpl("lonmax"),
                    new CommandLineArgumentDoubleImpl("latmin"),
                    new CommandLineArgumentDoubleImpl("latmax"),
                    new CommandLineArgumentDoubleImpl("mmin"),
                    new CommandLineArgumentDoubleImpl("mmax"),
                    new CommandLineArgumentDoubleImpl("zmin"),
                    new CommandLineArgumentDoubleImpl("zmax"),
                    new CommandLineArgumentDoubleImpl("p"),
                    new CommandLineArgumentStringWithAllowedValuesImpl("etype",
                            "observed", "deaggregation", "stochastic", "expert"),
                    new CommandLineArgumentDoubleImpl("tlon"),
                    new CommandLineArgumentDoubleImpl("tlat")
            );
        }

        @Override
        public List<IIdentifierWithBinding> getOutputIdentifiers() {
            return Arrays.asList(
                    new FileOutXmlImpl("selectedRows","test.xml")
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
            return Optional.of(new LogStderrHandler(LOGGER::debug));
        }

        @Override
        public Optional<IExitValueHandler> getExitValueHandler() {
            return Optional.of(new LogExitValueHandler(LOGGER::debug));
        }

        @Override
        public Optional<IStdoutHandler> getStdoutHandler() {
            return Optional.empty();
        }

        @Override
        public ProcessDescriptionsDocument getProcessDescription() {
            final String txt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<wps:ProcessDescriptions xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsDescribeProcess_response.xsd\" xml:lang=\"en-US\" service=\"WPS\" version=\"1.0.0\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ows=\"http://www.opengis.net/ows/1.1\">\n" +
                    "  <ProcessDescription statusSupported=\"true\" storeSupported=\"true\" wps:processVersion=\"1.0.0\">\n" +
                    "    <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.Quakeledger</ows:Identifier>\n" +
                    "    <ows:Title>QuakeledgerTest</ows:Title>\n" +
                    "    <DataInputs>\n" +
                    "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                    "        <ows:Identifier>lonmin</ows:Identifier>\n" +
                    "        <ows:Title>lonmin</ows:Title>\n" +
                    "        <LiteralData>\n" +
                    "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                    "          <ows:AnyValue/>\n" +
                    "          <DefaultValue>288</DefaultValue>\n" +
                    "        </LiteralData>\n" +
                    "      </Input>\n" +
                    "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                    "        <ows:Identifier>lonmax</ows:Identifier>\n" +
                    "        <ows:Title>lonmax</ows:Title>\n" +
                    "        <LiteralData>\n" +
                    "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                    "          <ows:AnyValue/>\n" +
                    "          <DefaultValue>292</DefaultValue>\n" +
                    "        </LiteralData>\n" +
                    "      </Input>\n" +
                    "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                    "        <ows:Identifier>latmin</ows:Identifier>\n" +
                    "        <ows:Title>latmin</ows:Title>\n" +
                    "        <LiteralData>\n" +
                    "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                    "          <ows:AnyValue/>\n" +
                    "          <DefaultValue>-70</DefaultValue>\n" +
                    "        </LiteralData>\n" +
                    "      </Input>\n" +
                    "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                    "        <ows:Identifier>latmax</ows:Identifier>\n" +
                    "        <ows:Title>latmax</ows:Title>\n" +
                    "        <LiteralData>\n" +
                    "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                    "          <ows:AnyValue/>\n" +
                    "          <DefaultValue>-10</DefaultValue>\n" +
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
                    "          <DefaultValue>5</DefaultValue>\n" +
                    "        </LiteralData>\n" +
                    "      </Input>\n" +
                    "      <Input minOccurs=\"1\" maxOccurs=\"1\">\n" +
                    "        <ows:Identifier>zmax</ows:Identifier>\n" +
                    "        <ows:Title>zmax</ows:Title>\n" +
                    "        <LiteralData>\n" +
                    "          <ows:DataType ows:reference=\"xs:double\"/>\n" +
                    "          <ows:AnyValue/>\n" +
                    "          <DefaultValue>140</DefaultValue>\n" +
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

            XmlOptions option = new XmlOptions();
            option.setLoadTrimTextBuffer();

            try {
                return ProcessDescriptionsDocument.Factory.parse(txt, option);
            } catch(final XmlException xmlException) {
                throw new RuntimeException(xmlException);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        final Quakeledger p = new Quakeledger();

        final Map<String, List<IData>> map = new HashMap<>();
        map.put("lonmin", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(288))));
        map.put("lonmax", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(292))));
        map.put("latmin", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(-70))));
        map.put("latmax", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(-10))));
        map.put("mmin", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(6.6))));
        map.put("mmax", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(8.5))));
        map.put("zmin", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(5))));
        map.put("zmax", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(140))));
        map.put("p", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(1.1))));
        map.put("etype", Collections.singletonList(new LiteralStringBinding("deaggregation")));
        map.put("tlon", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(-71.5730623712764))));
        map.put("tlat", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(-33.1299174879672))));


        final Map<String, IData> result = p.run(map);

        final GenericXMLDataBinding selectedRows = (GenericXMLDataBinding) result.get("selectedRows");
        final String text = selectedRows.getPayload().xmlText();

        System.out.println(text.substring(0, 10));
    }
}
