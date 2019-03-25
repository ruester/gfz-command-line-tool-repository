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

package org.n52.gfz.riesgos.bytetoidataconverter;

import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ConvertBytesAsGeojsonToGTVectorDataBinding implements IConvertByteArrayToIData {

    @Override
    public IData convertToIData(byte[] content) throws ConvertToIDataException {

        final FeatureJSON featureJSON = new FeatureJSON();
        try(final ByteArrayInputStream in = new ByteArrayInputStream(content)) {
            final FeatureCollection<?, ?> featureCollection = featureJSON.readFeatureCollection(in);
            return new GTVectorDataBinding(featureCollection);
        } catch(final IOException ioException) {
            throw new ConvertToIDataException(ioException);
        }
    }
}
