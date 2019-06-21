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

package org.n52.gfz.riesgos.configuration.parse.defaultformats;

import org.n52.gfz.riesgos.formats.IMimeTypeAndSchemaConstants;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is an enum with the options for the default formats in the
 * configuration files.
 */
public enum DefaultFormatOption implements IMimeTypeAndSchemaConstants {

    /**
     * Dummy value for no default format at all.
     */
    NONE("", null),

    /**
     * Enum for the geojson format.
     */
    GEOJSON("geojson",
            new FormatEntry(
                    MIME_TYPE_GEOJSON,
                    null,
                    DEFAULT_ENCODING,
                    true)),

    /**
     * Enum for GML 3.2.1 format.
     */
    GML("gml",
            new FormatEntry(
                    MIME_TYPE_XML,
                    SCHEMA_GML_3_2_1,
                    DEFAULT_ENCODING,
                    true)),

    /**
     * Enum for generic xml (no schema).
     */
    XML("xml",
            new FormatEntry(
                    MIME_TYPE_XML,
                    null,
                    DEFAULT_ENCODING,
                    true)),

    /**
     * Enum for QuakeML (the validated one).
     */
    QUAKEML("quakeml",
            new FormatEntry(
                    MIME_TYPE_XML,
                    SCHEMA_QUAKE_ML,
                    DEFAULT_ENCODING,
                    true)),

    /**
     * Enum for QuakeML (the original one that is not valid according to the
     * schema).
     */
    NON_VALID_QUAKEML("nonValidQuakeml",
            new FormatEntry(
                    MIME_TYPE_XML,
                    SCHEMA_QUAKE_ML_OLD,
                    DEFAULT_ENCODING,
                    true)),

    /**
     * Enum for shakemap xml.
     */
    SHAKEMAP("shakemap",
            new FormatEntry(
                    MIME_TYPE_XML,
                    SCHEMA_SHAKEMAP,
                    DEFAULT_ENCODING,
                    true)),

    /**
     * Enum for json.
     */
    JSON("json",
            new FormatEntry(
                    MIME_TYPE_JSON,
                    null,
                    DEFAULT_ENCODING,
                    true)),

    /**
     * Enum for normal encoded geotiff.
     */
    GEOTIFF("geotiff",
            new FormatEntry(
                    MIME_TYPE_GEOTIFF,
                    null,
                    DEFAULT_ENCODING,
                    true)),

    /**
     * Enum for base64 encoded geotiff.
     */
    GEOTIFF_BASE_64("geotiff64",
            new FormatEntry(
                    MIME_TYPE_GEOTIFF,
                    null,
                    ENCODING_BASE64,
                    true)),

    WMS("wms",
            new FormatEntry(
                    MIME_TYPE_WMS,
                    null,
                    null,
                    true));

    /**
     * Key with the name to loop up the option.
     */
    private final String key;

    /**
     * Format for this option.
     */
    private final FormatEntry format;

    /**
     * Default constructor with a key and a format.
     * @param aKey key to lookup
     * @param aFormat format of the option
     */
    DefaultFormatOption(
            final String aKey,
            final FormatEntry aFormat) {
        this.key = aKey;
        this.format = aFormat;
    }


    /**
     *
     * @return key / name to lookup
     */
    public String getKey() {
        return key;
    }

    /**
     *
     * @return format of the option
     */
    public FormatEntry getFormat() {
        return format;
    }

    /**
     *
     * @return Enum as a map with the lookup names as keys
     */
    public static Map<String, DefaultFormatOption> asMap() {
        return Stream.of(values()).collect(Collectors.toMap(
                DefaultFormatOption::getKey,
                Function.identity()
        ));
    }
}
