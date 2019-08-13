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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeMLDataProvider;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeMLEvent;
import org.n52.wps.io.GTHelper;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This is a implementation to query the quakeML data from an internal
 * SimpleFeatureCollection.
 *
 * While there may be more ways to represent quakeML in an feature collection,
 * this is suitable to transform xml quakeml to an feature collection and
 * to query this data then via the IQuakeML interface.
 *
 * Most things are build to provide the best representation in GeoJSON
 * (so there are mostly no type conversions, and the naming strategy matches the
 * json structure - by splitting the elements with dots
 * (origin.latitude.uncertainty for example).
 *
 * Like QuakeMLXmlImpl this class focus only on the
 * eventParameters and event elements.
 */
public class QuakeMLSimpleFeatureCollectionImpl
        implements IQuakeMLDataProvider {

    /**
     * Feature collection to tread as a IQuakeMLDataProvider.
     */
    private final FeatureCollection<SimpleFeatureType, SimpleFeature>
            featureCollection;

    /**
     * Default constructor.
     * @param aFeatureCollection feature collection with the quakeml data
     */
    public QuakeMLSimpleFeatureCollectionImpl(
            final FeatureCollection<SimpleFeatureType, SimpleFeature>
                    aFeatureCollection) {
        this.featureCollection = aFeatureCollection;
    }

    /**
     *
     * @return returns the list of events
     */
    @Override
    public List<IQuakeMLEvent> getEvents() {
        final List<IQuakeMLEvent> result = new ArrayList<>();
        final FeatureIterator<SimpleFeature> iterator =
                featureCollection.features();

        while (iterator.hasNext()) {
            final SimpleFeature feature = iterator.next();
            final IQuakeMLEvent event =
                    new QuakeMLEventSimpleFeatureImpl(feature);
            result.add(event);
        }

        return result;
    }

    /**
     *
     * @return returns the public id of the quakeml
     */
    @Override
    public Optional<String> getPublicId() {
        return Optional.empty();
    }

    /**
     * Implementation fo treat a simple feature as a quakeml event.
     */
    private static class QuakeMLEventSimpleFeatureImpl
            implements IQuakeMLEvent {
        /**
         * Feature to use for extracting the data.
         */
        private final SimpleFeature feature;

        /**
         * Default constructor.
         * @param aFeature feature to extract the data
         */
        QuakeMLEventSimpleFeatureImpl(final SimpleFeature aFeature) {
            this.feature = aFeature;
        }

        /**
         *
         * @return public id
         */
        @Override
        public String getPublicID() {
            return readFromField(Fields.PUBLIC_ID).orElse(feature.getID());
        }

        /**
         * Function to read an attribute from a field as optional string.
         * @param field field to read from
         * @return optional string
         */
        private Optional<String> readFromField(final Fields field) {
            return Optional.ofNullable((String) feature.getAttribute(
                    field.getFieldForFeatureCollection()));
        }

        /**
         *
         * @return preferred origin id
         */
        @Override
        public Optional<String> getPreferredOriginID() {
            return readFromField(Fields.PREFERRED_ORIGIN_ID);
        }

        /**
         *
         * @return preferred magnitude id
         */
        @Override
        public Optional<String> getPreferredMagnitudeID() {
            return readFromField(Fields.PREFERRED_MAGNITUDE_ID);
        }

        /**
         *
         * @return type
         */
        @Override
        public Optional<String> getType() {
            return readFromField(Fields.TYPE);
        }

        /**
         *
         * @return description
         */
        @Override
        public Optional<String> getDescription() {
            return readFromField(Fields.DESCRIPTION_TEXT);
        }

        /**
         *
         * @return origin public id
         */
        @Override
        public Optional<String> getOriginPublicID() {
            return readFromField(Fields.ORIGIN_PUBLIC_ID);
        }

        /**
         *
         * @return origin time value
         */
        @Override
        public Optional<String> getOriginTimeValue() {
            return readFromField(Fields.ORIGIN_TIME_VALUE);
        }

        /**
         *
         * @return origin time uncertainty
         */
        @Override
        public Optional<String> getOriginTimeUncertainty() {
            return readFromField(Fields.ORIGIN_TIME_UNCERTAINTY);
        }

        /**
         *
         * @return double origin latitude value
         */
        @Override
        public double getOriginLatitudeValue() {
            return ((Geometry) feature.getDefaultGeometry()).getCoordinate().y;
        }

        /**
         *
         * @return origin latitude uncertainty
         */
        @Override
        public Optional<String> getOriginLatitudeUncertainty() {
            return readFromField(Fields.ORIGIN_LATITUDE_UNCERTAINTY);
        }

        /**
         *
         * @return double origin longitude value
         */
        @Override
        public double getOriginLongitudeValue() {
            return ((Geometry) feature.getDefaultGeometry()).getCoordinate().x;
        }

        /**
         *
         * @return origin longitude uncertainty
         */
        @Override
        public Optional<String> getOriginLongitudeUncertainty() {
            return readFromField(Fields.ORIGIN_LONGITUDE_UNCERTAINTY);
        }

        /**
         *
         * @return origin depth value
         */
        @Override
        public Optional<String> getOriginDepthValue() {
            return readFromField(Fields.ORIGIN_DEPTH_VALUE);
        }

        /**
         *
         * @return origin depth uncertainty
         */
        @Override
        public Optional<String> getOriginDepthUncertainty() {
            return readFromField(Fields.ORIGIN_DEPTH_UNCERTAINTY);
        }

        /**
         *
         * @return origin depth type
         */
        @Override
        public Optional<String> getOriginDepthType() {
            return readFromField(Fields.ORIGIN_DEPTH_TYPE);
        }

        /**
         *
         * @return origin time fixed
         */
        @Override
        public Optional<String> getOriginTimeFixed() {
            return readFromField(Fields.ORIGIN_TIME_FIXED);
        }

        /**
         *
         * @return origin epicenter fixed
         */
        @Override
        public Optional<String> getOriginEpicenterFixed() {
            return readFromField(Fields.ORIGIN_EPICENTER_FIXED);
        }

        /**
         *
         * @return origin reference system id
         */
        @Override
        public Optional<String> getOriginReferenceSystemID() {
            return readFromField(Fields.ORIGIN_REFERENCE_SYSTEM_ID);
        }

        /**
         *
         * @return origin type
         */
        @Override
        public Optional<String> getOriginType() {
            return readFromField(Fields.ORIGIN_TYPE);
        }

        /**
         *
         * @return origin creation info value
         */
        @Override
        public Optional<String> getOriginCreationInfoValue() {
            return readFromField(Fields.ORIGIN_CREATION_INFO_VALUE);
        }

        /**
         *
         * @return origin quality azimuthal gap
         */
        @Override
        public Optional<String> getOriginQualityAzimuthalGap() {
            return readFromField(Fields.ORIGIN_QUALITY_AZIMUTHAL_GAP);
        }

        /**
         *
         * @return origin quality minimum distance
         */
        @Override
        public Optional<String> getOriginQualityMinimumDistance() {
            return readFromField(Fields.ORIGIN_QUALITY_MINIMUM_DISTANCE);
        }

        /**
         *
         * @return origin quality maximum distance
         */
        @Override
        public Optional<String> getOriginQualityMaximumDistance() {
            return readFromField(Fields.ORIGIN_QUALITY_MAXIMUM_DISTANCE);
        }

        /**
         *
         * @return origin quality used phase count
         */
        @Override
        public Optional<String> getOriginQualityUsedPhaseCount() {
            return readFromField(Fields.ORIGIN_QUALITY_USED_PHASE_COUNT);
        }

        /**
         *
         * @return origin quality used station count
         */
        @Override
        public Optional<String> getOriginQualityUsedStationCount() {
            return readFromField(Fields.ORIGIN_QUALITY_USED_STATION_COUNT);
        }

        /**
         *
         * @return origin quality standard error
         */
        @Override
        public Optional<String> getOriginQualityStandardError() {
            return readFromField(Fields.ORIGIN_QUALITY_STANDARD_ERROR);
        }

        /**
         *
         * @return origin evaluation mode
         */
        @Override
        public Optional<String> getOriginEvaluationMode() {
            return readFromField(Fields.ORIGIN_EVALUATION_MODE);
        }

        /**
         *
         * @return origin evalution status
         */
        @Override
        public Optional<String> getOriginEvaluationStatus() {
            return readFromField(Fields.ORIGIN_EVALUATION_STATUS);
        }

        /**
         *
         * @return origin uncertainty horizontal uncertainty
         */
        @Override
        public Optional<String> getOriginUncertaintyHorizontalUncertainty() {
            return readFromField(
                    Fields.ORIGIN_UNCERTAINTY_HORIZONTAL_UNCERTAINTY);
        }

        /**
         *
         * @return origin uncertainty min horizontal uncertainty
         */
        @Override
        public Optional<String> getOriginUncertaintyMinHorizontalUncertainty() {
            return readFromField(
                    Fields.ORIGIN_UNCERTAINTY_MIN_HORIZONTAL_UNCERTAINTY);
        }

        /**
         *
         * @return origin uncertainty max horizontal uncertainty
         */
        @Override
        public Optional<String> getOriginUncertaintyMaxHorizontalUncertainty() {
            return readFromField(
                    Fields.ORIGIN_UNCERTAINTY_MAX_HORIZONTAL_UNCERTAINTY);
        }

        /**
         *
         * @return origin uncertainty azimuth max horizontal uncertainty
         */
        @Override
        public Optional<String>
        getOriginUncertaintyAzimuthMaxHorizontalUncertainty() {
            return readFromField(
                Fields.ORIGIN_UNCERTAINTY_AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY);
        }

        /**
         *
         * @return magnitude public id
         */
        @Override
        public Optional<String> getMagnitudePublicID() {
            return readFromField(Fields.MAGNITUDE_PUBLIC_ID);
        }

        /**
         *
         * @return magnitude mag value
         */
        @Override
        public Optional<String> getMagnitudeMagValue() {
            return readFromField(Fields.MAGNITUDE_MAG_VALUE);
        }

        /**
         *
         * @return magnitude mag uncertainty
         */
        @Override
        public Optional<String> getMagnitudeMagUncertainty() {
            return readFromField(Fields.MAGNITUDE_MAG_UNCERTAINTY);
        }

        /**
         *
         * @return magnitude type
         */
        @Override
        public Optional<String> getMagnitudeType() {
            return readFromField(Fields.MAGNITUDE_TYPE);
        }

        /**
         *
         * @return magnitude evaluation status
         */
        @Override
        public Optional<String> getMagnitudeEvaluationStatus() {
            return readFromField(Fields.MAGNITUDE_EVALUATION_STATUS);
        }

        /**
         *
         * @return magnitude origin id
         */
        @Override
        public Optional<String> getMagnitudeOriginID() {
            return readFromField(Fields.MAGNITUDE_ORIGIN_ID);
        }

        /**
         *
         * @return magnitude station count
         */
        @Override
        public Optional<String> getMagnitudeStationCount() {
            return readFromField(Fields.MAGNITUDE_STATION_COUNT);
        }

        /**
         *
         * @return magnitude creation info value
         */
        @Override
        public Optional<String> getMagnitudeCreationInfoValue() {
            return readFromField(Fields.MAGNITUDE_CREATION_INFO_VALUE);
        }

        /**
         *
         * @return focal mechanism public id
         */
        @Override
        public Optional<String> getFocalMechanismPublicID() {
            return readFromField(Fields.FOCAL_MECHANISM_PUBLIC_ID);
        }

        /**
         *
         * @return strike value
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesNodalPlane1StrikeValue() {
            return readFromField(Fields
                    .FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_VALUE);
        }

        /**
         *
         * @return strike uncertainty
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty() {
            return readFromField(Fields.
                FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_UNCERTAINTY);
        }

        /**
         *
         * @return dip value
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesNodalPlane1DipValue() {
            return readFromField(Fields.
                    FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_VALUE);
        }

        /**
         *
         * @return dip uncertainty
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesNodalPlane1DipUncertainty() {
            return readFromField(Fields.
                    FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_UNCERTAINTY);
        }

        /**
         *
         * @return rake value
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesNodalPlane1RakeValue() {
            return readFromField(Fields.
                    FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_VALUE);
        }

        /**
         *
         * @return rake uncertainty
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty() {
            return readFromField(Fields.
                FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_UNCERTAINTY);
        }

        /**
         *
         * @return preferred plane
         */
        @Override
        public Optional<String>
        getFocalMechanismNodalPlanesPreferredNodalPlane() {
            return readFromField(Fields.
                    FOCAL_MECHANISM_NODAL_PLANES_PREFERRED_PLANE);
        }

        /**
         *
         * @return amplitude public id
         */
        @Override
        public Optional<String> getAmplitudePublicID() {
            return readFromField(Fields.AMPLITUDE_PUBLIC_ID);
        }

        /**
         *
         * @return amplitude type
         */
        @Override
        public Optional<String> getAmplitudeType() {
            return readFromField(Fields.AMPLITUDE_TYPE);
        }

        /**
         *
         * @return generic amplitude value
         */
        @Override
        public Optional<String> getAmplitudeGenericAmplitudeValue() {
            return readFromField(Fields.AMPLITUDE_GENERIC_AMPLITUDE_VALUE);
        }
    }

    /**
     * Function to transform any quakeml implementation to
     * a feature collection that later could be used
     * as input for the QuakeMLSimpleFeatureCollectionImpl class.
     * @param quakeML any quakeMl implementation
     * @return FeatureCollection featurecollection with all the quakeml data
     */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature>
    convertToSimpleFeatureCollection(final IQuakeMLDataProvider quakeML) {
        final DefaultFeatureCollection featureCollection =
                new DefaultFeatureCollection();


        final List<IQuakeMLEvent> events = quakeML.getEvents();

        final SimpleFeatureType sft = createFeatureType();

        for (final IQuakeMLEvent event : events) {

            final SimpleFeature feature = getFeatureFromEvent(event, sft);
            // explicit set the public id
            // for cases where the parser/generator overwrites
            // the id of the feature
            // (as it is in the GML Generator)
            feature.setAttribute(
                    Fields.PUBLIC_ID.getFieldForFeatureCollection(),
                    event.getPublicID());
            setFeatureProperties(feature, event);

            featureCollection.add(feature);
        }

        return featureCollection;
    }

    /**
     * Consumer to set a attribute field.
     */
    private static class SetAttributeIfPresent implements Consumer<String> {
        /**
         * Feature for setting its attributes.
         */
        private final SimpleFeature feature;
        /**
         * Field to set.
         */
        private final Fields field;

        /**
         * Default constructor.
         * @param aFeature the feature for setting the attributes
         * @param aField fhe field to set
         */
        SetAttributeIfPresent(
                final SimpleFeature aFeature,
                final Fields aField) {
            this.feature = aFeature;
            this.field = aField;
        }

        /**
         * Sets the value to the field in the feature.
         * @param value value to set
         */
        @Override
        public void accept(final String value) {
            feature.setAttribute(field.getFieldForFeatureCollection(), value);
        }
    }

    /**
     * Method to set all the fields for the origin section of the quakeml data.
     * @param feature feature to set
     * @param event event to extract from
     */
    private static void setFeaturePropertiesOrigin(
            final SimpleFeature feature,
            final IQuakeMLEvent event) {
        final Optional<String> originPublicID = event.getOriginPublicID();
        originPublicID.ifPresent(
                new SetAttributeIfPresent(feature, Fields.ORIGIN_PUBLIC_ID));

        final Optional<String> timeValue = event.getOriginTimeValue();
        timeValue.ifPresent(
                new SetAttributeIfPresent(feature, Fields.ORIGIN_TIME_VALUE));

        final Optional<String> timeUncertainty =
                event.getOriginTimeUncertainty();
        timeUncertainty.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_TIME_UNCERTAINTY));

        final Optional<String> depthValue = event.getOriginDepthValue();
        depthValue.ifPresent(
                new SetAttributeIfPresent(feature, Fields.ORIGIN_DEPTH_VALUE));

        final Optional<String> depthUncertainty =
                event.getOriginDepthUncertainty();
        depthUncertainty.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_DEPTH_UNCERTAINTY));

        final Optional<String> depthType = event.getOriginDepthType();
        depthType.ifPresent(
                new SetAttributeIfPresent(feature, Fields.ORIGIN_DEPTH_TYPE));

        final Optional<String> timeFixed = event.getOriginTimeFixed();
        timeFixed.ifPresent(
                new SetAttributeIfPresent(feature, Fields.ORIGIN_TIME_FIXED));

        final Optional<String> epicenterFixed = event.getOriginEpicenterFixed();
        epicenterFixed.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_EPICENTER_FIXED));

        final Optional<String> referenceSystemID =
                event.getOriginReferenceSystemID();
        referenceSystemID.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_REFERENCE_SYSTEM_ID));

        final Optional<String> type = event.getOriginType();
        type.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_TYPE));

        final Optional<String> creationInfo =
                event.getOriginCreationInfoValue();
        creationInfo.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_CREATION_INFO_VALUE));

        final Optional<String> qualityAzimuthalGap =
                event.getOriginQualityAzimuthalGap();
        qualityAzimuthalGap.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_QUALITY_AZIMUTHAL_GAP));

        final Optional<String> qualityMinimumDistance =
                event.getOriginQualityMinimumDistance();
        qualityMinimumDistance.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_QUALITY_MINIMUM_DISTANCE));

        final Optional<String> qualityMaximumDistance =
                event.getOriginQualityMaximumDistance();
        qualityMaximumDistance.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_QUALITY_MAXIMUM_DISTANCE));

        final Optional<String> qualityUsedPhaseCount =
                event.getOriginQualityUsedPhaseCount();
        qualityUsedPhaseCount.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_QUALITY_USED_PHASE_COUNT));

        final Optional<String> qualityUsedStationCount =
                event.getOriginQualityUsedStationCount();
        qualityUsedStationCount.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_QUALITY_USED_STATION_COUNT));

        final Optional<String> qualityStandardError =
                event.getOriginQualityStandardError();
        qualityStandardError.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_QUALITY_STANDARD_ERROR));

        final Optional<String> evaluationMode =
                event.getOriginEvaluationMode();
        evaluationMode.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_EVALUATION_MODE));

        final Optional<String> evaluationStatus =
                event.getOriginEvaluationStatus();
        evaluationStatus.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.ORIGIN_EVALUATION_STATUS));
    }

    /**
     * Method to set the fields of the origin uncertainty section of the
     * quakeml.
     * @param feature feature to set
     * @param event event to extract from
     */
    private static void setFeaturePropertiesOriginUncertainty(
            final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> horizontalUncertainty =
                event.getOriginUncertaintyHorizontalUncertainty();
        horizontalUncertainty.ifPresent(
                new SetAttributeIfPresent(
                        feature,
                        Fields.ORIGIN_UNCERTAINTY_HORIZONTAL_UNCERTAINTY));

        final Optional<String> minHorizontalUncertainty =
                event.getOriginUncertaintyMinHorizontalUncertainty();
        minHorizontalUncertainty.ifPresent(
                new SetAttributeIfPresent(
                        feature,
                        Fields.ORIGIN_UNCERTAINTY_MIN_HORIZONTAL_UNCERTAINTY));

        final Optional<String> maxHorizontalUncertainty =
                event.getOriginUncertaintyMaxHorizontalUncertainty();
        maxHorizontalUncertainty.ifPresent(
                new SetAttributeIfPresent(
                        feature,
                        Fields.ORIGIN_UNCERTAINTY_MAX_HORIZONTAL_UNCERTAINTY));

        final Optional<String> azimuthMaxHorizontalUncertainty =
                event.getOriginUncertaintyAzimuthMaxHorizontalUncertainty();
        azimuthMaxHorizontalUncertainty.ifPresent(new SetAttributeIfPresent(
                feature,
                Fields.ORIGIN_UNCERTAINTY_AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY));
    }

    /**
     * Method to set the fields of the magnitude section of the quakeml.
     * @param feature feature to set its fields
     * @param event event to extract from
     */
    private static void setFeaturePropertiesMagnitude(
            final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> magnitudePublicID =
                event.getMagnitudePublicID();
        magnitudePublicID.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.MAGNITUDE_PUBLIC_ID));

        final Optional<String> magValue = event.getMagnitudeMagValue();
        magValue.ifPresent(
                new SetAttributeIfPresent(feature, Fields.MAGNITUDE_MAG_VALUE));

        final Optional<String> magUncertainty =
                event.getMagnitudeMagUncertainty();
        magUncertainty.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.MAGNITUDE_MAG_UNCERTAINTY));

        final Optional<String> type = event.getMagnitudeType();
        type.ifPresent(
                new SetAttributeIfPresent(feature, Fields.MAGNITUDE_TYPE));

        final Optional<String> evaluationStatus =
                event.getMagnitudeEvaluationStatus();
        evaluationStatus.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.MAGNITUDE_EVALUATION_STATUS));

        final Optional<String> originID = event.getMagnitudeOriginID();
        originID.ifPresent(
                new SetAttributeIfPresent(feature, Fields.MAGNITUDE_ORIGIN_ID));

        final Optional<String> stationCount = event.getMagnitudeStationCount();
        stationCount.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.MAGNITUDE_STATION_COUNT));

        final Optional<String> creationInfo =
                event.getMagnitudeCreationInfoValue();
        creationInfo.ifPresent(
                new SetAttributeIfPresent(
                        feature, Fields.MAGNITUDE_CREATION_INFO_VALUE));

    }

    /**
     * Method to set the focal mechanism fields of the quakeml.
     * @param feature feature to set its fields
     * @param event event to extract the data from
     */
    private static void setFeaturePropertiesFocalMechanism(
            final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> focalMechanismPublicID =
                event.getFocalMechanismPublicID();
        focalMechanismPublicID.ifPresent(new SetAttributeIfPresent(
                feature, Fields.FOCAL_MECHANISM_PUBLIC_ID));

        final Optional<String> strikeValue =
                event.getFocalMechanismNodalPlanesNodalPlane1StrikeValue();
        strikeValue.ifPresent(new SetAttributeIfPresent(
            feature,
            Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_VALUE));

        final Optional<String> strikeUncertainty =
              event.getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty();
        strikeUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields.
              FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_UNCERTAINTY));

        final Optional<String> dipValue =
                event.getFocalMechanismNodalPlanesNodalPlane1DipValue();
        dipValue.ifPresent(new SetAttributeIfPresent(feature,
                Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_VALUE));

        final Optional<String> dipUncertainty = event
                .getFocalMechanismNodalPlanesNodalPlane1DipUncertainty();
        dipUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields
                .FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_UNCERTAINTY));

        final Optional<String> rakeValue = event
                .getFocalMechanismNodalPlanesNodalPlane1RakeValue();
        rakeValue.ifPresent(new SetAttributeIfPresent(feature, Fields
                .FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_VALUE));

        final Optional<String> rakeUncertainty =
                event.getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty();
        rakeUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields.
                FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_UNCERTAINTY));

        final Optional<String> preferredPlane =
                event.getFocalMechanismNodalPlanesPreferredNodalPlane();
        preferredPlane.ifPresent(new SetAttributeIfPresent(feature,
                Fields.FOCAL_MECHANISM_NODAL_PLANES_PREFERRED_PLANE));
    }

    /**
     * Method to set  fhe amplitude fields of the quakeml.
     * @param feature feature to set its fields
     * @param event event to extract from
     */
    private static void setFeaturePropertiesAmplitude(
            final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> amplitudePublicID = event.getAmplitudePublicID();
        amplitudePublicID.ifPresent(
                new SetAttributeIfPresent(feature, Fields.AMPLITUDE_PUBLIC_ID));

        final Optional<String> type = event.getAmplitudeType();
        type.ifPresent(
                new SetAttributeIfPresent(feature, Fields.AMPLITUDE_TYPE));

        final Optional<String> value =
                event.getAmplitudeGenericAmplitudeValue();
        value.ifPresent(
                new SetAttributeIfPresent(feature,
                        Fields.AMPLITUDE_GENERIC_AMPLITUDE_VALUE));
    }

    /**
     * Method to set the basic fields of the quakeml structure.
     * @param feature feature to set the fields
     * @param event event to extract the data from
     */
    private static void setFeatureProperties(
            final SimpleFeature feature, final IQuakeMLEvent event) {

        final Optional<String> preferredOriginID = event.getPreferredOriginID();
        preferredOriginID.ifPresent(
                new SetAttributeIfPresent(feature, Fields.PREFERRED_ORIGIN_ID));
        final Optional<String> preferredMagnitudeID =
                event.getPreferredMagnitudeID();
        preferredMagnitudeID.ifPresent(
                new SetAttributeIfPresent(feature,
                        Fields.PREFERRED_MAGNITUDE_ID));
        final Optional<String> type = event.getType();
        type.ifPresent(new SetAttributeIfPresent(feature, Fields.TYPE));

        final Optional<String> description = event.getDescription();
        description.ifPresent(
                new SetAttributeIfPresent(feature, Fields.DESCRIPTION_TEXT));

        setFeaturePropertiesOrigin(feature, event);
        setFeaturePropertiesOriginUncertainty(feature, event);
        setFeaturePropertiesMagnitude(feature, event);
        setFeaturePropertiesFocalMechanism(feature, event);
        setFeaturePropertiesAmplitude(feature, event);
    }

    /**
     * Function to extract the Coordinate of the event.
     * @param event event to extract the data from
     * @return Coordinate with long and lat value
     */
    private static Coordinate getCoordinate(final IQuakeMLEvent event) {
        return new Coordinate(
                event.getOriginLongitudeValue(),
                event.getOriginLatitudeValue()
        );
    }

    /**
     * Creates a Simplefeature from with the point geometry.
     * @param event event to extract from
     * @param sft simple feature type to build
     * @return simple feature
     */
    private static SimpleFeature getFeatureFromEvent(
            final IQuakeMLEvent event,
            final SimpleFeatureType sft) {
        final String id = event.getPublicID();
        final Point point =
                new GeometryFactory().createPoint(getCoordinate(event));
        return GTHelper.createFeature(id, point, sft);
    }

    /**
     * Method to build the feature type.
     * @return SimpleFeatureType
     */
    private static SimpleFeatureType createFeatureType() {
        final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        final String id = UUID.randomUUID().toString().substring(0, 5);
        final String namespace = "http://www.52north.org/" + id;
        final Name name = new NameImpl(namespace, "Feature-" + id);
        builder.setName(name);
        builder.setCRS(DefaultGeographicCRS.WGS84);

        builder.add("the_geom", Point.class);

        for (final QuakeMLSimpleFeatureCollectionImpl.Fields field
                : QuakeMLSimpleFeatureCollectionImpl.Fields.values()) {
            builder.add(
                    field.getFieldForFeatureCollection(),
                    field.getClassForField());
        }

        return builder.buildFeatureType();
    }

    /**
     * Enum with the Fields that can be set for the simplefeature
     * implementation for quakeml.
     */
    private enum Fields {
        /**
         * The public id.
         */
        PUBLIC_ID("publicID"),
        /**
         * Preferred origin id.
         */
        PREFERRED_ORIGIN_ID("preferredOriginID"),
        /**
         * Preferred Magnitude id.
         */
        PREFERRED_MAGNITUDE_ID("preferredMagnitudeID"),
        /**
         * Type.
         */
        TYPE("type"),
        /**
         * Description text.
         */
        DESCRIPTION_TEXT("description.text"),
        /**
         * Origin public id.
         */
        ORIGIN_PUBLIC_ID("origin.publicID"),
        /**
         * Origin time value.
         */
        ORIGIN_TIME_VALUE("origin.time.value"),
        /**
         * Origin time uncertainty.
         */
        ORIGIN_TIME_UNCERTAINTY("origin.time.uncertainty"),
        /**
         * Origin latitude uncertainty.
         */
        ORIGIN_LATITUDE_UNCERTAINTY("origin.latitude.uncertainty"),
        /**
         * Origin longitude uncertainty.
         */
        ORIGIN_LONGITUDE_UNCERTAINTY("origin.longitude.uncertainty"),
        /**
         * Origin depth value.
         */
        ORIGIN_DEPTH_VALUE("origin.depth.value"),
        /**
         * Origin depth uncertainty.
         */
        ORIGIN_DEPTH_UNCERTAINTY("origin.depth.uncertainty"),
        /**
         * Origin depth type.
         */
        ORIGIN_DEPTH_TYPE("origin.depthType"),
        /**
         * Origin time fixed.
         */
        ORIGIN_TIME_FIXED("origin.timeFixed"),
        /**
         * Origin epicenter fixed.
         */
        ORIGIN_EPICENTER_FIXED("origin.epicenterFixed"),
        /**
         * Origin reference system id.
         */
        ORIGIN_REFERENCE_SYSTEM_ID("origin.referenceSystemID"),
        /**
         * Origin type.
         */
        ORIGIN_TYPE("origin.type"),
        /**
         * Origin creation info value.
         */
        ORIGIN_CREATION_INFO_VALUE("origin.creationInfo.value"),
        /**
         * Origin quality azimuthal gap.
         */
        ORIGIN_QUALITY_AZIMUTHAL_GAP("origin.quality.azumuthalGap"),
        /**
         * Origin quality minimum distance.
         */
        ORIGIN_QUALITY_MINIMUM_DISTANCE("origin.quality.minimumDistance"),
        /**
         * Origin quality maximum distance.
         */
        ORIGIN_QUALITY_MAXIMUM_DISTANCE("origin.quality.maximumDistance"),
        /**
         * Origin quality used phase count.
         */
        ORIGIN_QUALITY_USED_PHASE_COUNT("origin.quality.usedPhaseCount"),
        /**
         * Origin quality used station count.
         */
        ORIGIN_QUALITY_USED_STATION_COUNT("origin.quality.usedStationCount"),
        /**
         * Origin quality standard error.
         */
        ORIGIN_QUALITY_STANDARD_ERROR("origin.quality.standardError"),
        /**
         * Origin evaluation mode.
         */
        ORIGIN_EVALUATION_MODE("origin.evaluationMode"),
        /**
         * Origin evaluation status.
         */
        ORIGIN_EVALUATION_STATUS("origin.evaluationStatus"),


        /**
         * Origin uncertainty horizontal uncertainty.
         */
        ORIGIN_UNCERTAINTY_HORIZONTAL_UNCERTAINTY(
                "originUncertainty.horizontalUncertainty"),
        /**
         * Origin uncertainty min horizontal uncertainty.
         */
        ORIGIN_UNCERTAINTY_MIN_HORIZONTAL_UNCERTAINTY(
                "originUncertainty.minHorizontalUncertainty"),
        /**
         * Origin uncertainty max horizontal uncertainty.
         */
        ORIGIN_UNCERTAINTY_MAX_HORIZONTAL_UNCERTAINTY(
                "originUncertainty.maxHorizontalUncertainty"),
        /**
         * Origin uncertainty azimith max horizontal uncertainty.
         */
        ORIGIN_UNCERTAINTY_AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY(
                "originUncertainty.azimuthMaxHorizontalUncertainty"),


        /**
         * Magnitude public id.
         */
        MAGNITUDE_PUBLIC_ID("magnitude.publicID"),
        /**
         * Magnitude mag value.
         */
        MAGNITUDE_MAG_VALUE("magnitude.mag.value"),
        /**
         * Magnitude mag uncertainty.
         */
        MAGNITUDE_MAG_UNCERTAINTY("magnitude.mag.uncertainty"),
        /**
         * Magnitude type.
         */
        MAGNITUDE_TYPE("magnitude.type"),
        /**
         * Magnitude evaluation status.
         */
        MAGNITUDE_EVALUATION_STATUS("magnitude.evaluationStatus"),
        /**
         * Magnitude origin id.
         */
        MAGNITUDE_ORIGIN_ID("magnitude.originID"),
        /**
         * Magnitude station count.
         */
        MAGNITUDE_STATION_COUNT("magnitude.stationCount"),
        /**
         * Magnitude creation info value.
         */
        MAGNITUDE_CREATION_INFO_VALUE("magnitude.creationInfo.value"),


        /**
         * Focal mechanism public id.
         */
        FOCAL_MECHANISM_PUBLIC_ID("focalMechanism.publicID"),
        /**
         * Strike value.
         */
        FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_VALUE(
                "focalMechanism.nodalPlanes.nodalPlane1.strike.value"),
        /**
         * Strike uncertainty.
         */
        FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_UNCERTAINTY(
                "focalMechanism.nodalPlanes.nodalPlane1.strike.uncertainty"),
        /**
         * dip value.
         */
        FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_VALUE(
                "focalMechanism.nodalPlanes.nodalPlane1.dip.value"),
        /**
         * Dip uncertainty.
         */
        FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_UNCERTAINTY(
                "focalMechanism.nodalPlanes.nodalPlane1.dip.uncertainty"),
        /**
         * Rake value.
         */
        FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_VALUE(
                "focalMechanism.nodalPlanes.nodalPlane1.rake.value"),
        /**
         * Rake uncertainty.
         */
        FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_UNCERTAINTY(
                "focalMechanism.nodalPlanes.nodalPlane1.rake.uncertainty"),
        /**
         * Preferred plane.
         */
        FOCAL_MECHANISM_NODAL_PLANES_PREFERRED_PLANE(
                "focalMechanism.nodalPlanes.preferredPlane"),


        /**
         * Amplitude public id.
         */
        AMPLITUDE_PUBLIC_ID("amplitude.publicID"),
        /**
         * Amplitude type.
         */
        AMPLITUDE_TYPE("amplitude.type"),
        /**
         * Amplitude generic amplitude value.
         */
        AMPLITUDE_GENERIC_AMPLITUDE_VALUE("amplitude.genericAmplitude.value");

        /**
         * Field with the name to read from the attributes.
         */
        private final String field;

        /**
         * Default constructor.
         * @param aField field with the name to read from the attributes
         */
        Fields(final String aField) {
            this.field = aField;
        }

        /**
         * Returns the field for the feature collection.
         * @return field
         */
        public String getFieldForFeatureCollection() {
            return field;
        }

        /**
         * Returns the class of the field.
         * @return at the moment always String
         */
        public Class<?> getClassForField() {
            return String.class;
        }
    }
}
