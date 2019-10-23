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
 * the original xml that does not).
 */
public class QuakeMLValidatedXmlImpl implements IQuakeMLDataProvider {

    /*
     * example: see in the test/resource folder
     * under
     * "org/n52/gfz/riesgos/convertformats/quakeml_validated_one_feature.xml"
     *
     *
     * *******************************************************
     * This implementation queries mostly this format, however there is the
     * possibility to have much more information
     * (multiple nodal planes for example).
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
     * when you look at the QuakeML-BED-1.2.xsd file for the
     * xml-schema-definition,
     * you can see that this implementation only focus on the
     * EventParameters and the Event types.
     */

    /**
     * Namespace for quakeml.
     */
    private static final String NS = "http://quakeml.org/xmlns/bed/1.2";

    /**
     * Event element.
     */
    private static final QName EVENT = new QName(NS, "event");
    /**
     * Event parameters element.
     */
    private static final QName EVENT_PARAMETERS =
            new QName(NS, "eventParameters");
    /**
     * QuakeML element.
     */
    private static final QName QUAKE_ML = new QName(NS, "quakeml");

    /**
     * Public ID element.
     */
    private static final QName PUBLIC_ID = new QName("publicID");
    /**
     * Preferred origin id element.
     */
    private static final QName PREFERRED_ORIGIN_ID =
            new QName(NS, "preferredOriginID");
    /**
     * Preferred magnitude id element.
     */
    private static final QName PREFERRED_MAGNITUDE_ID =
            new QName(NS, "preferredMagnitudeID");
    /**
     * Type element.
     */
    private static final QName TYPE = new QName(NS, "type");
    /**
     * Description element.
     */
    private static final QName DESCRIPTION =
            new QName(NS, "description");
    /**
     * Text element.
     */
    private static final QName TEXT = new QName(NS, "text");
    /**
     * Time element.
     */
    private static final QName TIME = new QName(NS, "time");
    /**
     * Value element.
     */
    private static final QName VALUE = new QName(NS, "value");
    /**
     * Uncertainty element.
     */
    private static final QName UNCERTAINTY =
            new QName(NS, "uncertainty");
    /**
     * Latitude element.
     */
    private static final QName LATITUDE = new QName(NS, "latitude");
    /**
     * Origin element.
     */
    private static final QName ORIGIN = new QName(NS, "origin");
    /**
     * Longitude element.
     */
    private static final QName LONGITUDE = new QName(NS, "longitude");
    /**
     * Depth element.
     */
    private static final QName DEPTH = new QName(NS, "depth");
    /**
     * Creation info element.
     */
    private static final QName CREATION_INFO =
            new QName(NS, "creationInfo");
    /**
     * Author element.
     */
    private static final QName AUTHOR = new QName(NS, "author");
    /**
     * Horizontal uncertainty element.
     */
    private static final QName HORIZONTAL_UNCERTAINTY =
            new QName(NS, "horizontalUncertainty");
    /**
     * Origin uncertainty element.
     */
    private static final QName ORIGIN_UNCERTAINTY =
            new QName(NS, "originUncertainty");
    /**
     * Min horizontal uncertainty element.
     */
    private static final QName MIN_HORIZONTAL_UNCERTAINTY =
            new QName(NS, "minHorizontalUncertainty");
    /**
     * Max horizontal uncertainty element.
     */
    private static final QName MAX_HORIZONTAL_UNCERTAINTY =
            new QName(NS, "maxHorizontalUncertainty");
    /**
     * Azimutz max horizontal uncertainty element.
     */
    private static final QName AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY =
            new QName(NS, "azimuthMaxHorizontalUncertainty");
    /**
     * Magnitude element.
     */
    private static final QName MAGNITUDE = new QName(NS, "magnitude");
    /**
     * Mag element.
     */
    private static final QName MAG = new QName(NS, "mag");
    /**
     * Focal mechanism element.
     */
    private static final QName FOCAL_MECHANISM =
            new QName(NS, "focalMechanism");
    /**
     * Strike element.
     */
    private static final QName STRIKE = new QName(NS, "strike");
    /**
     * Nodal plane 1 element.
     */
    private static final QName NODAL_PLANE_1 =
            new QName(NS, "nodalPlane1");
    /**
     * Nodal planes element.
     */
    private static final QName NODAL_PLANES = new QName(NS, "nodalPlanes");
    /**
     * Dip element.
     */
    private static final QName DIP = new QName(NS, "dip");
    /**
     * Rake element.
     */
    private static final QName RAKE = new QName(NS, "rake");

