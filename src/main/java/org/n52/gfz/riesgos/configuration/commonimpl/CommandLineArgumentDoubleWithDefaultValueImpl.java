package org.n52.gfz.riesgos.configuration.commonimpl;

import org.n52.gfz.riesgos.addtypeintoinputdescriptiontype.AddLiteralDoubleTypeWithDefaultValueToInputDescriptionTypeImpl;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralDoubleBindingToStringCmd;
import org.n52.gfz.riesgos.configuration.impl.IdentifierWithBindingImpl;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;

import java.util.Optional;

public class CommandLineArgumentDoubleWithDefaultValueImpl extends IdentifierWithBindingImpl {
    public CommandLineArgumentDoubleWithDefaultValueImpl(final String identifier, final double defaultValue) {
        super(
                identifier,
                LiteralDoubleBinding.class,
                Optional.empty(),
                Optional.of(new LiteralDoubleBindingToStringCmd()),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(String.valueOf(defaultValue)),
                Optional.empty(),
                Optional.empty()
        );
    }
}
