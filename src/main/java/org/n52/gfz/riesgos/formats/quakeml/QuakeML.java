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
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.formats.quakeml.impl.QuakeMLSimpleFeatureCollectionImpl;
import org.n52.gfz.riesgos.formats.quakeml.impl.QuakeMLOriginalXmlImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.List;

/**
 * Implementation for IQuakeML
 * Provides static factory methods fromXyz
 * and the implementations for the format conversion.
 */
public class QuakeML implements IQuakeML {

    private final IQuakeMLDataProvider dataProvider;

    /*
     * Constructor
     * Use the from... methods to construct the objects
     */
    private QuakeML(final IQuakeMLDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public List<IQuakeMLEvent> getEvents() {
        return dataProvider.getEvents();
    }

    @Override
    public XmlObject toOriginalXmlObject() {
        return QuakeMLOriginalXmlImpl.convertToOriginalXml(dataProvider);
    }

    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> toSimpleFeatureCollection() {
        return QuakeMLSimpleFeatureCollectionImpl.convertToSimpleFeatureCollection(dataProvider);
    }

    /**
     * Constructs the object from an xml object
     * @param xmlObject quakeml xml representation
     * @return IQuakeML object
     * @throws ConvertFormatException may throw a ConvertFormatException
     */
    public static IQuakeML fromOriginalXml(final XmlObject xmlObject) throws ConvertFormatException {
        return new QuakeML(new QuakeMLOriginalXmlImpl(xmlObject));
    }

    /**
     * Constructs the object from a simple feature collection
     * @param featureCollection quakeml feature collection implementation
     * @return IQuakeML object
     */
    public static IQuakeML fromFeatureCollection(final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection) {
        return new QuakeML(new QuakeMLSimpleFeatureCollectionImpl(featureCollection));
    }

}