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

/**
 * Complex input data for the process generation.
 */
public class ProcessDescriptionGeneratorInputDataComplexImpl
        implements IProcessDescriptionGeneratorInputData {

    /**
     * Identifier of the input.
     */
    private final String identifier;
    /**
     * Abstract of the input.
     */
    private final String inputAbstract;
    /**
     * Flag if the input is optional.
     */
    private final boolean isOptional;
    /**
     * Binding class of the input.
     */
    private final Class<? extends IData> bindingClass;

    /**
     * Default constructor.
     * @param aIdentifier identifier of the input
     * @param aInputAbstract abstract of the input
     * @param aIsOptional flag if the input is optional
     * @param aBindingClass binding class of the input
     */
    public ProcessDescriptionGeneratorInputDataComplexImpl(
            final String aIdentifier,
            final String aInputAbstract,
            final boolean aIsOptional,
            final Class<? extends IData> aBindingClass
    ) {
        this.identifier = aIdentifier;
        this.inputAbstract = aInputAbstract;
        this.isOptional = aIsOptional;
        this.bindingClass = aBindingClass;
    }

    /**
     *
     * @return identifier of the input
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     *
     * @return binding class of the input
     */
    @Override
    public Class<? extends IData> getBindingClass() {
        return bindingClass;
    }

    /**
     *
     * @return no supported crs
     */
    @Override
    public Optional<List<String>> getSupportedCrs() {
        return Optional.empty();
    }

    /**
     *
     * @return no allowed values
     */
    @Override
    public Optional<List<String>> getAllowedValues() {
        return Optional.empty();
    }


    /**
     *
     * @return no default value
     */
    @Override
    public Optional<String> getDefaultValue() {
        return Optional.empty();
    }

    /**
     *
     * @return no default format
     */
    @Override
    public Optional<FormatEntry> getDefaultFormat() {
        return Optional.empty();
    }

    /**
     *
     * @return true if the input is optional
     */
    @Override
    public boolean isOptional() {
        return isOptional;
    }

    /**
     *
     * @return abstract of the input
     */
    @Override
    public Optional<String> getAbstract() {
        return Optional.ofNullable(inputAbstract);
    }
}
