package org.n52.gfz.riesgos.repository;

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

import org.n52.gfz.riesgos.formats.geotiff.parsers.GeotiffParser;
import org.n52.gfz.riesgos.formats.json.generators.JsonGenerator;
import org.n52.gfz.riesgos.formats.json.parsers.JsonParser;
import org.n52.gfz.riesgos.formats.nrml.generators.NrmlGeoJsonGenerator;
import org.n52.gfz.riesgos.formats.nrml.generators.NrmlXmlGenerator;
import org.n52.gfz.riesgos.formats.nrml.parsers.NrmlXmlParser;
import org.n52.gfz.riesgos.formats.quakeml.generators.QuakeMLGML3Generator;
import org.n52.gfz.riesgos.formats.quakeml.generators.QuakeMLGeoJsonGenerator;
import org.n52.gfz.riesgos.formats.quakeml.generators.QuakeMLOriginalXmlGenerator;
import org.n52.gfz.riesgos.formats.quakeml.generators.QuakeMLValidatedXmlGenerator;
import org.n52.gfz.riesgos.formats.quakeml.parsers.QuakeMLGML3Parser;
import org.n52.gfz.riesgos.formats.quakeml.parsers.QuakeMLGeoJsonParser;
import org.n52.gfz.riesgos.formats.quakeml.parsers.QuakeMLOriginalXmlParser;
import org.n52.gfz.riesgos.formats.quakeml.parsers.QuakeMLValidatedXmlParser;
import org.n52.gfz.riesgos.formats.shakemap.generators.ShakemapGML3Generator;
import org.n52.gfz.riesgos.formats.shakemap.generators.ShakemapGeoJsonGenerator;
import org.n52.gfz.riesgos.formats.shakemap.generators.ShakemapGeotiffGenerator;
import org.n52.gfz.riesgos.formats.shakemap.generators.ShakemapWMSGenerator;
import org.n52.gfz.riesgos.formats.shakemap.generators.ShakemapXmlGenerator;
import org.n52.gfz.riesgos.formats.shakemap.parsers.ShakemapXmlParser;
import org.n52.gfz.riesgos.repository.modules.GfzRiesgosRepositoryCM;
import org.n52.wps.commons.WPSConfig;
import org.n52.wps.io.GeneratorFactory;
import org.n52.wps.io.IGenerator;
import org.n52.wps.io.IParser;
import org.n52.wps.io.ParserFactory;
import org.n52.wps.server.IAlgorithm;
import org.n52.wps.server.ITransactionalAlgorithmRepository;
import org.n52.wps.server.ProcessDescription;
import org.n52.wps.webapp.api.ConfigurationCategory;
import org.n52.wps.webapp.api.ConfigurationModule;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Repository for the algorithms for the gfz in riesgos using the
 * generation of services by providing configurations to call
 * command line tools in docker.
 */
public class GfzRiesgosRepository implements ITransactionalAlgorithmRepository {

    /**
     * The configuration module.
     */
    private final GfzRiesgosRepositoryCM configurationModule;

    /**
     * Default constructor.
     */
    public GfzRiesgosRepository() {

        final WPSConfig wpsConfig = WPSConfig.getInstance();

        registerGenerators();
        registerParsers();

        final ConfigurationModule cm = wpsConfig
                .getConfigurationModuleForClass(
                        getClass().getName(),
                        ConfigurationCategory.REPOSITORY);
        if (cm instanceof GfzRiesgosRepositoryCM) {
            configurationModule = (GfzRiesgosRepositoryCM) cm;
        } else {
            throw new RuntimeException("Configuration Module has wrong type");
        }
    }

    /**
     * Method to use to register all the generators to the server.
     */
    private void registerGenerators() {
        Stream.of(
                // quakeml
                new QuakeMLValidatedXmlGenerator(),
                new QuakeMLOriginalXmlGenerator(),
                new QuakeMLGeoJsonGenerator(),
                new QuakeMLGML3Generator(),
                // shakemap
                new ShakemapXmlGenerator(),
                new ShakemapGeoJsonGenerator(),
                new ShakemapGML3Generator(),
                new ShakemapGeotiffGenerator(),
                new ShakemapWMSGenerator(),
                // json
                new JsonGenerator(),
                // nrml
                new NrmlXmlGenerator(),
                new NrmlGeoJsonGenerator()
        ).forEach(new RegisterGeneratorTask());
    }

