package org.n52.gfz.riesgos.addtypeintoinputdescriptiontype;

import net.opengis.ows.x11.AllowedValuesDocument;
import net.opengis.ows.x11.DomainMetadataType;
import net.opengis.ows.x11.ValueType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.LiteralInputType;
import org.n52.gfz.riesgos.functioninterfaces.IAddTypeIntoInputDescriptionType;

import java.util.List;

public class AddLiteralStringTypeWithAllowedValuesAndDefaultValueIntoInputDescriptionTypeImpl implements IAddTypeIntoInputDescriptionType {

    private final String defaultValue;
    private final List<String> allowedValues;

    public AddLiteralStringTypeWithAllowedValuesAndDefaultValueIntoInputDescriptionTypeImpl(
            final String defaultValue,
            final List<String> allowedValues) {
        this.defaultValue = defaultValue;
        this.allowedValues = allowedValues;
    }

    @Override
    public void addType(InputDescriptionType type) {
        final LiteralInputType literalInputType = type.addNewLiteralData();
        final DomainMetadataType dataType = literalInputType.addNewDataType();
        dataType.setReference("xs:string");

        final AllowedValuesDocument.AllowedValues allowedValuesTag = literalInputType.addNewAllowedValues();
        for(final String allowedValue : allowedValues) {
            final ValueType allowedValueTag = allowedValuesTag.addNewValue();
            allowedValueTag.setStringValue(allowedValue);
        }

        literalInputType.setDefaultValue(defaultValue);
    }
}
