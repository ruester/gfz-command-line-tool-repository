package org.n52.gfz.riesgos.exitvaluehandler;

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

import org.n52.gfz.riesgos.exceptions.NonZeroExitValueException;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.ILogger;

import java.util.Objects;

/**
 * Handler for the exit value that throws an exception on a non zero
 * exit value.
 */
public class ExceptionIfExitValueIsNotEmptyHandler
        implements IExitValueHandler {

    private static final long serialVersionUID = 3241823280037086332L;

    /**
     * Handles the exit value.
     * @param exitValue value to handle
     * @param logger logger from the algorithm class
     * @throws NonZeroExitValueException
     */
    @Override
    public void handleExitValue(
            final int exitValue,
            final ILogger logger) throws NonZeroExitValueException {
        if (exitValue != 0) {
            throw new NonZeroExitValueException(exitValue);
        }
    }

    /**
     * Tests equality.
     * @param o other object
     * @return true if both are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    /**
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName());
    }
}
