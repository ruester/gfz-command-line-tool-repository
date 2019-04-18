package org.n52.gfz.riesgos.configuration;

/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 *
 *

 */

import org.n52.gfz.riesgos.configuration.impl.ConfigurationImpl;
import org.n52.gfz.riesgos.exitvaluehandler.LogExitValueHandler;
import org.n52.gfz.riesgos.stderrhandler.LogStderrHandler;

import java.util.Arrays;
import java.util.Optional;

/**
 * Factory class for providing predefined configurations
 */
public class ConfigurationFactory {

    private ConfigurationFactory() {
        // static
    }

    /**
     * Creates the configuration for Quakeledger
     * @param imageId imageId of the docker container
     * @return IConfiguration
     */
    public static IConfiguration createQuakeledger(final String imageId) {
        return new ConfigurationImpl.Builder(
                "QuakeledgerTest",
                imageId,
                "/usr/share/git/quakeledger",
                Arrays.asList("python3", "eventquery.py"))
                .withAddedInputIdentifiers(Arrays.asList(
                        IdentifierWithBindingFactory.createCommandLineArgumentBBox("input-boundingbox", Arrays.asList("EPSG:4326", "EPSG:4328")),
                        IdentifierWithBindingFactory.createCommandLineArgumentDouble("mmin", null, "6.6", null),
                        IdentifierWithBindingFactory.createCommandLineArgumentDouble("mmax", null, "8.5", null),
                        IdentifierWithBindingFactory.createCommandLineArgumentDouble("zmin", null, "5", null),
                        IdentifierWithBindingFactory.createCommandLineArgumentDouble("zmax", null, "140", null),
                        IdentifierWithBindingFactory.createCommandLineArgumentDouble("p", null, "0.1", null),
                        IdentifierWithBindingFactory.createCommandLineArgumentString(
                                "etype", null, "deaggregation",
                                Arrays.asList("observed", "deaggregation", "stochastic", "expert")),
                        IdentifierWithBindingFactory.createCommandLineArgumentDouble("tlon", null, "-71.5730623712764", null),
                        IdentifierWithBindingFactory.createCommandLineArgumentDouble("tlat", null, "-33.1299174879672", null)
                ))
                .withAddedOutputIdentifier(
                        IdentifierWithBindingFactory.createFileOutXmlWithSchema(
                                "selectedRows", "test.xml", "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd")
                )
                .withExitValueHandler(new LogExitValueHandler())
                .withStderrHandler(new LogStderrHandler())
                .build();
    }

    /**
     * Creates the configuration for shakyground
     * @param imageid imageId of the docker image
     * @return IConfiguration
     */
    public static IConfiguration createShakyground(final String imageid) {
        return new ConfigurationImpl.Builder(
                "ShakygroundTest",
                imageid,
                "/usr/share/git/shakyground",
                Arrays.asList("python3", "service.py"))
                .withAddedInputIdentifier(
                        IdentifierWithBindingFactory.createCommandLineArgumentXmlFileWithSchemaWithoutHeader(
                                "quakeMLFile", "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd")
                )
                .withAddedOutputIdentifier(
                        IdentifierWithBindingFactory.createStdoutXmlWithSchema(
                                "shakeMapFile", "http://earthquake.usgs.gov/eqcenter/shakemap")
                )
                .withStderrHandler(new LogStderrHandler())
                .withExitValueHandler(new LogExitValueHandler())
                .build();
    }
}
