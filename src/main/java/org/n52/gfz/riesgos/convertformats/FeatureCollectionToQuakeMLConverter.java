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

package org.n52.gfz.riesgos.convertformats;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.geotools.feature.FeatureCollection;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeML;
import org.n52.gfz.riesgos.formats.quakeml.IQuakeMLEvent;
import org.n52.gfz.riesgos.formats.quakeml.QuakeML;
import org.n52.gfz.riesgos.formats.quakeml.impl.QuakeMLXmlImpl;
import org.n52.gfz.riesgos.functioninterfaces.IConvertFormat;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.Optional;
import java.util.stream.Stream;

public class FeatureCollectionToQuakeMLConverter implements IConvertFormat<FeatureCollection<SimpleFeatureType, SimpleFeature>, XmlObject> {
    @Override
    public XmlObject convert(FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection) throws ConvertFormatException {

        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();
        cursor.toFirstContentToken();

        cursor.beginElement(QuakeMLXmlImpl.EVENT_PARAMETERS);
        cursor.insertAttributeWithValue("namespace", "http://quakeml.org/xmlns/quakeml/1.2");

        final IQuakeML quakeML = QuakeML.fromFeatureCollection(featureCollection);
        for(final IQuakeMLEvent event : quakeML.getEvents()) {
            final XmlObject eventXml = convertFeatureToXml(event);
            eventXml.newCursor().copyXmlContents(cursor);
        }

        cursor.dispose();

        return result;
    }

    private XmlObject convertFeatureToXml(final IQuakeMLEvent event) {
        final XmlObject result = XmlObject.Factory.newInstance();
        final XmlCursor cursor = result.newCursor();

        cursor.toFirstContentToken();
        cursor.beginElement(QuakeMLXmlImpl.EVENT);
        final String publicID = event.getPublicID();
        cursor.insertAttributeWithValue(QuakeMLXmlImpl.PUBLIC_ID, publicID);

        // TODO
        // check all elements that may stay null in the testcode

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

    private XmlObject convertFeatureToXmlOriginSection(final IQuakeMLEvent event) {
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

    private XmlObject convertFeatureToXmlOriginUncertaintySection(final IQuakeMLEvent event) {
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

    private XmlObject convertFeatureToXmlMagnitudeSection(final IQuakeMLEvent event) {
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

    private XmlObject convertFeatureToFocalMechanismSection(final IQuakeMLEvent event) {
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

    private XmlObject convertFeatureToAmplitudeSection(final IQuakeMLEvent event) {
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