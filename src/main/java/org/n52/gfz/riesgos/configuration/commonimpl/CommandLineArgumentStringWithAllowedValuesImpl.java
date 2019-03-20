package org.n52.gfz.riesgos.configuration.commonimpl;

import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralStringBindingToStringCmd;
import org.n52.gfz.riesgos.configuration.impl.IdentifierWithBindingImpl;
import org.n52.gfz.riesgos.validators.LiteralStringBindingWithAllowedValues;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.Optional;

public class CommandLineArgumentStringWithAllowedValuesImpl extends IdentifierWithBindingImpl {
    public CommandLineArgumentStringWithAllowedValuesImpl(final String identifier, final String... allowedValues) {
        super(
                identifier,
                LiteralStringBinding.class,
                Optional.of(new LiteralStringBindingWithAllowedValues(allowedValues)),
                Optional.of(new LiteralStringBindingToStringCmd()),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }
}
