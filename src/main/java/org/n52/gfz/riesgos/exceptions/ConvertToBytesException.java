package org.n52.gfz.riesgos.exceptions;

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

/**
 * Exception on converting to bytes
 */
public class ConvertToBytesException extends Exception {

    private static final long serialVersionUID = -5383408643796511049L;

    /**
     * @param message message of the error
     */
    public ConvertToBytesException(final String message) {
        super(message);
    }

    /**
     *
     * @param reason reason of the error
     */
    public ConvertToBytesException(final Throwable reason) {
        super(reason);
    }
}
