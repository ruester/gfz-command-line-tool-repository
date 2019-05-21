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
import org.n52.gfz.riesgos.formats.IMimeTypeAndSchemaConstants;
import org.n52.gfz.riesgos.formats.shakemap.IShakemap;
import org.n52.gfz.riesgos.formats.shakemap.binding.ShakemapXmlDataBinding;
import org.n52.gfz.riesgos.formats.shakemap.functions.ShakemapToSimpleFeatureCollection;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.io.datahandler.generator.GeoJSONGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * GeoJson Generator for Shakemaps
 */
public class ShakemapGeoJsonGenerator extends AbstractGenerator implements IMimeTypeAndSchemaConstants {

    private static Logger LOGGER = LoggerFactory.getLogger(ShakemapGeoJsonGenerator.class);
    private static final Function<IShakemap, SimpleFeatureCollection> TO_FEATURE_COLLECTION =
            new ShakemapToSimpleFeatureCollection();

    /**
     * Default constructor
     */
    public ShakemapGeoJsonGenerator() {
        super();

        supportedIDataTypes.add(ShakemapXmlDataBinding.class);
        supportedFormats.add(MIME_TYPE_GEOJSON);
        supportedEncodings.add(DEFAULT_ENCODING);
        formats.add(new FormatEntry(MIME_TYPE_GEOJSON, null, DEFAULT_ENCODING, true));
    }

    @Override
    public InputStream generateStream(final IData data, final String mimeType, final String schema) throws IOException {
        if(data instanceof ShakemapXmlDataBinding) {
            final ShakemapXmlDataBinding binding = (ShakemapXmlDataBinding) data;
            final IShakemap shakemap = binding.getPayloadShakemap();

            final SimpleFeatureCollection featureCollection = TO_FEATURE_COLLECTION.apply(shakemap);

            return new GeoJSONGenerator().generateStream(new GTVectorDataBinding(featureCollection), MIME_TYPE_GEOJSON, null);
        } else {
            LOGGER.error("Can't convert another data binding as ShakemapXmlDataBinding");
        }
        return null;
    }
}
