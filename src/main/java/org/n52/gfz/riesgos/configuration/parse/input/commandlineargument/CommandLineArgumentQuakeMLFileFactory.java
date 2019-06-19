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

package org.n52.gfz.riesgos.configuration.parse.input.commandlineargument;

import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.InputParameterFactory;
import org.n52.gfz.riesgos.configuration.parse.ParseUtils;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.List;

/**
 * Implementation to create a command line argument with a quakeml file.
 */
public class CommandLineArgumentQuakeMLFileFactory
        implements IAsCommandLineArgumentFactory {
    /**
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param isOptional true if the input is optional
     * @param optionalAbstract optional description of the parameter
     * @param defaultFormat optional default format
     * @param defaultCommandLineFlag optional default command line flag
     * @param defaultValue optional default value
     * @param allowedValues optional list with allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return input parameter
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    @Override
    public IInputParameter create(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final FormatEntry defaultFormat,
            final String defaultCommandLineFlag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if (ParseUtils.strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "default is not supported for quakeml");
        }
        if (ParseUtils.listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowed values are not supported for quakemml");
        }
        if (ParseUtils.listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for quakeml");
        }
        // ignore schema
        return InputParameterFactory.INSTANCE.createCommandLineArgumentQuakeML(
                identifier,
                isOptional,
                optionalAbstract,
                defaultFormat,
                defaultCommandLineFlag
        );
    }
}
