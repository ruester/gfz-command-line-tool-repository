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

import org.n52.gfz.riesgos.configuration.OutputParameterFactory;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enums with the supported factories to read the types from stderr.
 */
public enum FromStderrOption {
    /**
     * This is the enum to read a string from stderr.
     */
    STRING("string", OutputParameterFactory.INSTANCE::createStderrString),
    /**
     * This is the enum to read json from stderr.
     */
    JSON("json", OutputParameterFactory.INSTANCE::createStderrJson);

    /**
     * This is the constant of the readFrom value
     * for which we will use this enums.
     */
    private static final String STDERR = "stderr";

    /**
     * String with the name of the data type to look it up.
     */
    private final String dataType;

    /**
     * Factory that is used to create the identifiers.
     */
    private final IStderrOutputFactory factory;

    /**
     * This is default constructor for the enum.
     * @param aDataType string with the data type name
     * @param aFactory factory to create the identifier with this type
     */
    FromStderrOption(
            final String aDataType,
            final IStderrOutputFactory aFactory) {
        this.dataType = aDataType;
        this.factory = aFactory;
    }

    /**
     *
     * @return name of the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     *
     * @return factory to create the identifier
     */
    public IStderrOutputFactory getFactory() {
        return factory;
    }

    /**
     *
     * @return setting for readFrom that is supported
     * by this types
     */
    public static String readFrom() {
        return STDERR;
    }

    /**
     *
     * @return map with the data type values as keys
     */
    public static Map<String, FromStderrOption> asMap() {
        return Stream.of(FromStderrOption.values()).collect(Collectors.toMap(
                FromStderrOption::getDataType, Function.identity()
        ));
    }
}
