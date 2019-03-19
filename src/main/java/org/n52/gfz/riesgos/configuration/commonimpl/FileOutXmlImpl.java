package org.n52.gfz.riesgos.configuration.commonimpl;

import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGenericXMLDataBinding;
import org.n52.gfz.riesgos.configuration.impl.IdentifierWithBindingImpl;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;

import java.util.Optional;

public class FileOutXmlImpl extends IdentifierWithBindingImpl {
    public FileOutXmlImpl(final String identifier, final String path) {
        super(
                identifier,
                GenericXMLDataBinding.class,
                Optional.empty(),
                Optional.empty(),
                Optional.of(path),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(new ConvertBytesToGenericXMLDataBinding())
        );
    }
}
