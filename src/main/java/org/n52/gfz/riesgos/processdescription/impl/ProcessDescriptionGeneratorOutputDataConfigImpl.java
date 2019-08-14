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
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorOutputData;
import org.n52.wps.io.data.IData;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.List;
import java.util.Optional;

/**
 * Implementation that just wraps the output parameter from
 * the configuration.
 */
public class ProcessDescriptionGeneratorOutputDataConfigImpl
        implements IProcessDescriptionGeneratorOutputData {

    /**
     * Output parameter.
     */
    private final IOutputParameter outputParameter;

    /**
     * Default constructor.
     * @param aOutputParameter output parameter of a configuration
     */
    public ProcessDescriptionGeneratorOutputDataConfigImpl(
            final IOutputParameter aOutputParameter) {
        this.outputParameter = aOutputParameter;
    }

    /**
     *
     * @return identifier of the output parameter
     */
    @Override
    public String getIdentifier() {
        return outputParameter.getIdentifier();
    }

    /**
     *
     * @return binding class of the output parameter
     */
    @Override
    public Class<? extends IData> getBindingClass() {
        return outputParameter.getBindingClass();
    }

    /**
     *
     * @return supported crs of the output parameter
     */
    @Override
    public Optional<List<String>> getSupportedCrs() {
        return outputParameter.getSupportedCRSForBBox();
    }

    /**
     *
     * @return default format of the output parameter
     */
    @Override
    public Optional<FormatEntry> getDefaultFormat() {
        return outputParameter.getDefaultFormat();
    }

    /**
     *
     * @return true if the output parameter is optional
     */
    @Override
    public boolean isOptional() {
        return outputParameter.isOptional();
    }

    /**
     *
     * @return abstract of the output parameter
     */
    @Override
    public Optional<String> getAbstract() {
        return outputParameter.getAbstract();
    }
}
