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

package org.n52.gfz.riesgos.formats.nrml.generators;

import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.nrml.binding.NrmlXmlDataBinding;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * This is the xml generator for the nrml format.
 */
public class NrmlXmlGenerator extends AbstractGenerator {

    /**
     * Logger for unexpected conditions.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(NrmlXmlGenerator.class);

    /**
     * Creates new generator instance.
     */
    public NrmlXmlGenerator() {
        super();

        final FormatEntry nrml = DefaultFormatOption.NRML.getFormat();

        supportedIDataTypes.add(NrmlXmlDataBinding.class);
        supportedFormats.add(nrml.getMimeType());
        supportedSchemas.add(nrml.getSchema());
        supportedEncodings.add(nrml.getEncoding());

        formats.add(nrml);
    }

    /**
     * Converts the data to a stream.
     * @param data     the data
     * @param mimeType the mime type of the data
     * @param schema   the schema of the data
     * @return an <code>InputStream</code> containing the data
     */
    @Override
    public InputStream generateStream(final IData data,
                                      final String mimeType,
                                      final String schema) {
        if (data instanceof NrmlXmlDataBinding) {
            final NrmlXmlDataBinding binding = (NrmlXmlDataBinding) data;
            final XmlObject payload = binding.getPayload();
            return new ByteArrayInputStream(payload.xmlText().getBytes());
        } else {
            LOGGER.error(
                    "Can't convert another data binding as NrmlXmlDataBinding");
        }
        return null;
    }
}
