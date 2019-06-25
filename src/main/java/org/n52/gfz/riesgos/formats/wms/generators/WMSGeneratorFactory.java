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

import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.wps.io.GeneratorFactory;
import org.n52.wps.io.IGenerator;
import org.n52.wps.io.data.IData;
import org.n52.wps.webapp.api.FormatEntry;

/**
 * This is a factory enum for accessing the
 * wms generators.
 */
public enum WMSGeneratorFactory {

    /**
     * The only one implementation.
     * Because the class would be static at all
     * otherwise.
     */
    INSTANCE(DefaultFormatOption.WMS.getFormat());

    /**
     * Format entry for the wms.
     */
    private final FormatEntry wms;

    /**
     * Default constructor.
     * @param aWms format entry with the wms format
     */
    WMSGeneratorFactory(final FormatEntry aWms) {
        this.wms = aWms;
    }

    /**
     * Returns the Generator that should be used for wms
     * output.
     *
     * @param clazz the binding class for which the generator should be used
     * @return Generator
     */
    public IGenerator getWMSGenerator(final Class<? extends IData> clazz) {
        /*
         * Because at the moment there is some odd
         * behaviour (see in the workaround class for details)
         * we don't ask the server directly which generator it
         * would use to generate wms, but we provide an own
         * wrapper for the geoserver wms generator that takes
         * care about a problem in the current implementation.
         *
         * We hope to replace it in near time.
         */

        // return findWMSGenerator(clazz);

        /*
         * Instead use the wrapper for the geoserver.
         */
        return useWorkAroundWMSGenerator();

    }

    /**
     * Private method to ask the server which generator
     * it uses normally for wms output for the given class.
     * @param clazz the binding class for the generator
     * @return Generator
     */
    private IGenerator findWMSGenerator(final Class<? extends IData> clazz) {
        return GeneratorFactory.getInstance().getGenerator(
                wms.getSchema(),
                wms.getMimeType(),
                wms.getEncoding(),
                clazz);
    }

    /**
     * Just use the workaround solution for the current
     * geoserver wms generator.
     * @return Generator
     */
    private IGenerator useWorkAroundWMSGenerator() {
        return new GeoserverWMSGeneratorWorkAround();
    }
}