    // from here on there are just attributes -> no namespace required
    /**
     * Attribute name for preferred plane.
     */
    private static final QName PREFERRED_PLANE = new QName("preferredPlane");
    /**
     * Attribute name for depth type.
     */
    private static final QName DEPTH_TYPE = new QName(NS, "depthType");
    /**
     * Attribute name for time fixed.
     */
    private static final QName TIME_FIXED = new QName(NS, "timeFixed");
    /**
     * Attribute name for epicenter fixed.
     */
    private static final QName EPICENTER_FIXED = new QName("epicenterFixed");
    /**
     * Attribute name for reference system ID.
     */
    private static final QName REFERENCE_SYSTEM_ID =
            new QName(NS, "referenceSystemID");
    /**
     * Attribute name for quality.
     */
    private static final QName QUALITY = new QName(NS, "quality");
    /**
     * Attribute name for azimuhthal gap.
     */
    private static final QName AZIMUTHAL_GAP =
            new QName(NS, "azimuthalGap");
    /**
     * Attribute name for minimum distance.
     */
    private static final QName MINIMUM_DISTANCE =
            new QName(NS, "minimumDistance");
    /**
     * Attribute name for maximum distance.
     */
    private static final QName MAXIMUM_DISTANCE =
            new QName(NS, "maximumDistance");
    /**
     * Attribute name for used phase count.
     */
    private static final QName USED_PHASE_COUNT =
            new QName(NS, "usedPhaseCount");
    /**
     * Attribute name for used station count.
     */
    private static final QName USED_STATION_COUNT =
            new QName(NS, "usedStationCount");
    /**
     * Attribute name for standard error.
     */
    private static final QName STANDARD_ERROR = new QName(NS, "standardError");
    /**
     * Attribute name for evaluation mode.
     */
    private static final QName EVALUATION_MODE =
            new QName(NS, "evaluationMode");
    /**
     * Attribute name for evaluation status.
     */
    private static final QName EVALUATION_STATUS =
            new QName(NS, "evaluationStatus");
    /**
     * Attribute name for origin id.
     */
    private static final QName ORIGIN_ID = new QName(NS, "originID");
    /**
     * Attribute name for station count.
     */
    private static final QName STATION_COUNT = new QName(NS, "stationCount");
    /**
     * Attribute name for amplitude.
     */
    private static final QName AMPLITUDE = new QName(NS, "amplitude");
    /**
     * Attribute name for generic amplitude.
     */
    private static final QName GENERIC_AMPLITUDE =
            new QName(NS, "genericAmplitude");

    /**
     * Constant for NaN values.
     */
    private static final String NAN = "NaN";
    /**
     * Constant for a reference to the nodal plane.
     */
    private static final String NODAL_PLANE = "nodalPlane";

    /**
     * The xml object to work extract quakeml data.
     */
    private final XmlObject quakeML;

    /**
     * Default constructor.
     * @param xmlQuakeML quakeml xml object
     * @throws ConvertFormatException can throw an ConvertFormatException
     * on a problem on reading the elements
     */
    public QuakeMLValidatedXmlImpl(
            final XmlObject xmlQuakeML) throws ConvertFormatException {
        this.quakeML = validateNotNull(findEventParameters(xmlQuakeML));
    }

