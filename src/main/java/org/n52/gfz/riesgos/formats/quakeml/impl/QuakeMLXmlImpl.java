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

    public static final QName EVENT = new QName("event");
    public static final QName EVENT_PARAMETERS = new QName("eventParameters");
    public static final QName QUAKE_ML = new QName("q", "quakeml");

    public static final QName PUBLIC_ID = new QName("publicID");
    public static final QName PREFERRED_ORIGIN_ID = new QName("preferredOriginID");
    public static final QName PREFERRED_MAGNITUDE_ID = new QName("preferredMagnitudeID");
    public static final QName TYPE = new QName("type");
    public static final QName DESCRIPTION = new QName("description");
    public static final QName TEXT = new QName("text");
    public static final QName TIME = new QName("time");
    public static final QName VALUE = new QName("value");
    public static final QName UNCERTAINTY = new QName("uncertainty");
    public static final QName LATITUDE = new QName("latitude");
    public static final QName ORIGIN = new QName("origin");
    public static final QName LONGITUDE = new QName("longitude");
    public static final QName DEPTH = new QName("depth");
    public static final QName CREATION_INFO = new QName("creationInfo");
    public static final QName HORIZONTAL_UNCERTAINTY = new QName("horizontalUncertainty");
    public static final QName ORIGIN_UNCERTAINTY = new QName("originUncertainty");
    public static final QName MIN_HORIZONTAL_UNCERTAINTY = new QName("minHorizontalUncertainty");
    public static final QName MAX_HORIZONTAL_UNCERTAINTY = new QName("maxHorizontalUncertainty");
    public static final QName AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY = new QName("azimuthMaxHorizontalUncertainty");
    public static final QName MAGNITUDE = new QName("magnitude");
    public static final QName MAG = new QName("mag");
    public static final QName FOCAL_MECHANISM = new QName("focalMechanism");
    public static final QName STRIKE = new QName("strike");
    public static final QName NODAL_PLANE_1 = new QName("nodalPlane1");
    public static final QName NODAL_PLANES = new QName("nodalPlanes");
    public static final QName DIP = new QName("dip");
    public static final QName RAKE = new QName("rake");
    public static final QName PREFERRED_PLANE = new QName("preferredPlane");
    public static final QName DEPTH_TYPE = new QName("depthType");
    public static final QName TIME_FIXED = new QName("timeFixed");
    public static final QName EPICENTER_FIXED = new QName("epicenterFixed");
    public static final QName REFERENCE_SYSTEM_ID = new QName("referenceSystemID");
    public static final QName QUALITY = new QName("quality");
    public static final QName AZIMUTHAL_GAP = new QName("azimuthalGap");
    public static final QName MINIMUM_DISTANCE = new QName("minimumDistance");
    public static final QName MAXIMUM_DISTANCE = new QName("maximumDistance");
    public static final QName USED_PHASE_COUNT = new QName("usedPhaseCount");
    public static final QName USED_STATION_COUNT = new QName("usedStationCount");
    public static final QName STANDARD_ERROR = new QName("standardError");
    public static final QName EVALUATION_MODE = new QName("evaluationMode");
    public static final QName EVALUATION_STATUS = new QName("evaluationStatus");
    public static final QName ORIGIN_ID = new QName("originID");
    public static final QName STATION_COUNT = new QName("stationCount");
    public static final QName AMPLITUDE = new QName("amplitude");
    public static final QName GENERIC_AMPLITUDE = new QName("genericAmplitude");

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
}
