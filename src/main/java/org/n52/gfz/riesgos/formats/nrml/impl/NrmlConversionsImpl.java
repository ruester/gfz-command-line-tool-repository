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
import org.n52.gfz.riesgos.formats.nrml.INrmlConversions;
import org.n52.gfz.riesgos.formats.nrml.INrmlCostTypes;

/**
 * Implementation of the INrmlConversions interface.
 */
public class NrmlConversionsImpl implements INrmlConversions, INrmlQNames {

    /**
     * Xml element with the conversions tag.
     */
    private final XmlObject xml;

    /**
     * Creates a new instance by giving the xml element
     * with the conversions tag.
     * @param aXml xml element with the conversions tag.
     */
    NrmlConversionsImpl(final XmlObject aXml) {
        this.xml = aXml;
    }

    /**
     *
     * @return sub element with the cost types
     */
    @Override
    public INrmlCostTypes getCostTypes() {
        return new NrmlCostTypesImpl(xml.selectChildren(COST_TYPES)[0]);
    }
}