    /**
     * Finds the xml object with the event parameters.
     * @param root root of the xml document
     * @return xml object with the event parameters
     * @throws ConvertFormatException exception that is thrown in case
     * of a problem on the reading the xml data
     */
    private static XmlObject findEventParameters(
            final XmlObject root) throws ConvertFormatException {
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

    /**
     * Validates that an element is not null.
     * @param xmlObject element to check
     * @return the element if it is not null
     * @throws ConvertFormatException exception if the element is null
     */
    private static XmlObject validateNotNull(
            final XmlObject xmlObject) throws ConvertFormatException {
        if (xmlObject == null) {
            throw new ConvertFormatException("eventParameters is null");
        }
        return xmlObject;
    }

    /**
     *
     * @return string with the public id
     */
    @Override
    public Optional<String> getPublicId() {
        final XmlObject attribute = quakeML.selectAttribute(PUBLIC_ID);
        if (attribute != null) {
            return Optional.ofNullable(attribute.newCursor().getTextValue());
        }
        return Optional.empty();
    }

    /**
     *
     * @return list of the events in the xml object
     */
    @Override
    public List<IQuakeMLEvent> getEvents() {
        return Stream.of(quakeML.selectChildren(EVENT))
                .map(QuakeMLEventXmlImpl::new)
                .collect(Collectors.toList());
    }

    /**
     * Single event implementation that works with the xml object.
     */
    private static class QuakeMLEventXmlImpl implements IQuakeMLEvent {
        /**
         * Xml object to work with (contains only the single event).
         */
        private final XmlObject event;

        /**
         * Constructs the event class.
         * @param singleEvent xml with the xml data for the event
         */
        QuakeMLEventXmlImpl(final XmlObject singleEvent) {
            this.event = singleEvent;
        }

        /**
         *
         * @return public id of the event
         */
        @Override
        public String getPublicID() {
            return event.selectAttribute(PUBLIC_ID).newCursor().getTextValue();
        }

        /**
         * Private method to search for a string in a hierarchy
         * of sub elements.
         * @param children var arg with the qnames for the sub elements
         * @return optional with the text value
         */
        private Optional<String> getByFirstChildrenWithNLevel(
                final QName... children) {
            XmlObject searchElement = event;
            for (final QName childrenQName : children) {
                final XmlObject[] candidates =
                        searchElement.selectChildren(childrenQName);
                if (candidates.length > 0) {
                    searchElement = candidates[0];
                } else {
                    return Optional.empty();
                }
            }
            return Optional.ofNullable(
                    searchElement.newCursor().getTextValue());
        }

        /**
         *
         * @return preferred origin id
         */
        @Override
        public Optional<String> getPreferredOriginID() {
            return getByFirstChildrenWithNLevel(PREFERRED_ORIGIN_ID);
        }

        /**
         *
         * @return preferred magnitude id
         */
        @Override
        public Optional<String> getPreferredMagnitudeID() {
            return getByFirstChildrenWithNLevel(PREFERRED_MAGNITUDE_ID);
        }

        /**
         *
         * @return type
         */
        @Override
        public Optional<String> getType() {
            return getByFirstChildrenWithNLevel(TYPE);
        }

        /**
         *
         * @return description
         */
        @Override
        public Optional<String> getDescription() {
            return getByFirstChildrenWithNLevel(DESCRIPTION, TEXT);
        }

        /**
         * Private method to return the public id attribute of the given
         * sub element.
         * @param children qname for the sub element
         * @return optional of th public id text value
         */
        private Optional<String> getPublicIDOfFirstChildOneLevel(
                final QName children) {
            final XmlObject[] candidates = event.selectChildren(children);
            if (candidates.length > 0) {
                return Optional.ofNullable(
                        candidates[0]
                                .selectAttribute(PUBLIC_ID)
                                .newCursor()
                                .getTextValue());
            }
            return Optional.empty();
        }

        /**
         *
         * @return origin public id
         */
        @Override
        public Optional<String> getOriginPublicID() {
            return getPublicIDOfFirstChildOneLevel(ORIGIN);
        }

        /**
         *
         * @return origin public time value as string
         */
        @Override
        public Optional<String> getOriginTimeValue() {
            return getByFirstChildrenWithNLevel(ORIGIN, TIME, VALUE);
        }

        /**
         *
         * @return origin public time uncertainty
         */
        @Override
        public Optional<String> getOriginTimeUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN, TIME, UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         * Returns null if the value is nan.
         * @param possibleNaNValue value that may be "nan"
         * @return null or the given value
         */
        private String removeIfNanValue(final String possibleNaNValue) {
            if (possibleNaNValue.toLowerCase().equals("nan")) {
                return null;
            }
            return possibleNaNValue;
        }

        /**
         *
         * @return double value of the origin latitude
         */
        @Override
        public double getOriginLatitudeValue() {
            final Optional<String> str =
                    getByFirstChildrenWithNLevel(ORIGIN, LATITUDE, VALUE);
            if (str.isPresent()) {
                final String doubleStr = str.get();
                return parseDouble(doubleStr);
            }
            return Double.NaN;
        }


        /**
         *
         * @return origin latitude uncertainty
         */
        @Override
        public Optional<String> getOriginLatitudeUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN, LATITUDE, UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         * Parses the string to double.
         * @param strDouble string that should be parsed to double
         * @return double value or Double.NaN
         */
        private double parseDouble(final String strDouble) {
            if ("nan".equals(strDouble)) {
                return Double.NaN;
            }
            return Double.parseDouble(strDouble);
        }

        /**
         *
         * @return double value of the origin longitude
         */
        @Override
        public double getOriginLongitudeValue() {
            final Optional<String> str =
                    getByFirstChildrenWithNLevel(ORIGIN, LONGITUDE, VALUE);
            if (str.isPresent()) {
                final String doubleStr = str.get();
                return parseDouble(doubleStr);
            }
            return Double.NaN;
        }

        /**
         *
         * @return origin longitude uncertainty
         */
        @Override
        public Optional<String> getOriginLongitudeUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN, LONGITUDE, UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         *
         * @return origin depth value
         */
        @Override
        public Optional<String> getOriginDepthValue() {
            return getByFirstChildrenWithNLevel(ORIGIN, DEPTH, VALUE);
        }

        /**
         *
         * @return origin depth uncertainty
         */
        @Override
        public Optional<String> getOriginDepthUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN, DEPTH, UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         *
         * @return origin depth type
         */
        @Override
        public Optional<String> getOriginDepthType() {
            return getByFirstChildrenWithNLevel(ORIGIN, DEPTH_TYPE);
        }

        /**
         *
         * @return origin time fixed
         */
        @Override
        public Optional<String> getOriginTimeFixed() {
            return getByFirstChildrenWithNLevel(ORIGIN, TIME_FIXED);
        }

        /**
         *
         * @return origin epicenter fixed
         */
        @Override
        public Optional<String> getOriginEpicenterFixed() {
            return getByFirstChildrenWithNLevel(ORIGIN, EPICENTER_FIXED);
        }

        /**
         *
         * @return origin reference system id
         */
        @Override
        public Optional<String> getOriginReferenceSystemID() {
            return getByFirstChildrenWithNLevel(ORIGIN, REFERENCE_SYSTEM_ID);
        }

        /**
         *
         * @return origin time
         */
        @Override
        public Optional<String> getOriginType() {
            return getByFirstChildrenWithNLevel(ORIGIN, TYPE);
        }

        /**
         *
         * @return origin creation info value
         */
        @Override
        public Optional<String> getOriginCreationInfoValue() {
            return getByFirstChildrenWithNLevel(ORIGIN, CREATION_INFO, AUTHOR);
        }

        /**
         *
         * @return origin quality azimuthal gap
         */
        @Override
        public Optional<String> getOriginQualityAzimuthalGap() {
            return getByFirstChildrenWithNLevel(ORIGIN, QUALITY, AZIMUTHAL_GAP);
        }

        /**
         *
         * @return origin quality minimum distance
         */
        @Override
        public Optional<String> getOriginQualityMinimumDistance() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN, QUALITY, MINIMUM_DISTANCE);
        }

