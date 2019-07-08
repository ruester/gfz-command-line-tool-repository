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

import org.geotools.data.simple.SimpleFeatureCollection;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.shakemap.IShakemap;
import org.n52.gfz.riesgos.formats.shakemap.binding.ShakemapXmlDataBinding;
import org.n52.gfz.riesgos.formats.shakemap.functions.ShakemapToIsolines;
import org.n52.gfz.riesgos.formats.shakemap.functions.ShakemapToSimpleFeatureCollection;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.io.datahandler.generator.GML3BasicGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * Generator to transform the xml of a shakemap to gml3
 */
public class ShakemapGML3Generator extends AbstractGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShakemapGML3Generator.class);
    private static final FormatEntry GML = DefaultFormatOption.GML.getFormat();
    private static final Function<IShakemap, SimpleFeatureCollection> TO_FEATURE_COLLECTION =
            new ShakemapToIsolines();

    /**
     * Default constructor
     */
    public ShakemapGML3Generator() {
        super();

        supportedIDataTypes.add(ShakemapXmlDataBinding.class);
        supportedFormats.add(GML.getMimeType());
        supportedSchemas.add(GML.getSchema());
        supportedEncodings.add(GML.getEncoding());
        formats.add(GML);
    }

    @Override
    public InputStream generateStream(final IData data, final String mimeType, final String schema) throws IOException {
        if(data instanceof ShakemapXmlDataBinding) {
            final ShakemapXmlDataBinding binding = (ShakemapXmlDataBinding) data;
            final IShakemap shakemap = binding.getPayloadShakemap();

            final SimpleFeatureCollection collection = TO_FEATURE_COLLECTION.apply(shakemap);

            return new GML3BasicGenerator().generateStream(new GTVectorDataBinding(collection), GML.getMimeType(), GML.getSchema());
        } else {
            LOGGER.error("Can't convert another data binding as ShakemalXmlDataBinding");
        }
        return null;
    }
}
