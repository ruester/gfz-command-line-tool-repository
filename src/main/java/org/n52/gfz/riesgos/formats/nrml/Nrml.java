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

package org.n52.gfz.riesgos.formats.nrml;

import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.formats.nrml.impl.NrmlXmlImpl;

/**
 * This is the static class to get an INrml implementation.
 */
public final class Nrml {

    /**
     * The constructor is private, so the class should be used
     * static only.
     */
    private Nrml() {
        // static
    }

    /**
     * Returns the INrml instance from xml structure.
     * @param xmlObject xmlObject with the nrml data inside
     * @return INrml implementation that works with the xml
     */
    public static INrml fromOriginalXml(final XmlObject xmlObject) {
        return new NrmlXmlImpl(xmlObject);
    }
}