        /**
         *
         * @return origin quality maximum distance
         */
        @Override
        public Optional<String> getOriginQualityMaximumDistance() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN, QUALITY, MAXIMUM_DISTANCE);
        }

        /**
         *
         * @return origin quality used phase count
         */
        @Override
        public Optional<String> getOriginQualityUsedPhaseCount() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN, QUALITY, USED_PHASE_COUNT);
        }

        /**
         *
         * @return origin qulity used station count
         */
        @Override
        public Optional<String> getOriginQualityUsedStationCount() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN, QUALITY, USED_STATION_COUNT);
        }

        /**
         *
         * @return origin qulity standard error
         */
        @Override
        public Optional<String> getOriginQualityStandardError() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN, QUALITY, STANDARD_ERROR);
        }

        /**
         *
         * @return origin evaluation mode
         */
        @Override
        public Optional<String> getOriginEvaluationMode() {
            return getByFirstChildrenWithNLevel(ORIGIN, EVALUATION_MODE);
        }

        /**
         *
         * @return origin evaluation status
         */
        @Override
        public Optional<String> getOriginEvaluationStatus() {
            return getByFirstChildrenWithNLevel(ORIGIN, EVALUATION_STATUS);
        }

        /**
         *
         * @return origin uncertainty horizontal uncertainty
         */
        @Override
        public Optional<String> getOriginUncertaintyHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN, ORIGIN_UNCERTAINTY, HORIZONTAL_UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         *
         * @return origin uncertainty min horizontal uncertainty
         */
        @Override
        public Optional<String> getOriginUncertaintyMinHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN, ORIGIN_UNCERTAINTY, MIN_HORIZONTAL_UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         *
         * @return origin uncertainty max horizontal uncertainty
         */
        @Override
        public Optional<String> getOriginUncertaintyMaxHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN, ORIGIN_UNCERTAINTY, MAX_HORIZONTAL_UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         *
         * @return origin uncertainty azimuth max horizontal uncertainty
         */
        @Override
        public Optional<String>
        getOriginUncertaintyAzimuthMaxHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN,
                    ORIGIN_UNCERTAINTY,
                    AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY
            ).map(this::removeIfNanValue);
        }

        /**
         *
         * @return magnitude public id
         */
        @Override
        public Optional<String> getMagnitudePublicID() {
            return getPublicIDOfFirstChildOneLevel(MAGNITUDE);
        }

        /**
         *
         * @return magnitude mag value
         */
        @Override
        public Optional<String> getMagnitudeMagValue() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, MAG, VALUE);
        }

        /**
         *
         * @return magnitude mag uncertainty
         */
        @Override
        public Optional<String> getMagnitudeMagUncertainty() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, MAG, UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         *
         * @return magnitude type
         */
        @Override
        public Optional<String> getMagnitudeType() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, TYPE);
        }

        /**
         *
         * @return magnitude creation info value
         */
        @Override
        public Optional<String> getMagnitudeCreationInfoValue() {
            return getByFirstChildrenWithNLevel(
                    MAGNITUDE, CREATION_INFO, AUTHOR);
        }

        /**
         *
         * @return magnitude evaluation status
         */
        @Override
        public Optional<String> getMagnitudeEvaluationStatus() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, EVALUATION_STATUS);
        }

        /**
         *
         * @return magnitude origin id
         */
        @Override
        public Optional<String> getMagnitudeOriginID() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, ORIGIN_ID);
        }

        /**
         *
         * @return magnitude station count
         */
        @Override
        public Optional<String> getMagnitudeStationCount() {
            return getByFirstChildrenWithNLevel(MAGNITUDE, STATION_COUNT);
        }

        /**
         *
         * @return focal mechanism public id
         */
        @Override
        public Optional<String> getFocalMechanismPublicID() {
            return getPublicIDOfFirstChildOneLevel(FOCAL_MECHANISM);
        }

        /**
         *
         * @return strike value
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesNodalPlane1StrikeValue() {
            return getByFirstChildrenWithNLevel(
                    FOCAL_MECHANISM,
                    NODAL_PLANES,
                    NODAL_PLANE_1,
                    STRIKE,
                    VALUE);
        }

        /**
         *
         * @return strike uncertainty
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty() {
            return getByFirstChildrenWithNLevel(
                    FOCAL_MECHANISM,
                    NODAL_PLANES,
                    NODAL_PLANE_1,
                    STRIKE,
                    UNCERTAINTY
            ).map(this::removeIfNanValue);
        }

        /**
         *
         * @return dip value
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesNodalPlane1DipValue() {
            return getByFirstChildrenWithNLevel(
                    FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, DIP, VALUE);
        }

        /**
         *
         * @return dip uncertainty
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesNodalPlane1DipUncertainty() {
            return getByFirstChildrenWithNLevel(
                    FOCAL_MECHANISM,
                    NODAL_PLANES,
                    NODAL_PLANE_1,
                    DIP,
                    UNCERTAINTY
            ).map(this::removeIfNanValue);
        }

        /**
         *
         * @return rake value
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesNodalPlane1RakeValue() {
            return getByFirstChildrenWithNLevel(
                    FOCAL_MECHANISM, NODAL_PLANES, NODAL_PLANE_1, RAKE, VALUE);
        }

        /**
         *
         * @return rake uncertainty
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty() {
            return getByFirstChildrenWithNLevel(
                    FOCAL_MECHANISM,
                    NODAL_PLANES,
                    NODAL_PLANE_1,
                    RAKE,
                    UNCERTAINTY
            ).map(this::removeIfNanValue);
        }

        /**
         *
         * @return focal mechanism nodal plane preferred nodel plane
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesPreferredNodalPlane() {

            XmlObject searchElement = event;
            for (final QName childrenQName : Arrays.asList(
                    FOCAL_MECHANISM, NODAL_PLANES)) {
                final XmlObject[] candidates =
                        searchElement.selectChildren(childrenQName);
                if (candidates.length > 0) {
                    searchElement = candidates[0];
                }
            }

            final Optional<String> attributeText;
            if (searchElement != null) {
                final String pureAttributeTextAsNumber =
                        searchElement.selectAttribute(PREFERRED_PLANE)
                                .newCursor().getTextValue();
                final String pureAttributeText =
                        NODAL_PLANE + pureAttributeTextAsNumber;
                attributeText = Optional.of(pureAttributeText);
            } else {
                attributeText = Optional.empty();
            }

            return attributeText;
        }

        /**
         *
         * @return amplitude public id
         */
        @Override
        public Optional<String> getAmplitudePublicID() {
            return getPublicIDOfFirstChildOneLevel(AMPLITUDE);
        }

        /**
         *
         * @return amplitude type
         */
        @Override
        public Optional<String> getAmplitudeType() {
            return getByFirstChildrenWithNLevel(AMPLITUDE, TYPE);
        }

        /**
         *
         * @return amplitude generic amplitude value
         */
        @Override
        public Optional<String> getAmplitudeGenericAmplitudeValue() {
            return getByFirstChildrenWithNLevel(
                    AMPLITUDE, GENERIC_AMPLITUDE, VALUE);
        }
    }

    /**
     * Converts any IQuakeML to an XmlObject (and uses the validated quakeml).
     * @param quakeML the data provider to convert it to xml
     * @return XmlObject
     */
    public static XmlObject convertToValidatedXml(
            final IQuakeMLDataProvider quakeML) {

        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();
        cursor.toFirstContentToken();

        cursor.beginElement(EVENT_PARAMETERS);
        final String publicId = quakeML.getPublicId().orElse(
                "quakeml:quakeledger/0");
        cursor.insertAttributeWithValue(PUBLIC_ID, publicId);


        for (final IQuakeMLEvent event : quakeML.getEvents()) {
            final XmlObject eventXml = convertFeatureToXml(event);
            eventXml.newCursor().copyXmlContents(cursor);
        }

        cursor.dispose();

        return result;
    }

    /**
     * Test tht the text is not nan.
     * @param text text to test
     * @return true if not nan
     */
    private static boolean notNaN(final String text) {
        return !text.toLowerCase().equals("nan");
    }

    /**
     * Consumer implementation to insert an element.
     */
    private static class InsertElementWithText implements Consumer<String> {
        /**
         * XmlCursor to insert the data.
         */
        private final XmlCursor cursor;
        /**
         * QName for the element to insert.
         */
        private final QName qName;

        /**
         * Default constructor.
         * @param aCursor xml cursor to use for inserting
         * @param aQName qname for the element to insert to insert
         */
        InsertElementWithText(final XmlCursor aCursor, final QName aQName) {
            this.cursor = aCursor;
            this.qName = aQName;
        }

        /**
         *
         * @param value element text to insert
         */
        @Override
        public void accept(final String value) {
            cursor.insertElementWithText(qName, value);
        }
    }

    /**
     * Consumer implementation to create an an attribute.
     */
    private static class InsertAttributeWithText implements Consumer<String> {
        /**
         * Xml cursor to use for insert data.
         */
        private final XmlCursor cursor;
        /**
         * QName of the element to insert the attribute.
         */
        private final QName qName;

        /**
         * Default constructor.
         * @param aCursor xml cursor to use for inserting the data
         * @param aQName qname for the attribute to insert the text
         */
        InsertAttributeWithText(final XmlCursor aCursor, final QName aQName) {
            this.cursor = aCursor;
            this.qName = aQName;
        }

        /**
         * Inserts the text to for an attribute.
         * @param value attribute text to insert.
         */
        @Override
        public void accept(final String value) {
            cursor.insertAttributeWithValue(qName, value);
        }
    }

    /**
     * Method to convert an event to xml.
     * @param aEvent event to convert to xml
     * @return XmlObject with the quakeml data
     */
    private static XmlObject convertFeatureToXml(final IQuakeMLEvent aEvent) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();
        cursor.beginElement(EVENT);
        final String publicID = aEvent.getPublicID();
        cursor.insertAttributeWithValue(PUBLIC_ID, publicID);

        final Optional<String> preferredOriginID =
                aEvent.getPreferredOriginID();
        preferredOriginID.ifPresent(
                new InsertElementWithText(cursor, PREFERRED_ORIGIN_ID));

        final Optional<String> preferredMagnitudeID =
                aEvent.getPreferredMagnitudeID();
        preferredMagnitudeID.ifPresent(
                new InsertElementWithText(cursor, PREFERRED_MAGNITUDE_ID));

        final Optional<String> type = aEvent.getType();
        type.ifPresent(new InsertElementWithText(cursor, TYPE));

        final Optional<String> descriptionText = aEvent.getDescription();
        if (descriptionText.isPresent()) {
            cursor.beginElement(DESCRIPTION);
            cursor.insertElementWithText(TEXT, descriptionText.get());
            cursor.toNextToken();
        }

        final XmlObject partOrigin = convertFeatureToXmlOriginSection(aEvent);
        partOrigin.newCursor().copyXmlContents(cursor);
        final XmlObject partMagnitude =
                convertFeatureToXmlMagnitudeSection(aEvent);
        partMagnitude.newCursor().copyXmlContents(cursor);
        final XmlObject partFocalMechanism =
                convertFeatureToFocalMechanismSection(aEvent);
        partFocalMechanism.newCursor().copyXmlContents(cursor);
        final XmlObject partAmplitude =
                convertFeatureToAmplitudeSection(aEvent);
        partAmplitude.newCursor().copyXmlContents(cursor);

        cursor.dispose();

        return result;
    }

    /**
     * Convert the event to the origin section of the xml.
     * @param aEvent event to convert
     * @return XmlObject with the origin section only
     */
    private static XmlObject convertFeatureToXmlOriginSection(
            final IQuakeMLEvent aEvent) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(ORIGIN);

        final Optional<String> originPublicID = aEvent.getOriginPublicID();
        originPublicID.ifPresent(
                new InsertAttributeWithText(cursor, PUBLIC_ID));

        cursor.beginElement(TIME);

        final Optional<String> timeValue = aEvent.getOriginTimeValue();
        timeValue.ifPresent(new InsertElementWithText(cursor, VALUE));

        final String timeUncertainty = aEvent.getOriginTimeUncertainty()
                .filter(QuakeMLValidatedXmlImpl::notNaN)
                .orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(timeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(LATITUDE);

        cursor.insertElementWithText(
                VALUE,
                String.valueOf(aEvent.getOriginLatitudeValue()));
        final String latitudeUncertainty = aEvent.getOriginLatitudeUncertainty()
                .filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY)
                .accept(latitudeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(LONGITUDE);

        cursor.insertElementWithText(
                VALUE,
                String.valueOf(aEvent.getOriginLongitudeValue()));
        final String longitudeUncertainty =
                aEvent.getOriginLongitudeUncertainty()
                        .filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY)
                .accept(longitudeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(DEPTH);

        final Optional<String> depthValue = aEvent.getOriginDepthValue();
        depthValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        String depthUncertainty =
                aEvent.getOriginDepthUncertainty()
                        .filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(depthUncertainty);

        cursor.toNextToken();

        final Optional<String> depthType = aEvent.getOriginDepthType();
        depthType.ifPresent(new InsertElementWithText(cursor, DEPTH_TYPE));

        final Optional<String> timeFixed = aEvent.getOriginTimeFixed();
        timeFixed.ifPresent(new InsertElementWithText(cursor, TIME_FIXED));

        final Optional<String> epicenterFixed =
                aEvent.getOriginEpicenterFixed();
        epicenterFixed.ifPresent(
                new InsertElementWithText(cursor, EPICENTER_FIXED));

        final Optional<String> referenceSystemID =
                aEvent.getOriginReferenceSystemID();
        referenceSystemID.ifPresent(
                new InsertElementWithText(cursor, REFERENCE_SYSTEM_ID));

        final Optional<String> type = aEvent.getOriginType();
        type.ifPresent(new InsertElementWithText(cursor, TYPE));

        final Optional<String> creationInfo =
                aEvent.getOriginCreationInfoValue();
        if (creationInfo.isPresent()) {
            cursor.beginElement(CREATION_INFO);
            cursor.insertElementWithText(AUTHOR, creationInfo.get());
            cursor.toNextToken();
        }


        final Optional<String> qualityAzimuthalGap =
                aEvent.getOriginQualityAzimuthalGap();
        final Optional<String> qualityMinimumDistance =
                aEvent.getOriginQualityMinimumDistance();
        final Optional<String> qualityMaximumDistance =
                aEvent.getOriginQualityMaximumDistance();
        final Optional<String> qualityUsedPhaseCount =
                aEvent.getOriginQualityUsedPhaseCount();
        final Optional<String> qualityUsedStationCount =
                aEvent.getOriginQualityUsedStationCount();
        final Optional<String> qualityStandardError =
                aEvent.getOriginQualityStandardError();

        if (Stream.of(
                qualityAzimuthalGap,
                qualityMinimumDistance,
                qualityMaximumDistance,
                qualityUsedPhaseCount,
                qualityUsedStationCount,
                qualityStandardError
        ).anyMatch(Optional::isPresent)) {

            cursor.beginElement(QUALITY);

            qualityAzimuthalGap.ifPresent(
                    new InsertElementWithText(cursor, AZIMUTHAL_GAP));
            qualityMinimumDistance.ifPresent(
                    new InsertElementWithText(cursor, MINIMUM_DISTANCE));
            qualityMaximumDistance.ifPresent(
                    new InsertElementWithText(cursor, MAXIMUM_DISTANCE));
            qualityUsedPhaseCount.ifPresent(
                    new InsertElementWithText(cursor, USED_PHASE_COUNT));
            qualityUsedStationCount.ifPresent(
                    new InsertElementWithText(cursor, USED_STATION_COUNT));
            qualityStandardError.ifPresent(
                    new InsertElementWithText(cursor, STANDARD_ERROR));

            cursor.toNextToken();
        }

        final Optional<String> evaluationMode =
                aEvent.getOriginEvaluationMode();
        evaluationMode.ifPresent(
                new InsertElementWithText(cursor, EVALUATION_MODE));
        final Optional<String> evaluationStatus =
                aEvent.getOriginEvaluationStatus();
        evaluationStatus.ifPresent(
                new InsertElementWithText(cursor, EVALUATION_STATUS));

        final XmlObject partOriginUncertainty =
                convertFeatureToXmlOriginUncertaintySection(aEvent);
        partOriginUncertainty.newCursor().copyXmlContents(cursor);

        cursor.dispose();

        return result;
    }

    /**
     * Converts the event to an origin uncertainty section of the xml.
     * @param event event to convert
     * @return origin uncertaity section
     */
    private static XmlObject convertFeatureToXmlOriginUncertaintySection(
            final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(ORIGIN_UNCERTAINTY);

        final String horizontalUncertainty =
                event.getOriginUncertaintyHorizontalUncertainty()
                        .filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, HORIZONTAL_UNCERTAINTY)
                .accept(horizontalUncertainty);
        final String minHorizontalUncertainty =
                event.getOriginUncertaintyMinHorizontalUncertainty()
                        .filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, MIN_HORIZONTAL_UNCERTAINTY)
                .accept(minHorizontalUncertainty);
        final String maxHorizontalUncertainty =
                event.getOriginUncertaintyMaxHorizontalUncertainty()
                        .filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, MAX_HORIZONTAL_UNCERTAINTY)
                .accept(maxHorizontalUncertainty);
        final String azimuthMaxHorizontalUncertainty =
                event.getOriginUncertaintyAzimuthMaxHorizontalUncertainty()
                        .filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY)
                .accept(azimuthMaxHorizontalUncertainty);

        cursor.toNextToken();

        cursor.dispose();

        return result;
    }

    /**
     * Converts the event to a magnitude section of the quakeml xml.
     * @param event event to convert
     * @return magnitude section
     */
    private static XmlObject convertFeatureToXmlMagnitudeSection(
            final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(MAGNITUDE);
        final Optional<String> publicID = event.getMagnitudePublicID();
        publicID.ifPresent(new InsertAttributeWithText(cursor, PUBLIC_ID));

        cursor.beginElement(MAG);

        final Optional<String> magValue = event.getMagnitudeMagValue();
        magValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String magUncertainty =
                event.getMagnitudeMagUncertainty()
                        .filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(magUncertainty);

        cursor.toNextToken();


        final Optional<String> type = event.getMagnitudeType();
        type.ifPresent(new InsertElementWithText(cursor, TYPE));

        final Optional<String> evaluationStatus =
                event.getMagnitudeEvaluationStatus();
        evaluationStatus.ifPresent(
                new InsertElementWithText(cursor, EVALUATION_STATUS));

        final Optional<String> originID = event.getMagnitudeOriginID();
        originID.ifPresent(new InsertElementWithText(cursor, ORIGIN_ID));

        final Optional<String> stationCount = event.getMagnitudeStationCount();
        stationCount.ifPresent(
                new InsertElementWithText(cursor, STATION_COUNT));

        final Optional<String> creationInfo =
                event.getMagnitudeCreationInfoValue();
        if (creationInfo.isPresent()) {
            cursor.beginElement(CREATION_INFO);
            cursor.insertElementWithText(AUTHOR, creationInfo.get());
            cursor.toNextToken();
        }

        cursor.dispose();

        return result;
    }

    /**
     * Removes all chars if a nodal plane text.
     * @param textWithNodalPlaneTextInFront text to use for the replacement
     * @return string without chars in the text
     * (numbers and special chars only)
     */
    private static String removeNodalPlaneTextBefore(
            final String textWithNodalPlaneTextInFront) {
        return textWithNodalPlaneTextInFront.replaceAll(
                "[a-zA-Z]",
                "");
    }

    /**
     * Converts an event to the focal mechanism section of the quakeml xml.
     * @param event event to covert
     * @return focal mechanisnm section
     */
    private static XmlObject convertFeatureToFocalMechanismSection(
            final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(FOCAL_MECHANISM);
        final Optional<String> publicID = event.getFocalMechanismPublicID();
        publicID.ifPresent(new InsertAttributeWithText(cursor, PUBLIC_ID));

        cursor.beginElement(NODAL_PLANES);
        final Optional<String> preferredPlane =
                event.getFocalMechanismNodalPlanesPreferredNodalPlane();
        if (preferredPlane.isPresent()) {
            final String preferredPlaneValue = preferredPlane.get();
            final String preferredPlaneJustIntegerValue =
                    removeNodalPlaneTextBefore(preferredPlaneValue);
            new InsertAttributeWithText(cursor, PREFERRED_PLANE)
                    .accept(preferredPlaneJustIntegerValue);
        }



        cursor.beginElement(NODAL_PLANE_1);
        cursor.beginElement(STRIKE);

        final Optional<String> strikeValue =
                event.getFocalMechanismNodalPlanesNodalPlane1StrikeValue();
        strikeValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String strikeUncertainty =
                event.getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty()
                        .filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY)
                .accept(strikeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(DIP);
        final Optional<String> dipValue =
                event.getFocalMechanismNodalPlanesNodalPlane1DipValue();
        dipValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String dipUncertainty =
                event.getFocalMechanismNodalPlanesNodalPlane1DipUncertainty()
                        .filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(dipUncertainty);

        cursor.toNextToken();

        cursor.beginElement(RAKE);
        final Optional<String> rakeValue =
                event.getFocalMechanismNodalPlanesNodalPlane1RakeValue();
        rakeValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String rakeUncertainty =
                event.getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty()
                        .filter(QuakeMLValidatedXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(rakeUncertainty);

        cursor.toNextToken();

        cursor.toNextToken();

        cursor.dispose();

        return result;
    }

    /**
     * Converts the event to an amplitude section of the quakeml xml.
     * @param event event to convert
     * @return amplitude section
     */
    private static XmlObject convertFeatureToAmplitudeSection(
            final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        final Optional<String> publicID = event.getAmplitudePublicID();
        final Optional<String> type = event.getAmplitudeType();
        final Optional<String> genericAmplitudeValue =
                event.getAmplitudeGenericAmplitudeValue();

        if (Stream.of(
                publicID,
                type,
                genericAmplitudeValue
        ).anyMatch(Optional::isPresent)) {

            cursor.beginElement(AMPLITUDE);

            publicID.ifPresent(new InsertAttributeWithText(cursor, PUBLIC_ID));
            type.ifPresent(new InsertAttributeWithText(cursor, TYPE));

            if (genericAmplitudeValue.isPresent()) {
                cursor.beginElement(GENERIC_AMPLITUDE);
                cursor.insertElementWithText(
                        VALUE,
                        genericAmplitudeValue.get());
            }
        }
        cursor.dispose();

        return result;
    }
}
