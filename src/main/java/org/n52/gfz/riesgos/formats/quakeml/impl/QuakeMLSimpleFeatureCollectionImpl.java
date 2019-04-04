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

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeML;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeMLEvent;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuakeMLSimpleFeatureCollectionImpl implements IQuakeML {

    private final FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection;

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

    public enum Fields {
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
