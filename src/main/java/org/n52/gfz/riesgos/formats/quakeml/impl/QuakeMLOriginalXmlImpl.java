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
     * under
     *  "org/n52/gfz/riesgos/convertformats/
     * quakeml_from_original_quakeledger_one_feature.xml"
     *
     *
     * *******************************************************
     * This implementation queries mostly this format, however there is the
     * possibility to have much more information
     * (multiple nodal planes for example).
     * These information can't be queried with the current approach.
     * This implementations focus only on the way quakeml is provided by
     * the program in the following github repository:
     * https://github.com/GFZ-Centre-for-Early-Warning/quakeledger
     *
     * when you look at the QuakeML-BED-1.2.xsd file for the
     * xml-schema-definition,
     * you can see that this implementation only focus on the EventParameters
     * and the Event types.
     *
     * and the example shown here is not valid according to the xsd file.
     */

    /**
     * Event element.
     */
    private static final QName EVENT = new QName("event");
    /**
     * Event parameters element.
     */
    private static final QName EVENT_PARAMETERS = new QName("eventParameters");
    /**
     * Quakeml element.
     */
    private static final QName QUAKE_ML = new QName("q", "quakeml");

    /**
     * Public id.
     */
    private static final QName PUBLIC_ID = new QName("publicID");
    /**
     * Preferred origin id.
     */
    private static final QName PREFERRED_ORIGIN_ID =
            new QName("preferredOriginID");
    /**
     * Preferred magnitude id.
     */
    private static final QName PREFERRED_MAGNITUDE_ID =
            new QName("preferredMagnitudeID");
    /**
     * Type.
     */
    private static final QName TYPE = new QName("type");
    /**
     * Description.
     */
    private static final QName DESCRIPTION = new QName("description");
    /**
     * Text.
     */
    private static final QName TEXT = new QName("text");
    /**
     * Time.
     */
    private static final QName TIME = new QName("time");
    /**
     * Value.
     */
    private static final QName VALUE = new QName("value");
    /**
     * Uncertainty.
     */
    private static final QName UNCERTAINTY = new QName("uncertainty");
    /**
     * Latitude.
     */
    private static final QName LATITUDE = new QName("latitude");
    /**
     * Origin.
     */
    private static final QName ORIGIN = new QName("origin");
    /**
     * Longitude.
     */
    private static final QName LONGITUDE = new QName("longitude");
    /**
     * Depth.
     */
    private static final QName DEPTH = new QName("depth");
    /**
     * Creation info.
     */
    private static final QName CREATION_INFO = new QName("creationInfo");
    /**
     * Horizontal uncertainty.
     */
    private static final QName HORIZONTAL_UNCERTAINTY =
            new QName("horizontalUncertainty");
    /**
     * Origin uncertainty.
     */
    private static final QName ORIGIN_UNCERTAINTY =
            new QName("originUncertainty");
    /**
     * Min horizontal uncertainty.
     */
    private static final QName MIN_HORIZONTAL_UNCERTAINTY =
            new QName("minHorizontalUncertainty");
    /**
     * Max horizontal uncertainty.
     */
    private static final QName MAX_HORIZONTAL_UNCERTAINTY =
            new QName("maxHorizontalUncertainty");
    /**
     * Azimith max horizontal uncertainty.
     */
    private static final QName AZIMUTH_MAX_HORIZONTAL_UNCERTAINTY =
            new QName("azimuthMaxHorizontalUncertainty");
    /**
     * Magnitude.
     */
    private static final QName MAGNITUDE = new QName("magnitude");
    /**
     * Mag.
     */
    private static final QName MAG = new QName("mag");
    /**
     * Focal mechanism.
     */
    private static final QName FOCAL_MECHANISM = new QName("focalMechanism");
    /**
     * Strike.
     */
    private static final QName STRIKE = new QName("strike");
    /**
     * Nodal plane 1.
     */
    private static final QName NODAL_PLANE_1 = new QName("nodalPlane1");
    /**
     * Nodal planes.
     */
    private static final QName NODAL_PLANES = new QName("nodalPlanes");
    /**
     * Dip.
     */
    private static final QName DIP = new QName("dip");
    /**
     * Rake.
     */
    private static final QName RAKE = new QName("rake");
    /**
     * Preferred plane.
     */
    private static final QName PREFERRED_PLANE = new QName("preferredPlane");
    /**
     * Depth type.
     */
    private static final QName DEPTH_TYPE = new QName("depthType");
    /**
     * Time fixed.
     */
    private static final QName TIME_FIXED = new QName("timeFixed");
    /**
     * Epicenter fixed.
     */
    private static final QName EPICENTER_FIXED = new QName("epicenterFixed");
    /**
     * Reference system id.
     */
    private static final QName REFERENCE_SYSTEM_ID =
            new QName("referenceSystemID");
    /**
     * Quality.
     */
    private static final QName QUALITY = new QName("quality");
    /**
     * Azimuthal gap.
     */
    private static final QName AZIMUTHAL_GAP = new QName("azimuthalGap");
    /**
     * Minimum distance.
     */
    private static final QName MINIMUM_DISTANCE = new QName("minimumDistance");
    /**
     * Maximum distance.
     */
    private static final QName MAXIMUM_DISTANCE = new QName("maximumDistance");
    /**
     * Used phase count.
     */
    private static final QName USED_PHASE_COUNT =
            new QName("usedPhaseCount");
    /**
     * Used station count.
     */
    private static final QName USED_STATION_COUNT =
            new QName("usedStationCount");
    /**
     * Standard error.
     */
    private static final QName STANDARD_ERROR = new QName("standardError");
    /**
     * Evaluation mode.
     */
    private static final QName EVALUATION_MODE = new QName("evaluationMode");
    /**
     * Evaluation status.
     */
    private static final QName EVALUATION_STATUS =
            new QName("evaluationStatus");
    /**
     * Origin id.
     */
    private static final QName ORIGIN_ID = new QName("originID");
    /**
     * Station count.
     */
    private static final QName STATION_COUNT = new QName("stationCount");
    /**
     * Amplitude.
     */
    private static final QName AMPLITUDE = new QName("amplitude");
    /**
     * Generic amplitude.
     */
    private static final QName GENERIC_AMPLITUDE =
            new QName("genericAmplitude");

    /**
     * String constant for nan values.
     */
    private static final String NAN = "nan";
    /**
     * Prefix to add for the id.
     */
    private static final String ID_PREFIX = "quakeml:quakeledger/";

    /**
     * Pattern to match the id.
     */
    private static final Pattern PATTERN_TO_MATCH_ID =
            Pattern.compile("^.*/([0-9]+)$");


    /**
     * Xml element to extract all the fields for quakeml.
     */
    private final XmlObject quakeML;

    /**
     * Default constructor.
     * @param xmlQuakeMl xml element with the data for quakeml.
     * @throws ConvertFormatException it can throw a convert format exception
     * if there is no way to get the event parameters element
     */
    public QuakeMLOriginalXmlImpl(
            final XmlObject xmlQuakeMl) throws ConvertFormatException {
        this.quakeML = validateNotNull(findEventParameters(xmlQuakeMl));
    }

    /**
     * Method to find the event parameters in the basic root xml element.
     * @param root root xml element.
     * @return xml object with the event parameters
     * @throws ConvertFormatException it can throw a convert format exception
     * if there is no way to get the event parameters element
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
     * Validates that the element is not null.
     * @param xmlObject element to check
     * @return element if not null
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
     * @return list with the events
     */
    @Override
    public List<IQuakeMLEvent> getEvents() {
        return Stream.of(quakeML.selectChildren(EVENT))
                .map(QuakeMLEventXmlImpl::new)
                .collect(Collectors.toList());
    }

    /**
     *
     * @return public id of the quakeml data
     */
    @Override
    public Optional<String> getPublicId() {
        return Optional.empty();
    }


    /**
     * Class for the data for a single event.
     * Implementation for xml (old one).
     */
    private static class QuakeMLEventXmlImpl implements IQuakeMLEvent {
        /**
         * Xml element with the data for one event.
         */
        private final XmlObject event;

        /**
         * Default constructor.
         * @param aEvent xml element with the data for one event
         */
        QuakeMLEventXmlImpl(final XmlObject aEvent) {
            this.event = aEvent;
        }

        /**
         *
         * @return public id
         */
        @Override
        public String getPublicID() {
            return ID_PREFIX + event.selectAttribute(PUBLIC_ID)
                    .newCursor().getTextValue();
        }

        /**
         * Method to search for the text value of sub elements.
         * @param children hierarchy of sub elements
         * @return optional string with the text for the last element
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
         * @return Preferred origin id
         */
        @Override
        public Optional<String> getPreferredOriginID() {
            return getByFirstChildrenWithNLevel(PREFERRED_ORIGIN_ID)
                    .map(this::addIdPrefix);
        }

        /**
         * Adds a prefix to the id.
         * @param id id to add the prefix to
         * @return prefix + id
         */
        private String addIdPrefix(final String id) {
            return ID_PREFIX + id;
        }

        /**
         *
         * @return preferred magnitude id
         */
        @Override
        public Optional<String> getPreferredMagnitudeID() {
            return getByFirstChildrenWithNLevel(PREFERRED_MAGNITUDE_ID)
                    .map(this::addIdPrefix);
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
         * Searches for the public id of the actual children element.
         * @param children children element
         * @return optional string of the text of the children element
         */
        private Optional<String> getPublicIDOfFirstChildOneLevel(
                final QName children) {
            final XmlObject[] candidates = event.selectChildren(children);
            if (candidates.length > 0) {
                return Optional.ofNullable(
                        candidates[0].selectAttribute(PUBLIC_ID)
                                .newCursor().getTextValue());
            }
            return Optional.empty();
        }

        /**
         *
         * @return origin public id
         */
        @Override
        public Optional<String> getOriginPublicID() {
            return getPublicIDOfFirstChildOneLevel(ORIGIN)
                    .map(this::addIdPrefix);
        }

        /**
         *
         * @return origin time value
         */
        @Override
        public Optional<String> getOriginTimeValue() {
            return getByFirstChildrenWithNLevel(ORIGIN, TIME, VALUE);
        }

        /**
         * Function to replace an nan with a null.
         * @param possibleNaNValue string that may be nan
         * @return null in case of nan or the given value
         */
        private String removeIfNanValue(final String possibleNaNValue) {
            if ("nan".equalsIgnoreCase(possibleNaNValue)) {
                return null;
            }
            return possibleNaNValue;
        }

        /**
         *
         * @return time uncertainty
         */
        @Override
        public Optional<String> getOriginTimeUncertainty() {
            return getByFirstChildrenWithNLevel(ORIGIN, TIME, UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         *
         * @return origin latitude value
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
         * Function to parse doubles with extra handling for nan values.
         * @param strDouble string to parse to double
         * @return double
         */
        private double parseDouble(final String strDouble) {
            if ("nan".equals(strDouble)) {
                return Double.NaN;
            }
            return Double.parseDouble(strDouble);
        }


        /**
         *
         * @return longitude value
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
         * @return longitude uncertainty
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
         * @return origin type
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
            return getByFirstChildrenWithNLevel(ORIGIN, CREATION_INFO, VALUE);
        }

        /**
         *
         * @return origin azimuthal gap
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
         * @return origin quality used station count
         */
        @Override
        public Optional<String> getOriginQualityUsedStationCount() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN, QUALITY, USED_STATION_COUNT);
        }

        /**
         *
         * @return origin quality standard error
         */
        @Override
        public Optional<String> getOriginQualityStandardError() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN, QUALITY, STANDARD_ERROR);
        }

        /**
         *
         * @return origin evaluation model
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
                    ORIGIN_UNCERTAINTY, HORIZONTAL_UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         *
         * @return origin uncertainty min horizontal uncertainty
         */
        @Override
        public Optional<String> getOriginUncertaintyMinHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN_UNCERTAINTY, MIN_HORIZONTAL_UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         *
         * @return Origin uncertainty max horizontal uncertainty
         */
        @Override
        public Optional<String> getOriginUncertaintyMaxHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN_UNCERTAINTY, MAX_HORIZONTAL_UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         *
         * @return origin uncertainty azimuthal max horizontal uncertainty
         */
        @Override
        public Optional<String>
        getOriginUncertaintyAzimuthMaxHorizontalUncertainty() {
            return getByFirstChildrenWithNLevel(
                    ORIGIN_UNCERTAINTY, AZIMUTH_MAX_HORIZONTAL_UNCERTAINTY)
                    .map(this::removeIfNanValue);
        }

        /**
         *
         * @return magnitude public id
         */
        @Override
        public Optional<String> getMagnitudePublicID() {
            return getPublicIDOfFirstChildOneLevel(MAGNITUDE)
                    .map(this::addIdPrefix);
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
            return getByFirstChildrenWithNLevel(
                    MAGNITUDE, MAG, UNCERTAINTY).map(this::removeIfNanValue);
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
                    MAGNITUDE, CREATION_INFO, VALUE);
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
         * @return magntiude station count
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
            return getPublicIDOfFirstChildOneLevel(FOCAL_MECHANISM)
                    .map(this::addIdPrefix);
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
         * @return preferred nodal plane
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesPreferredNodalPlane() {
            return getByFirstChildrenWithNLevel(
                    FOCAL_MECHANISM, NODAL_PLANES, PREFERRED_PLANE);
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
     * Converts any IQuakeML to an XmlObject
     * (and uses the original quakeml format from quakeledger).
     * @param quakeML the data provider to convert it to xml
     * @return XmlObject
     */
    public static XmlObject convertToOriginalXml(
            final IQuakeMLDataProvider quakeML) {

        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();
        cursor.toFirstContentToken();

        cursor.beginElement(EVENT_PARAMETERS);
        cursor.insertAttributeWithValue(
                "namespace", "http://quakeml.org/xmlns/quakeml/1.2");

        for (final IQuakeMLEvent event : quakeML.getEvents()) {
            final XmlObject eventXml = convertFeatureToXml(event);
            eventXml.newCursor().copyXmlContents(cursor);
        }

        cursor.dispose();

        return result;
    }

    /**
     * Consumer to insert one element with text.
     */
    private static class InsertElementWithText implements Consumer<String> {
        /**
         * XmlCursor to insert the data.
         */
        private final XmlCursor cursor;
        /**
         * QName of the element to insert.
         */
        private final QName qName;

        /**
         * Default constructor.
         * @param aCursor xml cursor to insert the data
         * @param aQName qname of the element to insert
         */
        InsertElementWithText(final XmlCursor aCursor, final QName aQName) {
            this.cursor = aCursor;
            this.qName = aQName;
        }

        /**
         * Adds the string value as text in the given element.
         * @param value text value to set in the element
         */
        @Override
        public void accept(final String value) {
            cursor.insertElementWithText(qName, value);
        }
    }

    /**
     * Consumer to set an attribute with a value.
     */
    private static class InsertAttributeWithText implements Consumer<String> {
        /**
         * Xml cursor to insert the data.
         */
        private final XmlCursor cursor;
        /**
         * QName element for the attribute.
         */
        private final QName qName;

        /**
         * Default constructor.
         * @param aCursor xml cursor to insert the data
         * @param aQName qname for the attribute
         */
        InsertAttributeWithText(final XmlCursor aCursor, final QName aQName) {
            this.cursor = aCursor;
            this.qName = aQName;
        }

        /**
         * Sets the given text as attribute value.
         * @param value value to set attribute value
         */
        @Override
        public void accept(final String value) {
            cursor.insertAttributeWithValue(qName, value);
        }
    }


    /**
     * Method to convert the event to xml (original one).
     * @param event event to convert
     * @return XmlObject of the event
     */
    private static XmlObject convertFeatureToXml(
            final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();
        cursor.beginElement(EVENT);
        final String publicID = event.getPublicID();
        cursor.insertAttributeWithValue(PUBLIC_ID, onlyNumeric(publicID));

        final Optional<String> preferredOriginID =
                event.getPreferredOriginID()
                        .map(QuakeMLOriginalXmlImpl::onlyNumeric);
        preferredOriginID.ifPresent(
                new InsertElementWithText(cursor, PREFERRED_ORIGIN_ID));

        final Optional<String> preferredMagnitudeID =
                event.getPreferredMagnitudeID()
                        .map(QuakeMLOriginalXmlImpl::onlyNumeric);
        preferredMagnitudeID.ifPresent(
                new InsertElementWithText(cursor, PREFERRED_MAGNITUDE_ID));

        final Optional<String> type = event.getType();
        type.ifPresent(new InsertElementWithText(cursor, TYPE));

        final Optional<String> descriptionText = event.getDescription();
        if (descriptionText.isPresent()) {
            cursor.beginElement(DESCRIPTION);
            cursor.insertElementWithText(TEXT, descriptionText.get());
            cursor.toNextToken();
        }

        final XmlObject partOrigin = convertFeatureToXmlOriginSection(event);
        partOrigin.newCursor().copyXmlContents(cursor);
        final XmlObject partOriginUncertainty =
                convertFeatureToXmlOriginUncertaintySection(event);
        partOriginUncertainty.newCursor().copyXmlContents(cursor);
        final XmlObject partMagnitude =
                convertFeatureToXmlMagnitudeSection(event);
        partMagnitude.newCursor().copyXmlContents(cursor);
        final XmlObject partFocalMechanism =
                convertFeatureToFocalMechanismSection(event);
        partFocalMechanism.newCursor().copyXmlContents(cursor);
        final XmlObject partAmplitude = convertFeatureToAmplitudeSection(event);
        partAmplitude.newCursor().copyXmlContents(cursor);

        cursor.dispose();

        return result;
    }

    /**
     * Removes text as quakeml:quakeleder/ before a number.
     * @param textWithNumbersAtTheEnd text to clean
     * @return cleaned text
     */
    private static String onlyNumeric(final String textWithNumbersAtTheEnd) {
        // should transform something like
        // 'quakeml:quakeledger/84945' to '84945'
        // so should only match the number at the end
        final Matcher matcher =
                PATTERN_TO_MATCH_ID.matcher(textWithNumbersAtTheEnd);
        final String result;
        if (matcher.matches()) {
            result = matcher.group(1);
        } else {
            result = textWithNumbersAtTheEnd;
        }
        return result;
    }

    /**
     * Method to convert the origin section.
     * @param event event to extract from
     * @return xml object with the secion for the origin
     */
    private static XmlObject convertFeatureToXmlOriginSection(
            final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(ORIGIN);

        final Optional<String> originPublicID = event.getOriginPublicID()
                .map(QuakeMLOriginalXmlImpl::onlyNumeric);
        originPublicID.ifPresent(
                new InsertAttributeWithText(cursor, PUBLIC_ID));

        cursor.beginElement(TIME);

        final Optional<String> timeValue = event.getOriginTimeValue();
        timeValue.ifPresent(new InsertElementWithText(cursor, VALUE));

        final String timeUncertainty = event.getOriginTimeUncertainty()
                .filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(timeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(LATITUDE);

        cursor.insertElementWithText(VALUE,
                String.valueOf(event.getOriginLatitudeValue()));
        final String latitudeUncertainty = event.getOriginLatitudeUncertainty()
                .filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY)
                .accept(latitudeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(LONGITUDE);

        cursor.insertElementWithText(
                VALUE, String.valueOf(event.getOriginLongitudeValue()));
        final String longitudeUncertainty = event
                .getOriginLongitudeUncertainty()
                .filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY)
                .accept(longitudeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(DEPTH);

        final Optional<String> depthValue = event.getOriginDepthValue();
        depthValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String depthUncertainty = event.getOriginDepthUncertainty()
                .filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(depthUncertainty);

        cursor.toNextToken();

        final Optional<String> depthType = event.getOriginDepthType();
        depthType.ifPresent(new InsertElementWithText(cursor, DEPTH_TYPE));

        final Optional<String> timeFixed = event.getOriginTimeFixed();
        timeFixed.ifPresent(new InsertElementWithText(cursor, TIME_FIXED));

        final Optional<String> epicenterFixed = event.getOriginEpicenterFixed();
        epicenterFixed.ifPresent(
                new InsertElementWithText(cursor, EPICENTER_FIXED));

        final Optional<String> referenceSystemID =
                event.getOriginReferenceSystemID();
        referenceSystemID.ifPresent(
                new InsertElementWithText(cursor, REFERENCE_SYSTEM_ID));

        final Optional<String> type = event.getOriginType();
        type.ifPresent(new InsertElementWithText(cursor, TYPE));

        final Optional<String> creationInfo =
                event.getOriginCreationInfoValue();
        if (creationInfo.isPresent()) {
            cursor.beginElement(CREATION_INFO);
            cursor.insertElementWithText(VALUE, creationInfo.get());
            cursor.toNextToken();
        }


        final Optional<String> qualityAzimuthalGap =
                event.getOriginQualityAzimuthalGap();
        final Optional<String> qualityMinimumDistance =
                event.getOriginQualityMinimumDistance();
        final Optional<String> qualityMaximumDistance =
                event.getOriginQualityMaximumDistance();
        final Optional<String> qualityUsedPhaseCount =
                event.getOriginQualityUsedPhaseCount();
        final Optional<String> qualityUsedStationCount =
                event.getOriginQualityUsedStationCount();
        final Optional<String> qualityStandardError =
                event.getOriginQualityStandardError();

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

        final Optional<String> evaluationMode = event.getOriginEvaluationMode();
        evaluationMode.ifPresent(
                new InsertElementWithText(cursor, EVALUATION_MODE));
        final Optional<String> evaluationStatus =
                event.getOriginEvaluationStatus();
        evaluationStatus.ifPresent(
                new InsertElementWithText(cursor, EVALUATION_STATUS));

        cursor.dispose();

        return result;
    }

    /**
     * Tests that the value is not nan.
     * @param text text to test
     * @return true if the text is not nan
     */
    private static boolean notNaN(final String text) {
        return !"nan".equalsIgnoreCase(text);
    }

    /**
     * Converts the section of origin uncertainty to xml.
     * @param event event to extract from
     * @return quakeml section of origin uncertainty in xml
     */
    private static XmlObject convertFeatureToXmlOriginUncertaintySection(
            final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(ORIGIN_UNCERTAINTY);

        final String horizontalUncertainty =
                event.getOriginUncertaintyHorizontalUncertainty()
                        .filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, HORIZONTAL_UNCERTAINTY)
                .accept(horizontalUncertainty);
        final String minHorizontalUncertainty =
                event.getOriginUncertaintyMinHorizontalUncertainty()
                        .filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, MIN_HORIZONTAL_UNCERTAINTY)
                .accept(minHorizontalUncertainty);
        final String maxHorizontalUncertainty =
                event.getOriginUncertaintyMaxHorizontalUncertainty()
                        .filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, MAX_HORIZONTAL_UNCERTAINTY)
                .accept(maxHorizontalUncertainty);
        final String azimuthMaxHorizontalUncertainty =
                event.getOriginUncertaintyAzimuthMaxHorizontalUncertainty()
                        .filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, AZIMUTH_MAX_HORIZONTAL_UNCERTAINTY)
                .accept(azimuthMaxHorizontalUncertainty);

        cursor.toNextToken();

        cursor.dispose();

        return result;
    }

    /**
     * Converss the section for magnitude to xml.
     * @param event event to extract from
     * @return xml object with the magnitude section
     */
    private static XmlObject convertFeatureToXmlMagnitudeSection(
            final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(MAGNITUDE);
        final Optional<String> publicID = event.getMagnitudePublicID()
                .map(QuakeMLOriginalXmlImpl::onlyNumeric);
        publicID.ifPresent(new InsertAttributeWithText(cursor, PUBLIC_ID));

        cursor.beginElement(MAG);

        final Optional<String> magValue = event.getMagnitudeMagValue();
        magValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String magUncertainty = event.getMagnitudeMagUncertainty()
                .filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
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
            cursor.insertElementWithText(VALUE, creationInfo.get());
            cursor.toNextToken();
        }


        cursor.dispose();

        return result;
    }


    /**
     * Converts the section for the focal mechanism to xml.
     * @param event event to extract from
     * @return xml object with the focal mechanism
     */
    private static XmlObject convertFeatureToFocalMechanismSection(
            final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();

        cursor.beginElement(FOCAL_MECHANISM);
        final Optional<String> publicID =
                event.getFocalMechanismPublicID()
                        .map(QuakeMLOriginalXmlImpl::onlyNumeric);
        publicID.ifPresent(new InsertAttributeWithText(cursor, PUBLIC_ID));

        cursor.beginElement(NODAL_PLANES);
        cursor.beginElement(NODAL_PLANE_1);
        cursor.beginElement(STRIKE);

        final Optional<String> strikeValue =
                event.getFocalMechanismNodalPlanesNodalPlane1StrikeValue();
        strikeValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String strikeUncertainty =
                event.getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty()
                        .filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY)
                .accept(strikeUncertainty);

        cursor.toNextToken();

        cursor.beginElement(DIP);
        final Optional<String> dipValue =
                event.getFocalMechanismNodalPlanesNodalPlane1DipValue();
        dipValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String dipUncertainty =
                event.getFocalMechanismNodalPlanesNodalPlane1DipUncertainty()
                        .filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(dipUncertainty);

        cursor.toNextToken();

        cursor.beginElement(RAKE);
        final Optional<String> rakeValue =
                event.getFocalMechanismNodalPlanesNodalPlane1RakeValue();
        rakeValue.ifPresent(new InsertElementWithText(cursor, VALUE));
        final String rakeUncertainty =
                event.getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty()
                        .filter(QuakeMLOriginalXmlImpl::notNaN).orElse(NAN);
        new InsertElementWithText(cursor, UNCERTAINTY).accept(rakeUncertainty);

        cursor.toNextToken();

        cursor.toNextToken();

        final Optional<String> preferredPlane =
                event.getFocalMechanismNodalPlanesPreferredNodalPlane();
        preferredPlane.ifPresent(
                new InsertElementWithText(cursor, PREFERRED_PLANE));

        cursor.dispose();

        return result;
    }

    /**
     * Converts the secion about the amplitude to xml.
     * @param event event to extract from
     * @return xml element with the amplitude data in quakeml
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
            type.ifPresent(new InsertElementWithText(cursor, TYPE));

            if (genericAmplitudeValue.isPresent()) {
                cursor.beginElement(GENERIC_AMPLITUDE);
                cursor.insertElementWithText(
                        VALUE, genericAmplitudeValue.get());
            }
        }
        cursor.dispose();

        return result;
    }
}
