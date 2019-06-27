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
import org.n52.gfz.riesgos.formats.nrml.INrmlAsset;
import org.n52.gfz.riesgos.formats.nrml.INrmlAssets;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the INrmlAssets interface.
 */
public class NrmlAssetsImpl implements INrmlAssets, INrmlQNames {

    /**
     * Xml element with the assets tag.
     */
    private final XmlObject xml;

    /**
     * Creates a new instance by giving the xml element with the
     * assets tag.
     * @param aXml xml element with assets tag
     */
    NrmlAssetsImpl(final XmlObject aXml) {
        this.xml = aXml;
    }

    /**
     *
     * @return list of asset entries
     */
    @Override
    public List<INrmlAsset> getAssetList() {
        return Stream.of(xml.selectChildren(ASSET)).map(
                NrmlAssetImpl::new).collect(Collectors.toList());
    }
}
