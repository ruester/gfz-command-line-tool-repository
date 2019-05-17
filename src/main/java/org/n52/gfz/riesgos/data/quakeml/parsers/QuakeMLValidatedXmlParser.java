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

package org.n52.gfz.riesgos.data.quakeml.parsers;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.data.quakeml.QuakeMLXmlDataBinding;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.parser.AbstractParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class QuakeMLValidatedXmlParser extends AbstractParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuakeMLValidatedXmlParser.class);

    private static final String MIME_TYPE_XML = "text/xml";
    private static final String SCHEMA_QUAKE_ML = "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd";

    public QuakeMLValidatedXmlParser() {
        super();
        supportedIDataTypes.add(QuakeMLXmlDataBinding.class);
        supportedFormats.add(MIME_TYPE_XML);
        supportedSchemas.add(SCHEMA_QUAKE_ML);
    }

    @Override
    public IData parse(final InputStream stream, final String mimeType, final String schema) {

        // TODO
        // not tested on the server so far
        try {
            final XmlObject xmlObject = XmlObject.Factory.parse(stream);
            return new QuakeMLXmlDataBinding(xmlObject);
        } catch(final XmlException xmlException) {
            LOGGER.error("Can't parse the provided xml because of a XMLException");
            LOGGER.error(xmlException.toString());
            throw new RuntimeException(xmlException);
        } catch(final IOException ioException) {
            LOGGER.error("Can't parse the provided xml because of an IOException");
            LOGGER.error(ioException.toString());
            throw new RuntimeException(ioException);
        }
    }
}
