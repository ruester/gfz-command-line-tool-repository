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

import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorData;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorInputData;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorOutputData;
import org.n52.wps.io.data.IData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ProcessDescriptionGeneratorDataImpl implements IProcessDescriptionGeneratorData {

    private final String identifier;
    private final String fullQualifiedIdentifier;
    private final String processAbstract;
    private final List<IProcessDescriptionGeneratorInputData> inputData;
    private final List<IProcessDescriptionGeneratorOutputData> outputData;

    private ProcessDescriptionGeneratorDataImpl(
            final Builder builder) {
        this.identifier = builder.identifier;
        this.fullQualifiedIdentifier = builder.fullQualifiedIdentifier;
        this.processAbstract = builder.processAbstract;
        this.inputData = builder.inputData;
        this.outputData = builder.outputData;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getFullQualifiedIdentifier() {
        return fullQualifiedIdentifier;
    }

    @Override
    public Optional<String> getProcessAbstract() {
        return Optional.ofNullable(processAbstract);
    }

    @Override
    public List<IProcessDescriptionGeneratorInputData> getInputData() {
        return inputData;
    }

    @Override
    public List<IProcessDescriptionGeneratorOutputData> getOutputData() {
        return outputData;
    }

    public static class Builder {

        private final String identifier;
        private final String fullQualifiedIdentifier;
        private String processAbstract;

        private final List<IProcessDescriptionGeneratorInputData> inputData;
        private final List<IProcessDescriptionGeneratorOutputData> outputData;

        public Builder(final String identifier, final String fullQualifiedIdentifier) {
            this.identifier = identifier;
            this.fullQualifiedIdentifier = fullQualifiedIdentifier;
            this.processAbstract = null;

            this.inputData = new ArrayList<>();
            this.outputData = new ArrayList<>();
        }

        public Builder withProcessAbstract(final String processAbstract) {
            this.processAbstract = processAbstract;
            return this;
        }

        public Builder withLiteralStringInput(final String inputIdentifier, final String inputAbstract, final boolean isOptional) {
            inputData.add(new ProcessDescriptionGeneratorInputDataLiteralStringImpl(inputIdentifier, inputAbstract, isOptional));
            return this;
        }

        public Builder withRequiredComplexInput(final String inputIdentifier,
                                        final String inputAbstract,
                                        final Class<? extends IData> complexInputClass) {
            inputData.add(new ProcessDescriptionGeneratorInputDataComplexImpl(inputIdentifier, inputAbstract, false, complexInputClass));
            return this;
        }

        public Builder withRequiredComplexOutput(final String outputIdentifier,
                                                 final String outputAbstract,
                                                 final Class<? extends IData> complexOutputClass) {
            outputData.add(new ProcessDescriptionGeneratorOuputDataComplexImpl(outputIdentifier, outputAbstract, false, complexOutputClass));
            return this;
        }

        public Builder withOutputs(final Collection<IProcessDescriptionGeneratorOutputData> outputs) {
            outputData.addAll(outputs);
            return this;
        }


        public ProcessDescriptionGeneratorDataImpl build() {
            return new ProcessDescriptionGeneratorDataImpl(this);
        }

    }
}
