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
import org.n52.gfz.riesgos.configuration.InputParameterFactory;
import org.n52.gfz.riesgos.configuration.parse.ParseUtils;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

import java.util.List;

/**
 * Implementation to create a stdin input with json.
 */
public class StdinJsonFactory implements  IAsStdinInputFactory {
    /**
     *  Checks some attributes and deligates the creation.
     * @param identifier identifier of the data
     * @param isOptional true if the parameter is optional
     * @param optionalAbstract optional description of the parameter
     * @param defaultValue optional default value
     * @param allowedValues optional list with allowed values
     * @param schema optional schema
     * @return input parameter
     * @throws ParseConfigurationException exception that may be thrown if there
     * are values given that can't be used in this implementation.
     */
    @Override
    public IInputParameter create(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String defaultValue,
            final List<String> allowedValues,
            final String schema) throws ParseConfigurationException {
        if (ParseUtils.strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for json");
        }
        if (ParseUtils.strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "defaultValue is not supported for json");
        }
        if (ParseUtils.listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowedValues are not supported for json");
        }
        return InputParameterFactory.INSTANCE.createStdinJson(
                identifier,
                isOptional, optionalAbstract
        );
    }
}
