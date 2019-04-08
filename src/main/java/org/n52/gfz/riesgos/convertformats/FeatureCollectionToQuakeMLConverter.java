package org.n52.gfz.riesgos.convertformats;

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
import org.n52.gfz.riesgos.formats.quakeml.IQuakeML;
import org.n52.gfz.riesgos.formats.quakeml.QuakeML;
import org.n52.gfz.riesgos.formats.quakeml.impl.QuakeMLXmlImpl;
import org.n52.gfz.riesgos.functioninterfaces.IConvertFormat;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Class to convert a simplefeature collection with quakeml data to quake ml xml
 */
public class FeatureCollectionToQuakeMLConverter implements IConvertFormat<FeatureCollection<SimpleFeatureType, SimpleFeature>, XmlObject> {
    @Override
    public XmlObject convert(FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection) {
        final IQuakeML quakeML = QuakeML.fromFeatureCollection(featureCollection);
        return QuakeMLXmlImpl.convertToXml(quakeML);
    }
}