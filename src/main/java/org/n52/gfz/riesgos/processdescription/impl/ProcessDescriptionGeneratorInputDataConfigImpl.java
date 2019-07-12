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

public class ProcessDescriptionGeneratorInputDataConfigImpl implements IProcessDescriptionGeneratorInputData {

    private final IInputParameter inputParameter;

    public ProcessDescriptionGeneratorInputDataConfigImpl(final IInputParameter inputParameter) {
        this.inputParameter = inputParameter;
    }

    @Override
    public String getIdentifier() {
        return inputParameter.getIdentifier();
    }

    @Override
    public Class<? extends IData> getBindingClass() {
        return inputParameter.getBindingClass();
    }

    @Override
    public Optional<List<String>> getSupportedCrs() {
        return inputParameter.getAllowedValues();
    }

    @Override
    public Optional<List<String>> getAllowedValues() {
        return inputParameter.getAllowedValues();
    }

    @Override
    public Optional<String> getDefaultValue() {
        return inputParameter.getDefaultValue();
    }

    @Override
    public Optional<FormatEntry> getDefaultFormat() {
        return inputParameter.getDefaultFormat();
    }

    @Override
    public boolean isOptional() {
        return inputParameter.isOptional();
    }

    @Override
    public Optional<String> getAbstract() {
        return inputParameter.getAbstract();
    }
}
