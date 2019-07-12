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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This is a process for reading data from the cache.
 */
public class ReadDataFromCacheProcess extends AbstractSelfDescribingAlgorithm {

    /**
     * Identifier for the input of the cache key to read the data from.
     */
    private static final String IDENTIFIER_CACHE_KEY = "Cache-Key";
    /**
     * Identifier for the input of the output name to read from the key.
     */
    private static final String IDENTIFIER_OUTPUT_NAME = "Output-Name";

    /**
     * Abstract for the cache key input.
     */
    private static final String ABSTRACT_CACHE_KEY =
            "This is the key for the cache to read the data out";
    /**
     * Abstract for the input to specify the output name for which
     * we have data in.
     */
    private static final String ABSTRACT_OUTPUT_NAME =
            "This is the name of the data for which the cached "
                    + "dataset contains the dataset";

    /**
     * Output identifier for the process output here.
     */
    private static final String OUTPUT_IDENTIFIER = "Output";


    /**
     * Cache to access.
     */
    private final ICacher cacher;
    /**
     * Identifier of the process instance.
     */
    private final String identifier;
    /**
     * Optional abstract of the process.
     */
    private final String processAbstract;
    /**
     * binding class to read the data.
     */
    private final Class<? extends IData> clazz;
    /**
     * Optional abstract for the output.
     */
    private final String outputAbstract;


    /**
     * Create the process to read data from cache.
     * @param aCacher cache to read from
     * @param aIdentifier identifier (name) the process should have
     * @param aProcessAbstract optional abstract of the process
     * @param aClazz a class to read from the cached outputs
     * @param aOutputAbstract optional abstract for the output
     */
    public ReadDataFromCacheProcess(
            final ICacher aCacher,
            final String aIdentifier,
            final String aProcessAbstract,
            final Class<? extends IData> aClazz,
            final String aOutputAbstract) {
        super();

        this.identifier = aIdentifier;
        this.processAbstract = aProcessAbstract;
        this.cacher = aCacher;
        this.clazz = aClazz;
        this.outputAbstract = aOutputAbstract;
    }

    /**
     *
     * @return list with the input identifiers
     */
    @Override
    public List<String> getInputIdentifiers() {
        return Arrays.asList(
                IDENTIFIER_CACHE_KEY,
                IDENTIFIER_OUTPUT_NAME
        );
    }

    /**
     *
     * @return list with the output identifiers
     */
    @Override
    public List<String> getOutputIdentifiers() {
        return Collections.singletonList(OUTPUT_IDENTIFIER);
    }

    /**
     * Runs the process and gives the result map back.
     * @param inputData input data for the process
     * @return map with the resulting data
     * @throws ExceptionReport exception that is thrown if the process
     * can't find any output
     */
    @Override
    public Map<String, IData> run(
            final Map<String, List<IData>> inputData)
            throws ExceptionReport {
        final Map<String, IData> result = new HashMap<>();

        final String cacheKey = readStringFromInputMap(
                inputData,
                IDENTIFIER_CACHE_KEY);
        final Optional<String> optionalOutputName =
                readOptionalStringFromInputMap(
                        inputData,
                        IDENTIFIER_OUTPUT_NAME);

        final Optional<Map<String, IDataRecreator>> optionalCacheValues =
                cacher.getCachedResult(cacheKey);

        if (!optionalCacheValues.isPresent()) {
            throw new ExceptionReport(
                    "No Data could be found for the cache key '"
                            + cacheKey
                            + "'",
                    ExceptionReport.INVALID_PARAMETER_VALUE);
        }

        final Map<String, IDataRecreator> cacheValues =
                optionalCacheValues.get();
        final Map<String, IDataRecreator> filtered =
                filterForBindingClass(cacheValues, clazz);

        final IDataRecreator recreator;
        if (filtered.size() == 1) {
            recreator = filtered.values().iterator().next();
        } else {
            if (!optionalOutputName.isPresent()) {
                throw new ExceptionReport(
                        "There is no output-name given to check "
                        + "which output should be given back",
                        ExceptionReport.MISSING_PARAMETER_VALUE);
            }
            final String outputName = optionalOutputName.get();

            if (!cacheValues.containsKey(outputName)) {
                throw new ExceptionReport(
                        "No Data could be found for the output name '"
                                + outputName
                                + "'",
                        ExceptionReport.INVALID_PARAMETER_VALUE);
            }

            recreator = cacheValues.get(outputName);
        }


        final IData data = recreator.recreate();

        result.put(OUTPUT_IDENTIFIER, data);

        return result;
    }

