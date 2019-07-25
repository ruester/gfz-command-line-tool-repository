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
import org.n52.gfz.riesgos.formats.quakeml.impl.QuakeMLValidatedXmlImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.List;
import java.util.Optional;

/**
 * Implementation for IQuakeML
 * Provides static factory methods fromXyz
 * and the implementations for the format conversion.
 */
public final class QuakeML implements IQuakeML {

    /**
     * Internal used data provider for the implementation
     * specific details.
     */
    private final IQuakeMLDataProvider dataProvider;

    /*
     * Constructor
     * Use the from... methods to construct the objects
     */

    /**
     * Constructor with a specific data provider implementation.
     * @param aDataProvider data provider (specific to the format)
     */
    private QuakeML(final IQuakeMLDataProvider aDataProvider) {
        this.dataProvider = aDataProvider;
    }

    /**
     * Returns the list of events.
     * @return list of events.
     */
    @Override
    public List<IQuakeMLEvent> getEvents() {
        return dataProvider.getEvents();
    }

    /**
     *
     * @return returns the public id
     */
    @Override
    public Optional<String> getPublicId() {
        return dataProvider.getPublicId();
    }

    /**
     * Converts the quakeml to an xml object (original, non valid
     * format according to the xsd).
     * @return original xml quakeml format
     */
    @Override
    public XmlObject toOriginalXmlObject() {
        return QuakeMLOriginalXmlImpl.convertToOriginalXml(dataProvider);
    }

    /**
     * Converts the quakeml to an xml object (valid one).
     * @return validated xml quakeml format
     */

    @Override
    public XmlObject toValidatedXmlObject() {
        return QuakeMLValidatedXmlImpl.convertToValidatedXml(dataProvider);
    }

    /**
     * Converts the quakeml to a feature collection.
     * @return feature collcetion of the quakeml data
     */
    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature>
    toSimpleFeatureCollection() {
        return QuakeMLSimpleFeatureCollectionImpl
                .convertToSimpleFeatureCollection(dataProvider);
    }

    /**
     * Constructs the object from an xml object
     * (the one from the original quakeledger,
     * that is not valid according to the schema).
     * @param xmlObject quakeml xml representation
     * @return IQuakeML object
     * @throws ConvertFormatException may throw a ConvertFormatException
     */
    public static IQuakeML fromOriginalXml(final XmlObject xmlObject)
            throws ConvertFormatException {
        return new QuakeML(new QuakeMLOriginalXmlImpl(xmlObject));
    }

    /**
     * Constructs the object form an xml object
     * (the one that validates against the schema).
     * @param xmlObject quakeml
     * @return IQuakeML Object
     * @throws ConvertFormatException may throw a ConvertFormatException
     */
    public static IQuakeML fromValidatedXml(final XmlObject xmlObject)
            throws ConvertFormatException {
        return new QuakeML(new QuakeMLValidatedXmlImpl(xmlObject));
    }

    /**
     * Constructs the object from a simple feature collection.
     * @param featureCollection quakeml feature collection implementation
     * @return IQuakeML object
     */
    public static IQuakeML fromFeatureCollection(
            final FeatureCollection<SimpleFeatureType, SimpleFeature>
                    featureCollection) {
        return new QuakeML(
                new QuakeMLSimpleFeatureCollectionImpl(featureCollection));
    }

}
