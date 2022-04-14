/*
 * Copyright (C) 2022 GFZ German Research Centre for Geosciences
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

package org.n52.gfz.riesgos.util.constants;

/**
 * Constants for mimetypes to interact with http clients.
 */
public enum MimeTypes {
    /**
     * Singleton instance.
     */
    INSTANCE;

    /**
     * Application json mime type.
     */
    public static final String APPLICATION_JSON = "application/json";

    /**
     * Application xml mime type.
     */
    public static final String APPLICATION_XML = "application/xml";
    /**
     * Application zip mime type.
     */
    public static final String APPLICATION_ZIP = "application/zip";
    /**
     * Text plain mime type.
     */
    public static final String TEXT_PLAIN = "text/plain";

    /**
     * Protected constructor. Use the INSTANCE.
     */
    MimeTypes() {
        // empty
    }
}