    /**
     * Filters the map for recreators that support the same binding class.
     * @param cacheValues data with recreators from the cache
     * @param bindingClass class that should be included for one or more
     *                     recreators
     * @return map with the recreators that support the binding class
     */
    private Map<String, IDataRecreator> filterForBindingClass(
            final Map<String, IDataRecreator> cacheValues,
            final Class<? extends IData> bindingClass) {

        return cacheValues.entrySet().stream()
                .filter(
                        entry -> Objects.equals(
                                entry.getValue().getBindingClassToRecreate(),
                                bindingClass))
                .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
        ));
    }

    /**
     * Function to read a string key from the input data map.
     * @param inputData input data of the process
     * @param identifierToRead identifier to read from the input data
     * @return String with the value
     * @throws ExceptionReport exception that is thrown
     * if the identifier can't be found in the input data map
     * or if the type doesn't match
     */
    private String readStringFromInputMap(
            final Map<String, List<IData>> inputData,
            final String identifierToRead) throws ExceptionReport {
        if (!inputData.containsKey(identifierToRead)) {
            throw new ExceptionReport(
                    "Can't read "
                            + identifierToRead,
                    ExceptionReport.MISSING_PARAMETER_VALUE);
        }
        final List<IData> list = inputData.get(identifierToRead);
        if (list.isEmpty()) {
            throw new ExceptionReport(
                    "Can't read "
                            + identifierToRead,
                    ExceptionReport.MISSING_PARAMETER_VALUE);
        }

        final IData idata = list.get(0);

        if (!(idata instanceof LiteralStringBinding)) {
            throw new ExceptionReport(
                    identifierToRead
                            + " has the wrong binding class",
                    ExceptionReport.INVALID_PARAMETER_VALUE);
        }

        final LiteralStringBinding literalStringBinding =
                (LiteralStringBinding) idata;
        return literalStringBinding.getPayload();
    }

    /**
     * Searches for a string value in the input map.
     * @param inputData map with the input data for the process
     * @param identifierToRead identifier to read
     * @return optional with the data or empty
     */
    private Optional<String> readOptionalStringFromInputMap(
            final Map<String, List<IData>> inputData,
            final String identifierToRead) {

        if (!inputData.containsKey(identifierToRead)) {
            return Optional.empty();
        }
        final List<IData> list = inputData.get(identifierToRead);
        if (list.isEmpty()) {
            return Optional.empty();
        }

        final IData idata = list.get(0);

        if (!(idata instanceof LiteralStringBinding)) {
            return Optional.empty();
        }

        final LiteralStringBinding literalStringBinding =
                (LiteralStringBinding) idata;
        return Optional.ofNullable(literalStringBinding.getPayload());
    }

    /**
     * Returns the classes for the input data.
     * @param id identifier of the input parameter
     * @return always literal string
     */
    @Override
    public Class<?> getInputDataType(final String id) {
        // both the cache key and the output name
        return LiteralStringBinding.class;
    }

    /**
     * Returns the classes for the output data.
     * @param id identifier of the output parameter
     * @return always the binding class that is supported by the process
     */
    @Override
    public Class<?> getOutputDataType(final String id) {
        return clazz;
    }

    /**
     * Generates the process description.
     * @return Process description
     */
    @Override
    public ProcessDescription getDescription() {

        /*
         Always the cache key and an optional output-name as inputs.
         Always the supported class as one output.
         */
        final IProcessDescriptionGeneratorData generatorData =
                new ProcessDescriptionGeneratorDataImpl.Builder(
                identifier, getFullQualifiedIdentifier())
                .withProcessAbstract(processAbstract)
                .withLiteralStringInput(
                        IDENTIFIER_CACHE_KEY,
                        ABSTRACT_CACHE_KEY,
                        false)
                .withLiteralStringInput(
                        IDENTIFIER_OUTPUT_NAME,
                        ABSTRACT_OUTPUT_NAME,
                        true)
                .withRequiredComplexOutput(
                        OUTPUT_IDENTIFIER,
                        outputAbstract,
                        clazz)
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
     * This method is to get around that there are serveral instances
     * of this class around, but each should be a process on its own.
     *
     * @return full qualified identifier
     */
    public String getFullQualifiedIdentifier() {
        return IConfiguration.PATH_FULL_QUALIFIED + identifier;
    }
}
