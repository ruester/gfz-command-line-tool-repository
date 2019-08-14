package org.n52.gfz.riesgos.exceptions;

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

/**
 * Exception on converting to an IData binding class.
 */
public class ConvertToIDataException extends Exception {

    private static final long serialVersionUID = 5977356672962670247L;

    /**
     *
     * @param reason Throwable with the reason of the exception
     */
    public ConvertToIDataException(final Throwable reason) {

        super(reason);
    }
}
