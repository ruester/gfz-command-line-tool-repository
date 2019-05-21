package org.n52.gfz.riesgos.formats.quakeml.impl;

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


import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeMLDataProvider;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeMLEvent;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This implementation queries an underlying xml data set to
 * provide the information for the IQuakeML interface
 *
 * Also there is a function to convert any IQuakeML
 * to an xml structure.
 *
 * This is the code for the original quakeml as it is the output of
 * the original quakeledger.
 *
 * This xml does not match the schema for quakeml.
 */
public class QuakeMLOriginalXmlImpl implements IQuakeMLDataProvider {

    /*
     * example: see in the test/resource folder
     * under "org/n52/gfz/riesgos/convertformats/quakeml_from_original_quakeledger_one_feature.xml"
     *
     *
     * *******************************************************
     * This implementation queries mostly this format, however there is the
     * possibility to have much more information (multiple nodal planes for example).
     * These information can't be queried with the current approach.
     * This implementations focus only on the way quakeml is provided by
     * the program in the following github repository:
     * https://github.com/GFZ-Centre-for-Early-Warning/quakeledger
     *
     * when you look at the QuakeML-BED-1.2.xsd file for the xml-schema-definition,
     * you can see that this implementation only focus on the EventParameters and the Event types.
     *
     * and the example shown here is not valid according to the xsd file.
     */

    private static final QName EVENT = new QName("event");
    private static final QName EVENT_PARAMETERS = new QName("eventParameters");
    private static final QName QUAKE_ML = new QName("q", "quakeml");

    private static final QName PUBLIC_ID = new QName("publicID");
    private static final QName PREFERRED_ORIGIN_ID = new QName("preferredOriginID");
    private static final QName PREFERRED_MAGNITUDE_ID = new QName("preferredMagnitudeID");
    private static final QName TYPE = new QName("type");
    private static final QName DESCRIPTION = new QName("description");
    private static final QName TEXT = new QName("text");
    private static final QName TIME = new QName("time");
    private static final QName VALUE = new QName("value");
    private static final QName UNCERTAINTY = new QName("uncertainty");
    private static final QName LATITUDE = new QName("latitude");
    private static final QName ORIGIN = new QName("origin");
    private static final QName LONGITUDE = new QName("longitude");
    private static final QName DEPTH = new QName("depth");
    private static final QName CREATION_INFO = new QName("creationInfo");
    private static final QName HORIZONTAL_UNCERTAINTY = new QName("horizontalUncertainty");
    private static final QName ORIGIN_UNCERTAINTY = new QName("originUncertainty");
    private static final QName MIN_HORIZONTAL_UNCERTAINTY = new QName("minHorizontalUncertainty");
    private static final QName MAX_HORIZONTAL_UNCERTAINTY = new QName("maxHorizontalUncertainty");
    private static final QName AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY = new QName("azimuthMaxHorizontalUncertainty");
    private static final QName MAGNITUDE = new QName("magnitude");
    private static final QName MAG = new QName("mag");
    private static final QName FOCAL_MECHANISM = new QName("focalMechanism");
    private static final QName STRIKE = new QName("strike");
    private static final QName NODAL_PLANE_1 = new QName("nodalPlane1");
    private static final QName NODAL_PLANES = new QName("nodalPlanes");
    private static final QName DIP = new QName("dip");
    private static final QName RAKE = new QName("rake");
    private static final QName PREFERRED_PLANE = new QName("preferredPlane");
    private static final QName DEPTH_TYPE = new QName("depthType");
    private static final QName TIME_FIXED = new QName("timeFixed");
    private static final QName EPICENTER_FIXED = new QName("epicenterFixed");
    private static final QName REFERENCE_SYSTEM_ID = new QName("referenceSystemID");
    private static final QName QUALITY = new QName("quality");
    private static final QName AZIMUTHAL_GAP = new QName("azimuthalGap");
    private static final QName MINIMUM_DISTANCE = new QName("minimumDistance");
    private static final QName MAXIMUM_DISTANCE = new QName("maximumDistance");
    private static final QName USED_PHASE_COUNT = new QName("usedPhaseCount");
    private static final QName USED_STATION_COUNT = new QName("usedStationCount");
    private static final QName STANDARD_ERROR = new QName("standardError");
    private static final QName EVALUATION_MODE = new QName("evaluationMode");
    private static final QName EVALUATION_STATUS = new QName("evaluationStatus");
    private static final QName ORIGIN_ID = new QName("originID");
    private static final QName STATION_COUNT = new QName("stationCount");
    private static final QName AMPLITUDE = new QName("amplitude");
    private static final QName GENERIC_AMPLITUDE = new QName("genericAmplitude");

