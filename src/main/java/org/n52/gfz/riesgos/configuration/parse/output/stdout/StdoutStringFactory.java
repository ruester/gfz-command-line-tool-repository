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

package org.n52.gfz.riesgos.configuration.parse.output.stdout;

import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.configuration.OutputParameterFactory;
import org.n52.gfz.riesgos.configuration.parse.ParseUtils;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.wps.webapp.api.FormatEntry;

public class StdoutStringFactory implements IStdoutOutputFactory {
    /**
     * Factory method to create the identifier with the given data.
     *
     * @param identifier       identifier of the data
     * @param isOptional       true of the output is optional
     * @param optionalAbstract optional description of the parameter
     * @param defaultFormat    optional default format
     * @param schema           optional schema (for xml)
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception to throw
     *                                     if a attribute is not supported by the factory
     */
    @Override
    public IOutputParameter create(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final FormatEntry defaultFormat,
            final String schema) throws ParseConfigurationException {

        if (ParseUtils.INSTANCE.strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for string types");
        }

        if (defaultFormat != null) {
            throw new ParseConfigurationException(
                    "defaultFormat is not supported for string types");
        }

        return OutputParameterFactory.INSTANCE.createStdoutString(
                identifier,
                isOptional,
                optionalAbstract);
    }
}
