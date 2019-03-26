package org.n52.gfz.riesgos.algorithm.predefinedconfig;

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

import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.IdentifierWithBindingFactory;
import org.n52.gfz.riesgos.exitvaluehandler.LogExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.stderrhandler.LogStderrHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * QuakeledgerConfiguration is one predefined implementation of a riesgos process configuration.
 * It is used to query a python script to provide a list of
 * earth quake events using quakeml
 *
 * This class is temporary. It should be replaced with the parsed configuration.
 */
public class QuakeledgerConfiguration implements IConfiguration {


    private final String imageId;

    public QuakeledgerConfiguration(final String imageId) {

        this.imageId = imageId;
    }

    @Override
    public String getIdentifier() {
        return "QuakeledgerTest";
    }

    @Override
    public List<IIdentifierWithBinding> getInputIdentifiers() {
        return Arrays.asList(

                IdentifierWithBindingFactory.createCommandLineArgumentBBox("input-boundingbox", Arrays.asList("EPSG:4326", "EPSG:4328")),
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
                        "selectedRows", "test.xml", "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd")
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
        return Optional.of(new LogStderrHandler());
    }

    @Override
    public Optional<IExitValueHandler> getExitValueHandler() {
        return Optional.of(new LogExitValueHandler());
    }

    @Override
    public Optional<IStdoutHandler> getStdoutHandler() {
        return Optional.empty();
    }
}
