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
 * this is sutable to transform xml quakeml to an feature collection and
 * to query this data then via the IQuakeML interface.
 *
 * Most things are build to provide the best representation in GeoJSON
 * (so there are mostly no type conversions, and the naming stategy matches the
 * json structure - by splitting the elements with dots (origin.latitude.uncertainty for example).
 *
 * Like QuakeMLXmlImpl this class focus only on the eventParameters and event elements.
 */
public class QuakeMLSimpleFeatureCollectionImpl implements IQuakeMLDataProvider {

    private final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection;

    /**
     *
     * @param featureCollection feature collection with the quakeml data
     */
    public QuakeMLSimpleFeatureCollectionImpl(final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection) {
        this.featureCollection = featureCollection;
    }

    @Override
    public List<IQuakeMLEvent> getEvents() {
        final List<IQuakeMLEvent> result = new ArrayList<>();
        final FeatureIterator<SimpleFeature> iterator = featureCollection.features();

        while(iterator.hasNext()) {
            final SimpleFeature feature = iterator.next();
            final IQuakeMLEvent event = new QuakeMLEventSimpleFeatureImpl(feature);
            result.add(event);
        }

        return result;
    }

    private static class QuakeMLEventSimpleFeatureImpl  implements IQuakeMLEvent {
        private final SimpleFeature feature;

        QuakeMLEventSimpleFeatureImpl(final SimpleFeature feature) {
            this.feature = feature;
        }

        @Override
        public String getPublicID() {
            return feature.getID();
        }

        private Optional<String> readFromField(final Fields field) {
            return Optional.ofNullable((String) feature.getAttribute(field.getFieldForFeatureCollection()));
        }

        @Override
        public Optional<String> getPreferredOriginID() {
            return readFromField(Fields.PREFERRED_ORIGIN_ID);
        }

        @Override
        public Optional<String> getPreferredMagnitudeID() {
            return readFromField(Fields.PREFERRED_MAGNITUDE_ID);
        }

        @Override
        public Optional<String> getType() {
            return readFromField(Fields.TYPE);
        }

        @Override
        public Optional<String> getDescription() {
            return readFromField(Fields.DESCRIPTION_TEXT);
        }

        @Override
        public Optional<String> getOriginPublicID() {
            return readFromField(Fields.ORIGIN_PUBLIC_ID);
        }

        @Override
        public Optional<String> getOriginTimeValue() {
            return readFromField(Fields.ORIGIN_TIME_VALUE);
        }

        @Override
        public Optional<String> getOriginTimeUncertainty() {
            return readFromField(Fields.ORIGIN_TIME_UNCERTAINTY);
        }

        @Override
        public double getOriginLatitudeValue() {
            return ((Geometry) feature.getDefaultGeometry()).getCoordinate().y;
        }

        @Override
        public Optional<String> getOriginLatitudeUncertainty() {
            return readFromField(Fields.ORIGIN_LATITUDE_UNCERTAINTY);
        }

        @Override
        public double getOriginLongitudeValue() {
            return ((Geometry) feature.getDefaultGeometry()).getCoordinate().x;
        }

        @Override
        public Optional<String> getOriginLongitudeUncertainty() {
            return readFromField(Fields.ORIGIN_LONGITUDE_UNCERTAINTY);
        }

        @Override
        public Optional<String> getOriginDepthValue() {
            return readFromField(Fields.ORIGIN_DEPTH_VALUE);
        }

        @Override
        public Optional<String> getOriginDepthUncertainty() {
            return readFromField(Fields.ORIGIN_DEPTH_UNCERTAINTY);
        }

        @Override
        public Optional<String> getOriginDepthType() {
            return readFromField(Fields.ORIGIN_DEPTH_TYPE);
        }

        @Override
        public Optional<String> getOriginTimeFixed() {
            return readFromField(Fields.ORIGIN_TIME_FIXED);
        }

        @Override
        public Optional<String> getOriginEpicenterFixed() {
            return readFromField(Fields.ORIGIN_EPICENTER_FIXED);
        }

        @Override
        public Optional<String> getOriginReferenceSystemID() {
            return readFromField(Fields.ORIGIN_REFERENCE_SYSTEM_ID);
        }

        @Override
        public Optional<String> getOriginType() {
            return readFromField(Fields.ORIGIN_TYPE);
        }

        @Override
        public Optional<String> getOriginCreationInfoValue() {
            return readFromField(Fields.ORIGIN_CREATION_INFO_VALUE);
        }

        @Override
        public Optional<String> getOriginQualityAzimuthalGap() {
            return readFromField(Fields.ORIGIN_QUALITY_AZIMUTHAL_GAP);
        }

        @Override
        public Optional<String> getOriginQualityMinimumDistance() {
            return readFromField(Fields.ORIGIN_QUALITY_MINIMUM_DISTANCE);
        }

        @Override
        public Optional<String> getOriginQualityMaximumDistance() {
            return readFromField(Fields.ORIGIN_QUALITY_MAXIMUM_DISTANCE);
        }

        @Override
        public Optional<String> getOriginQualityUsedPhaseCount() {
            return readFromField(Fields.ORIGIN_QUALITY_USED_PHASE_COUNT);
        }

        @Override
        public Optional<String> getOriginQualityUsedStationCount() {
            return readFromField(Fields.ORIGIN_QUALITY_USED_STATION_COUNT);
        }

        @Override
        public Optional<String> getOriginQualityStandardError() {
            return readFromField(Fields.ORIGIN_QUALITY_STANDARD_ERROR);
        }

        @Override
        public Optional<String> getOriginEvaluationMode() {
            return readFromField(Fields.ORIGIN_EVALUATION_MODE);
        }

        @Override
        public Optional<String> getOriginEvaluationStatus() {
            return readFromField(Fields.ORIGIN_EVALUATION_STATUS);
        }

        @Override
        public Optional<String> getOriginUncertaintyHorizontalUncertainty() {
            return readFromField(Fields.ORIGIN_UNCERTAINTY_HORIZONTAL_UNCERTAINTY);
        }

        @Override
        public Optional<String> getOriginUncertaintyMinHorizontalUncertainty() {
            return readFromField(Fields.ORIGIN_UNCERTAINTY_MIN_HORIZONTAL_UNCERTAINTY);
        }

        @Override
        public Optional<String> getOriginUncertaintyMaxHorizontalUncertainty() {
            return readFromField(Fields.ORIGIN_UNCERTAINTY_MAX_HORIZONTAL_UNCERTAINTY);
        }

        @Override
        public Optional<String> getOriginUncertaintyAzimuthMaxHorizontalUncertainty() {
            return readFromField(Fields.ORIGIN_UNCERTAINTY_AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY);
        }

        @Override
        public Optional<String> getMagnitudePublicID() {
            return readFromField(Fields.MAGNITUDE_PUBLIC_ID);
        }

        @Override
        public Optional<String> getMagnitudeMagValue() {
            return readFromField(Fields.MAGNITUDE_MAG_VALUE);
        }

        @Override
        public Optional<String> getMagnitudeMagUncertainty() {
            return readFromField(Fields.MAGNITUDE_MAG_UNCERTAINTY);
        }

        @Override
        public Optional<String> getMagnitudeType() {
            return readFromField(Fields.MAGNITUDE_TYPE);
        }

        @Override
        public Optional<String> getMagnitudeEvaluationStatus() {
            return readFromField(Fields.MAGNITUDE_EVALUATION_STATUS);
        }

        @Override
        public Optional<String> getMagnitudeOriginID() {
            return readFromField(Fields.MAGNITUDE_ORIGIN_ID);
        }

        @Override
        public Optional<String> getMagnitudeStationCount() {
            return readFromField(Fields.MAGNITUDE_STATION_COUNT);
        }

        @Override
        public Optional<String> getMagnitudeCreationInfoValue() {
            return readFromField(Fields.MAGNITUDE_CREATION_INFO_VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismPublicID() {
            return readFromField(Fields.FOCAL_MECHANISM_PUBLIC_ID);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1StrikeValue() {
            return readFromField(Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty() {
            return readFromField(Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_UNCERTAINTY);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1DipValue() {
            return readFromField(Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1DipUncertainty() {
            return readFromField(Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_UNCERTAINTY);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1RakeValue() {
            return readFromField(Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_VALUE);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty() {
            return readFromField(Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_UNCERTAINTY);
        }

        @Override
        public Optional<String> getFocalMechanismNodalPlanesPreferredNodalPlane() {
            return readFromField(Fields.FOCAL_MECHANISM_NODAL_PLANES_PREFERRED_PLANE);
        }

        @Override
        public Optional<String> getAmplitudePublicID() {
            return readFromField(Fields.AMPLITUDE_PUBLIC_ID);
        }

        @Override
        public Optional<String> getAmplitudeType() {
            return readFromField(Fields.AMPLITUDE_TYPE);
        }

        @Override
        public Optional<String> getAmplitudeGenericAmplitudeValue() {
            return readFromField(Fields.AMPLITUDE_GENERIC_AMPLITUDE_VALUE);
        }
    }

    /**
     * Function to transform any quakeml implementation to a feature collection that later could be used
     * as input for the QuakeMLSimpleFeatureCollectionImpl class
     * @param quakeML any quakeMl implementation
     * @return FeatureCollection featurecollection with all the quakeml data
     */
    public static FeatureCollection<SimpleFeatureType, SimpleFeature> convertToSimpleFeatureCollection(final IQuakeMLDataProvider quakeML) {
        final DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();


        final List<IQuakeMLEvent> events = quakeML.getEvents();

        final SimpleFeatureType sft = createFeatureType();

        for(final IQuakeMLEvent event : events) {

            final SimpleFeature feature = getFeatureFromEvent(event, sft);

            setFeatureProperties(feature, event);

            featureCollection.add(feature);
        }

        return featureCollection;
    }

    private static class SetAttributeIfPresent implements Consumer<String> {
        private final SimpleFeature feature;
        private final Fields field;

        SetAttributeIfPresent(final SimpleFeature feature, final Fields field) {
            this.feature = feature;
            this.field = field;
        }

        @Override
        public void accept(String value) {
            feature.setAttribute(field.getFieldForFeatureCollection(), value);
        }
    }

    private static void setFeaturePropertiesOrigin(final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> originPublicID = event.getOriginPublicID();
        originPublicID.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_PUBLIC_ID));

        final Optional<String> timeValue = event.getOriginTimeValue();
        timeValue.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_TIME_VALUE));

        final Optional<String> timeUncertainty = event.getOriginTimeUncertainty();
        timeUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_TIME_UNCERTAINTY));

        final Optional<String> depthValue = event.getOriginDepthValue();
        depthValue.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_DEPTH_VALUE));

        final Optional<String> depthUncertainty = event.getOriginDepthUncertainty();
        depthUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_DEPTH_UNCERTAINTY));

        final Optional<String> depthType = event.getOriginDepthType();
        depthType.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_DEPTH_TYPE));

        final Optional<String> timeFixed = event.getOriginTimeFixed();
        timeFixed.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_TIME_FIXED));

        final Optional<String> epicenterFixed = event.getOriginEpicenterFixed();
        epicenterFixed.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_EPICENTER_FIXED));

        final Optional<String> referenceSystemID = event.getOriginReferenceSystemID();
        referenceSystemID.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_REFERENCE_SYSTEM_ID));

        final Optional<String> type = event.getOriginType();
        type.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_TYPE));

        final Optional<String> creationInfo = event.getOriginCreationInfoValue();
        creationInfo.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_CREATION_INFO_VALUE));

        final Optional<String> qualityAzimuthalGap = event.getOriginQualityAzimuthalGap();
        qualityAzimuthalGap.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_QUALITY_AZIMUTHAL_GAP));

        final Optional<String> qualityMinimumDistance = event.getOriginQualityMinimumDistance();
        qualityMinimumDistance.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_QUALITY_MINIMUM_DISTANCE));

        final Optional<String> qualityMaximumDistance = event.getOriginQualityMaximumDistance();
        qualityMaximumDistance.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_QUALITY_MAXIMUM_DISTANCE));

        final Optional<String> qualityUsedPhaseCount = event.getOriginQualityUsedPhaseCount();
        qualityUsedPhaseCount.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_QUALITY_USED_PHASE_COUNT));

        final Optional<String> qualityUsedStationCount = event.getOriginQualityUsedStationCount();
        qualityUsedStationCount.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_QUALITY_USED_STATION_COUNT));

        final Optional<String> qualityStandardError = event.getOriginQualityStandardError();
        qualityStandardError.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_QUALITY_STANDARD_ERROR));

        final Optional<String> evaluationMode = event.getOriginEvaluationMode();
        evaluationMode.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_EVALUATION_MODE));

        final Optional<String> evaluationStatus = event.getOriginEvaluationStatus();
        evaluationStatus.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_EVALUATION_STATUS));
    }

    private static void setFeaturePropertiesOriginUncertainty(final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> horizontalUncertainty = event.getOriginUncertaintyHorizontalUncertainty();
        horizontalUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_UNCERTAINTY_HORIZONTAL_UNCERTAINTY));

        final Optional<String> minHorizontalUncertainty = event.getOriginUncertaintyMinHorizontalUncertainty();
        minHorizontalUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_UNCERTAINTY_MIN_HORIZONTAL_UNCERTAINTY));

        final Optional<String> maxHorizontalUncertainty = event.getOriginUncertaintyMaxHorizontalUncertainty();
        maxHorizontalUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_UNCERTAINTY_MAX_HORIZONTAL_UNCERTAINTY));

        final Optional<String> azimuthMaxHorizontalUncertainty = event.getOriginUncertaintyAzimuthMaxHorizontalUncertainty();
        azimuthMaxHorizontalUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields.ORIGIN_UNCERTAINTY_AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY));
    }

    private static void setFeaturePropertiesMagnitude(final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> magnitudePublicID = event.getMagnitudePublicID();
        magnitudePublicID.ifPresent(new SetAttributeIfPresent(feature, Fields.MAGNITUDE_PUBLIC_ID));

        final Optional<String> magValue = event.getMagnitudeMagValue();
        magValue.ifPresent(new SetAttributeIfPresent(feature, Fields.MAGNITUDE_MAG_VALUE));

        final Optional<String> magUncertainty = event.getMagnitudeMagUncertainty();
        magUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields.MAGNITUDE_MAG_UNCERTAINTY));

        final Optional<String> type = event.getMagnitudeType();
        type.ifPresent(new SetAttributeIfPresent(feature, Fields.MAGNITUDE_TYPE));

        final Optional<String> evaluationStatus = event.getMagnitudeEvaluationStatus();
        evaluationStatus.ifPresent(new SetAttributeIfPresent(feature, Fields.MAGNITUDE_EVALUATION_STATUS));

        final Optional<String> originID = event.getMagnitudeOriginID();
        originID.ifPresent(new SetAttributeIfPresent(feature, Fields.MAGNITUDE_ORIGIN_ID));

        final Optional<String> stationCount = event.getMagnitudeStationCount();
        stationCount.ifPresent(new SetAttributeIfPresent(feature, Fields.MAGNITUDE_STATION_COUNT));

        final Optional<String> creationInfo = event.getMagnitudeCreationInfoValue();
        creationInfo.ifPresent(new SetAttributeIfPresent(feature, Fields.MAGNITUDE_CREATION_INFO_VALUE));

    }

    private static void setFeaturePropertiesFocalMechanism(final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> focalMechanismPublicID = event.getFocalMechanismPublicID();
        focalMechanismPublicID.ifPresent(new SetAttributeIfPresent(feature, Fields.FOCAL_MECHANISM_PUBLIC_ID));

        final Optional<String> strikeValue = event.getFocalMechanismNodalPlanesNodalPlane1StrikeValue();
        strikeValue.ifPresent(new SetAttributeIfPresent(feature, Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_VALUE));

        final Optional<String> strikeUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty();
        strikeUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_UNCERTAINTY));

        final Optional<String> dipValue = event.getFocalMechanismNodalPlanesNodalPlane1DipValue();
        dipValue.ifPresent(new SetAttributeIfPresent(feature, Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_VALUE));

        final Optional<String> dipUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1DipUncertainty();
        dipUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_UNCERTAINTY));

        final Optional<String> rakeValue = event.getFocalMechanismNodalPlanesNodalPlane1RakeValue();
        rakeValue.ifPresent(new SetAttributeIfPresent(feature, Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_VALUE));

        final Optional<String> rakeUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty();
        rakeUncertainty.ifPresent(new SetAttributeIfPresent(feature, Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_UNCERTAINTY));

        final Optional<String> preferredPlane = event.getFocalMechanismNodalPlanesPreferredNodalPlane();
        preferredPlane.ifPresent(new SetAttributeIfPresent(feature, Fields.FOCAL_MECHANISM_NODAL_PLANES_PREFERRED_PLANE));
    }

    private static void setFeaturePropertiesAmplitude(final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> amplitudePublicID = event.getAmplitudePublicID();
        amplitudePublicID.ifPresent(new SetAttributeIfPresent(feature, Fields.AMPLITUDE_PUBLIC_ID));

        final Optional<String> type = event.getAmplitudeType();
        type.ifPresent(new SetAttributeIfPresent(feature, Fields.AMPLITUDE_TYPE));

        final Optional<String> value = event.getAmplitudeGenericAmplitudeValue();
        value.ifPresent(new SetAttributeIfPresent(feature, Fields.AMPLITUDE_GENERIC_AMPLITUDE_VALUE));
    }

    private static void setFeatureProperties(final SimpleFeature feature, final IQuakeMLEvent event) {

        final Optional<String> preferredOriginID = event.getPreferredOriginID();
        preferredOriginID.ifPresent(new SetAttributeIfPresent(feature, Fields.PREFERRED_ORIGIN_ID));
        final Optional<String> preferredMagnitudeID = event.getPreferredMagnitudeID();
        preferredMagnitudeID.ifPresent(new SetAttributeIfPresent(feature, Fields.PREFERRED_MAGNITUDE_ID));
        final Optional<String> type = event.getType();
        type.ifPresent(new SetAttributeIfPresent(feature, Fields.TYPE));

        final Optional<String> description = event.getDescription();
        description.ifPresent(new SetAttributeIfPresent(feature, Fields.DESCRIPTION_TEXT));

        setFeaturePropertiesOrigin(feature, event);
        setFeaturePropertiesOriginUncertainty(feature, event);
        setFeaturePropertiesMagnitude(feature, event);
        setFeaturePropertiesFocalMechanism(feature, event);
        setFeaturePropertiesAmplitude(feature, event);
    }

    private static Coordinate getCoordinate(IQuakeMLEvent event) {
        return new Coordinate(
                event.getOriginLongitudeValue(),
                event.getOriginLatitudeValue()
        );
    }

    private static SimpleFeature getFeatureFromEvent(IQuakeMLEvent event, SimpleFeatureType sft) {
        final String id = event.getPublicID();
        final Point point = new GeometryFactory().createPoint(getCoordinate(event));
        return GTHelper.createFeature(id, point, sft);
    }

    private static SimpleFeatureType createFeatureType() {
        final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        final String id = UUID.randomUUID().toString().substring(0, 5);
        final String namespace = "http://www.52north.org/" + id;
        final Name name = new NameImpl(namespace, "Feature-" + id);
        builder.setName(name);
        builder.setCRS(DefaultGeographicCRS.WGS84);

        builder.add("the_geom", Point.class);

        for(final QuakeMLSimpleFeatureCollectionImpl.Fields field : QuakeMLSimpleFeatureCollectionImpl.Fields.values()) {
            builder.add(field.getFieldForFeatureCollection(), field.getClassForField());
        }

        return builder.buildFeatureType();
    }

    private enum Fields {
        PREFERRED_ORIGIN_ID("preferredOriginID"),
        PREFERRED_MAGNITUDE_ID("preferredMagnitudeID"),
        TYPE("type"),
        DESCRIPTION_TEXT("description.text"),
        ORIGIN_PUBLIC_ID("origin.publicID"),
        ORIGIN_TIME_VALUE("origin.time.value"),
        ORIGIN_TIME_UNCERTAINTY("origin.time.uncertainty"),
        ORIGIN_LATITUDE_UNCERTAINTY("origin.latitude.uncertainty"),
        ORIGIN_LONGITUDE_UNCERTAINTY("origin.longitude.uncertainty"),
        ORIGIN_DEPTH_VALUE("origin.depth.value"),
        ORIGIN_DEPTH_UNCERTAINTY("origin.depth.uncertainty"),
        ORIGIN_DEPTH_TYPE("origin.depthType"),
        ORIGIN_TIME_FIXED("origin.timeFixed"),
        ORIGIN_EPICENTER_FIXED("origin.epicenterFixed"),
        ORIGIN_REFERENCE_SYSTEM_ID("origin.referenceSystemID"),
        ORIGIN_TYPE("origin.type"),
        ORIGIN_CREATION_INFO_VALUE("origin.creationInfo.value"),
        ORIGIN_QUALITY_AZIMUTHAL_GAP("origin.quality.azumuthalGap"),
        ORIGIN_QUALITY_MINIMUM_DISTANCE("origin.quality.minimumDistance"),
        ORIGIN_QUALITY_MAXIMUM_DISTANCE("origin.quality.maximumDistance"),
        ORIGIN_QUALITY_USED_PHASE_COUNT("origin.quality.usedPhaseCount"),
        ORIGIN_QUALITY_USED_STATION_COUNT("origin.quality.usedStationCount"),
        ORIGIN_QUALITY_STANDARD_ERROR("origin.quality.standardError"),
        ORIGIN_EVALUATION_MODE("origin.evaluationMode"),
        ORIGIN_EVALUATION_STATUS("origin.evaluationStatus"),

        ORIGIN_UNCERTAINTY_HORIZONTAL_UNCERTAINTY("originUncertainty.horizontalUncertainty"),
        ORIGIN_UNCERTAINTY_MIN_HORIZONTAL_UNCERTAINTY("originUncertainty.minHorizontalUncertainty"),
        ORIGIN_UNCERTAINTY_MAX_HORIZONTAL_UNCERTAINTY("originUncertainty.maxHorizontalUncertainty"),
        ORIGIN_UNCERTAINTY_AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY("originUncertainty.azimuthMaxHorizontalUncertainty"),

        MAGNITUDE_PUBLIC_ID("magnitude.publicID"),
        MAGNITUDE_MAG_VALUE("magnitude.mag.value"),
        MAGNITUDE_MAG_UNCERTAINTY("magnitude.mag.uncertainty"),
        MAGNITUDE_TYPE("magnitude.type"),
        MAGNITUDE_EVALUATION_STATUS("magnitude.evaluationStatus"),
        MAGNITUDE_ORIGIN_ID("magnitude.originID"),
        MAGNITUDE_STATION_COUNT("magnitude.stationCount"),
        MAGNITUDE_CREATION_INFO_VALUE("magnitude.creationInfo.value"),

        FOCAL_MECHANISM_PUBLIC_ID("focalMechanism.publicID"),
        FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_VALUE("focalMechanism.nodalPlanes.nodalPlane1.strike.value"),
        FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_UNCERTAINTY("focalMechanism.nodalPlanes.nodalPlane1.strike.uncertainty"),
        FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_VALUE("focalMechanism.nodalPlanes.nodalPlane1.dip.value"),
        FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_UNCERTAINTY("focalMechanism.nodalPlanes.nodalPlane1.dip.uncertainty"),
        FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_VALUE("focalMechanism.nodalPlanes.nodalPlane1.rake.value"),
        FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_UNCERTAINTY("focalMechanism.nodalPlanes.nodalPlane1.rake.uncertainty"),
        FOCAL_MECHANISM_NODAL_PLANES_PREFERRED_PLANE("focalMechanism.nodalPlanes.preferredPlane"),

        AMPLITUDE_PUBLIC_ID("amplitude.publicID"),
        AMPLITUDE_TYPE("amplitude.type"),
        AMPLITUDE_GENERIC_AMPLITUDE_VALUE("amplitude.genericAmplitude.value");

        private final String field;

        Fields(final String field) {
            this.field = field;
        }

        public String getFieldForFeatureCollection() {
            return field;
        }

        public Class<?> getClassForField() {
            return String.class;
        }
    }
}
