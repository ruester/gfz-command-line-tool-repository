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

package org.n52.gfz.riesgos.data.quakeml.generators;

import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.data.IMimeTypeAndSchemaConstants;
import org.n52.gfz.riesgos.data.quakeml.QuakeMLXmlDataBinding;
import org.n52.wps.io.IOHandler;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Generator that takes the validated quakeml xml from the QuakeMLXmlDataBinding class
 */
public class QuakeMLValidatedXmlGenerator extends AbstractGenerator implements IMimeTypeAndSchemaConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuakeMLValidatedXmlGenerator.class);

    public QuakeMLValidatedXmlGenerator() {
        super();
        supportedIDataTypes.add(QuakeMLXmlDataBinding.class);
        supportedFormats.add(MIME_TYPE_XML);
        supportedSchemas.add(SCHEMA_QUAKE_ML);
        supportedEncodings.add(DEFAULT_ENCODING);
        formats.add(new FormatEntry(MIME_TYPE_XML, SCHEMA_QUAKE_ML, DEFAULT_ENCODING, true));
    }

    @Override
    public InputStream generateStream(final IData data, final String mimeType, final String schema) {
        if (data instanceof QuakeMLXmlDataBinding) {
                final QuakeMLXmlDataBinding binding = (QuakeMLXmlDataBinding) data;
                final XmlObject xmlObject = binding.getPayload();
                return new ByteArrayInputStream(xmlObject.xmlText().getBytes());
        } else {
            LOGGER.error("Can't convert another data binding as QuakeMLXmlDataBinding");
        }
        return null;
    }
}
