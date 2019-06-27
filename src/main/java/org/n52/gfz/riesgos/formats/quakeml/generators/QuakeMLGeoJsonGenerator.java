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

package org.n52.gfz.riesgos.formats.quakeml.generators;

import org.geotools.feature.FeatureCollection;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.quakeml.binding.QuakeMLXmlDataBinding;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeML;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.io.datahandler.generator.GeoJSONGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Generator that takes the IQuakeMLXmlDataBinding and returns GeoJson
 */
public class QuakeMLGeoJsonGenerator extends AbstractGenerator {

    private final static Logger LOGGER = LoggerFactory.getLogger(QuakeMLGeoJsonGenerator.class);
    private final static FormatEntry GEOJSON = DefaultFormatOption.GEOJSON.getFormat();

    public QuakeMLGeoJsonGenerator() {
        super();

        supportedIDataTypes.add(QuakeMLXmlDataBinding.class);
        supportedFormats.add(GEOJSON.getMimeType());
        supportedEncodings.add(GEOJSON.getEncoding());
        formats.add(GEOJSON);
    }

    @Override
    public InputStream generateStream(final IData data, final String mimeType, final String schema) throws IOException {
        if (data instanceof QuakeMLXmlDataBinding) {
            final QuakeMLXmlDataBinding binding = (QuakeMLXmlDataBinding) data;

            try {
                final IQuakeML quakeML = binding.getPayloadQuakeML();
                final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection =  quakeML.toSimpleFeatureCollection();

                return new GeoJSONGenerator().generateStream(new GTVectorDataBinding(featureCollection), GEOJSON.getEncoding(), null);
            } catch(final ConvertFormatException convertFormatException) {
                LOGGER.error("Can't convert the validated quakeml format to geojson");
                LOGGER.error(convertFormatException.toString());
                throw new RuntimeException(convertFormatException);
            }
        } else {
            LOGGER.error("Can't convert another data binding as QuakeMLXmlDataBinding");
        }
        return null;
    }
}
