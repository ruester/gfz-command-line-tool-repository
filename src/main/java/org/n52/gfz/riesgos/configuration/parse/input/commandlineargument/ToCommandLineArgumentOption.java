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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum with the supported factories to use data as command line arguments.
 */
public enum ToCommandLineArgumentOption {
    /**
     * This is the enum to use an int as command line argument.
     */
    INT("int",
            new CommandLineArgumentIntFactory()),
    /**
     * This is the enum to use a double as command line argument.
     */
    DOUBLE("double",
            new CommandLineArgumentDoubleFactory()),
    /**
     * This is the enum to use a boolean as command line argument.
     */
    BOOLEAN("boolean",
            new CommandLineArgumentBooleanFactory()),
    /**
     * This is the enum to use a stringas command line argument.
     */
    STRING("string",
            new CommandLineArgumentStringFactory()),
    /**
     * This is the enum to use a bounding box as command line argument.
     */
    BBOX("bbox",
            new CommandLineArgumentBBoxFactory()),
    /**
     * This is the enum to use an xml file as command line argument.
     */
    XML("xml",
            new CommandLineArgumentXmlFileFactory()),
    /**
     * This is the enum to use a geotiff file as command line argument.
     */
    GEOFITT("geotiff",
            new CommandLineArgumentGeotiffFileFactory()),
    /**
     * This is the enum to use a geojson file as command line argument.
     */
    GEOJSON("geojson",
            new CommandLineArgumentGeojsonFileFactory()),
    /**
     * This is the enum to use a shapefile as command line argument.
     */
    SHAPEFILE("shapefile",
            new CommandLineArgumentShapefileFactory()),
    /**
     * This is the enum to use a generic file as command line argument.
     */
    GENERIC_FILE("file",
            new CommandLineArgumentGenericFileFactory()),
    /**
     * This is the enum to use a quakeml file as command line argument.
     */
    QUAKEML("quakeml",
            new CommandLineArgumentQuakeMLFileFactory()),

    /**
     * This is the enum to use a nrml file as command line argument.
     */
    NRML("nrml", new CommandLineArgumentNrmlFileFactory()),
    /**
     * This is the enum to use a json file as command line argument.
     */
    JSON("json",
            new CommandLineArgumentJsonFileFactory());

    /**
     * This is the constant of the useAs value
     * for which we will use this enums.
     */
    private static final String USE_AS_CMD = "commandLineArgument";

    /**
     * String with the name of the data type to look it up.
     */
    private final String dataType;
    /**
     * Factory that is used to create the identifiers.
     */
    private final IAsCommandLineArgumentFactory factory;

    /**
     * This is the default constructor for the enum.
     * @param aDataType string with the data type name
     * @param aFactory factory to create the identifier with this type
     */
    ToCommandLineArgumentOption(
            final String aDataType,
            final IAsCommandLineArgumentFactory aFactory) {
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
    public IAsCommandLineArgumentFactory getFactory() {
        return factory;
    }

    /**
     *
     * @return setting for useAs that is supported
     * by this types
     */
    public static String useAs() {
        return USE_AS_CMD;
    }

    /**
     *
     * @return map with the data types as keys
     */
    public static Map<String, ToCommandLineArgumentOption> asMap() {
        return Stream.of(ToCommandLineArgumentOption.values())
                .collect(
                        Collectors.toMap(
                                ToCommandLineArgumentOption::getDataType,
                                Function.identity()
                        ));
    }
}
