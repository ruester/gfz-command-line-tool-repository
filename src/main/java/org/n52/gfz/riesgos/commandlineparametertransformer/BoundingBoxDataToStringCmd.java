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

public class BoundingBoxDataToStringCmd implements IConvertIDataToCommandLineParameter {
    @Override
    public List<String> convertToCommandLineParameter(IData iData) throws ConvertToStringCmdException {
        final List<String> result = new ArrayList<>();

        if(iData instanceof BoundingBoxData) {
            final BoundingBoxData bbox = (BoundingBoxData) iData;
            final double[] lowerCorner = bbox.getLowerCorner();
            final double[] upperCorner = bbox.getUpperCorner();

            if(lowerCorner.length < 2) {
                throw new ConvertToStringCmdException("Not enought coordinates in the bounding box lower corner");
            }
            if(upperCorner.length < 2) {
                throw new ConvertToStringCmdException("Not enought coordinates in the bounding box upper corner");
            }

            final double latmin = lowerCorner[0];
            final double lonmin = lowerCorner[1];
            final double latmax = upperCorner[0];
            final double lonmax = upperCorner[1];

            result.add(String.valueOf(lonmin));
            result.add(String.valueOf(lonmax));
            result.add(String.valueOf(latmin));
            result.add(String.valueOf(latmax));
        } else {
            throw new ConvertToStringCmdException("Wrong Binding class");
        }

        return result;
    }
}