    /**
     * Method to use to register all the parsers to the server.
     */
    private void registerParsers() {
        Stream.of(
                // geotiff
                new GeotiffParser(),
                // quakeml
                new QuakeMLValidatedXmlParser(),
                new QuakeMLOriginalXmlParser(),
                new QuakeMLGeoJsonParser(),
                new QuakeMLGML3Parser(),
                // shakemap
                new ShakemapXmlParser(),
                // json
                new JsonParser(),
                // nrml
                new NrmlXmlParser()
        ).forEach(new RegisterParserTask());
    }

    /**
     * Method to add an algorithm.
     * Currently not supported.
     * @param processIdentifier process identifier to add
     * @return nothing; Unsupported operation
     */
    @Override
    public boolean addAlgorithm(final Object processIdentifier) {
        throw new UnsupportedOperationException(
                "The repository is only configured via the json config. "
                 + "Adding an algorithm via java code is not supported");
    }

    /**
     * Method to remove an algorithm.
     * Currently not supported.
     * @param processIdentifier process identifier to remove
     * @return nothing; Unsupported operation
     */
    @Override
    public boolean removeAlgorithm(final Object processIdentifier) {
        throw new UnsupportedOperationException(
                "The repository is only configured via the json config. "
                + "Removing an algorithm via java code is not supported");
    }

    /**
     * Method to get a collection with the method names.
     * @return collection with all the algorithm names
     */
    @Override
    public Collection<String> getAlgorithmNames() {
        return configurationModule.getAlgorithmNames();
    }

    /**
     * Method to lookup an algorithm by identifier.
     * @param processIdentifier identifier to lookup the algorithm
     * @return algorithm
     */
    @Override
    public IAlgorithm getAlgorithm(final String processIdentifier) {
        return configurationModule.getAlgorithm(processIdentifier);
    }

    /**
     * Method to get the process description for the process
     * identified by the identifier.
     * @param processIdentifier identifier to lookup the algorithm
     * @return process description
     */
    @Override
    public ProcessDescription getProcessDescription(
            final String processIdentifier) {
        return configurationModule.getProcessDescription(processIdentifier);
    }

    /**
     * Test if the repo contains an algorithm by identifier.
     * @param processIdentifier identifier to lookup the algorithm
     * @return true if there is an algorithm for the identifier
     */
    @Override
    public boolean containsAlgorithm(final String processIdentifier) {
        return getAlgorithmNames().contains(processIdentifier);
    }

    /**
     * Shutdown-Hook.
     */
    @Override
    public void shutdown() {
        // nothing to do
    }

    /**
     * Warning:
     * This class uses the side-effects of adding a generator to
     * a modifiable list.
     * That may break if the getAllGenerators() Method is generated or
     * has an unmodifiable view.
     *
     * But in the current implementation that works.
     */
    private static class RegisterGeneratorTask implements Consumer<IGenerator> {

        /**
         * Reference to a list with all the generators currently supported
         * by the server.
         */
        private final List<IGenerator> allGenerators;

        /**
         * Creates an new Instance of this task.
         */
        RegisterGeneratorTask() {
            allGenerators = GeneratorFactory.getInstance().getAllGenerators();
        }

        /**
         * Adds the generator to the list of supported generators.
         * @param generator generator to add
         */
        public void accept(final IGenerator generator) {
            allGenerators.add(generator);
        }
    }

    /**
     * This is also an approach that make use of side-effects
     * to register a parser on the server.
     */
    private static class RegisterParserTask implements Consumer<IParser> {

        /**
         * Reference to a list of all parsers currently supported by the
         * server.
         */
        private final List<IParser> allParsers;

        /**
         * Creates an new instance of this task.
         */
        RegisterParserTask() {
            allParsers = ParserFactory.getInstance().getAllParsers();
        }

        /**
         * Adds the parser to the list of supported parsers.
         * @param parser parser to add
         */
        public void accept(final IParser parser) {
            allParsers.add(parser);
        }
    }
}
