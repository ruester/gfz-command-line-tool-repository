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

package org.n52.gfz.riesgos.configuration.parse.input.stdin;

import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.List;

/**
 * Interface for a factory to create the identifiers for
 * the supported types that will be used as text for stdin.
 */
@FunctionalInterface
public interface IAsStdinInputFactory {

    /**
     * Factory method to create the identifier with the given data.
     * Not all implementations support app of this arguments.
     *
     * @param identifier identifier of the data
     * @param isOptional true if the input is optional
     * @param optionalAbstract optional description of the parameter
     * @param defaultFormat optional default format
     * @param defaultValue optional default value
     * @param allowedValues optional list with allowed values
     * @param schema optional schema
     * @return IInputParameter
     * @throws ParseConfigurationException exception that will be thrown
     * if an unsupported argument is given to the implementation.
     */
    IInputParameter create(
            String identifier,
            boolean isOptional,
            String optionalAbstract,
            FormatEntry defaultFormat,
            String defaultValue,
            List<String> allowedValues,
            String schema
    ) throws ParseConfigurationException;
}
