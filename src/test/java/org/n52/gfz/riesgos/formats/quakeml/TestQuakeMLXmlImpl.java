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

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * This is the test class for the xml implementation
 */
public class TestQuakeMLXmlImpl {

    /**
     * Tests the old quakeml format
     */
    @Test
    public void testWithOneEvent() {
        String xmlRawContent = null;

        try(final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("org/n52/gfz/riesgos/convertformats/quakeml_from_original_quakeledger_one_feature.xml")) {
            assertNotNull("The input stream must not be null", inputStream);
            xmlRawContent = StringUtils.readFromStream(inputStream);
        } catch(final IOException ioException) {
            fail("There should be no excepton on loading the file from the resources");
        }
        try {
            final XmlObject xmlContent = XmlObject.Factory.parse(xmlRawContent);

            final IQuakeML quakeML = QuakeML.fromXml(xmlContent);

            final List<IQuakeMLEvent> events = quakeML.getEvents();

            assertEquals("The list has one element", 1, events.size());

            final IQuakeMLEvent event = events.get(0);

            assertEquals("The publicID is as expected", "84945", event.getPublicID());
            assertTrue("The preferredOriginID is present", event.getPreferredOriginID().isPresent());
            assertEquals("The preferredOriginID is as expected", "84945", event.getPreferredOriginID().get());
            assertTrue("The preferredMagnitudeID is present", event.getPreferredMagnitudeID().isPresent());
            assertEquals("The preferredMagnitudeID is as expected", "84945", event.getPreferredMagnitudeID().get());
            assertTrue("The type is present", event.getType().isPresent());
            assertEquals("The type is as expected", "earthquake", event.getType().get());
            assertTrue("The descriptionText is present", event.getDescription().isPresent());
            assertEquals("The descriptionText is as expected", "stochastic", event.getDescription().get());
            assertTrue("The originPublicID is present", event.getOriginPublicID().isPresent());
            assertEquals("The originPublicID is as expected", "84945", event.getOriginPublicID().get());
            assertTrue("The originTimeVlaue is present", event.getOriginTimeValue().isPresent());
            assertEquals("The originTimeValue is as expeced", "16773-01-01T00:00:00.000000Z", event.getOriginTimeValue().get());
            assertTrue("The originTimeUncertainty is present", event.getOriginTimeUncertainty().isPresent());
            assertEquals("The originTimeUncertainty is as expected", "nan", event.getOriginTimeUncertainty().get());
            assertTrue("The latitudeValue is as expected", Math.abs(-30.9227 - event.getOriginLatitudeValue()) < 0.001);
            assertTrue("The latitudeUncertainty is present", event.getOriginLatitudeUncertainty().isPresent());
            assertEquals("The latitudeUncertainty is as expected", "nan", event.getOriginLatitudeUncertainty().get());
            assertTrue("The longitudeValue is as expected", Math.abs(-71.49875 - event.getOriginLongitudeValue()) < 0.001);
            assertTrue("The longitudeUnvertainty is present", event.getOriginLongitudeUncertainty().isPresent());
            assertEquals("The longitudeUncertainty is as expected", "nan", event.getOriginLongitudeUncertainty().get());
            assertTrue("The depthValue is present", event.getOriginDepthValue().isPresent());
            assertEquals("The depthValue is as expected", "34.75117", event.getOriginDepthValue().get());
            assertTrue("The depthUncertainty is present", event.getOriginDepthUncertainty().isPresent());
            assertEquals("The depthUncertainty is as expected", "nan", event.getOriginDepthUncertainty().get());
            assertTrue("The originCreationInfoValue is present", event.getOriginCreationInfoValue().isPresent());
            assertEquals("The originCreationInfoValue is as expected", "GFZ", event.getOriginCreationInfoValue().get());
            assertTrue("The horizontalUncertainty is present", event.getOriginUncertaintyHorizontalUncertainty().isPresent());
            assertEquals("The horizontalUncertainty is as expected", "nan", event.getOriginUncertaintyHorizontalUncertainty().get());
            assertTrue("The minHorizontalUncertainty is present", event.getOriginUncertaintyMinHorizontalUncertainty().isPresent());
            assertEquals("The minHorizontalUncertainty is as expected", "nan", event.getOriginUncertaintyMinHorizontalUncertainty().get());
            assertTrue("The maxHorizontalUncertainty is present", event.getOriginUncertaintyMaxHorizontalUncertainty().isPresent());
            assertEquals("The maxHorizontalUncertainty is as expected", "nan", event.getOriginUncertaintyMaxHorizontalUncertainty().get());
            assertTrue("The azimutzMaxHorizontalUncertainty is present", event.getOriginUncertaintyAzimuthMaxHorizontalUncertainty().isPresent());
            assertEquals("The azimuthMaxHorizontalUncertainty is as expected", "nan", event.getOriginUncertaintyAzimuthMaxHorizontalUncertainty().get());
            assertTrue("The magnitude publicID is present", event.getMagnitudePublicID().isPresent());
            assertEquals("The magnitude publicID is as expected", "84945", event.getMagnitudePublicID().get());
            assertTrue("The magnitude value is present", event.getMagnitudeMagValue().isPresent());
            assertEquals("The magnitude value is as expected", "8.35", event.getMagnitudeMagValue().get());
            assertTrue("The magnitude uncertainty is present", event.getMagnitudeMagUncertainty().isPresent());
            assertEquals("The magnitude uncertainty is as expected", "nan", event.getMagnitudeMagUncertainty().get());
            assertTrue("The magnitude type is prsent", event.getMagnitudeType().isPresent());
            assertEquals("The magnitude type is as expected", "MW", event.getMagnitudeType().get());
            assertTrue("The magnitude creationInfo is present", event.getMagnitudeCreationInfoValue().isPresent());
            assertEquals("The magnitude creationInfo is as expected", "GFZ", event.getMagnitudeCreationInfoValue().get());
            assertTrue("The focal mechanism publicID is present", event.getFocalMechanismPublicID().isPresent());
            assertEquals("The focal mechanism publicID is as expected", "84945", event.getFocalMechanismPublicID().get());
            assertTrue("The strike value is as present", event.getFocalMechanismNodalPlanesNodalPlane1StrikeValue().isPresent());
            assertEquals("The strike value is as expected", "7.310981", event.getFocalMechanismNodalPlanesNodalPlane1StrikeValue().get());
            assertTrue("The strike unvertainty is present", event.getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty().isPresent());
            assertEquals("The strike uncertainty is as expected", "nan", event.getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty().get());
            assertTrue("The dip value is present", event.getFocalMechanismNodalPlanesNodalPlane1DipValue().isPresent());
            assertEquals("The dip value is as expected", "16.352970000000003", event.getFocalMechanismNodalPlanesNodalPlane1DipValue().get());
            assertTrue("The dip unvertainty is present", event.getFocalMechanismNodalPlanesNodalPlane1DipUncertainty().isPresent());
            assertEquals("The dip uncertainty is as expected", "nan", event.getFocalMechanismNodalPlanesNodalPlane1DipUncertainty().get());
            assertTrue("The rake value is present", event.getFocalMechanismNodalPlanesNodalPlane1RakeValue().isPresent());
            assertEquals("The rake value is as expected", "90.0", event.getFocalMechanismNodalPlanesNodalPlane1RakeValue().get());
            assertTrue("The rake uncertainty is present", event.getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty().isPresent());
            assertEquals("The rake uncertainty is as expected", "nan", event.getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty().get());
            assertTrue("The preferred planeis present", event.getFocalMechanismNodalPlanesPreferredNodalPlane().isPresent());
            assertEquals("The preferred plane is as expected", "nodalPlane1", event.getFocalMechanismNodalPlanesPreferredNodalPlane().get());

        } catch(final XmlException exception) {
            fail("There should be no xml exception");
        } catch(final ConvertFormatException exception) {
            fail("There should be no exception on converting");
        }
    }
}
