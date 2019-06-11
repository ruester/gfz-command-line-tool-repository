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

package org.n52.gfz.riesgos.stderrhandler;

import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;
import org.n52.gfz.riesgos.functioninterfaces.ILogger;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is an implementation that handles the error texts
 * of R scripts.
 */
public class RErrorMessageStderrHandler implements IStderrHandler {

    /**
     * Pattern that will search for the term "Error" on the beginning of a line.
     * Takes the Multiline flag to test the beginning (^) at every line.
     */
    private static final Pattern ERROR_PATTERN =
            Pattern.compile("^Error", Pattern.MULTILINE);

    /**
     * Handles stderr text for output of a R script.
     * @param stderr text to handle
     * @param logger logger of the algorithm - this implementation will
     *               not use the logger at all
     * @throws NonEmptyStderrException there may be an exception
     *                                 on an error error text
     */
    @Override
    public void handleStderr(
            final String stderr,
            final ILogger logger)
            throws NonEmptyStderrException {
        final Matcher matcher = ERROR_PATTERN.matcher(stderr);
        if (matcher.find()) {
            final int startIndex = matcher.start();
            final String errorMessage = stderr.substring(startIndex);
            throw new NonEmptyStderrException(errorMessage);
        }
    }

    /**
     * Tests equality.
     * @param o other object
     * @return true of this and o are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    /**
     * Computes the hash code of the object.
     * @return hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName());
    }
}
