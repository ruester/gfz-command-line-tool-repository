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

package org.n52.gfz.riesgos.formats.nrml.impl;

import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.formats.nrml.INrmlOccupancy;

/**
 * This is the implementation of the nrml occupancy interface.
 */
public class NrmlOccupancyImpl implements INrmlOccupancy, INrmlQNames {

    /**
     * Xml element with the occupancy xml element.
     */
    private final XmlObject xml;

    /**
     * Creates the occpancy implemetation.
     * @param aXml xml element with the occupancy xml element.
     */
    NrmlOccupancyImpl(final XmlObject aXml) {
        this.xml = aXml;
    }

    /**
     *
     * @return String with the period (day / night)
     */
    @Override
    public String getPeriod() {
        return xml.selectAttribute(PERIOD).newCursor().getTextValue();
    }

    /**
     *
     * @return number of the occupants
     */
    @Override
    public int getOccupants() {
        return Integer.parseInt(xml.selectAttribute(OCCUPANTS)
                .newCursor()
                .getTextValue());
    }
}
