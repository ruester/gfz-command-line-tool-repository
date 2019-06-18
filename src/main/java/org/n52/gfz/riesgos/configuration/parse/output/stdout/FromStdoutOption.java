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

import org.n52.gfz.riesgos.configuration.OutputParameterFactory;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enums with the supported factories to read the types from stdout.
 */
public enum FromStdoutOption {

    /**
     * This is the enum to read a string from stdout.
     */
    STRING("string",
            (identifier, isOptional, optionalAbstract, schema) ->
                    OutputParameterFactory.INSTANCE.createStdoutString(
                            identifier, isOptional, optionalAbstract)),
    /**
     * This is the enum to read generic xml from stdout.
     */
    XML("xml", OutputParameterFactory.INSTANCE::createStdoutXmlWithSchema),
    /**
     * This is the enum to read xml quakeml from stdout.
     */
    QUAKEML("quakeml",
            (identifier, isOptional, optionalAbstract, schema) ->
                    OutputParameterFactory.INSTANCE.createStdoutQuakeML(
                            identifier, isOptional, optionalAbstract)),
    /**
     * This is the enum to read xml shakemaps from stdout.
     */
    SHAKEMAP("shakemap",
            (identifier, isOptional, optionalAbstract, schema) ->
                    OutputParameterFactory.INSTANCE.createStdoutShakemap(
                            identifier, isOptional, optionalAbstract)),
    /**
     * This is the enum to read json from stdout.
     */
    JSON("json",
            (identifier, isOptional, optionalAbstract, schema) ->
                    OutputParameterFactory.INSTANCE.createStdoutJson(
                            identifier, isOptional, optionalAbstract));

    /**
     * This is the constant of the readFrom value
     * for which we will use this enums.
     */
    private static final String STDOUT = "stdout";

    /**
     * String with the name of the data type to look it up.
     */
    private final String dataType;

    /**
     * Factory that is used to create the identifiers.
     */
    private final IStdoutOutputFactory factory;

    /**
     * This is default constructor for the enum.
     * @param aDataType string with the data type name
     * @param aFactory factory to create the identifier with this type
     */
    FromStdoutOption(
            final String aDataType,
            final IStdoutOutputFactory aFactory) {
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
    public IStdoutOutputFactory getFactory() {
        return factory;
    }

    /**
     *
     * @return setting for readFrom that is supported
     * by this types
     */
    public static String readFrom() {
        return STDOUT;
    }

    /**
     *
     * @return map with the data types as keys
     */
    public static Map<String, FromStdoutOption> asMap() {
        return Stream.of(FromStdoutOption.values()).collect(Collectors.toMap(
                FromStdoutOption::getDataType, Function.identity()
        ));
    }
}
