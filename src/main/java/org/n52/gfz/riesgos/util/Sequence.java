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

package org.n52.gfz.riesgos.util;

/**
 * Implementation of a sequence.
 */
public class Sequence {

    /**
     * Variable to store the current count.
     */
    private int counter;

    /**
     * Constructs a new Sequence.
     * @param aCounter count to start with
     */
    public Sequence(final int aCounter) {
        this.counter = aCounter;
    }

    /**
     * Default constructor that starts with zero.
     */
    public Sequence() {
        this(0);
    }

    /**
     *
     * @return increments and returns the next value
     */
    public int nextValue() {
        return ++counter;
    }

    /**
     *
     * @return current value
     */
    public int getCurrent() {
        return counter;
    }
}
