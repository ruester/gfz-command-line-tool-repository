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

package org.n52.gfz.riesgos.configuration.parse.output.file;

import org.n52.gfz.riesgos.configuration.OutputParameterFactory;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enums with the supported factories to read the types from files.
 */
public enum FromFilesOption {

    /**
     * This is the enum to read xml from files.
     */
    XML("xml", OutputParameterFactory.INSTANCE::createFileOutXmlWithSchema),

    /**
     * This is the enum to read generic files.
     */
    FILE("file",
            (
                    identifier,
                    isOptional,
                    optionalAbstract,
                    defaultFormat,
                    path,
                    schema
            ) ->
                    // ignore schema
                    OutputParameterFactory.INSTANCE.createFileOutGeneric(
                            identifier,
                            isOptional,
                            optionalAbstract,
                            defaultFormat,
                            path)),
    /**
     * This is the enum to read geojson from files.
     */
    GEOJSON("geojson",
            (
                    identifier,
                    isOptional,
                    optionalAbstract,
                    defaultFormat,
                    path,
                    schema
            ) ->
                    // ignore schema
                    OutputParameterFactory.INSTANCE.createFileOutGeojson(
                            identifier,
                            isOptional,
                            optionalAbstract,
                            defaultFormat,
                            path)),
    /**
     * This is the enum to read geotiff rasters from files.
     */
    GEOTIFF("geotiff",
            (
                    identifier,
                    isOptional,
                    optionalAbstract,
                    defaultFormat,
                    path,
                    schema
            ) ->
                    // ignore schema
                    OutputParameterFactory.INSTANCE.createFileOutGeotiff(
                            identifier,
                            isOptional,
                            optionalAbstract,
                            defaultFormat,
                            path)),
    /**
     * This is the enum to read shapefiles from files.
     */
    SHP("shapefile",
            (
                    identifier,
                    isOptional,
                    optionalAbstract,
                    defaultFormat,
                    path,
                    schema
            ) ->
                    // ignore schema
                    OutputParameterFactory.INSTANCE.createFileOutShapeFile(
                            identifier,
                            isOptional,
                            optionalAbstract,
                            defaultFormat,
                            path)),
    /**
     * This is the enum to read xml quakeml from files.
     */
    QUAKEML("quakeml",
            (
                    identifier,
                    isOptional,
                    optionalAbstract,
                    defaultFormat,
                    path,
                    schema
            ) ->
                    // ignore schema
                    OutputParameterFactory.INSTANCE.createFileOutQuakeMLFile(
                            identifier,
                            isOptional,
                            optionalAbstract,
                            defaultFormat,
                            path)),

    /**
     * This is the enum to read xml nrml from files.
     */
    NRML("nrml",
            (
                    identifier,
                    isOptional,
                    optionalAbstract,
                    defaultFormat,
                    path,
                    schema
            ) ->
                    // ignore schema
                    OutputParameterFactory.INSTANCE.createFileOutNrml(
                            identifier,
                            isOptional,
                            optionalAbstract,
                            defaultFormat,
                            path)),



    /**
     * This is the enum to read xml shakemap from files.
     */
    SHAKEMAP("shakemap",
            (
                    identifier,
                    isOptional,
                    optionalAbstract,
                    defaultFormat,
                    path,
                    schema
            ) ->
                    // ignore schema
                    OutputParameterFactory.INSTANCE.createFileOutShakemap(
                            identifier,
                            isOptional,
                            optionalAbstract,
                            defaultFormat,
                            path)),
    /**
     * This is the enum to read json from files.
     */
    JSON("json",
            (
                    identifier,
                    isOptional,
                    optionalAbstract,
                    defaultFormat,
                    path,
                    schema
            ) ->
                    // ignore schema
                    OutputParameterFactory.INSTANCE.createFileOutJson(
                            identifier,
                            isOptional,
                            optionalAbstract,
                            defaultFormat,
                            path));

    /**
     * This is the constant of the readFrom value
     * for which we will use this enums.
     */
    private static final String READ_FROM_FILE = "file";

    /**
     * String with the name of the data type to look it up.
     */
    private final String dataType;

    /**
     * Factory that is used to create the identifiers.
     */
    private final IFileOutputFactory factory;

    /**
     * This is default constructor for the enum.
     * @param aDataType string with the data type name
     * @param aFactory factory to create the identifier with this type
     */
    FromFilesOption(
            final String aDataType,
            final IFileOutputFactory aFactory) {
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
    public IFileOutputFactory getFactory() {
        return factory;
    }

    /**
     *
     * @return setting for readFrom that is supported
     * by this types
     */
    public static String readFrom() {
        return READ_FROM_FILE;
    }

    /**
     *
     * @return Map with DataType names as keys
     */
    public static Map<String, FromFilesOption> asMap() {
        return Stream.of(FromFilesOption.values()).collect(Collectors.toMap(
                FromFilesOption::getDataType, Function.identity()
        ));
    }
}
