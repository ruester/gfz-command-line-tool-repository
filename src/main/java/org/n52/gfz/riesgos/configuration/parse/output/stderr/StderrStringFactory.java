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

package org.n52.gfz.riesgos.configuration.parse.output.stderr;

import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.configuration.OutputParameterFactory;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.wps.webapp.api.FormatEntry;

/**
 * Factory to create a string type from stderr.
 */
public class StderrStringFactory implements IStderrOutputFactory {
    /**
     * Factory method to create the identifier with the given data.
     *
     * @param identifier       identifier of the data
     * @param isOptional       true if the output is optional
     * @param optionalAbstract optional description of the parameter
     * @param defaultFormat    optional default format
     * @return output parameter
     * @throws ParseConfigurationException exception that is thrown
     *      * if one attribute is not supported by the factory
     */
    @Override
    public IOutputParameter create(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final FormatEntry defaultFormat)

            throws ParseConfigurationException {

        if (defaultFormat != null) {
            throw new ParseConfigurationException(
                    "defaultFormat is not supported for string types");
        }

        return OutputParameterFactory.INSTANCE.createStderrString(
                identifier,
                isOptional,
                optionalAbstract
        );
    }
}
