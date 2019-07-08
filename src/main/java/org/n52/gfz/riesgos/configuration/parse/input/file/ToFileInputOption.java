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

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enums with the supported factories to use data as input files.
 */
public enum ToFileInputOption {
    /**
     * This is a enum to read the input from a geotiff file.
     */
    GEOTIFF("geotiff", new InputFileGeotiffFactory()),
    /**
     * This is a enum to read the input from a geojson file.
     */
    GEOJSON("geojson", new InputFileGeojsonFactory()),
    /**
     * This is a enum to read the input from a shapefile.
     */
    SHAPEFILE("shapefile", new InputFileShapefileFactory()),
    /**
     * This is a enum to read the input from a generic file.
     */
    GENERIC_FILE("file", new InputFileGenericFactory()),
    /**
     * This is a enum to read the input from a quakeml xml file.
     */
    QUAKEML("quakeml", new InputFileQuakeMLFactory()),
    /**
     * This is a enum to read the input from a shakemap xml file.
     */
    SHAKEMAP("shakemap", new InputFileShakemapFactory()),

    /**
     * This is a enum to read the input from a nrml xml file.
     */
    NRML("nrml", new InputFileNrmlFactory()),
    /**
     * This is a enum to read the input from a json file.
     */
    JSON("json", new InputFileJsonFactory());

    /**
     * This is the constant of the useAs value
     * for which we will use this enums.
     */
    private static final String USE_AS_FILE = "file";

    /**
     * String with the name of the data type to look it up.
     */
    private final String dataType;

    /**
     * Factory that is used to create the identifiers.
     */
    private final IAsFileInputFactory factory;

    /**
     * This is default constructor for the enum.
     * @param aDataType string with the data type name
     * @param aFactory factory to create the identifier with this type
     */
    ToFileInputOption(
            final String aDataType,
            final IAsFileInputFactory aFactory) {
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
    public IAsFileInputFactory getFactory() {
        return factory;
    }

    /**
     *
     * @return setting for useAs that is supported
     * by this types
     */
    public static String useAs() {
        return USE_AS_FILE;
    }

    /**
     *
     * @return map with the data type names as keys
     */
    public static Map<String, ToFileInputOption> asMap() {
        return Stream.of(ToFileInputOption.values()).collect(Collectors.toMap(
                ToFileInputOption::getDataType, Function.identity()
        ));
    }
}
