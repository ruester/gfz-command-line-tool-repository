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
 */

package org.n52.gfz.riesgos.formats.shakemap.binding;

import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.formats.shakemap.IShakemap;
import org.n52.gfz.riesgos.formats.shakemap.Shakemap;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;

/**
 * Binding class for Shakemaps (XML).
 */
public final class ShakemapXmlDataBinding extends GenericXMLDataBinding {

    private static final long serialVersionUID = -2049159915689920061L;

    /**
     * Creates a new shakemap binding object.
     * @param shakemap internal used xml data structure
     */
    private ShakemapXmlDataBinding(final XmlObject shakemap) {
        super(shakemap);
    }

    /**
     *
     * @return returns the payload bound to an IShakemap interface
     */
    public IShakemap getPayloadShakemap() {
        return Shakemap.fromOriginalXml(getPayload());
    }

    /**
     * Creates a new shakemap binding from a xml object.
     * @param shakemap xml with the data of the shakemap
     * @return ShakemapXmlDataBinding
     */
    public static ShakemapXmlDataBinding fromXml(final XmlObject shakemap) {
        return new ShakemapXmlDataBinding(shakemap);
    }
}
