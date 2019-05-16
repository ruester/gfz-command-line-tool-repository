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

package org.n52.gfz.riesgos.formats.quakeml.impl;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeMLDataProvider;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeMLEvent;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This implementation queries an underlying xml data set to
 * provide the information for the IQuakeML interface
 *
 * Also there is a function to convert any IQuakeML
 * to an xml structure.
 *
 * This is the code for the validated quakeml as it is the output of
 * a customized quakeledger.
 *
 * This xml *does* match the schema for quakeml (in contrast with
 * the originial xml that does not).
 */
public class QuakeMLValidatedXmlImpl implements IQuakeMLDataProvider {

    /*
     * example: see in the test/resource folder
     * under "org/n52/gfz/riesgos/convertformats/quakeml_validated_one_feature.xml"
     *
     *
     * *******************************************************
     * This implementation queries mostly this format, however there is the
     * possibility to have much more information (multiple nodal planes for example).
     * These information can't be queried with the current approach.
     * This implementations focus only on the way quakeml is provided by
     * the program in the following github repository:
     * https://github.com/nbrinckm/quakeledger
     *
     * This is a modified version of the old quakeledger you can find at
     * https://github.com/GFZ-Centre-for-Early-Warning/quakeledger
     *
     * However it validates to the QuakeML-BED-1.2.xsd file
     *
     * when you look at the QuakeML-BED-1.2.xsd file for the xml-schema-definition,
     * you can see that this implementation only focus on the EventParameters and the Event types.
     */

    private static final String NS = "http://quakeml.org/xmlns/bed/1.2";

    private static final QName EVENT = new QName(NS,"event");
    private static final QName EVENT_PARAMETERS = new QName(NS, "eventParameters");
    private static final QName QUAKE_ML = new QName(NS, "quakeml");

    private static final QName PUBLIC_ID = new QName("publicID");
    private static final QName PREFERRED_ORIGIN_ID = new QName(NS,"preferredOriginID");
    private static final QName PREFERRED_MAGNITUDE_ID = new QName(NS,"preferredMagnitudeID");
    private static final QName TYPE = new QName(NS,"type");
    private static final QName DESCRIPTION = new QName(NS,"description");
    private static final QName TEXT = new QName(NS,"text");
    private static final QName TIME = new QName(NS,"time");
    private static final QName VALUE = new QName(NS,"value");
    private static final QName UNCERTAINTY = new QName(NS,"uncertainty");
    private static final QName LATITUDE = new QName(NS,"latitude");
    private static final QName ORIGIN = new QName(NS,"origin");
    private static final QName LONGITUDE = new QName(NS,"longitude");
    private static final QName DEPTH = new QName(NS,"depth");
    private static final QName CREATION_INFO = new QName(NS,"creationInfo");
    private static final QName AUTHOR = new QName(NS, "author");
    private static final QName HORIZONTAL_UNCERTAINTY = new QName(NS,"horizontalUncertainty");
    private static final QName ORIGIN_UNCERTAINTY = new QName(NS,"originUncertainty");
    private static final QName MIN_HORIZONTAL_UNCERTAINTY = new QName(NS,"minHorizontalUncertainty");
    private static final QName MAX_HORIZONTAL_UNCERTAINTY = new QName(NS,"maxHorizontalUncertainty");
    private static final QName AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY = new QName(NS,"azimuthMaxHorizontalUncertainty");
    private static final QName MAGNITUDE = new QName(NS,"magnitude");
    private static final QName MAG = new QName(NS,"mag");
    private static final QName FOCAL_MECHANISM = new QName(NS,"focalMechanism");
    private static final QName STRIKE = new QName(NS,"strike");
    private static final QName NODAL_PLANE_1 = new QName(NS,"nodalPlane1");
    private static final QName NODAL_PLANES = new QName(NS,"nodalPlanes");
    private static final QName DIP = new QName(NS,"dip");
    private static final QName RAKE = new QName(NS,"rake");
    // just an attribute -> no namespace required
    private static final QName PREFERRED_PLANE = new QName("preferredPlane");
    private static final QName DEPTH_TYPE = new QName(NS,"depthType");
    private static final QName TIME_FIXED = new QName(NS,"timeFixed");
    private static final QName EPICENTER_FIXED = new QName("epicenterFixed");
    private static final QName REFERENCE_SYSTEM_ID = new QName(NS,"referenceSystemID");
    private static final QName QUALITY = new QName(NS,"quality");
    private static final QName AZIMUTHAL_GAP = new QName(NS,"azimuthalGap");
    private static final QName MINIMUM_DISTANCE = new QName(NS,"minimumDistance");
    private static final QName MAXIMUM_DISTANCE = new QName(NS,"maximumDistance");
    private static final QName USED_PHASE_COUNT = new QName(NS,"usedPhaseCount");
    private static final QName USED_STATION_COUNT = new QName(NS,"usedStationCount");
    private static final QName STANDARD_ERROR = new QName(NS,"standardError");
    private static final QName EVALUATION_MODE = new QName(NS,"evaluationMode");
    private static final QName EVALUATION_STATUS = new QName(NS,"evaluationStatus");
    private static final QName ORIGIN_ID = new QName(NS,"originID");
    private static final QName STATION_COUNT = new QName(NS,"stationCount");
    private static final QName AMPLITUDE = new QName(NS,"amplitude");
    private static final QName GENERIC_AMPLITUDE = new QName(NS,"genericAmplitude");

