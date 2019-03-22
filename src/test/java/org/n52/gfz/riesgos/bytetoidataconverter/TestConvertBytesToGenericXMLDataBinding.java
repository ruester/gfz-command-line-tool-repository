package org.n52.gfz.riesgos.bytetoidataconverter;

/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 *
 *
 */

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test class for the ConvertBytesToGenericXMLDataBinding class
 */
public class TestConvertBytesToGenericXMLDataBinding {

    /**
     * Tests the conversion with an valid input
     */
    @Test
    public void testValid() {

        final String xmlStrInput = "<a><b>some text</b></a>";

        final byte[] content = xmlStrInput.getBytes();

        final IConvertByteArrayToIData converter = new ConvertBytesToGenericXMLDataBinding();

        try {
            final IData result = converter.convertToIData(content);
            if(! (result instanceof GenericXMLDataBinding)) {
                fail("Wrong IData type");
            }
            final GenericXMLDataBinding xmlBindingResult = (GenericXMLDataBinding) result;
            final XmlObject xmlResult = xmlBindingResult.getPayload();
            final String xmlStrResult = xmlResult.xmlText();
            assertEquals("The strings are equal", xmlStrInput, xmlStrResult);
        } catch(final ConvertToIDataException exception) {
            fail("There is an exception");
        }
    }

    @Test
    public void testNonValid() {
        final String xmlStrInput = "<a><b></c>";

        final byte[] content = xmlStrInput.getBytes();

        final IConvertByteArrayToIData converter = new ConvertBytesToGenericXMLDataBinding();

        try {
            converter.convertToIData(content);
            fail("There must be an exception");
        } catch(final ConvertToIDataException exception) {
            assertNotNull("There is a exception on the converion to an IData", exception);
            final Throwable cause = exception.getCause();
            assertNotNull("There is an inner cause", cause);
            if(! (cause instanceof XmlException)) {
                fail("This is not thrown because of an XmlException (non valid xml)");

            }

        }
    }
}
