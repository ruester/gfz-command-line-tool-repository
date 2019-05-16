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

package org.n52.gfz.riesgos.convertformats;

import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.formats.quakeml.QuakeML;
import org.n52.gfz.riesgos.functioninterfaces.IConvertFormat;

/**
 * Class to convert the old QuakeML XML (non valid according to the schema) to
 * the validated quakeml xml
 */
public class OriginalQuakeMLToValidatedQuakeMLConverter implements IConvertFormat<XmlObject, XmlObject> {

    @Override
    public XmlObject convert(final XmlObject xmlObject) throws ConvertFormatException {
        return QuakeML.fromOriginalXml(xmlObject).toValidatedXmlObject();
    }
}
