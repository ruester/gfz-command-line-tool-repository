package org.n52.gfz.riesgos.validators;

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

import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Validator, that checks that a value is one of some given
 * String values.
 */
public class LiteralStringBindingWithAllowedValues
        implements ICheckDataAndGetErrorMessage<LiteralStringBinding> {

    /**
     * Set with the allowed values.
     */
    private final Set<String> allowedValues;

    /**
     * Constructor with some allowed values.
     * @param values list with some allowed String values
     */
    public LiteralStringBindingWithAllowedValues(final List<String> values) {
        allowedValues = new HashSet<>();
        allowedValues.addAll(values);
    }

    /**
     * Checks a IData and (maybe) gives back the text of the problem.
     * @param wrappedStr element to check
     * @return empty if there is no problem with the value; else the text
     * of the problem description
     */
    @Override
    public Optional<String> check(final LiteralStringBinding wrappedStr) {
        final Optional<String> error;

        final String str = wrappedStr.getPayload();
        if (allowedValues.contains(str)) {
            error = Optional.empty();
        } else {
            error = Optional.of("Input is non of the allowed values");
        }


        return error;
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiteralStringBindingWithAllowedValues that =
                (LiteralStringBindingWithAllowedValues) o;
        return Objects.equals(allowedValues, that.allowedValues);
    }

    /**
     *
     * @return hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(allowedValues);
    }
}
