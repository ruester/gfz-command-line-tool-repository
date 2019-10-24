package org.n52.gfz.riesgos.util;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 */

/**
 * Utility class for XML schema translation.
 */
public class XmlSchemaFileTranslator {
    /**
     * Location of the QuakeML schema file.
     */
    public static final String QUAKEML_SCHEMA_RES =
        "/org/n52/gfz/riesgos/validators/xml/QuakeML-BED-1.2.xsd";

    /**
     * Location of the ShakeMap schema file.
     */
    public static final String SHAKEMAP_SCHEMA_RES =
        "/org/n52/gfz/riesgos/validators/xml/shakemap.xsd";

    /**
     * Mapping of all available schema translations.
     */
    private final Map<String, URI> translator;

    /**
     * Initialization of the mappings.
     */
    public XmlSchemaFileTranslator() {
        translator = new HashMap<>();
        URI quakemlSchemaFile = null,
            shakemapSchemaFile = null;

        try {
            quakemlSchemaFile = getClass().getResource(
                QUAKEML_SCHEMA_RES
            ).toURI();
            shakemapSchemaFile = getClass().getResource(
                SHAKEMAP_SCHEMA_RES
            ).toURI();
        } catch (URISyntaxException e) { }

        translator.put(
            "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd",
            quakemlSchemaFile
        );
        translator.put(
            "http://earthquake.usgs.gov/eqcenter/shakemap",
            shakemapSchemaFile
        );
    }

    /**
     * Check if a given string is a URL.
     * @param str String to check
     * @return true if string is a URL, otherwise false
     */
    public static boolean isURL(final String str) {
        try {
            URL url = new URL(str);
            url.openStream().close();
            return true;
        } catch (Exception e) { }

        return false;
    }

    /**
     * Translate given URI to URL or File.
     * @param uri uri that should be changed
     * @return File
     */
    public Object translateUri(final String uri) {
        if (translator.containsKey(uri)) {
            try {
                return translator.get(uri).toURL();
            } catch (Exception e) {
                return null;
            }
        }

        if (isURL(uri)) {
            try {
                return new URL(uri);
            } catch (Exception e) { }
        }

        return new File(uri);
    }
}
