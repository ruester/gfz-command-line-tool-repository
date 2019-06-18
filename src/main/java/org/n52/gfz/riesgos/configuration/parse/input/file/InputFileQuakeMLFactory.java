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
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

/**
 * Implementation to create a file input with quakeml.
 */
public class InputFileQuakeMLFactory implements IAsFileInputFactory {
    /**
     * Factory method to create the input parameter with the given data.
     * Not all implementations support all of this arguments.
     *
     * @param identifier       identifier of the data
     * @param isOptional       true if the input is optional
     * @param optionalAbstract optional abstract (description) of the data
     * @param path             path to the file
     * @param schema           optional schema
     * @return input parameter
     * @throws ParseConfigurationException exception that will be thrown
    * if an unsupported argument is given to the implementation.
     */
    @Override
    public IInputParameter create(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String path,
            final String schema)

            throws ParseConfigurationException {

        // schema is ignored here
        // takes the schema for quakeml
        return InputParameterFactory.INSTANCE.createFileInQuakeML(
                identifier,
                isOptional, optionalAbstract,
                path
        );
    }
}
