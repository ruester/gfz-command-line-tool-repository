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

import javax.xml.namespace.QName;

/**
 * Interface to provide the QNames in one single place.
 */
public interface INrmlQNames {

    /**
     * Namespace for the nrml QNames.
     */
    String NS = "http://openquake.org/xmlns/nrml/0.5";

    /**
     * Prefix for the nrml QNames.
     */
    String PREFIX = "nrml";

    /**
     * Helper method to create the QNames for accessing childrens.
     * @param localName localName for the element
     * @return QName in NS and with the prefix.
     */
    static QName createNrmQName(final String localName) {
        return new QName(NS, localName, PREFIX);
    }

    /**
     * QName for asset.
     */
    QName ASSET = createNrmQName("asset");

    /**
     * QName for assets.
     */
    QName ASSETS = createNrmQName("assets");

    /**
     * QName for category.
     */
    QName CATEGORY = new QName("category");

    /**
     * QName for conversions.
     */
    QName CONVERSIONS = createNrmQName("conversions");

    /**
     * QName for cost.
     */
    QName COST = createNrmQName("cost");

    /**
     * QName for costs.
     */
    QName COSTS = createNrmQName("costs");

    /**
     * QName for costType.
     */
    QName COST_TYPE = createNrmQName("costType");

    /**
     * QName for costTypes.
     */
    QName COST_TYPES = createNrmQName("costTypes");

    /**
     * QName for description.
     */
    QName DESCRIPTION = createNrmQName("description");

    /**
     * QName for exposureModel.
     */
    QName EXPOSURE_MODEL = createNrmQName("exposureModel");

    /**
     * QName for id.
     */
    QName ID = new QName("id");

    /**
     * QName for lat.
     */
    QName LAT = new QName("lat");

    /**
     * QName for location.
     */
    QName LOCATION = createNrmQName("location");

    /**
     * QName for lon.
     */
    QName LON = new QName("lon");

    /**
     * QName for name.
     */
    QName NAME = new QName("name");

    /**
     * QName for nrml.
     */
    QName NRML = createNrmQName("nrml");

    /**
     * QName for number.
     */
    QName NUMBER = new QName("number");

    /**
     * QName for occupancies.
     */
    QName OCCUPANCIES = createNrmQName("occupancies");

    /**
     * QName for occupancy.
     */
    QName OCCUPANCY = createNrmQName("occupancy");

    /**
     * QName for the occupants.
     */
    QName OCCUPANTS = new QName("occupants");

    /**
     * QName for the period.
     */
    QName PERIOD = new QName("period");

    /**
     * QName for taxonomy.
     */
    QName TAXONOMY = new QName("taxonomy");

    /**
     * QName for taxonomySource.
     */
    QName TAXONOMY_SOURCE = new QName("taxonomySource");

    /**
     * QName for type.
     */
    QName TYPE = new QName("type");

    /**
     * QName for unit.
     */
    QName UNIT = new QName("unit");

    /**
     * QName for value.
     */
    QName VALUE = new QName("value");
}
