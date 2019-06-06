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

package org.n52.gfz.riesgos.algorithm;

import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGenerator;
import org.n52.gfz.riesgos.processdescription.impl.ProcessDescriptionGeneratorForTransformationImpl;
import org.n52.wps.io.data.IComplexData;
import org.n52.wps.io.data.IData;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;
import org.n52.wps.server.ExceptionReport;
import org.n52.wps.server.ProcessDescription;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This is skeleton to provide processes that just takes the input as it is.
 * It it the job of the parsers and generators to transform the data formats.
 */
public class TransformDataFormatProcess extends AbstractSelfDescribingAlgorithm {

    private static final String INPUT_IDENTIFIER = "input";
    private static final String OUTPUT_IDENTIFIER = "output";

    private final String identifier;
    private final Class<? extends IComplexData> clazz;
    private final Logger logger;
    private final ICheckDataAndGetErrorMessage validator;
    private final String optionalAbstract;

    /**
     * Creates a new process to transform the binding class data
     *
     * @param identifier identifier of the process
     * @param clazz binding class the process uses
     * @param logger logger to write information to
     */
    public TransformDataFormatProcess(
            final String identifier,
            final Class<? extends IComplexData> clazz,
            final Logger logger,
            final ICheckDataAndGetErrorMessage validator,
            final String optionalAbstract) {
        this.identifier = identifier;
        this.clazz = clazz;
        this.logger = logger;
        this.validator = validator;
        this.optionalAbstract = optionalAbstract;
    }

    @Override
    public List<String> getInputIdentifiers() {
        return Collections.singletonList(INPUT_IDENTIFIER);
    }

    @Override
    public List<String> getOutputIdentifiers() {
        return Collections.singletonList(OUTPUT_IDENTIFIER);
    }

    @Override
    public Map<String, IData> run(final Map<String, List<IData>> inputData) throws ExceptionReport {

        final Map<String, IData> result = new HashMap<>();

        final List<IData> value = inputData.get(INPUT_IDENTIFIER);

        if(value.isEmpty()) {
            throw new ExceptionReport("Empty inputData list", ExceptionReport.OPERATION_NOT_SUPPORTED);
        } else {
            if (value.size() > 1) {
                logger.warn("Too many entries in inputData. Additional elements are ignored");
            }
            final IData data = value.get(0);

            if(validator != null) {
                final Optional<String> optionalErrorMessage = validator.check(data);
                if(optionalErrorMessage.isPresent()) {
                    throw new ExceptionReport(optionalErrorMessage.get(), ExceptionReport.INVALID_PARAMETER_VALUE);
                }
            }
            // if there is an error -> the exception is thrown before
            // only valid input is given back to the results
            result.put(OUTPUT_IDENTIFIER, data);
        }

        return result;
    }

    @Override
    public Class<?> getInputDataType(final String id) {
        return clazz;
    }

    @Override
    public Class<?> getOutputDataType(final String id) {
        return clazz;
    }

    @Override
    public ProcessDescription getDescription() {
        final IProcessDescriptionGenerator generator = new ProcessDescriptionGeneratorForTransformationImpl(
                identifier, IConfiguration.PATH_FULL_QUALIFIED + identifier, clazz,
                optionalAbstract,
                INPUT_IDENTIFIER, OUTPUT_IDENTIFIER);
        final ProcessDescriptionsDocument description = generator.generateProcessDescription();
        ProcessDescription processDescription = new ProcessDescription();
        processDescription.addProcessDescriptionForVersion(description.getProcessDescriptions().getProcessDescriptionArray(0), "1.0.0");
        logger.error(processDescription.toString());
        return processDescription;
    }
}
