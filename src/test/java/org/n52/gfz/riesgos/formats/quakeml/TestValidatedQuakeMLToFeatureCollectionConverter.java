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
 */

package org.n52.gfz.riesgos.formats.quakeml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.gml3.ApplicationSchemaConfiguration;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.junit.Test;
import org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.formats.quakeml.binding.QuakeMLXmlDataBinding;
import org.n52.gfz.riesgos.formats.quakeml.generators.QuakeMLGML3Generator;
import org.n52.gfz.riesgos.formats.quakeml.parsers.QuakeMLGML3Parser;
import org.n52.gfz.riesgos.util.StringUtils;
import org.n52.wps.io.GTHelper;
import org.n52.wps.io.SchemaRepository;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.datahandler.generator.GML3BasicGenerator;
import org.n52.wps.webapp.api.FormatEntry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.n52.gfz.riesgos.configuration.parse.defaultformats.DefaultFormatOption.GML;

/**
 * This are the tests for the conversion of the quakeml xml to a feature collection
 */
public class TestValidatedQuakeMLToFeatureCollectionConverter {

    /**
     * Test with one feature and the new (improved) xml that was validates against the schema.
     */
    @Test
    public void testOneFeatureValidatedQuakeledgerFormat() {


        String xmlRawContent = null;
        try {
            xmlRawContent = StringUtils.readFromResourceFile("org/n52/gfz/riesgos/formats/quakeml_validated_one_feature.xml");
        } catch(final IOException ioException) {
            fail("There should be no io exception on reading the input file");
        }
        try {
            final XmlObject xmlContent = XmlObject.Factory.parse(xmlRawContent);

            final FeatureCollection<SimpleFeatureType, SimpleFeature> result = QuakeML.fromValidatedXml(xmlContent).toSimpleFeatureCollection();
            final FeatureIterator<SimpleFeature> iterator = result.features();

            assertTrue("There is one element", iterator.hasNext());
            final SimpleFeature simpleFeature = iterator.next();
            assertNotNull("This feature is not null", simpleFeature);

            assertFalse("There is no other feature", iterator.hasNext());

            assertEquals("The preferredOriginID is as expected", "quakeml:quakeledger/84945", simpleFeature.getAttribute("preferredOriginID"));
            assertEquals("The preferredMagnitudeID is as expected", "quakeml:quakeledger/84945", simpleFeature.getAttribute("preferredMagnitudeID"));
            assertEquals("The type is as expected", "earthquake", simpleFeature.getAttribute("type"));
            assertEquals("The description.text is as excepted", "stochastic", simpleFeature.getAttribute("description.text"));
            assertEquals("The origin.publicID is as excepted", "quakeml:quakeledger/84945", simpleFeature.getAttribute("origin.publicID"));
            assertEquals("The origin.time.value is as expected", "16773-01-01T00:00:00.000000Z", simpleFeature.getAttribute("origin.time.value"));
            assertNull("The origin.time.uncertainty is as expected", simpleFeature.getAttribute("origin.time.uncertainty"));
            assertEquals("The origin.depth.value is as excepted", "34.75117", simpleFeature.getAttribute("origin.depth.value"));
            assertNull("The origin.depth.uncertainty is as expected", simpleFeature.getAttribute("origin.depth.uncertainty"));
            // it is not specified in the data input
            assertNull("The origin.depthType is as expected", simpleFeature.getAttribute("origin.depthType"));
            assertNull("The origin.timeFixed is as expected", simpleFeature.getAttribute("origin.timeFixed"));
            assertNull("The origin.epicenterFixed is as expected", simpleFeature.getAttribute("origin.epicenterFixed"));
            assertNull("The origin.referenceSystemID is as expected", simpleFeature.getAttribute("origin.referenceSystemID"));
            assertNull("The origin.type is as expected", simpleFeature.getAttribute("origin.type"));
            assertEquals("The origin.creationInfo.value is as expected", "GFZ", simpleFeature.getAttribute("origin.creationInfo.value"));
            assertNull("The origin.quality.azimuthalGap is as expected", simpleFeature.getAttribute("origin.quality.azimuthalGap"));
            assertNull("The origin.quality.minimumDistance is as expected", simpleFeature.getAttribute("origin.quality.minimumDistance"));
            assertNull("The origin.quality.maximumDistance is as expected", simpleFeature.getAttribute("origin.quality.maximumDistance"));
            assertNull("The origin.quality.usedPhaseCount is as expected", simpleFeature.getAttribute("origin.quality.usedPhaseCount"));
            assertNull("The origin.quality.usedStationCount is as expected", simpleFeature.getAttribute("origin.quality.usedStationCount"));
            assertNull("The origin.quality.standardError is as expected", simpleFeature.getAttribute("origin.quality.standardError"));
            assertNull("The origin.evaluationMode is as expected", simpleFeature.getAttribute("origin.evaluationMode"));
            assertNull("The origin.evaluationStatus is as expected", simpleFeature.getAttribute("origin.evaluationStatus"));
            assertNull("The originUncertainty.horizontalUncertainty is as expected", simpleFeature.getAttribute("originUncertainty.horizontalUncertainty"));
            assertNull("The originUncertainty.minHorizontalUncertainty is as expected", simpleFeature.getAttribute("originUncertainty.minHorizontalUncertainty"));
            assertNull("The originUncertainty.maxHorizontalUncertainty is as expected", simpleFeature.getAttribute("originUncertainty.maxHorizontalUncertainty"));
            assertNull("The originUncertainty.azimuthMaxHorizontalUncertainty is as expected", simpleFeature.getAttribute("originUncertainty.azimuthMaxHorizontalUncertainty"));
            assertEquals("The magnitude.publicID is as expected", "quakeml:quakeledger/84945", simpleFeature.getAttribute("magnitude.publicID"));
            assertEquals("The magnitude.mag.value is as expected", "8.35", simpleFeature.getAttribute("magnitude.mag.value"));
            assertNull("The magnitude.mag.uncertainty is as expected", simpleFeature.getAttribute("magnitude.mag.uncertainty"));
            assertEquals("The magnitude.type is as expected", "MW", simpleFeature.getAttribute("magnitude.type"));
            assertNull("The magnitude.evaluationStatus is as expected", simpleFeature.getAttribute("magnitude.evaluationStatus"));
            assertNull("The magnitude.originID is as expected", simpleFeature.getAttribute("magnitude.originID"));
            assertNull("The magnitude.stationCount is as expected", simpleFeature.getAttribute("magnitude.stationCount"));
            assertEquals("The magnitude.creationInfo.value is as expected", "GFZ", simpleFeature.getAttribute("magnitude.creationInfo.value"));
            assertEquals("The focalMechanism.publicID is as expected", "quakeml:quakeledger/84945", simpleFeature.getAttribute("focalMechanism.publicID"));
            assertEquals("The focalMechanism.nodalPlanes.nodalPlane1.strike.value is as expected", "7.310981", simpleFeature.getAttribute("focalMechanism.nodalPlanes.nodalPlane1.strike.value"));
            assertNull("The focalMechanism.nodalPlanes.nodalPlane1.strike.uncertainty is as expected", simpleFeature.getAttribute("focalMechanism.nodalPlanes.nodalPlane1.strike.uncertainty"));
            assertEquals("The focalMechanism.nodalPlanes.nodalPlane1.dip.value is as expected", "16.352970000000003", simpleFeature.getAttribute("focalMechanism.nodalPlanes.nodalPlane1.dip.value"));
            assertNull("The focalMechanism.nodalPlanes.nodalPlane1.dip.uncertainty is as expected", simpleFeature.getAttribute("focalMechanism.nodalPlanes.nodalPlane1.dip.uncertainty"));
            assertEquals("The focalMechanism.nodalPlanes.nodalPlane1.rake.value is as expected", "90.0", simpleFeature.getAttribute("focalMechanism.nodalPlanes.nodalPlane1.rake.value"));
            assertNull("The focalMechanism.nodalPlanes.nodalPlane1.rake.uncertainty is as expected", simpleFeature.getAttribute("focalMechanism.nodalPlanes.nodalPlane1.rake.uncertainty"));
            assertEquals("The focalMechanism.nodalPlanes.preferredPlane is as expected", "nodalPlane1", simpleFeature.getAttribute("focalMechanism.nodalPlanes.preferredPlane"));
            assertNull("The amplitude.publicID is as expected", simpleFeature.getAttribute("amplitude.publicID"));
            assertNull("The amplitude.type is as expected", simpleFeature.getAttribute("amplitude.type"));
            assertNull("The amplitude.genericAmplitude.value is as expected", simpleFeature.getAttribute("amplitude.genericAmplitude.value"));


            final Geometry geom = ((Geometry) simpleFeature.getDefaultGeometry());
            final Coordinate coordinate = geom.getCoordinate();
            final double x = coordinate.x;
            final double y = coordinate.y;

            // longitude
            assertTrue("The x coordinate is near to expected", Math.abs(x - (-71.49875)) < 0.01);
            // latitude
            assertTrue("The y coordinate is near to expected", Math.abs(y - (-30.9227)) < 0.01);

            assertEquals("The simple feature has the right id", "quakeml:quakeledger/84945", simpleFeature.getID());

            // but because it the id can be overwritten in the gml generator
            // it is necessary to have a extra field for the public id
            assertEquals("The additional field for the public id is the same", "quakeml:quakeledger/84945", simpleFeature.getAttribute("publicID"));



        } catch (final XmlException e) {
            fail("There should be no XmlException");
        } catch(final ConvertFormatException e) {
            fail("There should be no exception on converting");
        }
    }

    /**
     * Test with some realworld geofon xml.
     */
    @Test
    public void testGeofonFeatureValidatedQuakeledgerFormat() {


        String xmlRawContent = null;
        try {
            xmlRawContent = StringUtils.readFromResourceFile("org/n52/gfz/riesgos/formats/quakeml_validated_geofon.xml");
        } catch(final IOException ioException) {
            fail("There should be no io exception on reading the input file");
        }
        try {
            final XmlObject xmlContent = XmlObject.Factory.parse(xmlRawContent);

            final FeatureCollection<SimpleFeatureType, SimpleFeature> result = QuakeML.fromValidatedXml(xmlContent).toSimpleFeatureCollection();
            final FeatureIterator<SimpleFeature> iterator = result.features();

            assertTrue("There is one element", iterator.hasNext());
            final SimpleFeature simpleFeature = iterator.next();
            assertNotNull("This feature is not null", simpleFeature);

        } catch (final XmlException e) {
            fail("There should be no XmlException");
        } catch(final ConvertFormatException e) {
            fail("There should be no exception on converting");
        }
    }


}
