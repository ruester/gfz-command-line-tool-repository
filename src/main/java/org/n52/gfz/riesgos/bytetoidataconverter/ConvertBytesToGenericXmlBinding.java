package org.n52.gfz.riesgos.bytetoidataconverter;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ConvertBytesToGenericXmlBinding implements IConvertByteArrayToIData {
    @Override
    public IData convertToIData(final byte[] content) throws ConvertToIDataException {
        try(final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
            return new GenericXMLDataBinding(XmlObject.Factory.parse(byteArrayInputStream));
        } catch(final XmlException xmlException) {
            throw new ConvertToIDataException(xmlException);
        } catch(final IOException ioException) {
            throw new ConvertToIDataException(ioException);
        }
    }
}
