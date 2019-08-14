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

import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorOutputData;
import org.n52.wps.io.data.IData;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of IProcessDescriptionGeneratorOutputData for an complex
 * output.
 */
public class ProcessDescriptionGeneratorOuputDataComplexImpl
        implements IProcessDescriptionGeneratorOutputData {

    /**
     * Identifier of the output.
     */
    private final String identifier;
    /**
     * Abstract of the output.
     */
    private final String outputAbstract;
    /**
     * Flag if the output is optional.
     */
    private final boolean isOptional;
    /**
     * Binding class of the output.
     */
    private final Class<? extends IData> bindingClass;

    /**
     * Default constructor.
     * @param aIdentifier identifier of the output
     * @param aOutputAbstract abstract of the output
     * @param aIsOptional flag if the output is optional
     * @param aBindingClass binding class of the output
     */
    public ProcessDescriptionGeneratorOuputDataComplexImpl(
            final String aIdentifier,
            final String aOutputAbstract,
            final boolean aIsOptional,
            final Class<? extends IData> aBindingClass
    ) {
        this.identifier = aIdentifier;
        this.outputAbstract = aOutputAbstract;
        this.isOptional = aIsOptional;
        this.bindingClass = aBindingClass;
    }

    /**
     *
     * @return identifier of the output
     */
    @Override
    public String getIdentifier() {
        return identifier;
    }

    /**
     *
     * @return binding class of the output
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
     * @return no default format
     */
    @Override
    public Optional<FormatEntry> getDefaultFormat() {
        return Optional.empty();
    }

    /**
     *
     * @return true if optional
     */
    @Override
    public boolean isOptional() {
        return isOptional;
    }

    /**
     *
     * @return abstract of the output
     */
    @Override
    public Optional<String> getAbstract() {
        return Optional.ofNullable(outputAbstract);
    }
}
