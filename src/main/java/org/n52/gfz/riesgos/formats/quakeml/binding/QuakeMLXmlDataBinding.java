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

package org.n52.gfz.riesgos.formats.quakeml.binding;

import org.apache.xmlbeans.XmlObject;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;

/**
 * The QuakeMLXmlDataBinding is built on top of the GenericXMLDataBinding.
 *
 * The xml format of the payload is the one that is valid according to the schema.
 * There is support for the old one, but this one should not be stored in this
 * binding class.
 */
public class QuakeMLXmlDataBinding extends GenericXMLDataBinding {

    private static final long serialVersionUID = 1921993767115464931L;

    /**
     * default constructor
     * @param payload XmlObject to wrap
     */
    public QuakeMLXmlDataBinding(XmlObject payload) {
        super(payload);
    }

}