    private static final String NAN = "nan";
    private static final String ID_PREFIX = "quakeml:quakeledger/";

    private static final Pattern PATTERN_TO_MATCH_ID = Pattern.compile("^.*/([0-9]+)$");


    private final XmlObject quakeML;

    public QuakeMLOriginalXmlImpl(final XmlObject quakeML) throws ConvertFormatException {
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
    public List<IQuakeMLEvent> getEvents() {
        return Stream.of(quakeML.selectChildren(EVENT))
                .map(QuakeMLEventXmlImpl::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<String> getPublicId() {
        return Optional.empty();
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
            return ID_PREFIX + event.selectAttribute(PUBLIC_ID).newCursor().getTextValue();
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
            return getByFirstChildrenWithNLevel(PREFERRED_ORIGIN_ID).map(this::addIdPrefix);
        }

        private String addIdPrefix(final String id) {
            return ID_PREFIX + id;
        }

        @Override
        public Optional<String> getPreferredMagnitudeID() {
            return getByFirstChildrenWithNLevel(PREFERRED_MAGNITUDE_ID).map(this::addIdPrefix);
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
            return getPublicIDOfFirstChildOneLevel(ORIGIN).map(this::addIdPrefix);
        }

        @Override
        public Optional<String> getOriginTimeValue() {
            return getByFirstChildrenWithNLevel(ORIGIN, TIME, VALUE);
        }

        private String removeIfNanValue(final String possibleNaNValue) {
            if(possibleNaNValue.toLowerCase().equals("nan")) {
                return null;
            }
            return possibleNaNValue;
        }

        @Override
        public Optional<String> getOriginTimeUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN, TIME, UNCERTAINTY).map(this::removeIfNanValue);
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
            return getByFirstChildrenWithNLevel(ORIGIN, LATITUDE, UNCERTAINTY).map(this::removeIfNanValue);
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
            return getByFirstChildrenWithNLevel(ORIGIN, LONGITUDE, UNCERTAINTY).map(this::removeIfNanValue);
        }

        @Override
        public Optional<String> getOriginDepthValue() {
            return getByFirstChildrenWithNLevel(ORIGIN, DEPTH, VALUE);
        }

        @Override
        public Optional<String> getOriginDepthUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN, DEPTH, UNCERTAINTY).map(this::removeIfNanValue);
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
            return getByFirstChildrenWithNLevel(ORIGIN, CREATION_INFO, VALUE);
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
            return getByFirstChildrenWithNLevel(ORIGIN_UNCERTAINTY, HORIZONTAL_UNCERTAINTY).map(this::removeIfNanValue);
        }

        @Override
        public Optional<String> getOriginUncertaintyMinHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN_UNCERTAINTY, MIN_HORIZONTAL_UNCERTAINTY).map(this::removeIfNanValue);
        }

        @Override
        public Optional<String> getOriginUncertaintyMaxHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN_UNCERTAINTY, MAX_HORIZONTAL_UNCERTAINTY).map(this::removeIfNanValue);
        }

        @Override
        public Optional<String> getOriginUncertaintyAzimuthMaxHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN_UNCERTAINTY, AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY).map(this::removeIfNanValue);
        }

        @Override
        public Optional<String> getMagnitudePublicID() {
            return getPublicIDOfFirstChildOneLevel(MAGNITUDE).map(this::addIdPrefix);
        }

        @Override
        public Optional<String> getMagnitudeMagValue() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, MAG, VALUE);
        }

        @Override
        public Optional<String> getMagnitudeMagUncertainty() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, MAG, UNCERTAINTY).map(this::removeIfNanValue);
        }

        @Override
        public Optional<String> getMagnitudeType() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, TYPE);
        }

        @Override
        public Optional<String> getMagnitudeCreationInfoValue() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, CREATION_INFO, VALUE);
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
            return getPublicIDOfFirstChildOneLevel(FOCAL_MECHANISM).map(this::addIdPrefix);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1StrikeValue() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, STRIKE, VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, STRIKE, UNCERTAINTY).map(this::removeIfNanValue);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1DipValue() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, DIP, VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1DipUncertainty() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, DIP, UNCERTAINTY).map(this::removeIfNanValue);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1RakeValue() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, RAKE, VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, RAKE, UNCERTAINTY).map(this::removeIfNanValue);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesPreferredNodalPlane() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, PREFERRED_PLANE);
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
     * Converts any IQuakeML to an XmlObject (and uses the original quakeml format from quakeledger)
     * @param quakeML the data provider to convert it to xml
     * @return XmlObject
     */
    public static XmlObject convertToOriginalXml(final IQuakeMLDataProvider quakeML) {

        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();
        cursor.toFirstContentToken();

        cursor.beginElement(EVENT_PARAMETERS);
        cursor.insertAttributeWithValue("namespace", "http://quakeml.org/xmlns/quakeml/1.2");

        for(final IQuakeMLEvent event : quakeML.getEvents()) {
            final XmlObject eventXml = convertFeatureToXml(event);
            eventXml.newCursor().copyXmlContents(cursor);
        }

        cursor.dispose();

        return result;
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
        cursor.insertAttributeWithValue(PUBLIC_ID, onlyNumeric(publicID));

        final Optional<String> preferredOriginID = event.getPreferredOriginID().map(QuakeMLOriginalXmlImpl::onlyNumeric);
        preferredOriginID.ifPresent(new InsertElementWithText(cursor, PREFERRED_ORIGIN_ID));

        final Optional<String> preferredMagnitudeID = event.getPreferredMagnitudeID().map(QuakeMLOriginalXmlImpl::onlyNumeric);
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
        final XmlObject partOriginUncertainty = convertFeatureToXmlOriginUncertaintySection(event);
        partOriginUncertainty.newCursor().copyXmlContents(cursor);
        final XmlObject partMagnitude = convertFeatureToXmlMagnitudeSection(event);
        partMagnitude.newCursor().copyXmlContents(cursor);
        final XmlObject partFocalMechanism = convertFeatureToFocalMechanismSection(event);
        partFocalMechanism.newCursor().copyXmlContents(cursor);
        final XmlObject partAmplitude = convertFeatureToAmplitudeSection(event);
        partAmplitude.newCursor().copyXmlContents(cursor);

        cursor.dispose();

        return result;
    }

    private static String onlyNumeric(final String textWithNumbersAtTheEnd) {
        // should transform something like 'quakeml:quakeledger/84945' to '84945'
        // so should only match the number at the end
        final Matcher matcher = PATTERN_TO_MATCH_ID.matcher(textWithNumbersAtTheEnd);
        final String result;
        if(matcher.matches()) {
            result = matcher.group(1);
        } else {
            result = textWithNumbersAtTheEnd;
        }
        return result;
    }

    private static XmlObject convertFeatureToXmlOriginSection(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(ORIGIN);

        final Optional<String> originPublicID = event.getOriginPublicID().map(QuakeMLOriginalXmlImpl::onlyNumeric);
        originPublicID.ifPresent(new InsertAttributeWithText(cursor, PUBLIC_ID));

        cursor.beginElement(TIME);

        final Optional<String> timeValue = event.getOriginTimeValue();
        timeValue.ifPresent(new InsertElementWithText(cursor, VALUE));

        final String timeUncertainty = event.getOriginTimeUncertainty().filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(timeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(LATITUDE);

        cursor.insertElementWithText(VALUE, String.valueOf(event.getOriginLatitudeValue()));
        final String latitudeUncertainty = event.getOriginLatitudeUncertainty().filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(latitudeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(LONGITUDE);

        cursor.insertElementWithText(VALUE, String.valueOf(event.getOriginLongitudeValue()));
        final String longitudeUncertainty = event.getOriginLongitudeUncertainty().filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(longitudeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(DEPTH);

        final Optional<String> depthValue = event.getOriginDepthValue();
        depthValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String depthUncertainty = event.getOriginDepthUncertainty().filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
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
            cursor.insertElementWithText(VALUE, creationInfo.get());
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

        cursor.dispose();

        return result;
    }

    private static boolean notNaN(final String text) {
        return ! text.toLowerCase().equals("nan");
    }

    private static XmlObject convertFeatureToXmlOriginUncertaintySection(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(ORIGIN_UNCERTAINTY);

        final String horizontalUncertainty = event.getOriginUncertaintyHorizontalUncertainty().filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, HORIZONTAL_UNCERTAINTY).accept(horizontalUncertainty);
        final String minHorizontalUncertainty = event.getOriginUncertaintyMinHorizontalUncertainty().filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, MIN_HORIZONTAL_UNCERTAINTY).accept(minHorizontalUncertainty);
        final String maxHorizontalUncertainty = event.getOriginUncertaintyMaxHorizontalUncertainty().filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, MAX_HORIZONTAL_UNCERTAINTY).accept(maxHorizontalUncertainty);
        final String azimuthMaxHorizontalUncertainty = event.getOriginUncertaintyAzimuthMaxHorizontalUncertainty().filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
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
        final Optional<String> publicID = event.getMagnitudePublicID().map(QuakeMLOriginalXmlImpl::onlyNumeric);
        publicID.ifPresent(new InsertAttributeWithText(cursor, PUBLIC_ID));

        cursor.beginElement(MAG);

        final Optional<String> magValue = event.getMagnitudeMagValue();
        magValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String magUncertainty = event.getMagnitudeMagUncertainty().filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
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
            cursor.insertElementWithText(VALUE, creationInfo.get());
            cursor.toNextToken();
        }


        cursor.dispose();

        return result;
    }

    private static XmlObject convertFeatureToFocalMechanismSection(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(FOCAL_MECHANISM);
        final Optional<String> publicID = event.getFocalMechanismPublicID().map(QuakeMLOriginalXmlImpl::onlyNumeric);
        publicID.ifPresent(new InsertAttributeWithText(cursor, PUBLIC_ID));

        cursor.beginElement(NODAL_PLANES);
        cursor.beginElement(NODAL_PLANE_1);
        cursor.beginElement(STRIKE);

        final Optional<String> strikeValue = event.getFocalMechanismNodalPlanesNodalPlane1StrikeValue();
        strikeValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String strikeUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty().filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(strikeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(DIP);
        final Optional<String> dipValue = event.getFocalMechanismNodalPlanesNodalPlane1DipValue();
        dipValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String dipUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1DipUncertainty().filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(dipUncertainty);

        cursor.toNextToken();

        cursor.beginElement(RAKE);
        final Optional<String> rakeValue = event.getFocalMechanismNodalPlanesNodalPlane1RakeValue();
        rakeValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String rakeUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty().filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(rakeUncertainty);

        cursor.toNextToken();

        cursor.toNextToken();

        final Optional<String> preferredPlane = event.getFocalMechanismNodalPlanesPreferredNodalPlane();
        preferredPlane.ifPresent(new InsertElementWithText(cursor, PREFERRED_PLANE));

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
            type.ifPresent(new InsertElementWithText(cursor, TYPE));

            if (genericAmplitudeValue.isPresent()) {
                cursor.beginElement(GENERIC_AMPLITUDE);
                cursor.insertElementWithText(VALUE, genericAmplitudeValue.get());
            }
        }
        cursor.dispose();

        return result;
    }
}
