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

import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.quakeml.binding.QuakeMLXmlDataBinding;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeML;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Generator implementation that takes the QuakeMLXmlDataBinding and its validated xml format
 * and converts it into the original format from the original quakeledger (that is not valid
 * according to the schema).
 */
public class QuakeMLOriginalXmlGenerator extends AbstractGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuakeMLOriginalXmlGenerator.class);

    public QuakeMLOriginalXmlGenerator() {
        super();

        final FormatEntry quakeml = DefaultFormatOption.NON_VALID_QUAKEML.getFormat();
        supportedIDataTypes.add(QuakeMLXmlDataBinding.class);
        supportedFormats.add(quakeml.getMimeType());
        supportedSchemas.add(quakeml.getSchema());
        supportedEncodings.add(quakeml.getEncoding());
        formats.add(quakeml);
    }

    @Override
    public InputStream generateStream(final IData data, final String mimeType, final String schema) {
        if (data instanceof QuakeMLXmlDataBinding) {
            final QuakeMLXmlDataBinding binding = (QuakeMLXmlDataBinding) data;

            try {
                final IQuakeML quakeML = binding.getPayloadQuakeML();
                final XmlObject originalQuakeML = quakeML.toOriginalXmlObject();

                return new ByteArrayInputStream(originalQuakeML.xmlText().getBytes());
            } catch(final ConvertFormatException convertFormatException) {
                LOGGER.error("Can't convert the validated quakeml format to the original quakeml xml");
                LOGGER.error(convertFormatException.toString());
                throw new RuntimeException(convertFormatException);
            }
        } else {
            LOGGER.error("Can't convert another data binding as QuakeMLXmlDataBinding");
        }
        return null;
    }
}
