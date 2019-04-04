package org.n52.gfz.riesgos.convertformats;

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
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.apache.xmlbeans.XmlObject;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeML;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeMLEvent;
import org.n52.gfz.riesgos.formats.quakeml.QuakeML;
import org.n52.gfz.riesgos.formats.quakeml.impl.QuakeMLSimpleFeatureCollectionImpl;
import org.n52.gfz.riesgos.functioninterfaces.IConvertFormat;
import org.n52.wps.io.GTHelper;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Class to convert QuakeML XML to a FeatureCollection.
 * This class mostly resamples to code from
 * QuakeMLParser and QuakeMLGenerator by Benjamin Pross
 */
public class QuakeMLToFeatureCollectionConverter implements IConvertFormat<XmlObject, FeatureCollection<SimpleFeatureType, SimpleFeature>> {

    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> convert(final XmlObject xmlObject) throws ConvertFormatException {

        final IQuakeML quakeML = QuakeML.fromXml(xmlObject);
        final DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();


        final List<IQuakeMLEvent> events = quakeML.getEvents();

        final SimpleFeatureType sft = createFeatureType();

        // iterate events:
        for(final IQuakeMLEvent event : events) {

            final SimpleFeature feature = getFeatureFromEvent(event, sft);

            setFeatureProperties(feature, event);
            featureCollection.add(feature);
        }

        return featureCollection;
    }

