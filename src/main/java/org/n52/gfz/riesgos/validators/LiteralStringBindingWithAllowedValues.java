package org.n52.gfz.riesgos.validators;

import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class LiteralStringBindingWithAllowedValues implements ICheckDataAndGetErrorMessage {

    private final Set<String> allowedValues;

    public LiteralStringBindingWithAllowedValues(final String... values) {
        allowedValues = new HashSet<>();
        Stream.of(values).forEach(allowedValues::add);
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
