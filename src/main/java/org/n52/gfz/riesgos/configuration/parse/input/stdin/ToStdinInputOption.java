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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum with the supported factories to use data as stdin input.
 */
public enum ToStdinInputOption {
    /**
     * This is the enum to use the value as string on stdin.
     */
    STRING("string", new StdinStringFactory()),
    /**
     * This is the enum to use the value as json on stdin.
     */
    JSON("json", new StdinJsonFactory());

    /**
     * This is the constant of the useAs value
     * for which we will use this enums.
     */
    private static final String USE_AS_STDIN = "stdin";

    /**
     * String with the name of the data type to look it up.
     */
    private final String dataType;

    /**
     * Factory that is used to create the identifiers.
     */
    private final IAsStdinInputFactory factory;

    /**
     * This is default constructor for the enum.
     * @param aDataType string with the data type name
     * @param aFactory factory to create the identifier with this type
     */
    ToStdinInputOption(
            final String aDataType,
            final IAsStdinInputFactory aFactory) {
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
    public IAsStdinInputFactory getFactory() {
        return factory;
    }

    /**
     *
     * @return setting for useAs that is supported
     * by this types
     */
    public static String useAs() {
        return USE_AS_STDIN;
    }

    /**
     *
     * @return Map with the data type names as keys
     */
    public static Map<String, ToStdinInputOption> asMap() {
        return Stream.of(ToStdinInputOption.values()).collect(Collectors.toMap(
                ToStdinInputOption::getDataType, Function.identity()
        ));
    }
}
