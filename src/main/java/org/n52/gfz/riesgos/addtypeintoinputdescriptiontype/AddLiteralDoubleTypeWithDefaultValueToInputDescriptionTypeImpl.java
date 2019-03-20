package org.n52.gfz.riesgos.addtypeintoinputdescriptiontype;

import net.opengis.ows.x11.DomainMetadataType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.LiteralInputType;
import org.n52.gfz.riesgos.functioninterfaces.IAddTypeIntoInputDescriptionType;

public class AddLiteralDoubleTypeWithDefaultValueToInputDescriptionTypeImpl implements IAddTypeIntoInputDescriptionType {

    private final double defaultValue;

    public AddLiteralDoubleTypeWithDefaultValueToInputDescriptionTypeImpl(final double defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public void addType(InputDescriptionType type) {
        final LiteralInputType literalInputType = type.addNewLiteralData();
        final DomainMetadataType dataType = literalInputType.addNewDataType();
        dataType.setReference("xs:double");

        literalInputType.addNewAnyValue();

        literalInputType.setDefaultValue(String.valueOf(defaultValue));
    }
}