    private static final String NAN = "NaN";
    private static final String NODAL_PLANE = "nodalPlane";

    private final XmlObject quakeML;

    public QuakeMLValidatedXmlImpl(final XmlObject quakeML) throws ConvertFormatException {
        this.quakeML = validateNotNull(findEventParameters(quakeML));
    }

    private static XmlObject findEventParameters(final XmlObject root) throws ConvertFormatException {
        final XmlObject[] result = root.selectChildren(EVENT_PARAMETERS);
        if (result.length != 0) {
            return result[0];
        }
        final XmlObject[] quakeml = root.selectChildren(QUAKE_ML);
        if (quakeml.length == 1) {
            final XmlObject quakemlRoot = quakeml[0];
            return quakemlRoot.selectChildren(EVENT_PARAMETERS)[0];
        }
        throw new ConvertFormatException("eventParameters could not be found");
    }

    private static XmlObject validateNotNull(final XmlObject xmlObject) throws ConvertFormatException {
        if(xmlObject == null) {
            throw new ConvertFormatException("eventParameters is null");
        }
        return xmlObject;
    }

    @Override
    public Optional<String> getPublicId() {
        final XmlObject attribute = quakeML.selectAttribute(PUBLIC_ID);
        if(attribute != null) {
            return Optional.ofNullable(attribute.newCursor().getTextValue());
        }
        return Optional.empty();
    }

    @Override
    public List<IQuakeMLEvent> getEvents() {
        return Stream.of(quakeML.selectChildren(EVENT))
                .map(QuakeMLEventXmlImpl::new)
                .collect(Collectors.toList());
    }

    /*
     * class with the data for a single event
     */
    private static class QuakeMLEventXmlImpl implements IQuakeMLEvent {
        private final XmlObject event;

        QuakeMLEventXmlImpl(final XmlObject event) {
            this.event = event;
        }

        @Override
        public String getPublicID() {
            return event.selectAttribute(PUBLIC_ID).newCursor().getTextValue();
        }

        private Optional<String> getByFirstChildrenWithNLevel(final QName... children) {
            XmlObject searchElement = event;
            for(final QName childrenQName : children) {
                final XmlObject[] candidates = searchElement.selectChildren(childrenQName);
                if(candidates.length > 0) {
                    searchElement = candidates[0];
                } else {
                    return Optional.empty();
                }
            }
            return Optional.ofNullable(searchElement.newCursor().getTextValue());
        }

        @Override
        public Optional<String> getPreferredOriginID() {
            return getByFirstChildrenWithNLevel(PREFERRED_ORIGIN_ID);
        }

        @Override
        public Optional<String> getPreferredMagnitudeID() {
            return getByFirstChildrenWithNLevel(PREFERRED_MAGNITUDE_ID);
        }

        @Override
        public Optional<String> getType() {
            return getByFirstChildrenWithNLevel(TYPE);
        }

        @Override
        public Optional<String> getDescription() {
            return getByFirstChildrenWithNLevel(DESCRIPTION, TEXT);
        }

        private Optional<String> getPublicIDOfFirstChildOneLevel(final QName children) {
            final XmlObject[] candidates = event.selectChildren(children);
            if(candidates.length > 0) {
                return Optional.ofNullable(candidates[0].selectAttribute(PUBLIC_ID).newCursor().getTextValue());
            }
            return Optional.empty();
        }

