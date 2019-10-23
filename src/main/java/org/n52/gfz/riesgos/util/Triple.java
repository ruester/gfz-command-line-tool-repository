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

package org.n52.gfz.riesgos.util;

import java.util.Objects;

/**
 * Class for a triple.
 * @param <A> Type of first component
 * @param <B> Type of second component
 * @param <C> Type of third component
 */
public final class Triple<A, B, C> {

    /**
     * First component.
     */
    private final A first;

    /**
     * Second component.
     */
    private final B second;

    /**
     * Third component.
     */
    private final C third;

    /**
     * Constructor with all threee components.
     * @param aFirst First component
     * @param aSecond Second component
     * @param aThird Third component
     */
    public Triple(final A aFirst, final B aSecond, final C aThird) {
        this.first = aFirst;
        this.second = aSecond;
        this.third = aThird;
    }

    /**
     * Get first component.
     * @return First component
     */
    public A getFirst() {
        return first;
    }

    /**
     * Get second component.
     * @return Second component
     */
    public B getSecond() {
        return second;
    }

    /**
     * Get third component.
     * @return Third component
     */
    public C getThird() {
        return third;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(first, triple.first)
            && Objects.equals(second, triple.second)
            && Objects.equals(third, triple.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}
