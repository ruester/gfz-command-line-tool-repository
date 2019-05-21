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

package org.n52.gfz.riesgos.formats.quakeml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.n52.gfz.riesgos.util.StringUtils;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

/**
 * This are the test cases for the transformation of the quakeml simple feature collection
 * to xml.
 * It outputs the old xml format that validates against the schema
 */
public class TestFeatureCollectionToValidatedQuakeMLConverter {

    /**
     * This tests the conversion of one feature to xml using the new quakeml format
     * that is the as the output of the improved quakeledger process.
     * This *does* match the xml schema for quakeml.
     */
    @Test
    public void testOneFeatureQuakeMLToValidatedQuakeledgerFormat() {
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
        featureBuilder.set("preferredOriginID", "quakeml:quakeledger/84945");
        featureBuilder.set("preferredMagnitudeID", "quakeml:quakeledger/84945");
        featureBuilder.set("type", "earthquake");
        featureBuilder.set("description.text", "stochastic");
        featureBuilder.set("origin.publicID", "quakeml:quakeledger/84945");
        featureBuilder.set("origin.time.value", "16773-01-01T00:00:00.000000Z");
        featureBuilder.set("origin.time.uncertainty", null);
        featureBuilder.set("origin.latitude.uncertainty", null);
        featureBuilder.set("origin.longitude.uncertainty", null);
        featureBuilder.set("origin.depth.value", "34.75117");
        featureBuilder.set("origin.depth.uncertainty", null);
        featureBuilder.set("origin.creationInfo.value", "GFZ");
        featureBuilder.set("originUncertainty.horizontalUncertainty", null);
        featureBuilder.set("originUncertainty.minHorizontalUncertainty", null);
        featureBuilder.set("originUncertainty.maxHorizontalUncertainty", null);
        featureBuilder.set("originUncertainty.azimuthMaxHorizontalUncertainty", null);
        featureBuilder.set("magnitude.publicID", "quakeml:quakeledger/84945");
        featureBuilder.set("magnitude.mag.value", "8.35");
        featureBuilder.set("magnitude.mag.uncertainty", null);
        featureBuilder.set("magnitude.type", "MW");
        featureBuilder.set("magnitude.creationInfo.value", "GFZ");
        featureBuilder.set("focalMechanism.publicID", "quakeml:quakeledger/84945");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.strike.value", "7.310981");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.strike.uncertainty", null);
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.dip.value", "16.352970000000003");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.dip.uncertainty", null);
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.rake.value", "90.0");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.rake.uncertainty", null);
        featureBuilder.set("focalMechanism.nodalPlanes.preferredPlane", "nodalPlane1");


        final SimpleFeature feature = featureBuilder.buildFeature("quakeml:quakeledger/84945");
        final DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
        featureCollection.add(feature);

        try {
            final XmlObject result = QuakeML.fromFeatureCollection(featureCollection).toValidatedXmlObject();
            final String xmlRawContent = StringUtils.readFromResourceFile("org/n52/gfz/riesgos/convertformats/quakeml_validated_one_feature.xml");

            final XmlObject expectedResult = XmlObject.Factory.parse(xmlRawContent);

            assertEquals("The xml contents are the same", expectedResult.toString(), result.toString());
        } catch(final XmlException exception) {
            fail("There should be no exception on parsing xml");
        } catch(final IOException ioException) {
            fail("There should be no exception on loading the xml content");
        }
    }

    /**
     * This tests uses also "nan" string values, that should be treated as nulls
     */
    @Test
    public void testOneFeatureQuakeMLToValidatedQuakeledgerFormatWithNaNValues() {
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
        featureBuilder.set("preferredOriginID", "quakeml:quakeledger/84945");
        featureBuilder.set("preferredMagnitudeID", "quakeml:quakeledger/84945");
        featureBuilder.set("type", "earthquake");
        featureBuilder.set("description.text", "stochastic");
        featureBuilder.set("origin.publicID", "quakeml:quakeledger/84945");
        featureBuilder.set("origin.time.value", "16773-01-01T00:00:00.000000Z");
        featureBuilder.set("origin.time.uncertainty", "nan");
        featureBuilder.set("origin.latitude.uncertainty", "NaN");
        featureBuilder.set("origin.longitude.uncertainty", "nan");
        featureBuilder.set("origin.depth.value", "34.75117");
        featureBuilder.set("origin.depth.uncertainty", "NaN");
        featureBuilder.set("origin.creationInfo.value", "GFZ");
        featureBuilder.set("originUncertainty.horizontalUncertainty", "nan");
        featureBuilder.set("originUncertainty.minHorizontalUncertainty", "NaN");
        featureBuilder.set("originUncertainty.maxHorizontalUncertainty", "nan");
        featureBuilder.set("originUncertainty.azimuthMaxHorizontalUncertainty", "NaN");
        featureBuilder.set("magnitude.publicID", "quakeml:quakeledger/84945");
        featureBuilder.set("magnitude.mag.value", "8.35");
        featureBuilder.set("magnitude.mag.uncertainty", "nan");
        featureBuilder.set("magnitude.type", "MW");
        featureBuilder.set("magnitude.creationInfo.value", "GFZ");
        featureBuilder.set("focalMechanism.publicID", "quakeml:quakeledger/84945");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.strike.value", "7.310981");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.strike.uncertainty", "NaN");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.dip.value", "16.352970000000003");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.dip.uncertainty", "nan");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.rake.value", "90.0");
        featureBuilder.set("focalMechanism.nodalPlanes.nodalPlane1.rake.uncertainty", "NaN");
        featureBuilder.set("focalMechanism.nodalPlanes.preferredPlane", "nodalPlane1");


        final SimpleFeature feature = featureBuilder.buildFeature("quakeml:quakeledger/84945");
        final DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();
        featureCollection.add(feature);

        try {
            final XmlObject result = QuakeML.fromFeatureCollection(featureCollection).toValidatedXmlObject();
            final String xmlRawContent = StringUtils.readFromResourceFile("org/n52/gfz/riesgos/convertformats/quakeml_validated_one_feature.xml");

            final XmlObject expectedResult = XmlObject.Factory.parse(xmlRawContent);

            assertEquals("The xml contents are the same", expectedResult.toString(), result.toString());
        } catch(final XmlException exception) {
            fail("There should be no exception on parsing xml");
        } catch(final IOException ioException) {
            fail("There should be no exception on loading the xml content");
        }
    }
}