        @Override
        public Optional<String> getOriginPublicID() {
            return getPublicIDOfFirstChildOneLevel(ORIGIN);
        }

        @Override
        public Optional<String> getOriginTimeValue() {
            return getByFirstChildrenWithNLevel(ORIGIN, TIME, VALUE);
        }

        @Override
        public Optional<String> getOriginTimeUncertainty() {
            return removeIfNaN(getByFirstChildrenWithNLevel(ORIGIN, TIME, UNCERTAINTY));
        }

        private Optional<String> removeIfNaN(final Optional<String> possibleNaNValue) {
            return possibleNaNValue.map(this::removeIfNanValue);
        }

        private String removeIfNanValue(final String possibleNaNValue) {
            if(possibleNaNValue.toLowerCase().equals("nan")) {
                return null;
            }
            return possibleNaNValue;
        }

        @Override
        public double getOriginLatitudeValue() {
            final Optional<String> str = getByFirstChildrenWithNLevel(ORIGIN, LATITUDE, VALUE);
            if(str.isPresent()) {
                final String doubleStr = str.get();
                return parseDouble(doubleStr);
            }
            return Double.NaN;
        }


        @Override
        public Optional<String> getOriginLatitudeUncertainty() {
            return removeIfNaN(getByFirstChildrenWithNLevel(ORIGIN, LATITUDE, UNCERTAINTY));
        }

        private double parseDouble(final String strDouble) {
            if("nan".equals(strDouble)) {
                return Double.NaN;
            }
            return Double.parseDouble(strDouble);
        }


        @Override
        public double getOriginLongitudeValue() {
            final Optional<String> str = getByFirstChildrenWithNLevel(ORIGIN, LONGITUDE, VALUE);
            if(str.isPresent()) {
                final String doubleStr = str.get();
                return parseDouble(doubleStr);
            }
            return Double.NaN;
        }

        @Override
        public Optional<String> getOriginLongitudeUncertainty() {
            return removeIfNaN(getByFirstChildrenWithNLevel(ORIGIN, LONGITUDE, UNCERTAINTY));
        }

        @Override
        public Optional<String> getOriginDepthValue() {
            return getByFirstChildrenWithNLevel(ORIGIN, DEPTH, VALUE);
        }

        @Override
        public Optional<String> getOriginDepthUncertainty() {
            return removeIfNaN(getByFirstChildrenWithNLevel(ORIGIN, DEPTH, UNCERTAINTY));
        }

        @Override
        public Optional<String> getOriginDepthType() {
            return getByFirstChildrenWithNLevel(ORIGIN, DEPTH_TYPE);
        }

        @Override
        public Optional<String> getOriginTimeFixed() {
            return getByFirstChildrenWithNLevel(ORIGIN, TIME_FIXED);
        }

        @Override
        public Optional<String> getOriginEpicenterFixed() {
            return getByFirstChildrenWithNLevel(ORIGIN, EPICENTER_FIXED);
        }

        @Override
        public Optional<String> getOriginReferenceSystemID() {
            return getByFirstChildrenWithNLevel(ORIGIN, REFERENCE_SYSTEM_ID);
        }

        @Override
        public Optional<String> getOriginType() {
            return getByFirstChildrenWithNLevel(ORIGIN, TYPE);
        }

        @Override
        public Optional<String> getOriginCreationInfoValue() {
            return getByFirstChildrenWithNLevel(ORIGIN, CREATION_INFO, AUTHOR);
        }

        @Override
        public Optional<String> getOriginQualityAzimuthalGap() {
            return getByFirstChildrenWithNLevel(ORIGIN, QUALITY, AZIMUTHAL_GAP);
        }

        @Override
        public Optional<String> getOriginQualityMinimumDistance() {
            return getByFirstChildrenWithNLevel(ORIGIN, QUALITY, MINIMUM_DISTANCE);
        }

        @Override
        public Optional<String> getOriginQualityMaximumDistance() {
            return getByFirstChildrenWithNLevel(ORIGIN, QUALITY, MAXIMUM_DISTANCE);
        }

        @Override
        public Optional<String> getOriginQualityUsedPhaseCount() {
            return getByFirstChildrenWithNLevel(ORIGIN, QUALITY, USED_PHASE_COUNT);
        }

