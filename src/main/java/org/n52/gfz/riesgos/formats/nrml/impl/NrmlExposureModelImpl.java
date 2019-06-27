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
import org.n52.gfz.riesgos.formats.nrml.INrmlAssets;
import org.n52.gfz.riesgos.formats.nrml.INrmlConversions;
import org.n52.gfz.riesgos.formats.nrml.INrmlDescription;
import org.n52.gfz.riesgos.formats.nrml.INrmlExposureModel;

/**
 * Implementation of the INrmlExposureModel interface.
 */
public class NrmlExposureModelImpl implements INrmlExposureModel, INrmlQNames {

    /**
     * Xml element with the exposureModel tag.
     */
    private final XmlObject xml;

    /**
     * Creates a new instance by giving the xml element
     * with the exposureModel tag.
     * @param aXml xml element with the exposureModel tag
     */
    NrmlExposureModelImpl(final XmlObject aXml) {
        this.xml = aXml;
    }

    /**
     *
     * @return sub element with the description
     */
    @Override
    public INrmlDescription getDescription() {
        return new NrmlDescriptionImpl(xml.selectChildren(DESCRIPTION)[0]);
    }

    /**
     *
     * @return sub element with the conversions
     */
    @Override
    public INrmlConversions getConversions() {
        return new NrmlConversionsImpl(xml.selectChildren(CONVERSIONS)[0]);
    }

    /**
     *
     * @return sub element with the assets
     */
    @Override
    public INrmlAssets getAssets() {
        return new NrmlAssetsImpl(xml.selectChildren(ASSETS)[0]);
    }

    /**
     *
     * @return String with the id
     */
    @Override
    public String getId() {
        return xml.selectAttribute(ID).newCursor().getTextValue();
    }

    /**
     *
     * @return category of the exposure model
     */
    @Override
    public String getCategory() {
        return xml.selectAttribute(CATEGORY).newCursor().getTextValue();
    }

    /**
     *
     * @return taxonomy source of the exposure model
     */
    @Override
    public String getTaxonomySource() {
        return xml.selectAttribute(TAXONOMY_SOURCE).newCursor().getTextValue();
    }
}
