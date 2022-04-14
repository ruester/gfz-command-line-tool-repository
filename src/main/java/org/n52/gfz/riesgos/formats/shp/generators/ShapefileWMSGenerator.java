/*
 * Copyright (C) 2022 GFZ German Research Centre for Geosciences
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

package org.n52.gfz.riesgos.formats.shp.generators;

import org.apache.commons.io.IOUtils;
import org.geotools.feature.FeatureCollection;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.formats.IMimeTypeAndSchemaConstants;
import org.n52.gfz.riesgos.formats.wms.generators.RiesgosWmsGenerator;
import org.n52.gfz.riesgos.util.geoserver.exceptions.GeoserverClientException;
import org.n52.wps.io.data.GenericFileDataWithGT;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Generator to send shapefiles to an geoserver & return a
 * get map url for the WMS service.
 */
public class ShapefileWMSGenerator extends AbstractGenerator {

    /**
     * Logger for the Generator.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ShapefileWMSGenerator.class);

    /**
     * Our WMS format entry.
     */
    private static final FormatEntry WMS = DefaultFormatOption.WMS.getFormat();

    /**
     * Default constructor.
     * Runs super & registers the formats for which the generator can be used.
     */
    public ShapefileWMSGenerator() {
        super();

        supportedIDataTypes.add(GTVectorDataBinding.class);
        supportedFormats.add(WMS.getMimeType());
        supportedEncodings.add(IMimeTypeAndSchemaConstants.DEFAULT_ENCODING);

        formats.add(WMS);
    }

    /**
     * Generate the stream for the wms output.
     * @param data GTVectorDataBinding (only suppored type yet)
     * @param mimeType mimetype for wms
     * @param schema schema (will not be checked)
     * @return input stream that can create the wps answer
     * @throws IOException exception if we have problems generating wms output
     */
    @Override
    public InputStream generateStream(
            final IData data,
            final String mimeType,
            final String schema
    )  throws IOException {
        if (data instanceof GTVectorDataBinding) {
            final GTVectorDataBinding binding = (GTVectorDataBinding) data;
            final FeatureCollection<?, ?> featureCollection =
                    binding.getPayload();
            final GenericFileDataWithGT fileData =
                    new GenericFileDataWithGT(featureCollection);
            final File baseFile = fileData.getBaseFile(true);
            try {
                return IOUtils.toInputStream(
                        new RiesgosWmsGenerator().storeVectorAndReturnGetMapUrl(
                                baseFile
                        )
                );
            } catch (GeoserverClientException geoserverClientException) {
                throw new IOException(geoserverClientException);
            }
        } else {
            LOGGER.error(
                    "Can't convert another data binding "
                            + "as GTVectorDataBinding");
        }
        return null;
    }
}
