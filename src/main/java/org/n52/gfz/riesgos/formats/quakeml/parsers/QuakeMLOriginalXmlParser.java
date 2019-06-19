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

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.quakeml.binding.QuakeMLXmlDataBinding;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeML;
import org.n52.gfz.riesgos.formats.quakeml.QuakeML;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.parser.AbstractParser;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser that parses the original quakeml (the non valid one) to the validated quakeml
 */
public class QuakeMLOriginalXmlParser extends AbstractParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuakeMLOriginalXmlParser.class);

    /**
     * default constructor
     */
    public QuakeMLOriginalXmlParser() {
        super();

        final FormatEntry quakeml = DefaultFormatOption.NON_VALID_QUAKEML.getFormat();

        supportedIDataTypes.add(QuakeMLXmlDataBinding.class);
        supportedFormats.add(quakeml.getMimeType());
        supportedSchemas.add(quakeml.getSchema());
        supportedEncodings.add(quakeml.getSchema());
        formats.add(quakeml);
    }

    @Override
    public IData parse(final InputStream stream, final String mimeType, final String schema) {

        try {
            final XmlObject xmlObject = XmlObject.Factory.parse(stream);

            final IQuakeML quakeML = QuakeML.fromOriginalXml(xmlObject);
            return QuakeMLXmlDataBinding.fromQuakeML(quakeML);
        } catch(final XmlException xmlException) {
            LOGGER.error("Can't parse the provided xml because of a XMLException");
            LOGGER.error(xmlException.toString());
            throw new RuntimeException(xmlException);
        } catch(final IOException ioException) {
            LOGGER.error("Can't parse the provided xml because of an IOException");
            LOGGER.error(ioException.toString());
            throw new RuntimeException(ioException);
        } catch(final ConvertFormatException convertFormatException) {
            LOGGER.error("Can't convert the provided xml to validated quakeml");
            LOGGER.error(convertFormatException.toString());
            throw new RuntimeException(convertFormatException);
        }
    }
}
