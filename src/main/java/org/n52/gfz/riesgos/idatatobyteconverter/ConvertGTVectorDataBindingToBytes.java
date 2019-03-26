package org.n52.gfz.riesgos.idatatobyteconverter;

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

import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.n52.gfz.riesgos.exceptions.ConvertToBytesException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ConvertGTVectorDataBindingToBytes implements IConvertIDataToByteArray {

    private final Format format;

    /**
     * Constructor with format
     * @param format format to write the data to bytes
     */
    public ConvertGTVectorDataBindingToBytes(final Format format) {
        this.format = format;
    }

    @Override
    public byte[] convertToBytes(IData iData) throws ConvertToBytesException {
        if(iData instanceof GTVectorDataBinding) {
            final GTVectorDataBinding binding = (GTVectorDataBinding) iData;
            final FeatureCollection<?, ?> featureCollection = binding.getPayload();
            try(final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                format.writeFeatures(featureCollection, out);
                return out.toByteArray();
            } catch(final IOException ioException) {
                throw new ConvertToBytesException(ioException);
            }
        } else {
            throw new ConvertToBytesException("Wrong binding class");
        }
    }

    @FunctionalInterface
    private interface IFeatureWriter {
        void writeFeatures(final FeatureCollection<?, ?> featureCollection, final OutputStream out) throws IOException;
    }

    /**
     * Format-Options for writing the data to bytes
     */
    public enum Format implements IFeatureWriter {
        JSON((featureCollection, out) -> new FeatureJSON().writeFeatureCollection(featureCollection, out));

        private final IFeatureWriter featureWriter;

        Format(final IFeatureWriter featureWriter) {
            this.featureWriter = featureWriter;
        }

        @Override
        public void writeFeatures(final FeatureCollection<?, ?> featureCollection, final OutputStream out) throws IOException {
            this.featureWriter.writeFeatures(featureCollection, out);
        }
    }
}