    private void setFeaturePropertiesOrigin(final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> originPublicID = event.getOriginPublicID();
        if(originPublicID.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_PUBLIC_ID.getFieldForFeatureCollection(), originPublicID.get());
        }
        final Optional<String> timeValue = event.getOriginTimeValue();
        if(timeValue.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_TIME_VALUE.getFieldForFeatureCollection(), timeValue.get());
        }
        final Optional<String> timeUncertainty = event.getOriginTimeUncertainty();
        if(timeUncertainty.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_TIME_UNCERTAINTY.getFieldForFeatureCollection(), timeUncertainty.get());
        }
        final Optional<String> depthValue = event.getOriginDepthValue();
        if(depthValue.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_DEPTH_VALUE.getFieldForFeatureCollection(), depthValue.get());
        }
        final Optional<String> depthUncertainty = event.getOriginDepthUncertainty();
        if(depthUncertainty.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_DEPTH_UNCERTAINTY.getFieldForFeatureCollection(), depthUncertainty.get());
        }
        final Optional<String> depthType = event.getOriginDepthType();
        if(depthType.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_DEPTH_TYPE.getFieldForFeatureCollection(), depthType.get());
        }
        final Optional<String> timeFixed = event.getOriginTimeFixed();
        if(timeFixed.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_TIME_FIXED.getFieldForFeatureCollection(), timeFixed.get());
        }
        final Optional<String> epicenterFixed = event.getOriginEpicenterFixed();
        if(epicenterFixed.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_EPICENTER_FIXED.getFieldForFeatureCollection(), epicenterFixed.get());
        }
        final Optional<String> referenceSystemID = event.getOriginReferenceSystemID();
        if(referenceSystemID.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_REFERENCE_SYSTEM_ID.getFieldForFeatureCollection(), referenceSystemID.get());
        }
        final Optional<String> type = event.getOriginType();
        if(type.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_TYPE.getFieldForFeatureCollection(), type.get());
        }
        final Optional<String> creationInfo = event.getOriginCreationInfoValue();
        if(creationInfo.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_CREATION_INFO_VALUE.getFieldForFeatureCollection(), creationInfo.get());
        }
        final Optional<String> qualityAzimuthalGap = event.getOriginQualityAzimuthalGap();
        if(qualityAzimuthalGap.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_QUALITY_AZIMUTHAL_GAP.getFieldForFeatureCollection(), qualityAzimuthalGap.get());
        }
        final Optional<String> qualityMinimumDistance = event.getOriginQualityMinimumDistance();
        if(qualityMinimumDistance.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_QUALITY_MINIMUM_DISTANCE.getFieldForFeatureCollection(), qualityMinimumDistance.get());
        }
        final Optional<String> qualityMaximumDistance = event.getOriginQualityMaximumDistance();
        if(qualityMaximumDistance.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_QUALITY_MAXIMUM_DISTANCE.getFieldForFeatureCollection(), qualityMaximumDistance.get());
        }
        final Optional<String> qualityUsedPhaseCount = event.getOriginQualityUsedPhaseCount();
        if(qualityUsedPhaseCount.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_QUALITY_USED_PHASE_COUNT.getFieldForFeatureCollection(), qualityUsedPhaseCount.get());
        }
        final Optional<String> qualityUsedStationCount = event.getOriginQualityUsedStationCount();
        if(qualityUsedStationCount.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_QUALITY_USED_STATION_COUNT.getFieldForFeatureCollection(), qualityUsedStationCount.get());
        }
        final Optional<String> qualityStandardError = event.getOriginQualityStandardError();
        if(qualityStandardError.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_QUALITY_STANDARD_ERROR.getFieldForFeatureCollection(), qualityStandardError.get());
        }
        final Optional<String> evaluationMode = event.getOriginEvaluationMode();
        if(evaluationMode.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_EVALUATION_MODE.getFieldForFeatureCollection(), evaluationMode.get());
        }
        final Optional<String> evaluationStatus = event.getOriginEvaluationStatus();
        if(evaluationStatus.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_EVALUATION_STATUS.getFieldForFeatureCollection(), evaluationStatus.get());
        }
    }

    private void setFeaturePropertiesOriginUncertainty(final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> horizontalUncertainty = event.getOriginUncertaintyHorizontalUncertainty();
        if(horizontalUncertainty.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_UNCERTAINTY_HORIZONTAL_UNCERTAINTY.getFieldForFeatureCollection(), horizontalUncertainty.get());
        }
        final Optional<String> minHorizontalUncertainty = event.getOriginUncertaintyMinHorizontalUncertainty();
        if(minHorizontalUncertainty.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_UNCERTAINTY_MIN_HORIZONTAL_UNCERTAINTY.getFieldForFeatureCollection(), minHorizontalUncertainty.get());
        }
        final Optional<String> maxHorizontalUncertainty = event.getOriginUncertaintyMaxHorizontalUncertainty();
        if(maxHorizontalUncertainty.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_UNCERTAINTY_MAX_HORIZONTAL_UNCERTAINTY.getFieldForFeatureCollection(), maxHorizontalUncertainty.get());
        }
        final Optional<String> azimuthMaxHorizontalUncertainty = event.getOriginUncertaintyAzimuthMaxHorizontalUncertainty();
        if(azimuthMaxHorizontalUncertainty.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.ORIGIN_UNCERTAINTY_AZIMUTZ_MAX_HORIZONTAL_UNCERTAINTY.getFieldForFeatureCollection(), azimuthMaxHorizontalUncertainty.get());
        }
    }

    private void setFeaturePropertiesMagnitude(final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> magnitudePublicID = event.getMagnitudePublicID();
        if(magnitudePublicID.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.MAGNITUDE_PUBLIC_ID.getFieldForFeatureCollection(), magnitudePublicID.get());
        }
        final Optional<String> magValue = event.getMagnitudeMagValue();
        if(magValue.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.MAGNITUDE_MAG_VALUE.getFieldForFeatureCollection(), magValue.get());
        }
        final Optional<String> magUncertainty = event.getMagnitudeMagUncertainty();
        if(magUncertainty.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.MAGNITUDE_MAG_UNCERTAINTY.getFieldForFeatureCollection(), magUncertainty.get());
        }
        final Optional<String> type = event.getMagnitudeType();
        if(type.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.MAGNITUDE_TYPE.getFieldForFeatureCollection(), type.get());
        }
        final Optional<String> evaluationStatus = event.getMagnitudeEvaluationStatus();
        if(evaluationStatus.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.MAGNITUDE_EVALUATION_STATUS.getFieldForFeatureCollection(), evaluationStatus.get());
        }
        final Optional<String> originID = event.getMagnitudeOriginID();
        if(originID.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.MAGNITUDE_ORIGIN_ID.getFieldForFeatureCollection(), originID.get());
        }
        final Optional<String> stationCount = event.getMagnitudeStationCount();
        if(stationCount.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.MAGNITUDE_STATION_COUNT.getFieldForFeatureCollection(), stationCount.get());
        }
        final Optional<String> creationInfo = event.getMagnitudeCreationInfoValue();
        if(creationInfo.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.MAGNITUDE_CREATION_INFO_VALUE.getFieldForFeatureCollection(), creationInfo.get());
        }
    }

    private void setFeaturePropertiesFocalMechanism(final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> focalMechanismPublicID = event.getFocalMechanismPublicID();
        if(focalMechanismPublicID.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.FOCAL_MECHANISM_PUBLIC_ID.getFieldForFeatureCollection(), focalMechanismPublicID.get());
        }
        final Optional<String> strikeValue = event.getFocalMechanismNodalPlanesNodalPlane1StrikeValue();
        if(strikeValue.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_VALUE.getFieldForFeatureCollection(), strikeValue.get());
        }
        final Optional<String> strikeUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty();
        if(strikeUncertainty.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_STRIKE_UNCERTAINTY.getFieldForFeatureCollection(), strikeUncertainty.get());
        }
        final Optional<String> dipValue = event.getFocalMechanismNodalPlanesNodalPlane1DipValue();
        if(dipValue.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_VALUE.getFieldForFeatureCollection(), dipValue.get());
        }
        final Optional<String> dipUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1DipUncertainty();
        if(dipUncertainty.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_DIP_UNCERTAINTY.getFieldForFeatureCollection(), dipUncertainty.get());
        }
        final Optional<String> rakeValue = event.getFocalMechanismNodalPlanesNodalPlane1RakeValue();
        if(rakeValue.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_VALUE.getFieldForFeatureCollection(), rakeValue.get());
        }
        final Optional<String> rakeUncertainty = event.getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty();
        if(rakeUncertainty.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.FOCAL_MECHANISM_NODAL_PLANES_NODAL_PLANE_1_RAKE_UNCERTAINTY.getFieldForFeatureCollection(), rakeUncertainty.get());
        }
        final Optional<String> preferredPlane = event.getFocalMechanismNodalPlanesPreferredNodalPlane();
        if(preferredPlane.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.FOCAL_MECHANISM_NODAL_PLANES_PREFERRED_PLANE.getFieldForFeatureCollection(), preferredPlane.get());
        }
    }

    private void setFeaturePropertiesAmplitude(final SimpleFeature feature, final IQuakeMLEvent event) {
        final Optional<String> amplitudePublicID = event.getAmplitudePublicID();
        if(amplitudePublicID.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.AMPLITUDE_PUBLIC_ID.getFieldForFeatureCollection(), amplitudePublicID.get());
        }
        final Optional<String> type = event.getAmplitudeType();
        if(type.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.AMPLITUDE_TYPE.getFieldForFeatureCollection(), type.get());
        }
        final Optional<String> value = event.getAmplitudeGenericAmplitudeValue();
        if(value.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.AMPLITUDE_GENERIC_AMPLITUDE_VALUE.getFieldForFeatureCollection(), value.get());
        }
    }

    private void setFeatureProperties(final SimpleFeature feature, final IQuakeMLEvent event) {

        feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.PREFERRED_ORIGIN_ID.getFieldForFeatureCollection(), event.getPreferredOriginID().get());
        feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.PREFERRED_MAGNITUDE_ID.getFieldForFeatureCollection(), event.getPreferredMagnitudeID().get());
        feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.TYPE.getFieldForFeatureCollection(), event.getType().get());
        final Optional<String> description = event.getDescription();
        if(description.isPresent()) {
            feature.setAttribute(QuakeMLSimpleFeatureCollectionImpl.Fields.DESCRIPTION_TEXT.getFieldForFeatureCollection(), description.get());
        }
        setFeaturePropertiesOrigin(feature, event);
        setFeaturePropertiesOriginUncertainty(feature, event);
        setFeaturePropertiesMagnitude(feature, event);
        setFeaturePropertiesFocalMechanism(feature, event);
        setFeaturePropertiesAmplitude(feature, event);
    }

    private Coordinate getCoordinate(IQuakeMLEvent event) {
        return new Coordinate(
                event.getOriginLongitudeValue(),
                event.getOriginLatitudeValue()
        );
    }

    private SimpleFeature getFeatureFromEvent(IQuakeMLEvent event, SimpleFeatureType sft) {
        final String id = event.getPublicID();
        final Point point = new GeometryFactory().createPoint(getCoordinate(event));
        return GTHelper.createFeature(id, point, sft);
    }

    private SimpleFeatureType createFeatureType() {
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
}
