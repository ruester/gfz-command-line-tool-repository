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

package org.n52.gfz.riesgos.formats.wms.generators;

import org.apache.commons.io.IOUtils;
import org.n52.wps.commons.WPSConfig;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;
import org.n52.wps.io.datahandler.generator.GeoserverWMSGenerator;
import org.n52.wps.io.modules.generator.GeoserverWMSGeneratorCM;
import org.n52.wps.webapp.api.ConfigurationCategory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * This is a wrapper that works around some odd issues
 * in the current GeoserverWMSGenerator implementation.
 *
 * We hope that this solution is only temporary in order
 * to make the WMS generation work.
 */
public class GeoserverWMSGeneratorWorkAround extends AbstractGenerator {

    /**
     * The generator to use.
     */
    private final GeoserverWMSGenerator generator;
    /**
     * The configuration module of this generator.
     */
    private final GeoserverWMSGeneratorCM cm;

    /**
     * This is the default constructor.
     * It creates an instance of the GeoserverWMSGenerator.
     */
    public GeoserverWMSGeneratorWorkAround() {
        super();
        this.generator = new GeoserverWMSGenerator();
        this.cm = (GeoserverWMSGeneratorCM) WPSConfig
                .getInstance()
                .getConfigurationModuleForClass(
                        generator.getClass().getName(),
                        ConfigurationCategory.GENERATOR);
    }

    /**
     * The problem and the necessity of this is the following:
     *
     * The Geoserver WMS Generator copies the raster format that is given
     * here, to a geoserver instance and creates a store and a layer for
     * that.
     * To access the data via wms it returns a url to query it as a
     * png file.
     *
     * While it works to store the data on a custom geoserver
     * (you can define the hostname and the port in the
     * configuration module) it hardcodes the base url that is given back
     * as a link.
     *
     * This is always a url with https://riesgos.52north.org/ ...
     * and this must be replaced with the real hostname and the real port
     * of the geoserver.
     *
     * For this we access the configuration module.
     *
     * @param data the idata for that the wms should be generated
     * @param mimeType format of the data
     * @param schema schema of the data
     *
     * @return InputStream with the data to give back from the server
     * @throws IOException exception on handling the input and output
     */
    @Override
    public InputStream generateStream(
            final IData data,
            final String mimeType,
            final String schema)

            throws IOException {


        final String pureHosthame = cm.getGeoserverHost();
        final String purePort = cm.getGeoserverPort();

        final InputStream inputStreamRaw =
                generator.generateStream(data, mimeType, schema);

        if (inputStreamRaw == null) {
            return inputStreamRaw;
        }

        final ByteArrayOutputStream byteOutputStream =
                new ByteArrayOutputStream();

        IOUtils.copy(inputStreamRaw, byteOutputStream);

        final String text = new String(byteOutputStream.toByteArray());

        final String hostname =
                "http://" + pureHosthame + ":"  + purePort + "/";
        final String textReplaced =
                text.replace("https://riesgos.52north.org/", hostname);

        return new ByteArrayInputStream(textReplaced.getBytes());
    }


}
