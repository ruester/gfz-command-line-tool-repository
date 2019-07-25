package org.n52.gfz.riesgos.formats.quakeml;

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

import org.apache.xmlbeans.XmlObject;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Extended interface for QuakeML that also contains some conversion methods.
 */
public interface IQuakeML extends IQuakeMLDataProvider {

    /**
     * Transforms it to the "original" quakeml data,
     * that was used before carring about the validation
     * against the xsd file.
     * @return xml representation of quakeml
     * (original one from quakeledger process;
     * is not valid according to the xml schema)
     */
    XmlObject toOriginalXmlObject();

    /**
     *
     * Transforms it to the validated xml data.
     * @return xml representation of quakeml
     * that is valid according to the xsd
     */
    XmlObject toValidatedXmlObject();

    /**
     * Transforms it to a feature collection.
     *
     * @return simple feature collection representation of quakeml
     */
    FeatureCollection<SimpleFeatureType, SimpleFeature>
    toSimpleFeatureCollection();

}
