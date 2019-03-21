package org.n52.gfz.riesgos.bytetoidataconverter;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * This is an implementation to convert a byte[] array of xml information into
 * a GenericXMLDataBinding
 */
public class ConvertBytesToGenericXMLDataBinding implements IConvertByteArrayToIData {

    @Override
    public IData convertToIData(final byte[] content) throws ConvertToIDataException {
        try(final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            return new GenericXMLDataBinding(XmlObject.Factory.parse(byteArrayInputStream));
        } catch(final XmlException | IOException exception) {
            throw new ConvertToIDataException(exception);
        }
    }
}
