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

package org.n52.gfz.riesgos.formats.nrml.binding;

import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.formats.nrml.INrml;
import org.n52.gfz.riesgos.formats.nrml.Nrml;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;

/**
 * This is the data binding class for the nrml data.
 */
public final class NrmlXmlDataBinding extends GenericXMLDataBinding {

    private static final long serialVersionUID = 198688451627521811L;

    /**
     * This is a private constructor.
     * Use the fromXml static method instead.
     * @param nrml xml with nrml
     */
    private NrmlXmlDataBinding(final XmlObject nrml) {
        super(nrml);
    }

    /**
     *
     * @return return the INrml from the payload
     */
    public INrml getPayloadNrml() {
        return Nrml.fromOriginalXml(getPayload());
    }

    /**
     * Creates a new instance of the binding class.
     * @param nrml xml with nrml data
     * @return NrmlXmlDataBinding
     */
    public static NrmlXmlDataBinding fromXml(final XmlObject nrml) {
        return new NrmlXmlDataBinding(nrml);
    }
}