        @Override
        public Optional<String> getOriginQualityUsedStationCount() {
            return getByFirstChildrenWithNLevel(ORIGIN, QUALITY, USED_STATION_COUNT);
        }

        @Override
        public Optional<String> getOriginQualityStandardError() {
            return getByFirstChildrenWithNLevel(ORIGIN, QUALITY, STANDARD_ERROR);
        }

        @Override
        public Optional<String> getOriginEvaluationMode() {
            return getByFirstChildrenWithNLevel(ORIGIN, EVALUATION_MODE);
        }

        @Override
        public Optional<String> getOriginEvaluationStatus() {
            return getByFirstChildrenWithNLevel(ORIGIN, EVALUATION_STATUS);
        }

        @Override
        public Optional<String> getOriginUncertaintyHorizontalUncertainty() {
            return removeIfNaN(getByFirstChildrenWithNLevel(ORIGIN, ORIGIN_UNCERTAINTY, HORIZONTAL_UNCERTAINTY));
        }

        @Override
        public Optional<String> getOriginUncertaintyMinHorizontalUncertainty() {
            return removeIfNaN(getByFirstChildrenWithNLevel(ORIGIN, ORIGIN_UNCERTAINTY, MIN_HORIZONTAL_UNCERTAINTY));
        }

        @Override
        public Optional<String> getOriginUncertaintyMaxHorizontalUncertainty() {
            return removeIfNaN(getByFirstChildrenWithNLevel(ORIGIN, ORIGIN_UNCERTAINTY, MAX_HORIZONTAL_UNCERTAINTY));
        }

        @Override
        public Optional<String> getOriginUncertaintyAzimuthMaxHorizontalUncertainty() {
            return removeIfNaN(getByFirstChildrenWithNLevel(ORIGIN, ORIGIN_UNCERTAINTY, AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY));
        }

        @Override
        public Optional<String> getMagnitudePublicID() {
            return getPublicIDOfFirstChildOneLevel(MAGNITUDE);
        }

