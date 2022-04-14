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

package org.n52.gfz.riesgos.util;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

/**
 * Utility class for streams.
 * Used as a singleton as instances of them don't make sense.
 */
public enum StreamUtils {

    /**
     * Singleton.
     */
    INSTANCE;

    /**
     * Private constructor for the class that should only be used static.
     */
    StreamUtils() {
        // static
    }


    /**
     * Helper function to convert a dict with input streams to a json
     * object.
     *
     * Example: I have the following map:
     *
     * { "PGA": inputStream1, "SA": inputStream2 }
     *
     * and the inputStream1 will create a string like
     *
     * http://server:port/endpoint1
     *
     * and inputStream2 will creata a string like
     *
     * http://server2:port2/other
     *
     * This method will return an inputStream that will create:
     *
     * {"PGA":"http://server:port/endpoint1", "SA":"http://server2:port2/other"}
     *
     * So that the text can be used from a client to handle the string
     * as an json object.
     *
     * Note: This method will corrently don't care to quote the strings
     *       properly.
     * @param inputStreams map with input streams
     * @return input stream that will try to write a json object string
     */
    public InputStream combineInputStreamsAsJsonObject(
            final Map<String, InputStream> inputStreams) {
        final List<InputStream> textStreams = new ArrayList<>();
        textStreams.add(IOUtils.toInputStream("{"));

        final Iterator<String> keyIterator = inputStreams.keySet().iterator();

        while (keyIterator.hasNext()) {
            final String key = keyIterator.next();

            final String quotedKey = "\"" + key + "\"";

            textStreams.add(IOUtils.toInputStream(quotedKey));

            final String startValue = ":\"";
            final String endValue = "\"";

            textStreams.add(IOUtils.toInputStream(startValue));
            textStreams.add(inputStreams.get(key));
            textStreams.add(IOUtils.toInputStream(endValue));

            if (keyIterator.hasNext()) {
                textStreams.add(IOUtils.toInputStream(", "));
            }
        }

        textStreams.add(IOUtils.toInputStream("}"));

        return new SequenceInputStream(Collections.enumeration(textStreams));
    }

    /**
     * Compress a byte array.
     * @param data the byte array to compress
     * @return the compressed byte array
     * @throws IOException if compression failed
     */
    public static byte[] compress(final byte[] data) throws IOException {

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final DeflaterOutputStream deflaterOutputStream =
            new DeflaterOutputStream(
                outputStream, new Deflater(Deflater.BEST_COMPRESSION)
            );

        deflaterOutputStream.write(data);
        deflaterOutputStream.finish();
        return outputStream.toByteArray();
    }

    /**
     * Decompress a byte array.
     * @param data the byte array to decompress
     * @return the decompressed byte array
     * @throws IOException if decompression failed
     */
    public static byte[] decompress(final byte[] data) throws IOException {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final InflaterOutputStream inflaterOutputStream =
            new InflaterOutputStream(outputStream);

        IOUtils.copy(inputStream, inflaterOutputStream);

        inflaterOutputStream.finish();

        return outputStream.toByteArray();
    }
}
