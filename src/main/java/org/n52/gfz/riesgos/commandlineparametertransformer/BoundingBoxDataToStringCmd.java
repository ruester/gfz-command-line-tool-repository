package org.n52.gfz.riesgos.commandlineparametertransformer;

/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */


import org.n52.gfz.riesgos.exceptions.ConvertToStringCmdException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.bbox.BoundingBoxData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Function to convert an BoundingBox to a list of strings.
 * Used to add it as a command line argument.
 */
public class BoundingBoxDataToStringCmd
        implements IConvertIDataToCommandLineParameter {

    /**
     * Converts the IData to a list of arguments.
     * @param iData element to convert
     * @return list of strings
     * @throws ConvertToStringCmdException exception if the input can't be
     * handled by the function
     */
    @Override
    public List<String> convertToCommandLineParameter(
            final IData iData)
            throws ConvertToStringCmdException {
        final List<String> result = new ArrayList<>();

        if (iData instanceof BoundingBoxData) {
            final BoundingBoxData bbox = (BoundingBoxData) iData;
            final double[] lowerCorner = bbox.getLowerCorner();
            final double[] upperCorner = bbox.getUpperCorner();

            if (lowerCorner.length < 2 || upperCorner.length < 2) {
                throw new ConvertToStringCmdException(
                    "Not enough coordinates in the bounding box lower corner");
            }

            final double latMin = lowerCorner[0];
            final double lonMin = lowerCorner[1];
            final double latMax = upperCorner[0];
            final double lonMax = upperCorner[1];

            result.add(String.valueOf(lonMin));
            result.add(String.valueOf(lonMax));
            result.add(String.valueOf(latMin));
            result.add(String.valueOf(latMax));
        } else {
            throw new ConvertToStringCmdException("Wrong binding class");
        }

        return result;
    }

    /**
     *
     * @param o other object
     * @return true if this object equals the other one
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    /**
     *
     * @return hashcode of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName());
    }
}
