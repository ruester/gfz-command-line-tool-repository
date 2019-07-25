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

package org.n52.gfz.riesgos.configuration.parse.input.file;

import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.InputParameterFactory;
import org.n52.gfz.riesgos.configuration.parse.ParseUtils;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.wps.webapp.api.FormatEntry;

/**
 * Implementation to create a file input with geojson.
 */
public class InputFileGeojsonFactory implements IAsFileInputFactory {

    /**
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param isOptional true if the input is optional
     * @param optionalAbstract optional abstract (description) of the data
     * @param defaultFormat optional default format
     * @param path path to the file
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
            final String path,
            final String schema)

            throws ParseConfigurationException {

        if (ParseUtils.INSTANCE.strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for geojson");
        }
        return InputParameterFactory.INSTANCE.createFileInGeojson(
                identifier,
                isOptional,
                optionalAbstract,
                defaultFormat,
                path
        );
    }
}
