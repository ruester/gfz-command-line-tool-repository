package org.n52.gfz.riesgos.algorithm.impl;

import org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.commonimpl.CommandLineArgumentDoubleWithDefaultValueImpl;
import org.n52.gfz.riesgos.configuration.commonimpl.CommandLineArgumentStringWithDefaultValueAndAllowedValuesImpl;
import org.n52.gfz.riesgos.configuration.commonimpl.FileOutXmlWithSchemaImpl;
import org.n52.gfz.riesgos.exitvaluehandler.LogExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.stderrhandler.LogStderrHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    }


    /*
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
        map.put("p", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(0.1))));
        map.put("etype", Collections.singletonList(new LiteralStringBinding("deaggregation")));
        map.put("tlon", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(-71.5730623712764))));
        map.put("tlat", Collections.singletonList(new LiteralDoubleBinding(Double.valueOf(-33.1299174879672))));


        final Map<String, IData> result = p.run(map);

        final GenericXMLDataBinding selectedRows = (GenericXMLDataBinding) result.get("selectedRows");
        final String text = selectedRows.getPayload().xmlText();

        System.out.println(text.substring(0, 10));
    }*/
}
