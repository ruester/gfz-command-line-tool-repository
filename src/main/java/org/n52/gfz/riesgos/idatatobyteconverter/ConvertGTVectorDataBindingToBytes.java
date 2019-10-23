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
 */

import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.n52.gfz.riesgos.exceptions.ConvertToBytesException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Implementation to convert a GTVector to a byte array.
 */
public final class ConvertGTVectorDataBindingToBytes
    implements IConvertIDataToByteArray<GTVectorDataBinding> {

    /**
     * The format.
     */
    private final Format format;

    /**
     * Constructor with format.
     * @param argFormat format to write the data to bytes
     */
    public ConvertGTVectorDataBindingToBytes(final Format argFormat) {
        this.format = argFormat;
    }

    @Override
    public byte[] convertToBytes(
        final GTVectorDataBinding binding
    ) throws ConvertToBytesException {
        final FeatureCollection<?, ?> featureCollection = binding.getPayload();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            format.writeFeatures(featureCollection, out);
            return out.toByteArray();
        } catch (final IOException ioException) {
            throw new ConvertToBytesException(ioException);
        }

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConvertGTVectorDataBindingToBytes that =
            (ConvertGTVectorDataBindingToBytes) o;
        return format == that.format;
    }

    @Override
    public int hashCode() {
        return Objects.hash(format);
    }

    /**
     * Helper interface for feature writer.
     */
    @FunctionalInterface
    private interface IFeatureWriter {
        /**
         * Write features.
         * @param featureCollection feature collection
         * @param out output stream
         * @throws IOException on input/output error
         */
        void writeFeatures(
            FeatureCollection<?, ?> featureCollection,
            OutputStream out
        ) throws IOException;
    }

    /**
     * Format-Options for writing the data to bytes.
     */
    public enum Format implements IFeatureWriter {
        /**
         * Singleton.
         */
        JSON(
            (featureCollection, out) -> new FeatureJSON()
                .writeFeatureCollection(featureCollection, out)
        );

        /**
         * Feature writer.
         */
        private final IFeatureWriter featureWriter;

        /**
         * Constructor for feature writer.
         * @param argFeatureWriter feature writer
         */
        Format(final IFeatureWriter argFeatureWriter) {
            this.featureWriter = argFeatureWriter;
        }

        @Override
        public void writeFeatures(
            final FeatureCollection<?, ?> featureCollection,
            final OutputStream out
        ) throws IOException {
            this.featureWriter.writeFeatures(featureCollection, out);
        }
    }
}
