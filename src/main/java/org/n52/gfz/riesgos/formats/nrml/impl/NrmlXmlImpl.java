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
import org.n52.gfz.riesgos.formats.nrml.INrml;
import org.n52.gfz.riesgos.formats.nrml.INrmlExposureModel;

/**
 * This is the implementation of the overall interface to
 * work with nrml data.
 */
public class NrmlXmlImpl implements INrml, INrmlQNames {

    /**
     * The root element.
     */
    private final XmlObject xml;

    /**
     * This creates an new instance with the xml root element.
     * @param aXml root element with nrml
     */
    public NrmlXmlImpl(final XmlObject aXml) {
        this.xml = aXml;
    }

    /**
     * Returns the sub element with the exposure model.
     * @return INrmlExposureModel
     */
    @Override
    public INrmlExposureModel getExposureModel() {
        final XmlObject[] foundExposure = xml.selectChildren(NRML)[0]
                .selectChildren(EXPOSURE_MODEL);
        final XmlObject xmlExposureObject = foundExposure[0];
        return new NrmlExposureModelImpl(xmlExposureObject);
    }
}
