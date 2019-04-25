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

package org.n52.gfz.riesgos.convertformats;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertFormat;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

public class TestFeatureCollectionToQuakeMLConverter {

    @Test
    public void testOneFeatureQuakeML() {
        final SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
        typeBuilder.setName("location");
        typeBuilder.setCRS(DefaultGeographicCRS.WGS84);

        typeBuilder.add("the_geom", Point.class);

        for(final String fieldToAdd : Arrays.asList(
                "preferredOriginID",
                "preferredMagnitudeID",
                "type",
                "description.text",
                "origin.publicID",
                "origin.time.value",
                "origin.time.uncertainty",
                "origin.latitude.uncertainty",
                "origin.longitude.uncertainty",
                "origin.depth.value",
                "origin.depth.uncertainty",
                "origin.depthType",
                "origin.timeFixed",
                "origin.epicenterFixed",
                "origin.referenceSystemID",
                "origin.type",
                "origin.creationInfo.value",
                "origin.quality.azimuthalGap",
                "origin.quality.minimumDistance",
                "origin.quality.maximumDistance",
                "origin.quality.usedPhaseCount",
                "origin.quality.usedStationCount",
                "origin.quality.standardError",
                "origin.evaluationMode",
                "origin.evaluationStatus",
                "originUncertainty.horizontalUncertainty",
                "originUncertainty.minHorizontalUncertainty",
                "originUncertainty.maxHorizontalUncertainty",
                "originUncertainty.azimuthMaxHorizontalUncertainty",
                "magnitude.publicID",
                "magnitude.mag.value",
                "magnitude.mag.uncertainty",
                "magnitude.type",
                "magnitude.evaluationStatus",
                "magnitude.originID",
                "magnitude.stationCount",
                "magnitude.creationInfo.value",
                "focalMechanism.publicID",
                "focalMechanism.nodalPlanes.nodalPlane1.strike.value",
                "focalMechanism.nodalPlanes.nodalPlane1.strike.uncertainty",
                "focalMechanism.nodalPlanes.nodalPlane1.dip.value",
                "focalMechanism.nodalPlanes.nodalPlane1.dip.uncertainty",
                "focalMechanism.nodalPlanes.nodalPlane1.rake.value",
                "focalMechanism.nodalPlanes.nodalPlane1.rake.uncertainty",
                "focalMechanism.nodalPlanes.preferredPlane",
                "amplitude.publicID",
                "amplitude.type",
                "amplitude.genericAmplitude.value"
        )) {
            typeBuilder.add(fieldToAdd, String.class);
        }

        final SimpleFeatureType type = typeBuilder.buildFeatureType();

        final SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(type);

        final double x = -71.49875;
        final double y = -30.9227;

        final Point point = JTSFactoryFinder.getGeometryFactory().createPoint(new Coordinate(x, y));

        featureBuilder.set("the_geom", point);
        featureBuilder.set("preferredOriginID", "84945");
        featureBuilder.set("preferredMagnitudeID", "84945");
        featureBuilder.set("type", "earthquake");
        featureBuilder.set("description.text", "stochastic");
        featureBuilder.set("origin.publicID", "84945");
        featureBuilder.set("origin.time.value", "16773-01-01T00:00:00.000000Z");
        featureBuilder.set("origin.time.uncertainty", "nan");
        featureBuilder.set("origin.latitude.uncertainty", "nan");
        featureBuilder.set("origin.longitude.uncertainty", "nan");
        featureBuilder.set("origin.depth.value", "34.75117");
        featureBuilder.set("origin.depth.uncertainty", "nan");
        featureBuilder.set("origin.creationInfo.value", "GFZ");
        featureBuilder.set("originUncertainty.horizontalUncertainty", "nan");
        featureBuilder.set("originUncertainty.minHorizontalUncertainty", "nan");
        featureBuilder.set("originUncertainty.maxHorizontalUncertainty", "nan");
        featureBuilder.set("originUncertainty.azimuthMaxHorizontalUncertainty", "nan");
        featureBuilder.set("magnitude.publicID", "84945");
        featureBuilder.set("magnitude.mag.value", "8.35");
        featureBuilder.set("magnitude.mag.uncertainty", "nan");
        featureBuilder.set("magnitude.type", "MW");
        featureBuilder.set("magnitude.creationInfo.value", "GFZ");
        featureBuilder.set("focalMechanism.publicID", "84945");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.strike.value", "7.310981");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.strike.uncertainty", "nan");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.dip.value", "16.352970000000003");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.dip.uncertainty", "nan");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.rake.value", "90.0");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.rake.uncertainty", "nan");
        featureBuilder.set("focalMechanism.nodalPlanes.preferredPlane", "nodalPlane1");


        final SimpleFeature feature = featureBuilder.buildFeature("84945");
        final DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
        featureCollection.add(feature);

        try {
            final XmlObject result = new FeatureCollectionToQuakeMLConverter().convert(featureCollection);


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

            final XmlObject expectedResult = XmlObject.Factory.parse(xmlRawContent);

            final XmlOptions options = new XmlOptions();
            options.setSavePrettyPrint();

            assertEquals("The xml contents are the same", expectedResult.xmlText(options), result.xmlText(options));
        } catch(final XmlException exception) {
            fail("There should be no exception on parsing xml");
        }
    }
}
