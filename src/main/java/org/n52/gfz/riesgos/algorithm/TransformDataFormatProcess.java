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
 */

package org.n52.gfz.riesgos.algorithm;

import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGenerator;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorData;
import org.n52.gfz.riesgos.processdescription.impl.ProcessDescriptionGeneratorDataImpl;
import org.n52.gfz.riesgos.processdescription.impl.ProcessDescriptionGeneratorImpl;
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
public class TransformDataFormatProcess
        extends AbstractSelfDescribingAlgorithm {

    /**
     * Input identifier.
     */
    private static final String INPUT_IDENTIFIER = "input";
    /**
     * Abstract for the input.
     */
    private static final String INPUT_ABSTRACT =
            "This is the input parameter to transform to other formats";
    /**
     * Output identifier.
     */
    private static final String OUTPUT_IDENTIFIER = "output";
    /**
     * Abstract for the output.
     */
    private static final String OUTPUT_ABSTRACT =
            "This is the output parameter to transform to other formats";

    /**
     * Identifier for the process.
     */
    private final String identifier;
    /**
     * Supported class for the transformation processes.
     */
    private final Class<? extends IComplexData> clazz;
    /**
     * Logger for the instance.
     */
    private final Logger logger;
    /**
     * Optional validator for the data.
     */
    private final ICheckDataAndGetErrorMessage validator;
    /**
     * Optional abstract for the process.
     */
    private final String optionalAbstract;

    /**
     * Creates a new process to transform the binding class data.
     *
     * @param aIdentifier identifier of the process
     * @param aClazz binding class the process uses
     * @param aLogger logger to write information to
     * @param aValidator validator to check the input data
     * @param aOptionalAbstract optional abstract of the process
     */
    public TransformDataFormatProcess(
            final String aIdentifier,
            final Class<? extends IComplexData> aClazz,
            final Logger aLogger,
            final ICheckDataAndGetErrorMessage aValidator,
            final String aOptionalAbstract) {
        this.identifier = aIdentifier;
        this.clazz = aClazz;
        this.logger = aLogger;
        this.validator = aValidator;
        this.optionalAbstract = aOptionalAbstract;
    }

    /**
     *
     * @return list with the input identifiers
     */
    @Override
    public List<String> getInputIdentifiers() {
        return Collections.singletonList(INPUT_IDENTIFIER);
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
     * Runs the process.
     * Just reads one input data, validates it if necessary
     * and returns it.
     *
     * (The task of changing the input data is done by generators and
     * parsers).
     *
     * @param inputData input data for the process
     * @return map with the output data of the processes
     * @throws ExceptionReport exception that may be thrown in case of an error
     */
    @Override
    public Map<String, IData> run(
            final Map<String, List<IData>> inputData) throws ExceptionReport {

        final Map<String, IData> result = new HashMap<>();

        final List<IData> value = inputData.get(INPUT_IDENTIFIER);

        if (value.isEmpty()) {
            throw new ExceptionReport(
                    "Empty inputData list",
                    ExceptionReport.OPERATION_NOT_SUPPORTED);
        } else {
            if (value.size() > 1) {
                logger.warn(
                        "Too many entries in inputData. "
                        + "Additional elements are ignored");
            }
            final IData data = value.get(0);

            if (validator != null) {
                @SuppressWarnings("unchecked")
                final Optional<String> optionalErrorMessage =
                        validator.check(data);
                if (optionalErrorMessage.isPresent()) {
                    throw new ExceptionReport(
                            optionalErrorMessage.get(),
                            ExceptionReport.INVALID_PARAMETER_VALUE);
                }
            }
            // if there is an error -> the exception is thrown before
            // only valid input is given back to the results
            result.put(OUTPUT_IDENTIFIER, data);
        }

        return result;
    }

    /**
     * Queries the class of the input data.
     * @param id identifier of the input data
     * @return always only the supported class
     */
    @Override
    public Class<?> getInputDataType(final String id) {
        return clazz;
    }

    /**
     * Queries the class of the output data.
     * @param id identifier of the output data
     * @return always only the supported class
     */
    @Override
    public Class<?> getOutputDataType(final String id) {
        return clazz;
    }

    /**
     * Generates the process description.
     * @return process description
     */
    @Override
    public ProcessDescription getDescription() {

        final IProcessDescriptionGeneratorData generatorData =
                new ProcessDescriptionGeneratorDataImpl.Builder(
                        identifier,
                        IConfiguration.PATH_FULL_QUALIFIED
                                + identifier)
                .withProcessAbstract(optionalAbstract)
                .withRequiredComplexInput(
                        INPUT_IDENTIFIER,
                        INPUT_ABSTRACT,
                        clazz)
                .withRequiredComplexOutput(
                        OUTPUT_IDENTIFIER,
                        OUTPUT_ABSTRACT,
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
}
