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
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Validator, that checks that a value is one of some given
 * String values
 */
public class LiteralStringBindingWithAllowedValues implements ICheckDataAndGetErrorMessage {

    private final Set<String> allowedValues;

    /**
     *
     * @param values list with some allowed String values
     */
    public LiteralStringBindingWithAllowedValues(final List<String> values) {
        allowedValues = new HashSet<>();
        allowedValues.addAll(values);
    }

    @Override
    public Optional<String> check(final IData iData) {
        final Optional<String> error;
        if(iData instanceof LiteralStringBinding) {
            final LiteralStringBinding wrappedStr = (LiteralStringBinding) iData;
            final String str = wrappedStr.getPayload();
            if(allowedValues.contains(str)) {
                error = Optional.empty();
            } else {
                error = Optional.of("Input is non of the allowed values");
            }
        } else {
            error = Optional.of("Unexpected input type");
        }

        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiteralStringBindingWithAllowedValues that = (LiteralStringBindingWithAllowedValues) o;
        return Objects.equals(allowedValues, that.allowedValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowedValues);
    }
}
