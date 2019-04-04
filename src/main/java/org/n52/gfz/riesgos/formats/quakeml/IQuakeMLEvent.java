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
 *
 *
 */

import java.util.Optional;

public interface IQuakeMLEvent {
    String getPublicID();
    Optional<String> getPreferredOriginID();
    Optional<String> getPreferredMagnitudeID();
    Optional<String> getType();
    Optional<String> getDescription();

    Optional<String> getOriginPublicID();
    Optional<String> getOriginTimeValue();
    Optional<String> getOriginTimeUncertainty();
    double getOriginLatitudeValue();
    Optional<String> getOriginLatitudeUncertainty();
    double getOriginLongitudeValue();
    Optional<String> getOriginLongitudeUncertainty();
    Optional<String> getOriginDepthValue();
    Optional<String> getOriginDepthUncertainty();
    Optional<String> getOriginDepthType();
    Optional<String> getOriginTimeFixed();
    Optional<String> getOriginEpicenterFixed();
    Optional<String> getOriginReferenceSystemID();
    Optional<String> getOriginType();
    Optional<String> getOriginCreationInfoValue();
    Optional<String> getOriginQualityAzimuthalGap();
    Optional<String> getOriginQualityMinimumDistance();
    Optional<String> getOriginQualityMaximumDistance();
    Optional<String> getOriginQualityUsedPhaseCount();
    Optional<String> getOriginQualityUsedStationCount();
    Optional<String> getOriginQualityStandardError();
    Optional<String> getOriginEvaluationMode();
    Optional<String> getOriginEvaluationStatus();

    Optional<String> getOriginUncertaintyHorizontalUncertainty();
    Optional<String> getOriginUncertaintyMinHorizontalUncertainty();
    Optional<String> getOriginUncertaintyMaxHorizontalUncertainty();
    Optional<String> getOriginUncertaintyAzimuthMaxHorizontalUncertainty();

    Optional<String> getMagnitudePublicID();
    Optional<String> getMagnitudeMagValue();
    Optional<String> getMagnitudeMagUncertainty();
    Optional<String> getMagnitudeType();
    Optional<String> getMagnitudeEvaluationStatus();
    Optional<String> getMagnitudeOriginID();
    Optional<String> getMagnitudeStationCount();
    Optional<String> getMagnitudeCreationInfoValue();

    Optional<String> getFocalMechanismPublicID();
    Optional<String> getFocalMechanismNodalPlanesNodalPlane1StrikeValue();
    Optional<String> getFocalMechanismNodalPlanesNodalPlane1StrikeUncertainty();
    Optional<String> getFocalMechanismNodalPlanesNodalPlane1DipValue();
    Optional<String> getFocalMechanismNodalPlanesNodalPlane1DipUncertainty();
    Optional<String> getFocalMechanismNodalPlanesNodalPlane1RakeValue();
    Optional<String> getFocalMechanismNodalPlanesNodalPlane1RakeUncertainty();
    Optional<String> getFocalMechanismNodalPlanesPreferredNodalPlane();

    Optional<String> getAmplitudePublicID();
    Optional<String> getAmplitudeType();
    Optional<String> getAmplitudeGenericAmplitudeValue();
}
