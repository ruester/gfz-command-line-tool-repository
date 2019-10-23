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
import org.n52.wps.io.datahandler.parser.GML3BasicParser;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Parser for gml to quakeml binding.
 */
public class QuakeMLGML3Parser extends AbstractParser {

    /**
     * Logger to log unexpected behaviour.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(QuakeMLGML3Parser.class);

    /**
     * Default constructor.
     */
    public QuakeMLGML3Parser() {
        super();

        final FormatEntry gml = DefaultFormatOption.GML.getFormat();

        supportedIDataTypes.add(QuakeMLXmlDataBinding.class);
        supportedFormats.add(gml.getMimeType());
        supportedSchemas.add(gml.getSchema());
        supportedEncodings.add(gml.getEncoding());
        formats.add(gml);
    }

    /**
     * Parses the input stream to a Idata binding.
     * @param stream input stream with the data
     * @param mimeType mimeType of the data
     * @param schema schema of the data
     * @return QuakeMLXmlDataBinding
     */
    @Override
    public IData parse(
            final InputStream stream,
            final String mimeType,
            final String schema) {

        try {
            final GML3BasicParser gmlParser = new GML3BasicParser();

            final GTVectorDataBinding bindingClass = gmlParser.parse(
                    stream,
                    null,
                    null);
            final FeatureCollection<?, ?> featureCollection =
                    bindingClass.getPayload();

            if (featureCollection instanceof SimpleFeatureCollection) {
                final SimpleFeatureCollection simpleFeatureCollection =
                        (SimpleFeatureCollection) featureCollection;

                final IQuakeML quakeML = QuakeML.fromFeatureCollection(
                        simpleFeatureCollection);
                return QuakeMLXmlDataBinding.fromQuakeML(quakeML);

            } else {
                throw new ConvertFormatException(
                        "No simple feature collection provided");
            }

        } catch (final ConvertFormatException convertFormatException) {
            LOGGER.error(
                    "Can't parse the provided gml to xml",
                    convertFormatException);
            throw new RuntimeException(convertFormatException);
        }
    }
}
