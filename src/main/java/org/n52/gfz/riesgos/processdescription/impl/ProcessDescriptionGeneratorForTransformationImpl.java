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

package org.n52.gfz.riesgos.processdescription.impl;

import net.opengis.ows.x11.CodeType;
import net.opengis.ows.x11.LanguageStringType;
import net.opengis.wps.x100.ComplexDataCombinationType;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import net.opengis.wps.x100.SupportedComplexDataInputType;
import net.opengis.wps.x100.SupportedComplexDataType;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGenerator;
import org.n52.wps.io.GeneratorFactory;
import org.n52.wps.io.IGenerator;
import org.n52.wps.io.IParser;
import org.n52.wps.io.ParserFactory;
import org.n52.wps.io.data.IComplexData;
import org.n52.wps.io.data.IData;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Implementation of the process description generation
 */
public class ProcessDescriptionGeneratorForTransformationImpl implements IProcessDescriptionGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDescriptionGeneratorForTransformationImpl.class);

    private final String identifier;
    private final String fullQualifiedIdentifier;
    private final Class<? extends IComplexData> bindingClass;
    private final String inputIdentifier;
    private final String outputIdentifier;

    private final Supplier<List<IParser>> parserSupplier;
    private final Supplier<List<IGenerator>> generatorSupplier;

    /**
     * Constructor (for testing purpose)
     * @param identifier the identifier (title) of the process
     * @param fullQualifiedIdentifier the full qualified identifier of the data (to simulate a package path)
     * @param bindingClass the class to use for the binding
     * @param parserSupplier supplier for getting all the parsers
     * @param generatorSupplier supplier for getting all the generators
     */
    public ProcessDescriptionGeneratorForTransformationImpl(
            final String identifier,
            final String fullQualifiedIdentifier,
            final Class<? extends IComplexData> bindingClass,
            final String inputIdentifier,
            final String outputIdentifier,
            final Supplier<List<IParser>> parserSupplier,
            final Supplier<List<IGenerator>> generatorSupplier) {
        this.identifier = identifier;
        this.fullQualifiedIdentifier = fullQualifiedIdentifier;
        this.bindingClass = bindingClass;
        this.inputIdentifier = inputIdentifier;
        this.outputIdentifier = outputIdentifier;

        this.parserSupplier = parserSupplier;
        this.generatorSupplier = generatorSupplier;
    }


    public ProcessDescriptionGeneratorForTransformationImpl(
            final String identifier,
            final String fullQualifiedIdentifier,
            final Class<? extends IComplexData> bindingClass,
            final String inputIdentifier,
            final String outputIdentifier) {
        this(identifier, fullQualifiedIdentifier, bindingClass,
                inputIdentifier, outputIdentifier,
                () -> ParserFactory.getInstance().getAllParsers(),
                () -> GeneratorFactory.getInstance().getAllGenerators());
    }

    @Override
    public ProcessDescriptionsDocument generateProcessDescription() {

        final ProcessDescriptionsDocument result = ProcessDescriptionsDocument.Factory.newInstance();
        final ProcessDescriptionsDocument.ProcessDescriptions processDescriptions = result.addNewProcessDescriptions();
        processDescriptions.setLang("en-US");
        processDescriptions.setService("WPS");
        processDescriptions.setVersion("1.0.0");
        final ProcessDescriptionType processDescriptionType = processDescriptions.addNewProcessDescription();

        final CodeType processIdentifier = processDescriptionType.addNewIdentifier();
        processIdentifier.setStringValue(fullQualifiedIdentifier);

        final LanguageStringType processTitle = processDescriptionType.addNewTitle();
        processTitle.setStringValue(identifier);

        processDescriptionType.setStatusSupported(true);
        processDescriptionType.setStoreSupported(true);
        processDescriptionType.setProcessVersion("1.0.0");

        final ProcessDescriptionType.DataInputs dataInputs = processDescriptionType.addNewDataInputs();


        final InputDescriptionType inputDescriptionType = dataInputs.addNewInput();
        inputDescriptionType.setMinOccurs(BigInteger.ONE);
        inputDescriptionType.setMaxOccurs(BigInteger.ONE);

        final CodeType inputIdentifier = inputDescriptionType.addNewIdentifier();
        inputIdentifier.setStringValue(this.inputIdentifier);

        final LanguageStringType inputTitle = inputDescriptionType.addNewTitle();
        inputTitle.setStringValue(this.inputIdentifier);

        final Class<? extends IData> inputDataTypeClass = bindingClass;
        final List<Class<?>> interfacesInputData = findInterfaces(inputDataTypeClass);


        final SupportedComplexDataInputType complexDataInputData = inputDescriptionType.addNewComplexData();
        final List<IParser> parsers = parserSupplier.get();
        final List<IParser> foundParsers = findParser(parsers, inputDataTypeClass);
        addInputFormats(complexDataInputData, foundParsers);


        final ProcessDescriptionType.ProcessOutputs processOutputs = processDescriptionType.addNewProcessOutputs();
        final OutputDescriptionType outputDescriptionType = processOutputs.addNewOutput();

        final CodeType outputIdentifier = outputDescriptionType.addNewIdentifier();
        outputIdentifier.setStringValue(this.outputIdentifier);

        final LanguageStringType outputTitle = outputDescriptionType.addNewTitle();
        outputTitle.setStringValue(this.outputIdentifier);

        final Class<?> outputDataTypeClass = bindingClass;
        final List<Class<?>> interfacesOutputData = findInterfaces(outputDataTypeClass);


        final SupportedComplexDataType complexDataOutputData = outputDescriptionType.addNewComplexOutput();
        final List<IGenerator> generators = generatorSupplier.get();
        final List<IGenerator> foundGenerators = findGenerators(generators, outputDataTypeClass);
        addOutputFormats(complexDataOutputData, foundGenerators);

        return result;
    }

    /*
     * searches for all the interfaces a class implements
     * It is used to search for IData implementations that are using
     * the classes for LiteralData and ComplexData.
     * If the current class has no interfaces the search will be extended
     * to the super class
     */
    private List<Class<?>> findInterfaces(final Class<?> clazz) {
        final List<Class<?>> result = Arrays.asList(clazz.getInterfaces());
        final Class<?> superClass = clazz.getSuperclass();
        if(result.isEmpty() && superClass != null) {
            return findInterfaces(superClass);
        }
        return result;
    }

    /*
     * searches for a parser that supports a specific binding class
     */
    private List<IParser> findParser(final List<IParser> allParsers, final Class<?> clazz) {
        return allParsers.stream().filter(new ParserSupportsClass(clazz)).collect(Collectors.toList());
    }

    /*
     * searches for a generator that supports a specific binding class
     */
    private List<IGenerator> findGenerators(final List<IGenerator> allGenerators, final Class<?> clazz) {
        return allGenerators.stream().filter(new GeneratorSupportsClass(clazz)).collect(Collectors.toList());
    }

    /*
     * adds a complex input format to the description of the data input
     * uses all the parsers that support the given class
     *
     * The code may add a schema to text/xml to provide a schema even for the GenericXMLDataBinding class
     */
    private void addInputFormats(final SupportedComplexDataInputType complexData, final List<IParser> foundParsers) {
        final ComplexDataCombinationsType supportedInputFormat = complexData.addNewSupported();

        for(final IParser parser : foundParsers) {
            final List<FormatEntry> supportedFullFormats = parser.getSupportedFullFormats();
            if (complexData.getDefault() == null) {
                ComplexDataCombinationType defaultInputFormat = complexData.addNewDefault();
                final FormatEntry format = supportedFullFormats.get(0);
                final ComplexDataDescriptionType supportedFormat = defaultInputFormat.addNewFormat();
                supportedFormat.setMimeType(format.getMimeType());
                String encoding = format.getEncoding();
                if (encoding != null && !encoding.equals("")) {
                    supportedFormat.setEncoding(encoding);
                }

                String schema = format.getSchema();
                if (schema != null && !schema.equals("")) {
                    supportedFormat.setSchema(schema);
                }
            }

            for(final FormatEntry format : supportedFullFormats) {
                final ComplexDataDescriptionType supportedFormat = supportedInputFormat.addNewFormat();
                supportedFormat.setMimeType(format.getMimeType());
                if (format.getEncoding() != null) {
                    supportedFormat.setEncoding(format.getEncoding());
                }

                if (format.getSchema() != null) {
                    supportedFormat.setSchema(format.getSchema());
                }
            }
        }
    }

    /*
    * adds a complex output format to the description of the process outputs
     * uses all the generators that support the given class
     *
     * The code may add a schema to text/xml to provide a schema even for the GenericXMLDataBinding class
     */
    private void addOutputFormats(SupportedComplexDataType complexData, List<IGenerator> foundGenerators) {
        final ComplexDataCombinationsType supportedOutputFormat = complexData.addNewSupported();

        for(final IGenerator generator : foundGenerators) {
            final List<FormatEntry> supportedFullFormats = generator.getSupportedFullFormats();
            if (complexData.getDefault() == null) {
                ComplexDataCombinationType defaultInputFormat = complexData.addNewDefault();
                final FormatEntry format = supportedFullFormats.get(0);
                final ComplexDataDescriptionType supportedFormat = defaultInputFormat.addNewFormat();
                supportedFormat.setMimeType(format.getMimeType());
                String encoding = format.getEncoding();
                if (encoding != null && !encoding.equals("")) {
                    supportedFormat.setEncoding(encoding);
                }

                String schema = format.getSchema();
                if (schema != null && !schema.equals("")) {
                    supportedFormat.setSchema(schema);
                }
            }

            for(final FormatEntry format : supportedFullFormats) {
                final ComplexDataDescriptionType supportedFormat = supportedOutputFormat.addNewFormat();
                supportedFormat.setMimeType(format.getMimeType());
                if (format.getEncoding() != null) {
                    supportedFormat.setEncoding(format.getEncoding());
                }

                if (format.getSchema() != null) {
                    supportedFormat.setSchema(format.getSchema());
                }
            }
        }
    }

    /*
     * Predicate to filter parser that support the class
     */
    private static class ParserSupportsClass implements Predicate<IParser> {
        private final Class<?> clazz;

        ParserSupportsClass(final Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean test(final IParser parser) {
            return Arrays.asList(parser.getSupportedDataBindings()).contains(clazz);
        }
    }

    /*
     * Predicate to filter generators that support the class
     */
    private static class GeneratorSupportsClass implements Predicate<IGenerator> {
        private final Class<?> clazz;

        GeneratorSupportsClass(final Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean test(final IGenerator generator) {
            return Arrays.asList(generator.getSupportedDataBindings()).contains(clazz);
        }
    }
}
