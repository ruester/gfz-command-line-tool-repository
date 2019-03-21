package org.n52.gfz.riesgos.configuration;

import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGenericXMLDataBinding;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralDoubleBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralStringBindingToStringCmd;
import org.n52.gfz.riesgos.configuration.impl.IdentifierWithBindingImpl;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.List;

public class IdentifierWithBindingFactory {
    private IdentifierWithBindingFactory() {
        // static
    }

    public static IIdentifierWithBinding createCommandLineArgumentDoubleWithDefaultValue(
            final String identifier, final double defaultValue) {
        return new IdentifierWithBindingImpl.Builder(identifier, LiteralDoubleBinding.class)
                .withFunctionToTransformToCmd(new LiteralDoubleBindingToStringCmd())
                .withDefaultValue(String.valueOf(defaultValue))
                .build();
    }

    public static IIdentifierWithBinding createCommandLineArgumentStringWithDefaultValueAndAllowedValues(
            final String identifier, final String defaultValue, final List<String> allowedValues) {
        return new IdentifierWithBindingImpl.Builder(identifier, LiteralStringBinding.class)
                .withFunctionToTransformToCmd(new LiteralStringBindingToStringCmd())
                .withDefaultValue(defaultValue)
                .withAllowedValues(allowedValues)
                .build();
    }

    public static IIdentifierWithBinding createFileOutXmlWithSchema(
            final String identifier,
            final String path,
            final String schema) {
        return new IdentifierWithBindingImpl.Builder(identifier, GenericXMLDataBinding.class)
                .withPath(path)
                .withFunctionToReadFromBytes(new ConvertBytesToGenericXMLDataBinding())
                .withSchema(schema)
                .build();
    }

}
