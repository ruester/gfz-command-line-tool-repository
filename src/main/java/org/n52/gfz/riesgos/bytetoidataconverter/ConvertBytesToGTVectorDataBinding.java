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
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Function to create a GTVectorDataBinding from a byte array.
 */
public class ConvertBytesToGTVectorDataBinding
        implements IConvertByteArrayToIData<GTVectorDataBinding> {

    private static final long serialVersionUID = 7638158856073948956L;

    /**
     * Format for the conversion.
     */
    private final Format format;

    /**
     * Constructor with the format to read the data in.
     * @param aFormat format to read from bytes
     */
    public ConvertBytesToGTVectorDataBinding(final Format aFormat) {
        this.format = aFormat;
    }

    /**
     * Returns a GTVectorDataBinding from the byte array.
     * @param content byte array to convert
     * @return GTVectorDataBinding
     * @throws ConvertToIDataException exception that is thrown on
     * an io exception
     */
    @Override
    public GTVectorDataBinding convertToIData(
            final byte[] content) throws ConvertToIDataException {

        try (ByteArrayInputStream inStream =
                     new ByteArrayInputStream(content)) {
            final FeatureCollection<?, ?> featureCollection =
                    format.readFeatures(inStream);
            return new GTVectorDataBinding(featureCollection);
        } catch (final IOException ioException) {
            throw new ConvertToIDataException(ioException);
        }
    }


    /**
     * Tests for equality.
     * @param o other object
     * @return true if both are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConvertBytesToGTVectorDataBinding that =
                (ConvertBytesToGTVectorDataBinding) o;
        return format == that.format;
    }

    /**
     *
     * @return hashcode of the instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(format);
    }

    /**
     * Interface for reading features.
     */
    @FunctionalInterface
    private interface IFeatureReader {
        /**
         * Reads the features.
         * @param inputStream input stream to read from
         * @return feature collection
         * @throws IOException exception in case of a problem handling io
         */
        FeatureCollection<?, ?> readFeatures(
                InputStream inputStream) throws IOException;
    }


    /**
     * Format options to read features from bytes.
     */
    public enum Format implements IFeatureReader {
        /**
         * Json format.
         */
        JSON((in) -> new FeatureJSON().readFeatureCollection(in));

        /**
         * Reader implementation to use.
         */
        private final IFeatureReader featureReader;

        /**
         * Sets the reader for the format.
         * @param aFeatureReader reader implementation
         */
        Format(final IFeatureReader aFeatureReader) {
            this.featureReader = aFeatureReader;
        }

        /**
         * Reads the features into the feature collection.
         * @param inputStream input stream to read from
         * @return feature collection
         * @throws IOException io exception in case of trouble on handling
         * files and io
         */
        @Override
        public FeatureCollection<?, ?> readFeatures(
                final InputStream inputStream) throws IOException {
            return featureReader.readFeatures(inputStream);
        }

    }
}
