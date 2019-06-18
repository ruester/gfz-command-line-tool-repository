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

package org.n52.gfz.riesgos.configuration.parse.output.exitvalue;

import org.n52.gfz.riesgos.configuration.OutputParameterFactory;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enums for the creation of output parameters for the exit value.
 */
public enum FromExitValueOption {
    /**
     * This is the enum to read ints from the exit value.
     */
    INT("int", OutputParameterFactory.INSTANCE::createExitValueInt);

    /**
     * This is the constant of the readFrom value
     * for which we will use this enums.
     */
    private static final String EXIT_VALUE = "exitValue";

    /**
     * String with the name of the data type to look it up.
     */
    private final String dataType;

    /**
     * Factory that is used to create the identifiers.
     */
    private final IExitValueOutputFactory factory;

    /**
     * This is default constructor for the enum.
     * @param aDataType string with the data type name
     * @param aFactory factory to create the identifier with this type
     */
    FromExitValueOption(
            final String aDataType,
            final IExitValueOutputFactory aFactory) {
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
    public IExitValueOutputFactory getFactory() {
        return factory;
    }

    /**
     *
     * @return setting for readFrom that is supported
     * by this types
     */
    public static String readFrom() {
        return EXIT_VALUE;
    }

    /**
     *
     * @return Map with the data type values as keys
     */
    public static Map<String, FromExitValueOption> asMap() {
        return Stream.of(FromExitValueOption.values()).collect(Collectors.toMap(
                FromExitValueOption::getDataType, Function.identity()
        ));
    }
}
