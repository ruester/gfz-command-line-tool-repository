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

import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorInputData;
import org.n52.wps.io.data.IData;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.List;
import java.util.Optional;

public class ProcessDescriptionGeneratorInputDataComplexImpl implements IProcessDescriptionGeneratorInputData {

    private final String identifier;
    private final String inputAbstract;
    private final boolean isOptional;
    private final Class<? extends IData> bindingClass;

    public ProcessDescriptionGeneratorInputDataComplexImpl(
            final String identifier,
            final String inputAbstract,
            final boolean isOptional,
            final Class<? extends IData> bindingClass
    ) {
        this.identifier = identifier;
        this.inputAbstract = inputAbstract;
        this.isOptional = isOptional;
        this.bindingClass = bindingClass;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Class<? extends IData> getBindingClass() {
        return bindingClass;
    }

    @Override
    public Optional<List<String>> getSupportedCrs() {
        return Optional.empty();
    }

    @Override
    public Optional<List<String>> getAllowedValues() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getDefaultValue() {
        return Optional.empty();
    }

    @Override
    public Optional<FormatEntry> getDefaultFormat() {
        return Optional.empty();
    }

    @Override
    public boolean isOptional() {
        return isOptional;
    }

    @Override
    public Optional<String> getAbstract() {
        return Optional.ofNullable(inputAbstract);
    }
}
