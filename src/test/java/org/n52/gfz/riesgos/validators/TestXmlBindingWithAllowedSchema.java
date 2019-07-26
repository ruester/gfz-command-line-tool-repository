package org.n52.gfz.riesgos.validators;

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
 */

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import org.junit.Test;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assume.assumeTrue;

/**
 * Test case for TestXmlBindingWithAllowedSchema
 */
public class TestXmlBindingWithAllowedSchema {

    private final String schemaQuakeml = "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd";
    private final String schemaShakemap = "http://earthquake.usgs.gov/eqcenter/shakemap";

    private final Path quakemlfile = Paths.get("src", "test", "resources", "org", "n52", "gfz", "riesgos", "formats", "quakeml.xml");
    private final Path shakemapfile = Paths.get("src", "test", "resources", "org", "n52", "gfz", "riesgos", "formats", "shakemap.xml");
    private final Path shakemapfilegithub = Paths.get("src", "test", "resources", "org", "n52", "gfz", "riesgos", "formats", "shakemap_github.xml");

    /**
     * If the xml value does validate against the schema everything is fine
     * and no error message must be given back
     */
    @Test
    public void testValidQuakeml() throws XmlException, IOException {
        final ICheckDataAndGetErrorMessage<GenericXMLDataBinding> validator = new XmlBindingWithAllowedSchema<>(schemaQuakeml);
        final String filecontent = new String(Files.readAllBytes(quakemlfile));
        final XmlObject content = XmlObject.Factory.parse(filecontent);
        final GenericXMLDataBinding value = new GenericXMLDataBinding(content);
        final Optional<String> errorMessage = validator.check(value);

        errorMessage.ifPresent(System.err::println);

        assertFalse("We expect the input file to validate", errorMessage.isPresent());
    }

    /**
     * The xml value does not validate against schema, so there must be a message
     */
    @Test
    public void testInvalidQuakeml() throws XmlException {
        final ICheckDataAndGetErrorMessage<GenericXMLDataBinding> validator = new XmlBindingWithAllowedSchema<>(schemaQuakeml);
        final XmlObject content = XmlObject.Factory.parse("<test></test>");
        final GenericXMLDataBinding value = new GenericXMLDataBinding(content);
        final Optional<String> errorMessage = validator.check(value);

        assertTrue("There is a message indicating that there is a problem with the data", errorMessage.isPresent());
    }

    /**
     * If the xml value does validate against the schema everything is fine
     * and no error message must be given back
     */
    @Test
    public void testValidShakemap() throws XmlException, IOException {
        final ICheckDataAndGetErrorMessage<GenericXMLDataBinding> validator = new XmlBindingWithAllowedSchema<>(schemaShakemap);
        final String filecontent = new String(Files.readAllBytes(shakemapfile));
        final XmlObject content = XmlObject.Factory.parse(filecontent);
        final GenericXMLDataBinding value = new GenericXMLDataBinding(content);
        final Optional<String> errorMessage = validator.check(value);

        errorMessage.ifPresent(System.err::println);

        assertFalse("We expect the input file to validate", errorMessage.isPresent());
    }

    /**
     * Test for valid xml with external schema url (QuakeML)
     * and no error message must be given back
     */
    @Test
    public void testValidQuakemlExternalSchema() throws XmlException, IOException {
        final String externalSchema = "http://quake.ethz.ch/schema/xsd/QuakeML-BED-1.2.xsd";

        boolean connected = false;

        try {
            new URL(externalSchema).openStream().close();
            connected = true;
        } catch (Exception e) {
            return;
        }

        // skip test if no internet connection available or URL broken
        assumeTrue(connected);

        final ICheckDataAndGetErrorMessage<GenericXMLDataBinding> validator = new XmlBindingWithAllowedSchema<>(externalSchema);
        final String filecontent = new String(Files.readAllBytes(quakemlfile));
        final XmlObject content = XmlObject.Factory.parse(filecontent);
        final GenericXMLDataBinding value = new GenericXMLDataBinding(content);
        final Optional<String> errorMessage = validator.check(value);

        errorMessage.ifPresent(System.err::println);

        assertFalse("We expect the input file to validate", errorMessage.isPresent());
    }

    /**
     * Test for valid xml with external schema url (ShakeMap)
     * and no error message must be given back
     */
    @Test
    public void testValidShakemapExternalSchema() throws XmlException, IOException {
        final String externalSchema = "https://raw.githubusercontent.com/bpross-52n/shakemap-xmlbeans/master/src/main/xsd/shakemap.xsd";

        boolean connected = false;

        try {
            new URL(externalSchema).openStream().close();
            connected = true;
        } catch (Exception e) {
            return;
        }

        // skip test if no internet connection available or URL broken
        assumeTrue(connected);

        final ICheckDataAndGetErrorMessage<GenericXMLDataBinding> validator = new XmlBindingWithAllowedSchema<>(externalSchema);
        final String filecontent = new String(Files.readAllBytes(shakemapfilegithub));
        final XmlObject content = XmlObject.Factory.parse(filecontent);
        final GenericXMLDataBinding value = new GenericXMLDataBinding(content);
        final Optional<String> errorMessage = validator.check(value);

        errorMessage.ifPresent(System.err::println);

        assertFalse("We expect the input file to validate", errorMessage.isPresent());
    }

    /**
     * The xml value does not validate against schema, so there must be a message
     */
    @Test
    public void testInvalidShakemap() throws XmlException {
        final ICheckDataAndGetErrorMessage<GenericXMLDataBinding> validator = new XmlBindingWithAllowedSchema<>(schemaShakemap);
        final XmlObject content = XmlObject.Factory.parse("<test></test>");
        final GenericXMLDataBinding value = new GenericXMLDataBinding(content);
        final Optional<String> errorMessage = validator.check(value);

        assertTrue("There is a message indicating that there is a problem with the data", errorMessage.isPresent());
    }

}
