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

package org.n52.gfz.riesgos.formats.jsonfile.binding;


import org.n52.gfz.riesgos.util.StreamUtils;

import java.io.IOException;
import java.io.Serializable;

/**
 * Wrapper for the JSON contents.
 */
public final class JsonFileData implements Serializable {

    private static final long serialVersionUID = -3251367434563873184L;

    /**
     * Configuration value to use internal compression.
     */
    private static final boolean USE_COMPRESSION_INTERNALLY = false;

    /**
     * The content of the JSON file.
     */
    private final byte[] content;

    /**
     * Constructor with the json contents.
     * @param theContent json object to store
     */
    private JsonFileData(final byte[] theContent) {
        this.content = theContent;
    }

    /**
     * Convert to compressed content if configured.
     * @param inputContent the uncompressed JSON content
     * @return a JsonFileData with the content in it
     * @throws IOException if compression failed
     */
    public static JsonFileData fromUncompressedBytes(
        final byte[] inputContent
    ) throws IOException {
        final byte[] content;

        if (USE_COMPRESSION_INTERNALLY) {
            content = StreamUtils.compress(inputContent);
        } else {
            content = inputContent;
        }

        return new JsonFileData(content);
    }

    /**
     * Get the content.
     * @return the content
     * @throws IOException if decompression failed
     */
    public byte[] getContent() throws IOException {
        if (USE_COMPRESSION_INTERNALLY) {
            return StreamUtils.decompress(content);
        }
        return content;
    }
}
