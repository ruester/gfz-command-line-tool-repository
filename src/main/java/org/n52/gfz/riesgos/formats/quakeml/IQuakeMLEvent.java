package org.n52.gfz.riesgos.formats.quakeml;

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

import java.util.Optional;

/**
 * Interface for an quakeml event.
 * Used for allowing different formats (xml, simple feature collection) to
 * give quakeml data back and to convert any implementation in the other
 * formats.
 */
public interface IQuakeMLEvent {

    /**
     *
     * @return public id of the event (something like quakeml:quakeledger/84945)
     */
    String getPublicID();
    /**
     *
     * @return optional public id of the origin of the event. In the cases here
     * always the same as the event id
     * (something like quakeml:quakeledger/84945)
     */
    Optional<String> getPreferredOriginID();
    /**
     *
     * @return optional public id of the magnitude of the event.
     * In the cases here
     * always the same as the event id
     * (something like quakeml:quakeledger/84945)
     */
    Optional<String> getPreferredMagnitudeID();
    /**
     *
     * @return type of the event (something like earthquake)
     */
    Optional<String> getType();
    /**
     *
     * @return description of the event (something like stochastic)
     */
    Optional<String> getDescription();

    /*========================================================================*/

    /**
     *
     * @return origin id (something like quakeml:quakeledger/84945)
     */
    Optional<String> getOriginPublicID();
    /**
     *
     * @return time the event happened
     * (something like 16773-01-01T00:00:00.000000Z)
     */
    Optional<String> getOriginTimeValue();
    /**
     *
     * @return Optional value for the uncertainty of the time value
     * (in the cases handled here always empty)
     */
    Optional<String> getOriginTimeUncertainty();
    /**
     *
     * @return latitude value of the location of the earthquake
     */
    double getOriginLatitudeValue();
    /**
     *
     * @return optional uncertainty for the latitude (in the cases handled here
     * always empty / NaN)
     */
    Optional<String> getOriginLatitudeUncertainty();
    /**
     *
     * @return longitude value of the location of the earthquake
     */
    double getOriginLongitudeValue();
    /**
     *
     * @return optional uncertainty of the longitude (in the cases handled here
     * always empty / NaN)
     */
    Optional<String> getOriginLongitudeUncertainty();
    /**
     *
     * @return depth of the earthquake in km
     */
    Optional<String> getOriginDepthValue();
    /**
     *
     * @return optional uncertainty of the depth (in the cases handled here
     * always empty / NaN)
     */
    Optional<String> getOriginDepthUncertainty();
    /**
     *
     * @return optional value for the depth type (in the cases handled here
     * always empty)
     */
    Optional<String> getOriginDepthType();
    /**
     *
     * @return optional value for the time fixed (in the cases handled here
     * always empty)
     */
    Optional<String> getOriginTimeFixed();
    /**
     *
     * @return optional value for the epicenter fixed (in the cases handled here
     * always empty)
     */
    Optional<String> getOriginEpicenterFixed();
    /**
     *
     * @return optional value for a changed origin reference system id
     * (in the cases handled
     * here always empty)
     */
    Optional<String> getOriginReferenceSystemID();
    /**
     *
     * @return optional value for the type of the origin (in the cases handled
     * here always empty)
     */
    Optional<String> getOriginType();
    /**
     *
     * @return optional info for the creation
     * (in the cases handled here always GFZ)
     */
    Optional<String> getOriginCreationInfoValue();
    /**
     *
     * @return optional quality azimuthal gap of the origin
     * (in the cases handled here always empty)
     */
    Optional<String> getOriginQualityAzimuthalGap();
    /**
     *
     * @return optional quality minimum distance of the origin
     * (in the cases handled here always empty)
     */
    Optional<String> getOriginQualityMinimumDistance();
    /**
     *
     * @return optional quality maximum distance of the origin
     * (in the cases handled here always empty)
     */
    Optional<String> getOriginQualityMaximumDistance();
    /**
     *
     * @return optional quality value with the phase count of the origin
     * (in the cases handled here always empty)
     */
    Optional<String> getOriginQualityUsedPhaseCount();
    /**
     *
     * @return optional quality value with the station count of the origin
     * (in the cases handled here always empty)
     */
    Optional<String> getOriginQualityUsedStationCount();
    /**
     *
     * @return optional quality value for the standard error of the origin
     * (in the cases handled here always empty)
     */
    Optional<String> getOriginQualityStandardError();
    /**
     *
     * @return optional evaluation of the mode for the origin
     * (in the cases handled here always empty)
     */
    Optional<String> getOriginEvaluationMode();
    /**
     *
     * @return optional evaluation of the status for the origin
     * (in the cases handled here always empty)
     */
    Optional<String> getOriginEvaluationStatus();

