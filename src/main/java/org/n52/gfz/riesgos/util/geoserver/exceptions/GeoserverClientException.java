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

package org.n52.gfz.riesgos.util.geoserver.exceptions;

/**
 * Parent class for all exceptions when working with the geoserver.
 */
public class GeoserverClientException extends Exception {

    private static final long serialVersionUID = 24234235435545L;

    /**
     * Construct the exception based on an inner exception.
     * @param innerException the exception that caused the problem.
     */
    public GeoserverClientException(final Exception innerException) {
        super(innerException);
    }

    /**
     * Just construct an exception without further information.
     */
    public GeoserverClientException() {
        super();
    }

    /**
     * Constructor with a string.
     * @param aText an error message
     */
    public GeoserverClientException(final String aText) {
        super(aText);
    }
}
