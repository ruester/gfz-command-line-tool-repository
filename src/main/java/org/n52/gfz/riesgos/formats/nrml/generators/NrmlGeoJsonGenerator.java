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

import org.geotools.data.simple.SimpleFeatureCollection;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.nrml.INrml;
import org.n52.gfz.riesgos.formats.nrml.binding.NrmlXmlDataBinding;
import org.n52.gfz.riesgos.formats.nrml.functions.NrmlToFeatureCollection;
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
 * Generator to extract all the data from the nrml and to
 * wirte geojson to the stream.
 */
public class NrmlGeoJsonGenerator extends AbstractGenerator {

    /**
     * Logger to log unexpceted conditions.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(NrmlGeoJsonGenerator.class);
    /**
     * Format entry for geojson.
     */
    private static final FormatEntry GEOJSON =
            DefaultFormatOption.GEOJSON.getFormat();

    /**
     * Function to convert nrml to a simple feature collection.
     */
    private static final Function<INrml, SimpleFeatureCollection>
            TO_FEATURE_COLLECTION = new NrmlToFeatureCollection();

    /**
     * Default constructor.
     */
    public NrmlGeoJsonGenerator() {
        super();

        supportedIDataTypes.add(NrmlXmlDataBinding.class);
        supportedFormats.add(GEOJSON.getMimeType());
        supportedEncodings.add(GEOJSON.getEncoding());
        formats.add(GEOJSON);
    }

    /**
     * @param data     the data
     * @param mimeType the mime type of the data
     * @param schema   the schema of the data
     * @return an <code>InputStream</code> containing the data
     * @throws IOException if the <code>InputStream</code> cannot be created
     *                     <p>
     *                     generates final output data produced by an IAlgorithm
     *                     and returns an InputStream for subsequent access.
     */
    @Override
    public InputStream generateStream(
            final IData data,
            final String mimeType,
            final String schema) throws IOException {

        if (data instanceof NrmlXmlDataBinding) {
            final NrmlXmlDataBinding binding = (NrmlXmlDataBinding) data;
            final INrml nrml = binding.getPayloadNrml();

            final SimpleFeatureCollection featureCollection =
                    TO_FEATURE_COLLECTION.apply(nrml);

            return new GeoJSONGenerator().generateStream(
                    new GTVectorDataBinding(featureCollection),
                    GEOJSON.getMimeType(), GEOJSON.getSchema());
        } else {
            LOGGER.error(
                    "Can't convert another data binding as NrmlXmlDataBinding");
        }

        return null;
    }
}