    /*========================================================================*/


    /**
     *
     * @return optional uncertainty of the horizontal uncertainty
     * (in the cases handled here always empty / NaN)
     */
    Optional<String> getOriginUncertaintyHorizontalUncertainty();
    /**
     *
     * @return optional uncertainty of the min horizontal uncertainty
     * (in the cases handled here always empty / NaN)
     */
    Optional<String> getOriginUncertaintyMinHorizontalUncertainty();
    /**
     *
     * @return optional uncertainty of the max horizontal uncertainty
     * (in the cases handled here always empty / NaN)
     */
    Optional<String> getOriginUncertaintyMaxHorizontalUncertainty();
    /**
     *
     * @return optional uncertainty of the azimuth horizontal uncertainty
     * (in the cases handled here always empty / NaN)
     */
    Optional<String> getOriginUncertaintyAzimuthMaxHorizontalUncertainty();

    /*========================================================================*/


    /**
     *
     * @return id of the magnitude (something like quakeml:quakeledger/84945)
     */
    Optional<String> getMagnitudePublicID();
    /**
     *
     * @return value of the magnitude
     */
    Optional<String> getMagnitudeMagValue();
    /**
     *
     * @return uncertainty of the magnitude (in the cases handled here
     * always empty / NaN)
     */
    Optional<String> getMagnitudeMagUncertainty();
    /**
     *
     * @return optional type of the magnitude (something like MW)
     */
    Optional<String> getMagnitudeType();
    /**
     *
     * @return optional evaluation status of the magnitude (in the
     * cases handled here always empty)
     */
    Optional<String> getMagnitudeEvaluationStatus();
    /**
     *
     * @return optional origin id of the magnitude (in the cases handled here
     * always empty)
     */
    Optional<String> getMagnitudeOriginID();
    /**
     *
     * @return optional station count of the magnitude
     * (in the cases handled here always empty)
     */
    Optional<String> getMagnitudeStationCount();
    /**
     *
     * @return optional creation info of the magnitude
     * (something like GFZ)
     */
    Optional<String> getMagnitudeCreationInfoValue();

    /*========================================================================*/

    /**
     *
     * @return id of the focal mechanism
     * (something like quakeml:quakeledger/84945)
     */
    Optional<String> getFocalMechanismPublicID();
    /**
     *
     * @return double value of the strike of the first nodal plane
     */
    Optional<String> getFocalMechanismNodalPlanesNodalPlane1StrikeValue();
    /**
     *
     * @return optional uncertainty of the strike of the first nodal plane
     * (in the cases handled here always empty / NaN)
     */
    Optional<String> getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty();
    /**
     *
     * @return double value for the dip of the first nodal plane
     */
    Optional<String> getFocalMechanismNodalPlanesNodalPlane1DipValue();
    /**
     *
     * @return optional uncertainty of the dip of the first nodal plane
     * (in the cases handled here always empty / NaN)
     */
    Optional<String> getFocalMechanismNodalPlanesNodalPlane1DipUncertainty();
    /**
     *
     * @return double value for the rake of the first nodal plane
     */
    Optional<String> getFocalMechanismNodalPlanesNodalPlane1RakeValue();
    /**
     *
     * @return optional uncertainty of the rake of the first nodal plane
     * (in the cases handled here always empty / NaN)
     */
    Optional<String> getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty();
    /**
     *
     * @return Optional value of fhe preferred nodal plane (something
     * like nodalPlane1).
     */
    Optional<String> getFocalMechanismNodalPlanesPreferredNodalPlane();

    /*========================================================================*/

    /**
     *
     * @return optional id of the amplitude
     * (in the cases handled here always empty)
     */
    Optional<String> getAmplitudePublicID();
    /**
     *
     * @return optional type of the amplitude
     * (in the cases handled here always empty)
     */
    Optional<String> getAmplitudeType();
    /**
     *
     * @return optional value of the generic amplitude
     * (in the cases handled here always empty)
     */
    Optional<String> getAmplitudeGenericAmplitudeValue();
}
