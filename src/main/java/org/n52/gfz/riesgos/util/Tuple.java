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

import java.util.Map;

/**
 * Utility class to provide a tuple of two values
 * @param <A> a first type
 * @param <B> a second type
 */
public class Tuple<A, B> {

    private final A first;
    private final B second;

    /**
     * creates a new tuple
     * @param first first element
     * @param second second element
     */
    public Tuple(final A first, final B second) {
        this.first = first;
        this.second = second;
    }

    /**
     *
     * @return first
     */
    public A getFirst() {
        return first;
    }

    /**
     *
     * @return second
     */
    public B getSecond() {
        return second;
    }

    /**
     * Creates a tuple from a map entry
     * @param entry entry with key (first) and value (second)
     * @param <AA> first type
     * @param <BB> second type
     * @return new Tuple(key, value)
     */
    public static <AA, BB> Tuple<AA, BB> fromEntry(final Map.Entry<AA, BB> entry) {
        return new Tuple<>(entry.getKey(), entry.getValue());
    }
}