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

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;

/**
 * This is the test class for the xml implementation
 */
public class TestQuakeMLOriginalXmlImpl implements ICommonTestQuakeMLXmlTestFunctions {

    /**
     * Tests the old quakeml format
     */
    @Test
    public void testWithOneEvent() {
        try {
            final XmlObject xmlContent = readOriginalOneFeature();

            final IQuakeML quakeML = QuakeML.fromOriginalXml(xmlContent);

            final List<IQuakeMLEvent> events = quakeML.getEvents();

            assertEquals("The list has one element", 1, events.size());

            assertFalse("The publicID of the eventparameters not is present", quakeML.getPublicId().isPresent());

            final IQuakeMLEvent event = events.get(0);

            assertEquals("The publicID is as expected", "quakeml:quakeledger/84945", event.getPublicID());
            assertTrue("The preferredOriginID is present", event.getPreferredOriginID().isPresent());
            assertEquals("The preferredOriginID is as expected", "quakeml:quakeledger/84945", event.getPreferredOriginID().get());
            assertTrue("The preferredMagnitudeID is present", event.getPreferredMagnitudeID().isPresent());
            assertEquals("The preferredMagnitudeID is as expected", "quakeml:quakeledger/84945", event.getPreferredMagnitudeID().get());
            assertTrue("The type is present", event.getType().isPresent());
            assertEquals("The type is as expected", "earthquake", event.getType().get());
            assertTrue("The descriptionText is present", event.getDescription().isPresent());
            assertEquals("The descriptionText is as expected", "stochastic", event.getDescription().get());
            assertTrue("The originPublicID is present", event.getOriginPublicID().isPresent());
            assertEquals("The originPublicID is as expected", "quakeml:quakeledger/84945", event.getOriginPublicID().get());
            assertTrue("The originTimeValue is present", event.getOriginTimeValue().isPresent());
            assertEquals("The originTimeValue is as expected", "16773-01-01T00:00:00.000000Z", event.getOriginTimeValue().get());
            // those nan values should be treated as nulls
            assertFalse("The originTimeUncertainty is not present", event.getOriginTimeUncertainty().isPresent());
            assertTrue("The latitudeValue is as expected", Math.abs(-30.9227 - event.getOriginLatitudeValue()) < 0.001);
            assertFalse("The latitudeUncertainty is not present", event.getOriginLatitudeUncertainty().isPresent());
            assertTrue("The longitudeValue is as expected", Math.abs(-71.49875 - event.getOriginLongitudeValue()) < 0.001);
            assertFalse("The longitudeUncertainty is not present", event.getOriginLongitudeUncertainty().isPresent());
            assertTrue("The depthValue is present", event.getOriginDepthValue().isPresent());
            assertEquals("The depthValue is as expected", "34.75117", event.getOriginDepthValue().get());
            assertFalse("The depthUncertainty is not present", event.getOriginDepthUncertainty().isPresent());
            assertTrue("The originCreationInfoValue is present", event.getOriginCreationInfoValue().isPresent());
            assertEquals("The originCreationInfoValue is as expected", "GFZ", event.getOriginCreationInfoValue().get());
            assertFalse("The horizontalUncertainty is not present", event.getOriginUncertaintyHorizontalUncertainty().isPresent());
            assertFalse("The minHorizontalUncertainty is not present", event.getOriginUncertaintyMinHorizontalUncertainty().isPresent());
            assertFalse("The maxHorizontalUncertainty is not present", event.getOriginUncertaintyMaxHorizontalUncertainty().isPresent());
            assertFalse("The azimuthMaxHorizontalUncertainty is not present", event.getOriginUncertaintyAzimuthMaxHorizontalUncertainty().isPresent());
            assertTrue("The magnitude publicID is present", event.getMagnitudePublicID().isPresent());
            assertEquals("The magnitude publicID is as expected", "quakeml:quakeledger/84945", event.getMagnitudePublicID().get());
            assertTrue("The magnitude value is present", event.getMagnitudeMagValue().isPresent());
            assertEquals("The magnitude value is as expected", "8.35", event.getMagnitudeMagValue().get());
            assertFalse("The magnitude uncertainty is not present", event.getMagnitudeMagUncertainty().isPresent());
            assertTrue("The magnitude type is present", event.getMagnitudeType().isPresent());
            assertEquals("The magnitude type is as expected", "MW", event.getMagnitudeType().get());
            assertTrue("The magnitude creationInfo is present", event.getMagnitudeCreationInfoValue().isPresent());
            assertEquals("The magnitude creationInfo is as expected", "GFZ", event.getMagnitudeCreationInfoValue().get());
            assertTrue("The focal mechanism publicID is present", event.getFocalMechanismPublicID().isPresent());
            assertEquals("The focal mechanism publicID is as expected", "quakeml:quakeledger/84945", event.getFocalMechanismPublicID().get());
            assertTrue("The strike value is as present", event.getFocalMechanismNodalPlanesNodalPlane1StrikeValue().isPresent());
            assertEquals("The strike value is as expected", "7.310981", event.getFocalMechanismNodalPlanesNodalPlane1StrikeValue().get());
            assertFalse("The strike uncertainty is not present", event.getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty().isPresent());
            assertTrue("The dip value is present", event.getFocalMechanismNodalPlanesNodalPlane1DipValue().isPresent());
            assertEquals("The dip value is as expected", "16.352970000000003", event.getFocalMechanismNodalPlanesNodalPlane1DipValue().get());
            assertFalse("The dip uncertainty is not present", event.getFocalMechanismNodalPlanesNodalPlane1DipUncertainty().isPresent());
            assertTrue("The rake value is present", event.getFocalMechanismNodalPlanesNodalPlane1RakeValue().isPresent());
            assertEquals("The rake value is as expected", "90.0", event.getFocalMechanismNodalPlanesNodalPlane1RakeValue().get());
            assertFalse("The rake uncertainty is not present", event.getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty().isPresent());
            assertTrue("The preferred plane is present", event.getFocalMechanismNodalPlanesPreferredNodalPlane().isPresent());
            assertEquals("The preferred plane is as expected", "nodalPlane1", event.getFocalMechanismNodalPlanesPreferredNodalPlane().get());

        } catch(final XmlException exception) {
            fail("There should be no xml exception");
        } catch(final ConvertFormatException exception) {
            fail("There should be no exception on converting");
        } catch(final IOException ioException) {
            fail("There should be no exception on loading the file from the resources");
        }
    }

