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

package org.n52.gfz.riesgos.formats.nrml.parsers;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.nrml.binding.NrmlXmlDataBinding;
import org.n52.wps.io.datahandler.parser.AbstractParser;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser to parse nrml xml data.
 */
public class NrmlXmlParser extends AbstractParser {

    /**
     * Logger for unexpected conditions.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(NrmlXmlParser.class);

    /**
     * Default constructor without arguments.
     */
    public NrmlXmlParser() {
        super();


        final FormatEntry nrml = DefaultFormatOption.NRML.getFormat();

        supportedIDataTypes.add(NrmlXmlDataBinding.class);
        supportedFormats.add(nrml.getMimeType());
        supportedSchemas.add(nrml.getSchema());
        supportedEncodings.add(nrml.getEncoding());
        formats.add(nrml);
    }

    /**
     * Parses the content to a binding class.
     * @param input input stream with the data
     * @param mimeType mimetype of the format
     * @param schema schema of the format
     * @return NrmlXmlDataBinding
     */
    @Override
    public NrmlXmlDataBinding parse(
            final InputStream input,
            final String mimeType,
            final String schema) {
        try {
            final XmlObject xmlObject = XmlObject.Factory.parse(input);
            return NrmlXmlDataBinding.fromXml(xmlObject);
        } catch (final XmlException xmlException) {
            LOGGER.error(
                    "Can't parse the provided xml because of an XMLException");
            LOGGER.error(xmlException.toString());
            throw new RuntimeException(xmlException);
        } catch (final IOException ioException) {
            LOGGER.error(
                    "Can't parse the provided xml because of an IOException");
            LOGGER.error(ioException.toString());
            throw new RuntimeException(ioException);
        }
    }
}
