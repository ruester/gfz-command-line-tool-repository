package org.n52.gfz.riesgos.bytetoidataconverter;

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
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Function to create a GTVectorDataBinding from a byte array
 */
public class ConvertBytesToGTVectorDataBinding implements IConvertByteArrayToIData {

    private final Format format;

    /**
     * Constructor with the format to read the data in
     * @param format format to read from bytes
     */
    public ConvertBytesToGTVectorDataBinding(final Format format) {
        this.format = format;
    }

    @Override
    public IData convertToIData(byte[] content) throws ConvertToIDataException {

        try(final ByteArrayInputStream in = new ByteArrayInputStream(content)) {
            final FeatureCollection<?, ?> featureCollection = format.readFeatures(in);
            return new GTVectorDataBinding(featureCollection);
        } catch(final IOException ioException) {
            throw new ConvertToIDataException(ioException);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConvertBytesToGTVectorDataBinding that = (ConvertBytesToGTVectorDataBinding) o;
        return format == that.format;
    }

    @Override
    public int hashCode() {
        return Objects.hash(format);
    }

    @FunctionalInterface
    private interface IFeatureReader {
        FeatureCollection<?, ?> readFeatures(final InputStream inputStream) throws IOException;
    }


    /**
     * Format options to read features from bytes
     */
    public enum Format implements IFeatureReader {
        JSON((in) -> new FeatureJSON().readFeatureCollection(in));

        private IFeatureReader featureReader;

        Format(final IFeatureReader featureReader) {
            this.featureReader = featureReader;
        }

        @Override
        public FeatureCollection<?, ?> readFeatures(final InputStream inputStream) throws IOException {
            return featureReader.readFeatures(inputStream);
        }

    }
}