    /**
     * when wrapped as a quakeml object and then converted to the same
     * xml format that was putted in, then the content should be the same
     */
    @Test
    public void testOutputIsTheSameAsInput() {
        try {
            final XmlObject xmlContent = readOriginalOneFeature();

            final IQuakeML quakeML = QuakeML.fromOriginalXml(xmlContent);

            final XmlObject xmlOutput = quakeML.toOriginalXmlObject();

            assertEquals("The input and the output in the same format are equal", xmlContent.toString(), xmlOutput.toString());
        } catch(final XmlException xmlException) {
            fail("There should be no xml exception");
        } catch(final IOException ioException) {
            fail("There should be no io exception on reading the contents");
        } catch(final ConvertFormatException convertException) {
            fail("There should be no exception on conversion");
        }
    }

    @Test
    public void testConversionToValidatedOutput() {
        try {
            final XmlObject validatedXml = readValidatedOneFeature();
            final XmlObject origignalXml = readOriginalOneFeature();

            final IQuakeML quakeml = QuakeML.fromOriginalXml(origignalXml);

            final XmlObject validatedXmlRecreated = quakeml.toValidatedXmlObject();

            assertEquals("The converted quakeml should match the validated one", validatedXml.toString(), validatedXmlRecreated.toString());

        } catch(final XmlException exception) {
            fail("There should be no xml exception");
        } catch(final IOException ioException) {
            fail("There should be no exception on loading the file form the resources");
        } catch(final ConvertFormatException exception) {
            fail("There should be no exception on converting");
        }
    }
}
