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

public class CachedProcess extends AbstractSelfDescribingAlgorithm {

    /**
     * Identifier for the input of the cache key to read the data from.
     */
    private static final String IDENTIFIER_CACHE_KEY = "Cache-Key";

    private static final String ABSTRACT_CACHE_KEY = "This is the key for the cache to lookup the existing results for the process";

    private final ICachableProcess algorithm;
    private final String identifier;
    private final String optionalAbstract;

    public CachedProcess(
            final ICachableProcess algorithm,
            final String identifier,
            final String optionalAbstract) {
        this.algorithm = algorithm;
        this.identifier = identifier;
        this.optionalAbstract = optionalAbstract;
    }

    @Override
    public Map<String, IData> run(Map<String, List<IData>> inputData) throws ExceptionReport {

        final String cacheKey = readStringFromInputMap(inputData, IDENTIFIER_CACHE_KEY);


        final ICacher cache = algorithm.getCache();
        final Optional<Map<String, IDataRecreator>> optionalCacheResult = cache.getCachedResult(cacheKey);

        if (!optionalCacheResult.isPresent()) {
            throw new ExceptionReport("Can't find any data in the cache for the given hash '" + cacheKey + "'", ExceptionReport.INVALID_PARAMETER_VALUE);
        }

        final Map<String, IDataRecreator> cacheResult = optionalCacheResult.get();

        return cacheResult.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().recreate()
        ));
    }

    private String readStringFromInputMap(final Map<String, List<IData>> inputData, final String identifierToRead) throws ExceptionReport {
        if (!inputData.containsKey(identifierToRead)) {
            throw new ExceptionReport("Can't read " + identifierToRead, ExceptionReport.MISSING_PARAMETER_VALUE);
        }
        final List<IData> list = inputData.get(identifierToRead);
        if (list.isEmpty()) {
            throw new ExceptionReport("Can't read " + identifierToRead, ExceptionReport.MISSING_PARAMETER_VALUE);
        }

        final IData idata = list.get(0);

        if (! (idata instanceof LiteralStringBinding)) {
            throw new ExceptionReport(identifierToRead + " has the wrong binding class", ExceptionReport.INVALID_PARAMETER_VALUE);
        }

        final LiteralStringBinding literalStringBinding = (LiteralStringBinding) idata;
        return literalStringBinding.getPayload();
    }

    @Override
    public List<String> getInputIdentifiers() {
        return Collections.singletonList(IDENTIFIER_CACHE_KEY);
    }

    @Override
    public List<String> getOutputIdentifiers() {
        return algorithm.getOutputIdentifiers();
    }

    @Override
    public Class<?> getInputDataType(final String id) {
        // both the cache key and the output name
        return LiteralStringBinding.class;
    }

    @Override
    public Class<?> getOutputDataType(final String id) {
        return algorithm.getOutputDataType(id);
    }

    public ProcessDescription getDescription() {

        final IProcessDescriptionGeneratorData generatorData = new ProcessDescriptionGeneratorDataImpl.Builder(
                identifier, getFullQualifiedIdentifier())
                .withProcessAbstract(optionalAbstract)
                // just one literal string
                .withLiteralStringInput(IDENTIFIER_CACHE_KEY, ABSTRACT_CACHE_KEY, false)
                // but all the outputs from the wrapped algorithm
                .withOutputs(algorithm.getOutputDataForProcessGeneration())
                .build();

        final IProcessDescriptionGenerator generator = new ProcessDescriptionGeneratorImpl(generatorData);
        final ProcessDescriptionsDocument description = generator.generateProcessDescription();
        ProcessDescription processDescription = new ProcessDescription();
        processDescription.addProcessDescriptionForVersion(description.getProcessDescriptions().getProcessDescriptionArray(0), "1.0.0");
        return processDescription;
    }


    public String getFullQualifiedIdentifier() {
        return IConfiguration.PATH_FULL_QUALIFIED + identifier;
    }

}
