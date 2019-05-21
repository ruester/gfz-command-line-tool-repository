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
 *
 *
 */

package org.n52.gfz.riesgos.formats;

/**
 * Constants for mime types and schema values
 */
public interface IMimeTypeAndSchemaConstants {

    /**
     * Mime-Type for xml
     */
    String MIME_TYPE_XML = "text/xml";

    /**
     * Mime-Type for geojson
     */
    String MIME_TYPE_GEOJSON = "application/vnd.geo+json";

    /**
     * Mime-Type for geotiff
     */
    String MIME_TYPE_GEOTIFF = "image/geotiff";

    /**
     * Schema for quakeML
     */
    String SCHEMA_QUAKE_ML = "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd";

    /**
     * Dummy value for the quakeML that does not match the schema
     */
    String SCHEMA_QUAKE_ML_OLD = "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd (original; not conform to schema)";

    /**
     * Schema for GML 3.2.1
     */
    String SCHEMA_GML_3_2_1 = "http://schemas.opengis.net/gml/3.2.1/base/feature.xsd";

    /**
     * Schema for shakemap
     */
    String SCHEMA_SHAKEMAP = "http://earthquake.usgs.gov/eqcenter/shakemap/xml/schemas/shakemap.xsd";
}
