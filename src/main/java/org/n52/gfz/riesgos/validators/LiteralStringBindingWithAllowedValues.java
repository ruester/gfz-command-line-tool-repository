package org.n52.gfz.riesgos.validators;

import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.HashSet;
import java.util.List;
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
}
