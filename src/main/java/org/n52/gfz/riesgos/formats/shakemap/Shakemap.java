/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 *
 *
 */

package org.n52.gfz.riesgos.formats.shakemap;

import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.formats.shakemap.impl.ShakemapXmlImpl;

/**
 * Static Factory class
 */
public class Shakemap {

    private Shakemap() {
        // static class
    }

    /**
     * Creates a new Shakemap object
     * @param xmlObject xml with the data of the shakemap
     * @return IShakemap
     */
    public static IShakemap fromOriginalXml(final XmlObject xmlObject) {
        return new ShakemapXmlImpl(xmlObject);
    }
}
