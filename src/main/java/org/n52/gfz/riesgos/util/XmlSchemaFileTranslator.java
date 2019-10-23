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
 * Utility class for XML schema translation
 */
public class XmlSchemaFileTranslator {
    public static final String quakemlSchemaResource =
        "/org/n52/gfz/riesgos/validators/xml/QuakeML-BED-1.2.xsd";
    public static final String shakemapSchemaResource =
        "/org/n52/gfz/riesgos/validators/xml/shakemap.xsd";
    final Map<String, URI> translator;

    public XmlSchemaFileTranslator() {
        translator = new HashMap<>();
        URI quakemlSchemaFile = null,
            shakemapSchemaFile = null;

        try {
            quakemlSchemaFile = getClass().getResource(
                quakemlSchemaResource
            ).toURI();
            shakemapSchemaFile = getClass().getResource(
                shakemapSchemaResource
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

    public static boolean isURL(final String str) {
        try {
            URL url = new URL(str);
            url.openStream().close();
            return true;
        } catch (Exception e) { }

        return false;
    }

    /**
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
