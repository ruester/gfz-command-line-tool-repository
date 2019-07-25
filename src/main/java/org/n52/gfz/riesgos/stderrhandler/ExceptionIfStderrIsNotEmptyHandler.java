package org.n52.gfz.riesgos.stderrhandler;

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

import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;
import org.n52.gfz.riesgos.functioninterfaces.ILogger;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;

import java.util.Objects;

/**
 * Handler for stderr that throws an exception on non empty stderr.
 */
public class ExceptionIfStderrIsNotEmptyHandler implements IStderrHandler {

    private static final long serialVersionUID = 775455431436144636L;

    /**
     * Handles stderr text.
     * @param stderr text to handle
     * @param logger logger of the algorithm
     * @throws NonEmptyStderrException there may be an exception
     * on non empty stderr
     */
    @Override
    public void handleStderr(
            final String stderr,
            final ILogger logger)

            throws NonEmptyStderrException {

        if (!stderr.trim().isEmpty()) {
            throw new NonEmptyStderrException(stderr);
        }
    }

    /**
     * Tests for equality.
     * @param o other object
     * @return true if both are equal.
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
     * @return hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName());
    }
}