        @Override
        public Optional<String> getMagnitudeMagValue() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, MAG, VALUE);
        }

        @Override
        public Optional<String> getMagnitudeMagUncertainty() {
            return removeIfNaN(getByFirstChildrenWithNLevel(MAGNITUDE, MAG, UNCERTAINTY));
        }

        @Override
        public Optional<String> getMagnitudeType() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, TYPE);
        }

        @Override
        public Optional<String> getMagnitudeCreationInfoValue() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, CREATION_INFO, AUTHOR);
        }

        @Override
        public Optional<String> getMagnitudeEvaluationStatus() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, EVALUATION_STATUS);
        }

        @Override
        public Optional<String> getMagnitudeOriginID() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, ORIGIN_ID);
        }

        @Override
        public Optional<String> getMagnitudeStationCount() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, STATION_COUNT);
        }

        @Override
        public Optional<String> getFocalMechanismPublicID() {
            return getPublicIDOfFirstChildOneLevel(FOCAL_MECHANISM);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1StrikeValue() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, STRIKE, VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty() {
            return removeIfNaN(getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, STRIKE, UNCERTAINTY));
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1DipValue() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, DIP, VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1DipUncertainty() {
            return removeIfNaN(getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, DIP, UNCERTAINTY));
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1RakeValue() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, RAKE, VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty() {
            return removeIfNaN(getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, RAKE, UNCERTAINTY));
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesPreferredNodalPlane() {

            XmlObject searchElement = event;
            for (final QName childrenQName : Arrays.asList(FOCAL_MECHANISM, NODAL_PLANES)) {
                final XmlObject[] candidates = searchElement.selectChildren(childrenQName);
                if (candidates.length > 0) {
                    searchElement = candidates[0];
                }
            }

            final Optional<String> attributeText;
            if (searchElement != null) {
                final String pureAttributeTextAsNumber = searchElement.selectAttribute(PREFERRED_PLANE).newCursor().getTextValue();
                final String pureAttributeText = NODAL_PLANE + pureAttributeTextAsNumber;
                attributeText = Optional.ofNullable(pureAttributeText);
            } else {
                attributeText = Optional.empty();
            }

            return attributeText;
        }

        @Override
        public Optional<String> getAmplitudePublicID() {
            return getPublicIDOfFirstChildOneLevel(AMPLITUDE);
        }

        @Override
        public Optional<String> getAmplitudeType() {
            return getByFirstChildrenWithNLevel(AMPLITUDE, TYPE);
        }

        @Override
        public Optional<String> getAmplitudeGenericAmplitudeValue() {
            return getByFirstChildrenWithNLevel(AMPLITUDE, GENERIC_AMPLITUDE, VALUE);
        }
    }

    /**
     * Converts any IQuakeML to an XmlObject (and uses the validated quakeml)
     * @param quakeML the data provider to convert it to xml
     * @return XmlObject
     */
    public static XmlObject convertToValidatedXml(final IQuakeMLDataProvider quakeML) {

        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();
        cursor.toFirstContentToken();

        cursor.beginElement(EVENT_PARAMETERS);
        final String publicId = quakeML.getPublicId().orElse("quakeml:quakeledger/0");
        cursor.insertAttributeWithValue(PUBLIC_ID, publicId);


        for(final IQuakeMLEvent event : quakeML.getEvents()) {
            final XmlObject eventXml = convertFeatureToXml(event);
            eventXml.newCursor().copyXmlContents(cursor);
        }

        cursor.dispose();

        return result;
    }

    private static boolean notNaN(final String atext) {
        return ! atext.toLowerCase().equals("nan");
    }

    private static class InsertElementWithText implements Consumer<String> {
        private final XmlCursor cursor;
        private final QName qName;

        InsertElementWithText(final XmlCursor cursor, final QName qName) {
            this.cursor = cursor;
            this.qName = qName;
        }

        @Override
        public void accept(final String value) {
            cursor.insertElementWithText(qName, value);
        }
    }

    private static class InsertAttributeWithText implements Consumer<String> {
        private final XmlCursor cursor;
        private final QName qName;

        InsertAttributeWithText(final XmlCursor cursor, final QName qName) {
            this.cursor = cursor;
            this.qName = qName;
        }

        @Override
        public void accept(final String value) {
            cursor.insertAttributeWithValue(qName, value);
        }
    }


    private static XmlObject convertFeatureToXml(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();
        cursor.beginElement(EVENT);
        final String publicID = event.getPublicID();
        cursor.insertAttributeWithValue(PUBLIC_ID, publicID);

        final Optional<String> preferredOriginID = event.getPreferredOriginID();
        preferredOriginID.ifPresent(new InsertElementWithText(cursor, PREFERRED_ORIGIN_ID));

        final Optional<String> preferredMagnitudeID = event.getPreferredMagnitudeID();
        preferredMagnitudeID.ifPresent(new InsertElementWithText(cursor, PREFERRED_MAGNITUDE_ID));

        final Optional<String> type = event.getType();
        type.ifPresent(new InsertElementWithText(cursor, TYPE));

        final Optional<String> descriptionText = event.getDescription();
        if(descriptionText.isPresent()) {
            cursor.beginElement(DESCRIPTION);
            cursor.insertElementWithText(TEXT, descriptionText.get());
            cursor.toNextToken();
        }

        final XmlObject partOrigin = convertFeatureToXmlOriginSection(event);
        partOrigin.newCursor().copyXmlContents(cursor);
        final XmlObject partMagnitude = convertFeatureToXmlMagnitudeSection(event);
        partMagnitude.newCursor().copyXmlContents(cursor);
        final XmlObject partFocalMechanism = convertFeatureToFocalMechanismSection(event);
        partFocalMechanism.newCursor().copyXmlContents(cursor);
        final XmlObject partAmplitude = convertFeatureToAmplitudeSection(event);
        partAmplitude.newCursor().copyXmlContents(cursor);

        cursor.dispose();

        return result;
    }

    private static XmlObject convertFeatureToXmlOriginSection(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(ORIGIN);

        final Optional<String> originPublicID = event.getOriginPublicID();
        originPublicID.ifPresent(new InsertAttributeWithText(cursor, PUBLIC_ID));

        cursor.beginElement(TIME);

        final Optional<String> timeValue = event.getOriginTimeValue();
        timeValue.ifPresent(new InsertElementWithText(cursor, VALUE));

        final String timeUncertainty = event.getOriginTimeUncertainty().filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(timeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(LATITUDE);

        cursor.insertElementWithText(VALUE, String.valueOf(event.getOriginLatitudeValue()));
        final String latitudeUncertainty = event.getOriginLatitudeUncertainty().filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(latitudeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(LONGITUDE);

        cursor.insertElementWithText(VALUE, String.valueOf(event.getOriginLongitudeValue()));
        final String longitudeUncertainty = event.getOriginLongitudeUncertainty().filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(longitudeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(DEPTH);

        final Optional<String> depthValue = event.getOriginDepthValue();
        depthValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        String depthUncertainty = event.getOriginDepthUncertainty().filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(depthUncertainty);

        cursor.toNextToken();

        final Optional<String> depthType = event.getOriginDepthType();
        depthType.ifPresent(new InsertElementWithText(cursor, DEPTH_TYPE));

        final Optional<String> timeFixed = event.getOriginTimeFixed();
        timeFixed.ifPresent(new InsertElementWithText(cursor, TIME_FIXED));

        final Optional<String> epicenterFixed = event.getOriginEpicenterFixed();
        epicenterFixed.ifPresent(new InsertElementWithText(cursor, EPICENTER_FIXED));

        final Optional<String> referenceSystemID = event.getOriginReferenceSystemID();
        referenceSystemID.ifPresent(new InsertElementWithText(cursor, REFERENCE_SYSTEM_ID));

        final Optional<String> type = event.getOriginType();
        type.ifPresent(new InsertElementWithText(cursor, TYPE));

        final Optional<String> creationInfo = event.getOriginCreationInfoValue();
        if(creationInfo.isPresent()) {
            cursor.beginElement(CREATION_INFO);
            cursor.insertElementWithText(AUTHOR, creationInfo.get());
            cursor.toNextToken();
        }


        final Optional<String> qualityAzimuthalGap = event.getOriginQualityAzimuthalGap();
        final Optional<String> qualityMinimumDistance = event.getOriginQualityMinimumDistance();
        final Optional<String> qualityMaximumDistance = event.getOriginQualityMaximumDistance();
        final Optional<String> qualityUsedPhaseCount = event.getOriginQualityUsedPhaseCount();
        final Optional<String> qualityUsedStationCount = event.getOriginQualityUsedStationCount();
        final Optional<String> qualityStandardError = event.getOriginQualityStandardError();

        if(Stream.of(qualityAzimuthalGap, qualityMinimumDistance, qualityMaximumDistance,
                qualityUsedPhaseCount, qualityUsedStationCount, qualityStandardError).anyMatch(Optional::isPresent)) {

            cursor.beginElement(QUALITY);

            qualityAzimuthalGap.ifPresent(new InsertElementWithText(cursor, AZIMUTHAL_GAP));
            qualityMinimumDistance.ifPresent(new InsertElementWithText(cursor, MINIMUM_DISTANCE));
            qualityMaximumDistance.ifPresent(new InsertElementWithText(cursor, MAXIMUM_DISTANCE));
            qualityUsedPhaseCount.ifPresent(new InsertElementWithText(cursor, USED_PHASE_COUNT));
            qualityUsedStationCount.ifPresent(new InsertElementWithText(cursor, USED_STATION_COUNT));
            qualityStandardError.ifPresent(new InsertElementWithText(cursor, STANDARD_ERROR));

            cursor.toNextToken();
        }

        final Optional<String> evaluationMode = event.getOriginEvaluationMode();
        evaluationMode.ifPresent(new InsertElementWithText(cursor, EVALUATION_MODE));
        final Optional<String> evaluationStatus = event.getOriginEvaluationStatus();
        evaluationStatus.ifPresent(new InsertElementWithText(cursor, EVALUATION_STATUS));

        final XmlObject partOriginUncertainty = convertFeatureToXmlOriginUncertaintySection(event);
        partOriginUncertainty.newCursor().copyXmlContents(cursor);

        cursor.dispose();

        return result;
    }

    private static XmlObject convertFeatureToXmlOriginUncertaintySection(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(ORIGIN_UNCERTAINTY);

        final String horizontalUncertainty = event.getOriginUncertaintyHorizontalUncertainty().filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, HORIZONTAL_UNCERTAINTY).accept(horizontalUncertainty);
        final String minHorizontalUncertainty = event.getOriginUncertaintyMinHorizontalUncertainty().filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, MIN_HORIZONTAL_UNCERTAINTY).accept(minHorizontalUncertainty);
        final String maxHorizontalUncertainty = event.getOriginUncertaintyMaxHorizontalUncertainty().filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, MAX_HORIZONTAL_UNCERTAINTY).accept(maxHorizontalUncertainty);
        final String azimuthMaxHorizontalUncertainty = event.getOriginUncertaintyAzimuthMaxHorizontalUncertainty().filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY).accept(azimuthMaxHorizontalUncertainty);

        cursor.toNextToken();

        cursor.dispose();

        return result;
    }

    private static XmlObject convertFeatureToXmlMagnitudeSection(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(MAGNITUDE);
        final Optional<String> publicID = event.getMagnitudePublicID();
        publicID.ifPresent(new InsertAttributeWithText(cursor, PUBLIC_ID));

        cursor.beginElement(MAG);

        final Optional<String> magValue = event.getMagnitudeMagValue();
        magValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String magUncertainty = event.getMagnitudeMagUncertainty().filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(magUncertainty);

        cursor.toNextToken();


        final Optional<String> type = event.getMagnitudeType();
        type.ifPresent(new InsertElementWithText(cursor, TYPE));

        final Optional<String> evaluationStatus = event.getMagnitudeEvaluationStatus();
        evaluationStatus.ifPresent(new InsertElementWithText(cursor, EVALUATION_STATUS));

        final Optional<String> originID = event.getMagnitudeOriginID();
        originID.ifPresent(new InsertElementWithText(cursor, ORIGIN_ID));

        final Optional<String> stationCount = event.getMagnitudeStationCount();
        stationCount.ifPresent(new InsertElementWithText(cursor, STATION_COUNT));

        final Optional<String> creationInfo = event.getMagnitudeCreationInfoValue();
        if(creationInfo.isPresent()) {
            cursor.beginElement(CREATION_INFO);
            cursor.insertElementWithText(AUTHOR, creationInfo.get());
            cursor.toNextToken();
        }


        cursor.dispose();

        return result;
    }

    private static String removeNodalPlaneTextBefore(final String textWithNodalPlaneTextInFront) {
        return textWithNodalPlaneTextInFront.replaceAll("[a-zA-Z]", "");
    }

    private static XmlObject convertFeatureToFocalMechanismSection(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(FOCAL_MECHANISM);
        final Optional<String> publicID = event.getFocalMechanismPublicID();
        publicID.ifPresent(new InsertAttributeWithText(cursor, PUBLIC_ID));

        cursor.beginElement(NODAL_PLANES);
        final Optional<String> preferredPlane = event.getFocalMechanismNodalPlanesPreferredNodalPlane();
        if(preferredPlane.isPresent()) {
            final String preferredPlaneValue = preferredPlane.get();
            final String preferredPlaneJustIntegerValue = removeNodalPlaneTextBefore(preferredPlaneValue);
            new InsertAttributeWithText(cursor, PREFERRED_PLANE).accept(preferredPlaneJustIntegerValue);
        }



        cursor.beginElement(NODAL_PLANE_1);
        cursor.beginElement(STRIKE);

        final Optional<String> strikeValue = event.getFocalMechanismNodalPlanesNodalPlane1StrikeValue();
        strikeValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String strikeUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty().filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(strikeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(DIP);
        final Optional<String> dipValue = event.getFocalMechanismNodalPlanesNodalPlane1DipValue();
        dipValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String dipUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1DipUncertainty().filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(dipUncertainty);

        cursor.toNextToken();

        cursor.beginElement(RAKE);
        final Optional<String> rakeValue = event.getFocalMechanismNodalPlanesNodalPlane1RakeValue();
        rakeValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String rakeUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty().filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(rakeUncertainty);

        cursor.toNextToken();

        cursor.toNextToken();

        cursor.dispose();

        return result;
    }

    private static XmlObject convertFeatureToAmplitudeSection(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        final Optional<String> publicID = event.getAmplitudePublicID();
        final Optional<String> type = event.getAmplitudeType();
        final Optional<String> genericAmplitudeValue = event.getAmplitudeGenericAmplitudeValue();

        if(Stream.of(publicID, type, genericAmplitudeValue).anyMatch(Optional::isPresent)) {

            cursor.beginElement(AMPLITUDE);

            publicID.ifPresent(new InsertAttributeWithText(cursor, PUBLIC_ID));
            type.ifPresent(new InsertAttributeWithText(cursor, TYPE));

            if (genericAmplitudeValue.isPresent()) {
                cursor.beginElement(GENERIC_AMPLITUDE);
                cursor.insertElementWithText(VALUE, genericAmplitudeValue.get());
            }
        }
        cursor.dispose();

        return result;
    }
}
