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

package org.n52.gfz.riesgos.formats.quakeml;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.util.StringUtils;

import java.io.IOException;

public interface ICommonTestQuakeMLXmlTestFunctions {

    default XmlObject readOriginalOneFeature() throws IOException, XmlException {
        final String content = StringUtils.readFromResourceFile("org/n52/gfz/riesgos/formats/quakeml_from_original_quakeledger_one_feature.xml");
        return XmlObject.Factory.parse(content);
    }

    default XmlObject readValidatedOneFeature() throws IOException, XmlException {
        final String content = StringUtils.readFromResourceFile("org/n52/gfz/riesgos/formats/quakeml_validated_one_feature.xml");
        return XmlObject.Factory.parse(content);
    }
}
