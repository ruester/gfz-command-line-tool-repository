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

package org.n52.gfz.riesgos.processdescription.impl;

import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorData;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorInputData;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorOutputData;
import org.n52.wps.io.data.IData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the IProcessDescriptionGeneratorData that
 * has a builder pattern for adding inputs and outputs.
 */
public final class ProcessDescriptionGeneratorDataImpl
        implements IProcessDescriptionGeneratorData {

    /**
     * Identifier of the process.
     */
    private final String identifier;
    /**
     * Full qualified identifier of the process.
     */
    private final String fullQualifiedIdentifier;
    /**
     * Abstract of the process.
     */
    private final String processAbstract;
    /**
     * List with inputs.
     */
    private final List<IProcessDescriptionGeneratorInputData> inputData;
    /**
     * List with the outputs.
     */
    private final List<IProcessDescriptionGeneratorOutputData> outputData;

    /**
     * Private constructor. Use the builder instead.
     * @param builder builder to extract the data from
     */
    private ProcessDescriptionGeneratorDataImpl(
            final Builder builder) {
        this.identifier = builder.identifier;
        this.fullQualifiedIdentifier = builder.fullQualifiedIdentifier;
        this.processAbstract = builder.processAbstract;
        this.inputData = builder.inputData;
        this.outputData = builder.outputData;
    }

    /**
     *
     * @return identifier of the process
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     *
     * @return full qualified identifier of the process
     */
    @Override
    public String getFullQualifiedIdentifier() {
        return fullQualifiedIdentifier;
    }

    /**
     *
     * @return process abstract
     */
    @Override
    public Optional<String> getProcessAbstract() {
        return Optional.ofNullable(processAbstract);
    }

    /**
     *
     * @return inputs
     */
    @Override
    public List<IProcessDescriptionGeneratorInputData> getInputData() {
        return inputData;
    }

    /**
     *
     * @return outputs
     */
    @Override
    public List<IProcessDescriptionGeneratorOutputData> getOutputData() {
        return outputData;
    }

    /**
     * Builder class to generate the process description data.
     */
    public static class Builder {

        /**
         * Identifier of the process.
         */
        private final String identifier;
        /**
         * Full qualified identifier of the process.
         */
        private final String fullQualifiedIdentifier;
        /**
         * Abstract of the process.
         */
        private String processAbstract;

        /**
         * List with the inputs.
         */
        private final List<IProcessDescriptionGeneratorInputData> inputData;
        /**
         * List with the outputs.
         */
        private final List<IProcessDescriptionGeneratorOutputData> outputData;

        /**
         * Default constructor.
         * @param aIdentifier identifier of the data
         * @param aFullQualifiedIdentifier full qualified identifier of the data
         */
        public Builder(
                final String aIdentifier,
                final String aFullQualifiedIdentifier) {
            this.identifier = aIdentifier;
            this.fullQualifiedIdentifier = aFullQualifiedIdentifier;
            this.processAbstract = null;

            this.inputData = new ArrayList<>();
            this.outputData = new ArrayList<>();
        }

        /**
         * Adds an abstract.
         * @param aProcessAbstract process abstract
         * @return builder
         */
        public Builder withProcessAbstract(final String aProcessAbstract) {
            this.processAbstract = aProcessAbstract;
            return this;
        }

        /**
         * Adds an literal string as input.
         * @param inputIdentifier input identifier
         * @param inputAbstract input abstract
         * @param isOptional true if the string is optional
         * @return builder
         */
        public Builder withLiteralStringInput(
                final String inputIdentifier,
                final String inputAbstract,
                final boolean isOptional) {
            inputData.add(
                    new ProcessDescriptionGeneratorInputDataLiteralStringImpl(
                            inputIdentifier, inputAbstract, isOptional));
            return this;
        }

        /**
         * Adds a required complex input.
         * @param inputIdentifier input identifier
         * @param inputAbstract input abstract
         * @param complexInputClass binding class
         * @return builder
         */
        public Builder withRequiredComplexInput(
                final String inputIdentifier,
                final String inputAbstract,
                final Class<? extends IData> complexInputClass) {
            inputData.add(new ProcessDescriptionGeneratorInputDataComplexImpl(
                    inputIdentifier,
                    inputAbstract,
                    false,
                    complexInputClass));
            return this;
        }

        /**
         * Adds a required complex output.
         * @param outputIdentifier output identifier
         * @param outputAbstract output abstract
         * @param complexOutputClass binding class
         * @return builder
         */
        public Builder withRequiredComplexOutput(
                final String outputIdentifier,
                final String outputAbstract,
                final Class<? extends IData> complexOutputClass) {
            outputData.add(new ProcessDescriptionGeneratorOuputDataComplexImpl(
                    outputIdentifier,
                    outputAbstract,
                    false,
                    complexOutputClass));
            return this;
        }

        /**
         * Adds an output.
         * @param outputs output
         * @return builder
         */
        public Builder withOutputs(
                final Collection<IProcessDescriptionGeneratorOutputData>
                        outputs) {
            outputData.addAll(outputs);
            return this;
        }

        /**
         * Builds the ProcessDescriptionGeneratorDataImpl.
         * @return ProcessDescriptionGeneratorDataImpl
         */
        public ProcessDescriptionGeneratorDataImpl build() {
            return new ProcessDescriptionGeneratorDataImpl(this);
        }

    }
}
