package org.n52.gfz.riesgos.configuration.commonimpl;

import org.n52.gfz.riesgos.addtypeintooutputdescriptiontype.AddComplexXmlTypeWithSchemaIntoOutputDescriptionTypeImpl;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGenericXMLDataBinding;
import org.n52.gfz.riesgos.configuration.impl.IdentifierWithBindingImpl;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;

import java.util.Optional;

public class FileOutXmlWithSchemaImpl extends IdentifierWithBindingImpl {
    public FileOutXmlWithSchemaImpl(final String identifier, final String path, final String schema) {
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
                Optional.of(new ConvertBytesToGenericXMLDataBinding()),
                Optional.empty(),
                Optional.of(new AddComplexXmlTypeWithSchemaIntoOutputDescriptionTypeImpl(schema))
        );
    }
}
