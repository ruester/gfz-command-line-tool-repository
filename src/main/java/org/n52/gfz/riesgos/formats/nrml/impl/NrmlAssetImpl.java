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
import org.n52.gfz.riesgos.formats.nrml.INrmlAsset;
import org.n52.gfz.riesgos.formats.nrml.INrmlCosts;
import org.n52.gfz.riesgos.formats.nrml.INrmlLocation;
import org.n52.gfz.riesgos.formats.nrml.INrmlOccupancies;

/**
 * Implementation of the INrmlAsset interface.
 */
public class NrmlAssetImpl implements INrmlAsset, INrmlQNames {

    /**
     * Xml element with the asset tag.
     */
    private final XmlObject xml;

    /**
     * Creates a new instance by giving the xml
     * element with the asset tag.
     * @param aXml xml element with the asset tag
     */
    NrmlAssetImpl(final XmlObject aXml) {
        this.xml = aXml;
    }

    /**
     *
     * @return id of the asset
     */
    @Override
    public String getId() {
        return xml.selectAttribute(ID).newCursor().getTextValue();
    }

    /**
     *
     * @return number of entries for that asset
     */
    @Override
    public int getNumber() {
        return Integer.parseInt(
                xml.selectAttribute(NUMBER).newCursor().getTextValue());
    }

    /**
     *
     * @return taxonomy of the asset
     */
    @Override
    public String getTaxonomy() {
        return xml.selectAttribute(TAXONOMY).newCursor().getTextValue();
    }

    /**
     *
     * @return sub element with the location
     */
    @Override
    public INrmlLocation getLocation() {
        return new NrmlLocationImpl(xml.selectChildren(LOCATION)[0]);
    }

    /**
     *
     * @return sub element with the costs
     */
    @Override
    public INrmlCosts getCosts() {
        return new NrmlCostsImpl(xml.selectChildren(COSTS)[0]);
    }

    /**
     *
     * @return sub element with the occupancies
     */
    @Override
    public INrmlOccupancies getOccupancies() {
        return new NrmlOccupanciesImpl(xml.selectChildren(OCCUPANCIES)[0]);
    }
}
