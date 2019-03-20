package org.n52.gfz.riesgos.addtypeintooutputdescriptiontype;

import net.opengis.wps.x100.ComplexDataCombinationType;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.SupportedComplexDataType;
import org.n52.gfz.riesgos.functioninterfaces.IAddTypeIntoOutputDescriptionType;

public class AddComplexXmlTypeWithSchemaIntoOutputDescriptionTypeImpl implements IAddTypeIntoOutputDescriptionType {

    private final String schema;

    public AddComplexXmlTypeWithSchemaIntoOutputDescriptionTypeImpl(final String schema) {
        this.schema = schema;
    }

    @Override
    public void addType(OutputDescriptionType type) {
        final SupportedComplexDataType datatype = type.addNewComplexOutput();
        final ComplexDataCombinationType defaultType =  datatype.addNewDefault();
        final ComplexDataDescriptionType descriptionTypeForDefaultFormat = defaultType.addNewFormat();
        descriptionTypeForDefaultFormat.setMimeType("text/xml");
        descriptionTypeForDefaultFormat.setSchema(schema);

        final ComplexDataCombinationsType supportedType = datatype.addNewSupported();
        final ComplexDataDescriptionType descriptionTypeForSupportedFormat = supportedType.addNewFormat();
        descriptionTypeForSupportedFormat.setMimeType("text/xml");
        descriptionTypeForSupportedFormat.setSchema(schema);

    }
}
