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
import org.n52.gfz.riesgos.formats.quakeml.IQuakeML;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeMLEvent;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuakeMLXmlImpl implements IQuakeML {

    /*
     * example:
     *
     *
<eventParameters namespace="http://quakeml.org/xmlns/quakeml/1.2">
  <event publicID="84945">
    <preferredOriginID>84945</preferredOriginID>
    <preferredMagnitudeID>84945</preferredMagnitudeID>
    <type>earthquake</type>
    <description>
      <text>stochastic</text>
    </description>
    <origin publicID="84945">
      <time>
        <value>16773-01-01T00:00:00.000000Z</value>
        <uncertainty>nan</uncertainty>
      </time>
      <latitude>
        <value>-30.9227</value>
        <uncertainty>nan</uncertainty>
      </latitude>
      <longitude>
        <value>-71.49875</value>
        <uncertainty>nan</uncertainty>
      </longitude>
      <depth>
        <value>34.75117</value>
        <uncertainty>nan</uncertainty>
      </depth>
      <creationInfo>
        <value>GFZ</value>
      </creationInfo>
    </origin>
    <originUncertainty>
      <horizontalUncertainty>nan</horizontalUncertainty>
      <minHorizontalUncertainty>nan</minHorizontalUncertainty>
      <maxHorizontalUncertainty>nan</maxHorizontalUncertainty>
      <azimuthMaxHorizontalUncertainty>nan</azimuthMaxHorizontalUncertainty>
    </originUncertainty>
    <magnitude publicID="84945">
      <mag>
        <value>8.35</value>
        <uncertainty>nan</uncertainty>
      </mag>
      <type>MW</type>
      <creationInfo>
        <value>GFZ</value>
      </creationInfo>
    </magnitude>
    <focalMechanism publicID="84945">
      <nodalPlanes>
        <nodalPlane1>
          <strike>
            <value>7.310981</value>
            <uncertainty>nan</uncertainty>
          </strike>
          <dip>
            <value>16.352970000000003</value>
            <uncertainty>nan</uncertainty>
          </dip>
          <rake>
            <value>90.0</value>
            <uncertainty>nan</uncertainty>
          </rake>
        </nodalPlane1>
        <preferredPlane>nodalPlane1</preferredPlane>
      </nodalPlanes>
    </focalMechanism>
  </event>
 </eventParameters>
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

    private final XmlObject quakeML;

    public QuakeMLXmlImpl(final XmlObject quakeML) throws ConvertFormatException {
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

    private static class QuakeMLEventXmlImpl implements IQuakeMLEvent {
        private final XmlObject event;

        public QuakeMLEventXmlImpl(final XmlObject event) {
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

        private Optional<String> getByAttributeOfFirstChildOneLevel(final QName children, final QName attribute) {
            final XmlObject[] candidates = event.selectChildren(children);
            if(candidates.length > 0) {
                return Optional.ofNullable(candidates[0].selectAttribute(attribute).newCursor().getTextValue());
            }
            return Optional.empty();
        }

        @Override
        public Optional<String> getOriginPublicID() {
            return getByAttributeOfFirstChildOneLevel(ORIGIN, PUBLIC_ID);
        }

        @Override
        public Optional<String> getOriginTimeValue() {
            return getByFirstChildrenWithNLevel(ORIGIN, TIME, VALUE);
        }

        @Override
        public Optional<String> getOriginTimeUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN, TIME, UNCERTAINTY);
        }

        @Override
        public double getOriginLatitudeValue() {
            // must be there to create a point
            return parseDouble(getByFirstChildrenWithNLevel(ORIGIN, LATITUDE, VALUE).get());
        }


        @Override
        public Optional<String> getOriginLatitudeUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN, LATITUDE, UNCERTAINTY);
        }

        private double parseDouble(final String strDouble) {
            if("nan".equals(strDouble)) {
                return Double.NaN;
            }
            return Double.parseDouble(strDouble);
        }


        @Override
        public double getOriginLongitudeValue() {
            // must be there
            return parseDouble(getByFirstChildrenWithNLevel(ORIGIN, LONGITUDE, VALUE).get());
        }

        @Override
        public Optional<String> getOriginLongitudeUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN, LONGITUDE, UNCERTAINTY);
        }

        @Override
        public Optional<String> getOriginDepthValue() {
            return getByFirstChildrenWithNLevel(ORIGIN, DEPTH, VALUE);
        }

        @Override
        public Optional<String> getOriginDepthUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN, DEPTH, UNCERTAINTY);
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
            return getByFirstChildrenWithNLevel(ORIGIN_UNCERTAINTY, HORIZONTAL_UNCERTAINTY);
        }

        @Override
        public Optional<String> getOriginUncertaintyMinHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN_UNCERTAINTY, MIN_HORIZONTAL_UNCERTAINTY);
        }

        @Override
        public Optional<String> getOriginUncertaintyMaxHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN_UNCERTAINTY, MAX_HORIZONTAL_UNCERTAINTY);
        }

        @Override
        public Optional<String> getOriginUncertaintyAzimuthMaxHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN_UNCERTAINTY, AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY);
        }

        @Override
        public Optional<String> getMagnitudePublicID() {
            return getByAttributeOfFirstChildOneLevel(MAGNITUDE, PUBLIC_ID);
        }

        @Override
        public Optional<String> getMagnitudeMagValue() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, MAG, VALUE);
        }

        @Override
        public Optional<String> getMagnitudeMagUncertainty() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, MAG, UNCERTAINTY);
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
            return getByAttributeOfFirstChildOneLevel(FOCAL_MECHANISM, PUBLIC_ID);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1StrikeValue() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, STRIKE, VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, STRIKE, UNCERTAINTY);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1DipValue() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, DIP, VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1DipUncertainty() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, DIP, UNCERTAINTY);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1RakeValue() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, RAKE, VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, RAKE, UNCERTAINTY);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesPreferredNodalPlane() {
            return getByFirstChildrenWithNLevel(FOCAL_MECHANISM, NODAL_PLANES, PREFERRED_PLANE);
        }

        @Override
        public Optional<String> getAmplitudePublicID() {
            return getByAttributeOfFirstChildOneLevel(AMPLITUDE, PUBLIC_ID);
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

    public static XmlObject convertToXml(final IQuakeML quakeML) {

        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();
        cursor.toFirstContentToken();

        cursor.beginElement(QuakeMLXmlImpl.EVENT_PARAMETERS);
        cursor.insertAttributeWithValue("namespace", "http://quakeml.org/xmlns/quakeml/1.2");

        for(final IQuakeMLEvent event : quakeML.getEvents()) {
            final XmlObject eventXml = convertFeatureToXml(event);
            eventXml.newCursor().copyXmlContents(cursor);
        }

        cursor.dispose();

        return result;
    }


    private static XmlObject convertFeatureToXml(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();
        cursor.beginElement(QuakeMLXmlImpl.EVENT);
        final String publicID = event.getPublicID();
        cursor.insertAttributeWithValue(QuakeMLXmlImpl.PUBLIC_ID, publicID);

        final Optional<String> preferredOriginID = event.getPreferredOriginID();
        if(preferredOriginID.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.PREFERRED_ORIGIN_ID, preferredOriginID.get());
        }

        final Optional<String> preferredMagnitudeID = event.getPreferredMagnitudeID();
        if(preferredMagnitudeID.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.PREFERRED_MAGNITUDE_ID, preferredMagnitudeID.get());
        }

        final Optional<String> type = event.getType();
        if(type.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.TYPE, type.get());
        }

        final Optional<String> descriptionText = event.getDescription();
        if(descriptionText.isPresent()) {
            cursor.beginElement(QuakeMLXmlImpl.DESCRIPTION);
            cursor.insertElementWithText(QuakeMLXmlImpl.TEXT, descriptionText.get());
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

    private static XmlObject convertFeatureToXmlOriginSection(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(QuakeMLXmlImpl.ORIGIN);

        final Optional<String> originPublicID = event.getOriginPublicID();
        if(originPublicID.isPresent()) {
            cursor.insertAttributeWithValue(QuakeMLXmlImpl.PUBLIC_ID, originPublicID.get());
        }

        cursor.beginElement(QuakeMLXmlImpl.TIME);
        final Optional<String> timeValue = event.getOriginTimeValue();
        if(timeValue.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.VALUE, timeValue.get());
        }

        final Optional<String> timeUncertainty = event.getOriginTimeUncertainty();
        if(timeUncertainty.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.UNCERTAINTY, timeUncertainty.get());
        }
        cursor.toNextToken();

        cursor.beginElement(QuakeMLXmlImpl.LATITUDE);
        cursor.insertElementWithText(QuakeMLXmlImpl.VALUE, String.valueOf(event.getOriginLatitudeValue()));
        final Optional<String> latitudeUncertainty = event.getOriginLatitudeUncertainty();
        if(latitudeUncertainty.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.UNCERTAINTY, latitudeUncertainty.get());
        }
        cursor.toNextToken();

        cursor.beginElement(QuakeMLXmlImpl.LONGITUDE);
        cursor.insertElementWithText(QuakeMLXmlImpl.VALUE, String.valueOf(event.getOriginLongitudeValue()));
        final Optional<String> longitudeUncertainty = event.getOriginLongitudeUncertainty();
        if(longitudeUncertainty.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.UNCERTAINTY, longitudeUncertainty.get());
        }
        cursor.toNextToken();

        cursor.beginElement(QuakeMLXmlImpl.DEPTH);
        final Optional<String> depthValue = event.getOriginDepthValue();
        if(depthValue.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.VALUE, depthValue.get());
        }
        final Optional<String> depthUncertainty = event.getOriginDepthUncertainty();
        if(depthUncertainty.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.UNCERTAINTY, depthUncertainty.get());
        }
        cursor.toNextToken();

        final Optional<String> depthType = event.getOriginDepthType();
        if(depthType.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.DEPTH_TYPE, depthType.get());
        }

        final Optional<String> timeFixed = event.getOriginTimeFixed();
        if(timeFixed.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.TIME_FIXED, timeFixed.get());
        }

        final Optional<String> epicenterFixed = event.getOriginEpicenterFixed();
        if(epicenterFixed.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.EPICENTER_FIXED, epicenterFixed.get());
        }

        final Optional<String> referenceSystemID = event.getOriginReferenceSystemID();
        if(referenceSystemID.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.REFERENCE_SYSTEM_ID, referenceSystemID.get());
        }

        final Optional<String> type = event.getOriginType();
        if(type.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.TYPE, type.get());
        }

        final Optional<String> creationInfo = event.getOriginCreationInfoValue();
        if(creationInfo.isPresent()) {
            cursor.beginElement(QuakeMLXmlImpl.CREATION_INFO);
            cursor.insertElementWithText(QuakeMLXmlImpl.VALUE, creationInfo.get());
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

            cursor.beginElement(QuakeMLXmlImpl.QUALITY);

            if (qualityAzimuthalGap.isPresent()) {
                cursor.insertElementWithText(QuakeMLXmlImpl.AZIMUTHAL_GAP, qualityAzimuthalGap.get());
            }
            if (qualityMinimumDistance.isPresent()) {
                cursor.insertElementWithText(QuakeMLXmlImpl.MINIMUM_DISTANCE, qualityMinimumDistance.get());
            }
            if (qualityMaximumDistance.isPresent()) {
                cursor.insertElementWithText(QuakeMLXmlImpl.MAXIMUM_DISTANCE, qualityMaximumDistance.get());
            }
            if (qualityUsedPhaseCount.isPresent()) {
                cursor.insertElementWithText(QuakeMLXmlImpl.USED_PHASE_COUNT, qualityUsedPhaseCount.get());
            }
            if (qualityUsedStationCount.isPresent()) {
                cursor.insertElementWithText(QuakeMLXmlImpl.USED_STATION_COUNT, qualityUsedStationCount.get());
            }
            if (qualityStandardError.isPresent()) {
                cursor.insertElementWithText(QuakeMLXmlImpl.STANDARD_ERROR, qualityStandardError.get());
            }


            cursor.toNextToken();
        }

        final Optional<String> evaluationMode = event.getOriginEvaluationMode();
        if(evaluationMode.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.EVALUATION_MODE, evaluationMode.get());
        }

        final Optional<String> evaluationStatus = event.getOriginEvaluationStatus();
        if(evaluationStatus.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.EVALUATION_STATUS, evaluationStatus.get());
        }


        cursor.dispose();

        return result;
    }

    private static XmlObject convertFeatureToXmlOriginUncertaintySection(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(QuakeMLXmlImpl.ORIGIN_UNCERTAINTY);
        final Optional<String> horizontalUncertainty = event.getOriginUncertaintyHorizontalUncertainty();
        if(horizontalUncertainty.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.HORIZONTAL_UNCERTAINTY, horizontalUncertainty.get());
        }
        final Optional<String> minHorizontalUncertainty = event.getOriginUncertaintyMinHorizontalUncertainty();
        if(minHorizontalUncertainty.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.MIN_HORIZONTAL_UNCERTAINTY, minHorizontalUncertainty.get());
        }
        final Optional<String> maxHorizontalUncertainty = event.getOriginUncertaintyMaxHorizontalUncertainty();
        if(maxHorizontalUncertainty.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.MAX_HORIZONTAL_UNCERTAINTY, maxHorizontalUncertainty.get());
        }
        final Optional<String> azimuthMaxHorizontalUncertainty = event.getOriginUncertaintyAzimuthMaxHorizontalUncertainty();
        if(azimuthMaxHorizontalUncertainty.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY, azimuthMaxHorizontalUncertainty.get());
        }

        cursor.toNextToken();

        cursor.dispose();

        return result;
    }

    private static XmlObject convertFeatureToXmlMagnitudeSection(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(QuakeMLXmlImpl.MAGNITUDE);
        final Optional<String> publicID = event.getMagnitudePublicID();
        if(publicID.isPresent()) {
            cursor.insertAttributeWithValue(QuakeMLXmlImpl.PUBLIC_ID, publicID.get());
        }

        cursor.beginElement(QuakeMLXmlImpl.MAG);

        final Optional<String> magValue = event.getMagnitudeMagValue();
        if(magValue.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.VALUE, magValue.get());
        }
        final Optional<String> magUncertainty = event.getMagnitudeMagUncertainty();
        if(magUncertainty.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.UNCERTAINTY, magUncertainty.get());
        }

        cursor.toNextToken();


        final Optional<String> type = event.getMagnitudeType();
        if(type.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.TYPE, type.get());
        }

        final Optional<String> evaluationStatus = event.getMagnitudeEvaluationStatus();
        if(evaluationStatus.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.EVALUATION_STATUS, evaluationStatus.get());
        }

        final Optional<String> originID = event.getMagnitudeOriginID();
        if(originID.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.ORIGIN_ID, originID.get());
        }

        final Optional<String> stationCount = event.getMagnitudeStationCount();
        if(stationCount.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.STATION_COUNT, stationCount.get());
        }

        final Optional<String> creationInfo = event.getMagnitudeCreationInfoValue();
        if(creationInfo.isPresent()) {
            cursor.beginElement(QuakeMLXmlImpl.CREATION_INFO);
            cursor.insertElementWithText(QuakeMLXmlImpl.VALUE, creationInfo.get());
            cursor.toNextToken();
        }


        cursor.dispose();

        return result;
    }

    private static XmlObject convertFeatureToFocalMechanismSection(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(QuakeMLXmlImpl.FOCAL_MECHANISM);
        final Optional<String> publicID = event.getFocalMechanismPublicID();
        if(publicID.isPresent()) {
            cursor.insertAttributeWithValue(QuakeMLXmlImpl.PUBLIC_ID, publicID.get());
        }

        cursor.beginElement(QuakeMLXmlImpl.NODAL_PLANES);
        cursor.beginElement(QuakeMLXmlImpl.NODAL_PLANE_1);
        cursor.beginElement(QuakeMLXmlImpl.STRIKE);
        final Optional<String> strikeValue = event.getFocalMechanismNodalPlanesNodalPlane1StrikeValue();
        if(strikeValue.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.VALUE, strikeValue.get());
        }
        final Optional<String> strikeUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty();
        if(strikeUncertainty.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.UNCERTAINTY, strikeUncertainty.get());
        }
        cursor.toNextToken();

        cursor.beginElement(QuakeMLXmlImpl.DIP);
        final Optional<String> dipValue = event.getFocalMechanismNodalPlanesNodalPlane1DipValue();
        if(dipValue.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.VALUE, dipValue.get());
        }
        final Optional<String> dipUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1DipUncertainty();
        if(dipUncertainty.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.UNCERTAINTY, dipUncertainty.get());
        }
        cursor.toNextToken();

        cursor.beginElement(QuakeMLXmlImpl.RAKE);
        final Optional<String> rakeValue = event.getFocalMechanismNodalPlanesNodalPlane1RakeValue();
        if(rakeValue.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.VALUE, rakeValue.get());
        }
        final Optional<String> rakeUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty();
        if(rakeUncertainty.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.UNCERTAINTY, rakeUncertainty.get());
        }
        cursor.toNextToken();

        cursor.toNextToken();

        final Optional<String> preferredPlane = event.getFocalMechanismNodalPlanesPreferredNodalPlane();
        if(preferredPlane.isPresent()) {
            cursor.insertElementWithText(QuakeMLXmlImpl.PREFERRED_PLANE, preferredPlane.get());
        }

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

            cursor.beginElement(QuakeMLXmlImpl.AMPLITUDE);


            if (publicID.isPresent()) {
                cursor.insertAttributeWithValue(QuakeMLXmlImpl.PUBLIC_ID, publicID.get());
            }
            if (type.isPresent()) {
                cursor.insertElementWithText(QuakeMLXmlImpl.TYPE, type.get());
            }

            if (genericAmplitudeValue.isPresent()) {
                cursor.beginElement(QuakeMLXmlImpl.GENERIC_AMPLITUDE);
                cursor.insertElementWithText(QuakeMLXmlImpl.VALUE, genericAmplitudeValue.get());
            }
        }
        cursor.dispose();

        return result;
    }
}
