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

import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.gfz.riesgos.cache.ICacher;
import org.n52.gfz.riesgos.cache.IDataRecreator;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGenerator;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorData;
import org.n52.gfz.riesgos.processdescription.impl.ProcessDescriptionGeneratorDataImpl;
import org.n52.gfz.riesgos.processdescription.impl.ProcessDescriptionGeneratorImpl;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;
import org.n52.wps.server.ExceptionReport;
import org.n52.wps.server.ProcessDescription;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This is a wrapper process to read all the output data
 * of an existing process from the cache.
 */
public class CachedProcess extends AbstractSelfDescribingAlgorithm {

    /**
     * Identifier for the input of the cache key to read the data from.
     */
    private static final String IDENTIFIER_CACHE_KEY = "Cache-Key";

    /**
     * Abstract for the input cache key.
     */
    private static final String ABSTRACT_CACHE_KEY =
            "This is the key for the cache to "
            + "lookup the existing results for the process";

    /**
     * Inner algorithm to read its output from the cache.
     */
    private final ICachableProcess algorithm;
    /**
     * Identifier of the newly created process.
     */
    private final String identifier;
    /**
     * Optional abstract of the newly created process.
     */
    private final String optionalAbstract;

    /**
     * Creates a new cached process.
     * @param aAlgorithm process that should be wrapped so that its output
     *                   can be read from the cache.
     * @param aIdentifier identifier of the new process
     * @param aOptionalAbstract optional process abstract of the new process
     */
    public CachedProcess(
            final ICachableProcess aAlgorithm,
            final String aIdentifier,
            final String aOptionalAbstract) {
        this.algorithm = aAlgorithm;
        this.identifier = aIdentifier;
        this.optionalAbstract = aOptionalAbstract;
    }

    /**
     * Runs the process and returns the output from the cache.
     * @param inputData map with the input data
     * @return map with the output data
     * @throws ExceptionReport exception that is thrown in case of an error
     */
    @Override
    public Map<String, IData> run(
            final Map<String, List<IData>> inputData) throws ExceptionReport {

        final String cacheKey = readStringFromInputMap(
                inputData,
                IDENTIFIER_CACHE_KEY);


        final ICacher cache = algorithm.getCache();
        final Optional<Map<String, IDataRecreator>> optionalCacheResult =
                cache.getCachedResult(cacheKey);

        if (!optionalCacheResult.isPresent()) {
            throw new ExceptionReport(
                    "Can't find any data in the cache for "
                            + "the given hash '" + cacheKey + "'",
                    ExceptionReport.INVALID_PARAMETER_VALUE);
        }

        final Map<String, IDataRecreator> cacheResult =
                optionalCacheResult.get();

        return cacheResult.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().recreate()
        ));
    }

    /**
     * Reads a string from the input data map.
     * @param inputData input data map
     * @param identifierToRead identifier to read
     * @return value for the identifier
     * @throws ExceptionReport exceütion that is thrown in case of an error
     */
    private String readStringFromInputMap(
            final Map<String, List<IData>> inputData,
            final String identifierToRead) throws ExceptionReport {
        if (!inputData.containsKey(identifierToRead)) {
            throw new ExceptionReport(
                    "Can't read " + identifierToRead,
                    ExceptionReport.MISSING_PARAMETER_VALUE);
        }
        final List<IData> list = inputData.get(identifierToRead);
        if (list.isEmpty()) {
            throw new ExceptionReport(
                    "Can't read " + identifierToRead,
                    ExceptionReport.MISSING_PARAMETER_VALUE);
        }

        final IData idata = list.get(0);

        if (!(idata instanceof LiteralStringBinding)) {
            throw new ExceptionReport(
                    identifierToRead + " has the wrong binding class",
                    ExceptionReport.INVALID_PARAMETER_VALUE);
        }

        final LiteralStringBinding literalStringBinding =
                (LiteralStringBinding) idata;
        return literalStringBinding.getPayload();
    }

    /**
     * Returns the list of the input identifiers.
     * @return list of the input identifiers
     */
    @Override
    public List<String> getInputIdentifiers() {
        return Collections.singletonList(IDENTIFIER_CACHE_KEY);
    }

    /**
     * Returns the list of the output identifiers.
     * Uses the inner process.
     * @return list of output identifiers
     */
    @Override
    public List<String> getOutputIdentifiers() {
        return algorithm.getOutputIdentifiers();
    }

    /**
     * Returns the binding class for the input parameter.
     * @param id identifier for the input parameter
     * @return binding class for the input parameter (always literal string
     * in this case)
     */
    @Override
    public Class<?> getInputDataType(final String id) {
        return LiteralStringBinding.class;
    }

    /**
     * Returns the binding class for the output parameter.
     * @param id identifier for the output parameter.
     * @return binding class for the output parameter
     */
    @Override
    public Class<?> getOutputDataType(final String id) {
        return algorithm.getOutputDataType(id);
    }

    /**
     * Generates the process description.
     * @return Process description
     */
    @Override
    public ProcessDescription getDescription() {

        final IProcessDescriptionGeneratorData generatorData =
                new ProcessDescriptionGeneratorDataImpl.Builder(
                identifier, getFullQualifiedIdentifier())
                .withProcessAbstract(optionalAbstract)
                // just one literal string
                .withLiteralStringInput(
                        IDENTIFIER_CACHE_KEY,
                        ABSTRACT_CACHE_KEY,
                        false)
                // but all the outputs from the wrapped algorithm
                .withOutputs(algorithm.getOutputDataForProcessGeneration())
                .build();

        final IProcessDescriptionGenerator generator =
                new ProcessDescriptionGeneratorImpl(generatorData);
        final ProcessDescriptionsDocument description =
                generator.generateProcessDescription();
        ProcessDescription processDescription = new ProcessDescription();
        processDescription.addProcessDescriptionForVersion(
                description.getProcessDescriptions()
                        .getProcessDescriptionArray(0), "1.0.0");
        return processDescription;
    }


    /**
     * Returns the a full qualified identifier as replacement for
     * a package path.
     * @return full qualified identifier
     */
    public String getFullQualifiedIdentifier() {
        return IConfiguration.PATH_FULL_QUALIFIED + identifier;
    }

}
