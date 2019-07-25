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

import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorData;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorInputData;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorOutputData;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation that wraps a configuration to provide
 * the data for the process description generation.
 */
public class ProcessDescriptionGeneratorDataConfigImpl
        implements IProcessDescriptionGeneratorData {

    /**
     * Configuration to wrap.
     */
    private final IConfiguration configuration;

    /**
     * Default constructor.
     * @param aConfiguration configuration to wrap
     */
    public ProcessDescriptionGeneratorDataConfigImpl(
            final IConfiguration aConfiguration) {
        this.configuration = aConfiguration;
    }

    /**
     *
     * @return identifier of the process of this configuration
     */
    @Override
    public String getIdentifier() {
        return configuration.getIdentifier();
    }

    /**
     *
     * @return full qualified identifier of the process of this configuration
     */
    @Override
    public String getFullQualifiedIdentifier() {
        return configuration.getFullQualifiedIdentifier();
    }

    /**
     *
     * @return abstract from the configuration
     */
    @Override
    public Optional<String> getProcessAbstract() {
        return configuration.getAbstract();
    }

    /**
     *
     * @return inputs of the configuration
     */
    @Override
    public List<IProcessDescriptionGeneratorInputData> getInputData() {
        return configuration.getInputIdentifiers().stream()
                .map(ProcessDescriptionGeneratorInputDataConfigImpl::new)
                .collect(Collectors.toList());
    }

    /**
     *
     * @return outputs of the configuration
     */
    @Override
    public List<IProcessDescriptionGeneratorOutputData> getOutputData() {
        return configuration.getOutputIdentifiers().stream()
                .map(ProcessDescriptionGeneratorOutputDataConfigImpl::new)
                .collect(Collectors.toList());
    }
}
