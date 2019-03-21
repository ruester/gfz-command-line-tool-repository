package org.n52.gfz.riesgos.algorithm.impl;

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

import org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.IdentifierWithBindingFactory;
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
 * This class is temporary. It should be replaced with the configuration only.
 *
 */
public class Quakeledger extends BaseGfzRiesgosService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Quakeledger.class);

    /**
     * Constructor with the imageId - because it will change when build on another system
     * @param imageId the id of the docker image to run the code inside a container
     */
    public Quakeledger(final String imageId) {
        super(createQuakeledgerConfig(imageId), LOGGER);
    }

    private static IConfiguration createQuakeledgerConfig(final String imageId) {
        return new QuakeledgerConfig(imageId);
    }

    private static class QuakeledgerConfig implements IConfiguration {

        private final String imageId;

        private QuakeledgerConfig(final String imageId) {
            this.imageId = imageId;
        }

        @Override
        public String getIdentifier() {
            return "QuakeledgerTest";
        }

        @Override
        public List<IIdentifierWithBinding> getInputIdentifiers() {
            return Arrays.asList(
                    IdentifierWithBindingFactory.createCommandLineArgumentDoubleWithDefaultValue("lonmin", 288),
                    IdentifierWithBindingFactory.createCommandLineArgumentDoubleWithDefaultValue("lonmax", 292),
                    IdentifierWithBindingFactory.createCommandLineArgumentDoubleWithDefaultValue("latmin", -70),
                    IdentifierWithBindingFactory.createCommandLineArgumentDoubleWithDefaultValue("latmax", -10),
                    IdentifierWithBindingFactory.createCommandLineArgumentDoubleWithDefaultValue("mmin", 6.6),
                    IdentifierWithBindingFactory.createCommandLineArgumentDoubleWithDefaultValue("mmax", 8.5),
                    IdentifierWithBindingFactory.createCommandLineArgumentDoubleWithDefaultValue("zmin", 5),
                    IdentifierWithBindingFactory.createCommandLineArgumentDoubleWithDefaultValue("zmax", 140),
                    IdentifierWithBindingFactory.createCommandLineArgumentDoubleWithDefaultValue("p", 0.1),
                    IdentifierWithBindingFactory.createCommandLineArgumentStringWithDefaultValueAndAllowedValues(
                            "etype", "deaggregation",
                            Arrays.asList("observed", "deaggregation", "stochastic", "expert")),
                    IdentifierWithBindingFactory.createCommandLineArgumentDoubleWithDefaultValue("tlon", -71.5730623712764),
                    IdentifierWithBindingFactory.createCommandLineArgumentDoubleWithDefaultValue("tlat", -33.1299174879672)
            );
        }

        @Override
        public List<IIdentifierWithBinding> getOutputIdentifiers() {
            return Collections.singletonList(
                    IdentifierWithBindingFactory.createFileOutXmlWithSchema(
                            "selectedRows","test.xml", "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd")
            );
        }

        @Override
        public String getImageId() {
            return imageId;
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
     * a method for debugging on a system to check if and where unexpected behaviour happens
     */
    /*
    public static void main(String[] args) throws Exception {
        final Quakeledger p = new Quakeledger("sha256:71b93ade61bf41da8d68419bec12ec1e274eae28b36bc64cc156e1be33294821");

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
