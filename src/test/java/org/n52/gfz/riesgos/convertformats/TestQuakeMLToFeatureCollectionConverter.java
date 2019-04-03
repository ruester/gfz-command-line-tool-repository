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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.junit.Test;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class TestQuakeMLToFeatureCollectionConverter {

    @Test
    public void testOneFeature() {
        final String xmlRawContent =
                "<eventParameters namespace=\"http://quakeml.org/xmlns/quakeml/1.2\">\n" +
                "  <event publicID=\"84945\">\n" +
                "    <preferredOriginID>84945</preferredOriginID>\n" +
                "    <preferredMagnitudeID>84945</preferredMagnitudeID>\n" +
                "    <type>earthquake</type>\n" +
                "    <description>\n" +
                "      <text>stochastic</text>\n" +
                "    </description>\n" +
                "    <origin publicID=\"84945\">\n" +
                "      <time>\n" +
                "        <value>16773-01-01T00:00:00.000000Z</value>\n" +
                "        <uncertainty>nan</uncertainty>\n" +
                "      </time>\n" +
                "      <latitude>\n" +
                "        <value>-30.9227</value>\n" +
                "        <uncertainty>nan</uncertainty>\n" +
                "      </latitude>\n" +
                "      <longitude>\n" +
                "        <value>-71.49875</value>\n" +
                "        <uncertainty>nan</uncertainty>\n" +
                "      </longitude>\n" +
                "      <depth>\n" +
                "        <value>34.75117</value>\n" +
                "        <uncertainty>nan</uncertainty>\n" +
                "      </depth>\n" +
                "      <creationInfo>\n" +
                "        <value>GFZ</value>\n" +
                "      </creationInfo>\n" +
                "    </origin>\n" +
                "    <originUncertainty>\n" +
                "      <horizontalUncertainty>nan</horizontalUncertainty>\n" +
                "      <minHorizontalUncertainty>nan</minHorizontalUncertainty>\n" +
                "      <maxHorizontalUncertainty>nan</maxHorizontalUncertainty>\n" +
                "      <azimuthMaxHorizontalUncertainty>nan</azimuthMaxHorizontalUncertainty>\n" +
                "    </originUncertainty>\n" +
                "    <magnitude publicID=\"84945\">\n" +
                "      <mag>\n" +
                "        <value>8.35</value>\n" +
                "        <uncertainty>nan</uncertainty>\n" +
                "      </mag>\n" +
                "      <type>MW</type>\n" +
                "      <creationInfo>\n" +
                "        <value>GFZ</value>\n" +
                "      </creationInfo>\n" +
                "    </magnitude>\n" +
                "    <focalMechanism publicID=\"84945\">\n" +
                "      <nodalPlanes>\n" +
                "        <nodalPlane1>\n" +
                "          <strike>\n" +
                "            <value>7.310981</value>\n" +
                "            <uncertainty>nan</uncertainty>\n" +
                "          </strike>\n" +
                "          <dip>\n" +
                "            <value>16.352970000000003</value>\n" +
                "            <uncertainty>nan</uncertainty>\n" +
                "          </dip>\n" +
                "          <rake>\n" +
                "            <value>90.0</value>\n" +
                "            <uncertainty>nan</uncertainty>\n" +
                "          </rake>\n" +
                "        </nodalPlane1>\n" +
                "        <preferredPlane>nodalPlane1</preferredPlane>\n" +
                "      </nodalPlanes>\n" +
                "    </focalMechanism>\n" +
                "  </event>\n" +
                "</eventParameters>";

        try {
            final XmlObject xmlContent = XmlObject.Factory.parse(xmlRawContent);

            final FeatureCollection<SimpleFeatureType, SimpleFeature> result = new QuakeMLToFeatureCollectionConverter().convert    (xmlContent);
            final FeatureIterator<SimpleFeature> iterator = result.features();

            assertTrue("There is one element", iterator.hasNext());
            final SimpleFeature simpleFeature = iterator.next();
            assertTrue("This feature is not null", simpleFeature != null);

            assertFalse("There is no other feature", iterator.hasNext());

            assertEquals("The preferredOriginID is as expected", "84945", simpleFeature.getAttribute("preferredOriginID"));
            assertEquals("The preferredMagnitudeID is as expected", "84945", simpleFeature.getAttribute("preferredMagnitudeID"));
            assertEquals("The type is as expected", "earthquake", simpleFeature.getAttribute("type"));
            assertEquals("The description.text is as excepted", "stochastic", simpleFeature.getAttribute("description.text"));
            assertEquals("The origin.publicID is as excepted", "84945", simpleFeature.getAttribute("origin.publicID"));
            assertEquals("The origin.time.value is as expected", "16773-01-01T00:00:00.000000Z", simpleFeature.getAttribute("origin.time.value"));
            assertEquals("The origin.time.uncertainty is as expected", "nan", simpleFeature.getAttribute("origin.time.uncertainty"));
            assertEquals("The origin.depth.value is as excepted", "34.75117", simpleFeature.getAttribute("origin.depth.value"));
            assertEquals("The origin.depth.uncertainty is as expected", "nan", simpleFeature.getAttribute("origin.depth.uncertainty"));
            // it is not specified in the data input
            assertEquals("The origin.depthType is as expected", null, simpleFeature.getAttribute("origin.depthType"));
            assertEquals("The origin.timeFixed is as expected", null, simpleFeature.getAttribute("origin.timeFixed"));
            assertEquals("The origin.epicenterFixed is as expected", null, simpleFeature.getAttribute("origin.epicenterFixed"));
            assertEquals("The origin.referenceSystemID is as expected", null, simpleFeature.getAttribute("origin.referenceSystemID"));
            assertEquals("The origin.type is as expected", null, simpleFeature.getAttribute("origin.type"));
            assertEquals("The origin.creationInfo.value is as expected", "GFZ", simpleFeature.getAttribute("origin.creationInfo.value"));
            assertEquals("The origin.quality.azimuthalGap is as expected", null, simpleFeature.getAttribute("origin.quality.azimuthalGap"));
            assertEquals("The origin.quality.minimumDistance is as expected", null, simpleFeature.getAttribute("origin.quality.minimumDistance"));
            assertEquals("The origin.quality.maximumDistance is as expected", null, simpleFeature.getAttribute("origin.quality.maximumDistance"));
            assertEquals("The origin.quality.usedPhaseCount is as expected", null, simpleFeature.getAttribute("origin.quality.usedPhaseCount"));
            assertEquals("The origin.quality.usedStationCount is as expected", null, simpleFeature.getAttribute("origin.quality.usedStationCount"));
            assertEquals("The origin.quality.standardError is as expected", null, simpleFeature.getAttribute("origin.quality.standardError"));
            assertEquals("The origin.evaluationMode is as expected", null, simpleFeature.getAttribute("origin.evaluationMode"));
            assertEquals("The origin.evaluationStatus is as expected", null, simpleFeature.getAttribute("origin.evaluationStatus"));
            assertEquals("The originUncertainty.horizontalUncertainty is as expected", "nan", simpleFeature.getAttribute("originUncertainty.horizontalUncertainty"));
            assertEquals("The originUncertainty.minHorizontalUncertainty is as expected", "nan", simpleFeature.getAttribute("originUncertainty.minHorizontalUncertainty"));
            assertEquals("The originUncertainty.maxHorizontalUncertainty is as expected", "nan", simpleFeature.getAttribute("originUncertainty.maxHorizontalUncertainty"));
            assertEquals("The originUncertainty.azimuthMaxHorizontalUncertainty is as expected", "nan", simpleFeature.getAttribute("originUncertainty.azimuthMaxHorizontalUncertainty"));
            assertEquals("The magnitude.publicID is as expected", "84945", simpleFeature.getAttribute("magnitude.publicID"));
            assertEquals("The magnitude.mag.value is as expected", "8.35", simpleFeature.getAttribute("magnitude.mag.value"));
            assertEquals("The magnitude.mag.uncertainty is as expected", "nan", simpleFeature.getAttribute("magnitude.mag.uncertainty"));
            assertEquals("The magnitude.type is as expected", "MW", simpleFeature.getAttribute("magnitude.type"));
            assertEquals("The magnitude.evaluationStatus is as expected", null, simpleFeature.getAttribute("magnitude.evaluationStatus"));
            assertEquals("The magnitude.originID is as expected", null, simpleFeature.getAttribute("magnitude.originID"));
            assertEquals("The magnitude.stationCount is as expected", null, simpleFeature.getAttribute("magnitude.stationCount"));
            assertEquals("The magnitude.creationInfo.value is as expected", "GFZ", simpleFeature.getAttribute("magnitude.creationInfo.value"));
            assertEquals("The focalMechanism.publicID, is as expected", "84945", simpleFeature.getAttribute("focalMechanism.publicID"));
            assertEquals("The focalMechanism.nodalPlanes.nodalPlane1.strike.value is as expected", "7.310981", simpleFeature.getAttribute("focalMechanism.nodalPlanes.nodalPlane1.strike.value"));
            assertEquals("The focalMechanism.nodalPlanes.nodalPlane1.strike.uncertainty is as expected", "nan", simpleFeature.getAttribute("focalMechanism.nodalPlanes.nodalPlane1.strike.uncertainty"));
            assertEquals("The focalMechanism.nodalPlanes.nodalPlane1.dip.value is as expected", "16.352970000000003", simpleFeature.getAttribute("focalMechanism.nodalPlanes.nodalPlane1.dip.value"));
            assertEquals("The focalMechanism.nodalPlanes.nodalPlane1.dip.uncertainty is as expected", "nan", simpleFeature.getAttribute("focalMechanism.nodalPlanes.nodalPlane1.dip.uncertainty"));
            assertEquals("The focalMechanism.nodalPlanes.nodalPlane1.rake.value is as expected", "90.0", simpleFeature.getAttribute("focalMechanism.nodalPlanes.nodalPlane1.rake.value"));
            assertEquals("The focalMechanism.nodalPlanes.nodalPlane1.rake.uncertainty is as expected", "nan", simpleFeature.getAttribute("focalMechanism.nodalPlanes.nodalPlane1.rake.uncertainty"));
            assertEquals("The focalMechanism.nodalPlanes.preferredPlane is as expected", "nodalPlane1", simpleFeature.getAttribute("focalMechanism.nodalPlanes.preferredPlane"));
            assertEquals("The amplitude.publicID is as expected", null, simpleFeature.getAttribute("amplitude.publicID"));
            assertEquals("The amplitude.type is as expected", null, simpleFeature.getAttribute("amplitude.type"));
            assertEquals("The amplitude.genericAmplitude.value is as expected", null, simpleFeature.getAttribute("amplitude.genericAmplitude.value"));

            final Geometry geom = ((Geometry) simpleFeature.getDefaultGeometry());
            final Coordinate coordinate = geom.getCoordinate();
            final double x = coordinate.x;
            final double y = coordinate.y;

            // longitude
            assertTrue("The x coordinate is near to expected", Math.abs(x - (-71.49875)) < 0.01);
            // latitude
            assertTrue("The y coordinate is near to expected", Math.abs(y - (-30.9227)) < 0.01);


        } catch (final XmlException e) {
            fail("There should be no XmlException");
        } catch(final ConvertFormatException e) {
            fail("There should be no exception on converting");
        }
    }
}
