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

package org.n52.gfz.riesgos.formats.shakemap.generators;

import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.IMimeTypeAndSchemaConstants;
import org.n52.gfz.riesgos.formats.shakemap.binding.ShakemapXmlDataBinding;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Xml Generator for shakemaps.
 * This is the default format for shakemaps.
 */
public class ShakemapXmlGenerator extends AbstractGenerator implements IMimeTypeAndSchemaConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShakemapXmlDataBinding.class);

    /**
     * Default constructor
     */
    public ShakemapXmlGenerator() {
        super();

        final FormatEntry shakemap = DefaultFormatOption.SHAKEMAP.getFormat();

        supportedIDataTypes.add(ShakemapXmlDataBinding.class);
        supportedFormats.add(shakemap.getMimeType());
        supportedSchemas.add(shakemap.getSchema());
        supportedEncodings.add(shakemap.getEncoding());
        formats.add(shakemap);
    }

    @Override
    public InputStream generateStream(final IData data, final String mimeType, final String schema) {
        if(data instanceof ShakemapXmlDataBinding) {
            final ShakemapXmlDataBinding binding = (ShakemapXmlDataBinding) data;
            final XmlObject xmlObject = binding.getPayload();
            return new ByteArrayInputStream(xmlObject.xmlText().getBytes());
        } else {
            LOGGER.error("Can't convert another data binding as ShakemapXmlDataBinding");
        }
        return null;
    }
}
