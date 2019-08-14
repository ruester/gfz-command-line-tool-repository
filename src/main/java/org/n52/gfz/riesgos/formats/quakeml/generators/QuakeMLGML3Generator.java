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
import org.n52.wps.io.datahandler.generator.GML3BasicGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Generator to transform the validated xml
 * from the QuakeMLXmlDataBinding to gml.
 */
public class QuakeMLGML3Generator extends AbstractGenerator {

    /**
     * Logger to log unexpected behaviour.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(QuakeMLGML3Generator.class);
    /**
     * Format entry for gml.
     */
    private static final FormatEntry GML = DefaultFormatOption.GML.getFormat();

    /**
     * Default constructor.
     */
    public QuakeMLGML3Generator() {
        super();

        supportedIDataTypes.add(QuakeMLXmlDataBinding.class);
        supportedFormats.add(GML.getMimeType());
        supportedSchemas.add(GML.getSchema());
        supportedEncodings.add(GML.getEncoding());
        formats.add(GML);
    }

    /**
     * Generates an input stream to read the given data afterwards.
     * @param data data for the input stream
     * @param mimeType mime type of the data
     * @param schema schema of the data
     * @return input stream
     * @throws IOException exception that may be thrown in case of a problem
     * with IO
     */
    @Override
    public InputStream generateStream(
            final IData data,
            final String mimeType,
            final String schema) throws IOException {
        if (data instanceof QuakeMLXmlDataBinding) {
            final QuakeMLXmlDataBinding binding = (QuakeMLXmlDataBinding) data;

            try {
                final IQuakeML quakeML = binding.getPayloadQuakeML();
                final FeatureCollection<SimpleFeatureType, SimpleFeature>
                        featureCollection =
                        quakeML.toSimpleFeatureCollection();

                return new GML3BasicGenerator().generateStream(
                        new GTVectorDataBinding(featureCollection),
                        GML.getMimeType(),
                        GML.getSchema());
            } catch (final ConvertFormatException convertFormatException) {
                LOGGER.error(
                        "Can't convert the validated quakeml format to gml3",
                        convertFormatException);
                throw new IOException(convertFormatException);
            }
        } else {
            LOGGER.error(
                    "Can't convert another data binding "
                            + "as QuakeMLXmlDataBinding");
        }
        return null;
    }
}
