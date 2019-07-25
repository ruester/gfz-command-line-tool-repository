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

import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorInputData;
import org.n52.wps.io.data.IData;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.List;
import java.util.Optional;

/**
 * Implementation that just wraps in input parameter of
 * the configuration.
 */
public class ProcessDescriptionGeneratorInputDataConfigImpl
        implements IProcessDescriptionGeneratorInputData {

    /**
     * Input parameter to get all the information.
     */
    private final IInputParameter inputParameter;

    /**
     * Default constructor.
     * @param aInputParameter input parameter to wrap and to read from
     */
    public ProcessDescriptionGeneratorInputDataConfigImpl(
            final IInputParameter aInputParameter) {
        this.inputParameter = aInputParameter;
    }

    /**
     *
     * @return identifier
     */
    @Override
    public String getIdentifier() {
        return inputParameter.getIdentifier();
    }

    /**
     *
     * @return binding class
     */
    @Override
    public Class<? extends IData> getBindingClass() {
        return inputParameter.getBindingClass();
    }

    /**
     *
     * @return supported crs
     */
    @Override
    public Optional<List<String>> getSupportedCrs() {
        return inputParameter.getSupportedCRSForBBox();
    }

    /**
     *
     * @return allowed values
     */
    @Override
    public Optional<List<String>> getAllowedValues() {
        return inputParameter.getAllowedValues();
    }

    /**
     *
     * @return default value
     */
    @Override
    public Optional<String> getDefaultValue() {
        return inputParameter.getDefaultValue();
    }

    /**
     *
     * @return default format
     */
    @Override
    public Optional<FormatEntry> getDefaultFormat() {
        return inputParameter.getDefaultFormat();
    }

    /**
     *
     * @return flag if the input is optional
     */
    @Override
    public boolean isOptional() {
        return inputParameter.isOptional();
    }

    /**
     *
     * @return abstract
     */
    @Override
    public Optional<String> getAbstract() {
        return inputParameter.getAbstract();
    }
}
