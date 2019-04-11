package org.n52.gfz.riesgos.validators;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

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

import org.junit.Test;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test case for TestXmlBindingWithAllowedSchema
 */
public class TestXmlBindingWithAllowedSchema {

    //private final String schemaQuakeml = "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd";
    //private final String schemaShakemap = "http://earthquake.usgs.gov/eqcenter/shakemap";
    private final Path schemaQuakeml = Paths.get("src", "main", "resources", "org", "n52", "gfz", "riesgos", "validators", "xml", "QuakeML-BED-1.2.xsd");
    private final Path schemaShakemap = Paths.get("src", "main", "resources", "org", "n52", "gfz", "riesgos", "validators", "xml", "shakemap.xsd");

    private final Path quakemlfile = Paths.get("src", "test", "resources", "org", "n52", "gfz", "riesgos", "convertformats", "quakeml.xml");
    private final Path shakemapfile = Paths.get("src", "test", "resources", "org", "n52", "gfz", "riesgos", "convertformats", "shakemap.xml");

    /**
     * If the xml value does validate against the schema everything is fine
     * and no error message must be given back
     */
    @Test
    public void testValidQuakeml() throws XmlException, IOException {
        final ICheckDataAndGetErrorMessage validator = new XmlBindingWithAllowedSchema(schemaQuakeml.toString());
        final String filecontent = new String(Files.readAllBytes(quakemlfile));
        final XmlObject content = XmlObject.Factory.parse(filecontent);
        final IData value = new GenericXMLDataBinding(content);
        final Optional<String> errorMessage = validator.check(value);

        if (errorMessage.isPresent()) {
            System.err.println(errorMessage.get());
        }

        assertFalse("We expect the input file to validate", errorMessage.isPresent());
    }

    /**
     * The xml value does not validate against schema, so there must be a message
     */
    @Test
    public void testInvalidQuakeml() throws XmlException {
        final ICheckDataAndGetErrorMessage validator = new XmlBindingWithAllowedSchema(schemaQuakeml.toString());
        final XmlObject content = XmlObject.Factory.parse("<test></test>");
        final IData value = new GenericXMLDataBinding(content);
        final Optional<String> errorMessage = validator.check(value);

        assertTrue("There is a message indicating that there is a problem with the data", errorMessage.isPresent());
    }

    /**
     * If the xml value does validate against the schema everything is fine
     * and no error message must be given back
     */
    @Test
    public void testValidShakemap() throws XmlException, IOException {
        final ICheckDataAndGetErrorMessage validator = new XmlBindingWithAllowedSchema(schemaShakemap.toString());
        final String filecontent = new String(Files.readAllBytes(shakemapfile));
        final XmlObject content = XmlObject.Factory.parse(filecontent);
        final IData value = new GenericXMLDataBinding(content);
        final Optional<String> errorMessage = validator.check(value);

        if (errorMessage.isPresent()) {
            System.err.println(errorMessage.get());
        }

        assertFalse("We expect the input file to validate", errorMessage.isPresent());
    }

    /**
     * The xml value does not validate against schema, so there must be a message
     */
    @Test
    public void testInvalidShakemap() throws XmlException {
        final ICheckDataAndGetErrorMessage validator = new XmlBindingWithAllowedSchema(schemaShakemap.toString());
        final XmlObject content = XmlObject.Factory.parse("<test></test>");
        final IData value = new GenericXMLDataBinding(content);
        final Optional<String> errorMessage = validator.check(value);

        assertTrue("There is a message indicating that there is a problem with the data", errorMessage.isPresent());
    }

    /**
     * The value has the wrong type, so there must be a message
     */
    @Test
    public void testWrongType() {
        final ICheckDataAndGetErrorMessage validator = new XmlBindingWithAllowedSchema(schemaQuakeml.toString());
        final IData value = new LiteralIntBinding(1);
        final Optional<String> errorMessage = validator.check(value);

        assertTrue("There is a message indicating that there is a problem with the data", errorMessage.isPresent());
    }
}
