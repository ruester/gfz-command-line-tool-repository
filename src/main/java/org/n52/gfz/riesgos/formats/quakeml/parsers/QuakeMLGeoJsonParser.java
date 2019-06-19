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

package org.n52.gfz.riesgos.formats.quakeml.parsers;


import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.quakeml.binding.QuakeMLXmlDataBinding;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeML;
import org.n52.gfz.riesgos.formats.quakeml.QuakeML;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.datahandler.parser.AbstractParser;
import org.n52.wps.io.datahandler.parser.GeoJSONParser;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Parser to convert geojson to validated quakeml
 */
public class QuakeMLGeoJsonParser extends AbstractParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuakeMLGeoJsonParser.class);

    /**
     * default constructor
     */
    public QuakeMLGeoJsonParser() {
        super();

        final FormatEntry geojson = DefaultFormatOption.GEOJSON.getFormat();
        supportedIDataTypes.add(QuakeMLXmlDataBinding.class);
        supportedFormats.add(geojson.getMimeType());
        supportedEncodings.add(geojson.getEncoding());
        formats.add(geojson);
    }

    @Override
    public IData parse(final InputStream stream, final String mimeType, final String schema) {

        try {
            final GeoJSONParser geoJsonParser = new GeoJSONParser();

            final IData geoJson = geoJsonParser.parse(stream, null, null);
            if(geoJson instanceof GTVectorDataBinding) {
                final GTVectorDataBinding bindingClass = (GTVectorDataBinding) geoJson;
                final FeatureCollection<?, ?> featureCollection = bindingClass.getPayload();

                if(featureCollection instanceof SimpleFeatureCollection) {
                    final SimpleFeatureCollection simpleFeatureCollection = (SimpleFeatureCollection) featureCollection;

                    final IQuakeML quakeML = QuakeML.fromFeatureCollection(simpleFeatureCollection);
                    return QuakeMLXmlDataBinding.fromQuakeML(quakeML);

                } else {
                    throw new ConvertFormatException("No simplefeature collection provided");
                }
            } else {
                throw new ConvertFormatException("No GTVectorDataBinding provided");
            }
        } catch(final ConvertFormatException convertFormatException) {
            LOGGER.error("Can't parse the provided geojson to xml");
            LOGGER.error(convertFormatException.toString());
            throw new RuntimeException(convertFormatException);
        }
    }

}
