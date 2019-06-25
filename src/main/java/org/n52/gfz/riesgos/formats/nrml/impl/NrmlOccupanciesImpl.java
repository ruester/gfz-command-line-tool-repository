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
import org.n52.gfz.riesgos.formats.nrml.INrmlOccupancies;
import org.n52.gfz.riesgos.formats.nrml.INrmlOccupancy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the INrmlOccupancies interface.
 */
public class NrmlOccupanciesImpl implements INrmlOccupancies, INrmlQNames {

    /**
     * The xml element with the occupancies entry.
     */
    private final XmlObject xml;

    /**
     * Creates a new NrmlOccupanciesImpl instance by using
     * the xml element with the occupancies entry.
     * @param aXml xml element with the occupancies entry.
     */
    NrmlOccupanciesImpl(final XmlObject aXml) {
        this.xml = aXml;
    }

    /**
     *
     * @return list with occupancy entries
     */
    @Override
    public List<INrmlOccupancy> getOccupancyList() {
        return Stream.of(xml.selectChildren(OCCUPANCY)).map(
                NrmlOccupancyImpl::new).collect(Collectors.toList());
    }

}
