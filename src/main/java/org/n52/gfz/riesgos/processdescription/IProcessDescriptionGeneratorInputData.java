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

package org.n52.gfz.riesgos.processdescription;

import org.n52.wps.io.data.IData;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.List;
import java.util.Optional;

/**
 * Interface for an input element for the process description
 * generation.
 */
public interface IProcessDescriptionGeneratorInputData {

    /**
     * Identifier of the input.
     * @return identifier of the input
     */
    String getIdentifier();

    /**
     * Binding class of the input.
     * @return binding class of the input
     */
    Class<? extends IData> getBindingClass();

    /**
     * Optional list of supported crs.
     * @return optional list of supported crs
     */
    Optional<List<String>> getSupportedCrs();

    /**
     * Optional list of allowed values.
     * @return optional list of allowed values
     */
    Optional<List<String>> getAllowedValues();

    /**
     * Optional default value.
     * @return optional default value
     */
    Optional<String> getDefaultValue();

    /**
     * Optional default format.
     * @return optional default format
     */
    Optional<FormatEntry> getDefaultFormat();

    /**
     * True if the input is optional.
     * @return true if the input is optional
     */
    boolean isOptional();

    /**
     * Optional text with the abstract of the input.
     * @return optional text with the abstract of the input.
     */
    Optional<String> getAbstract();
}
