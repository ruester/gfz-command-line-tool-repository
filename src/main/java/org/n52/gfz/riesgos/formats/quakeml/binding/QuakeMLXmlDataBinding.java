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
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeML;
import org.n52.gfz.riesgos.formats.quakeml.QuakeML;
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

    /*
     * The class uses the validated xml as payload object
     */
    private QuakeMLXmlDataBinding(final XmlObject validatedXml) {
        super(validatedXml);
    }

    /**
     * get the quakeML as payload
     * @return IQuakeML
     * @throws ConvertFormatException may throws an ConvertFormatException (if there are no tags for the events)
     */
    public IQuakeML getPayloadQuakeML() throws ConvertFormatException {
        return QuakeML.fromValidatedXml(getPayload());
    }

    /**
     * returns the validated xml that is used as payload
     * @return validated quakeml xml
     */
    public XmlObject getPayloadValidatedXml() {
        return getPayload();
    }

    /**
     * Creates the binding from the validated xml
     * @param validatedXml validated quakeml xml
     * @return QuakeMLXmlDataBinding
     */
    public static QuakeMLXmlDataBinding fromValidatedXml(final XmlObject validatedXml) {
        return new QuakeMLXmlDataBinding(validatedXml);
    }

    /**
     * Creates the binding for any QuakeML-Implementation
     * @param quakeML any quakeml implementation
     * @return QuakeMLXmlDataBinding
     */
    public static QuakeMLXmlDataBinding fromQuakeML(final IQuakeML quakeML) {
        return new QuakeMLXmlDataBinding(quakeML.toValidatedXmlObject());
    }

}
