package org.n52.gfz.riesgos.processdescription.impl;

/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

import net.opengis.ows.x11.AllowedValuesDocument;
import net.opengis.ows.x11.CodeType;
import net.opengis.ows.x11.DomainMetadataType;
import net.opengis.ows.x11.LanguageStringType;
import net.opengis.wps.x100.CRSsType;
import net.opengis.wps.x100.ComplexDataCombinationType;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.LiteralInputType;
import net.opengis.wps.x100.LiteralOutputType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import net.opengis.wps.x100.SupportedCRSsType;
import net.opengis.wps.x100.SupportedComplexDataInputType;
import net.opengis.wps.x100.SupportedComplexDataType;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGenerator;
import org.n52.wps.io.GeneratorFactory;
import org.n52.wps.io.IGenerator;
import org.n52.wps.io.IParser;
import org.n52.wps.io.ParserFactory;
import org.n52.wps.io.data.IBBOXData;
import org.n52.wps.io.data.IComplexData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.ILiteralData;
import org.n52.wps.webapp.api.FormatEntry;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Implementation of the process description generation
 */
public class ProcessDescriptionGeneratorImpl implements IProcessDescriptionGenerator {

    private final Supplier<List<IParser>> parserSupplier;
    private final Supplier<List<IGenerator>> generatorSupplier;

    /**
     * Constructor (for testing purpose)
     * @param parserSupplier supplier for getting all the parsers
     * @param generatorSupplier supplier for getting all the generators
     */
    public ProcessDescriptionGeneratorImpl(final Supplier<List<IParser>> parserSupplier, final Supplier<List<IGenerator>> generatorSupplier) {
        this.parserSupplier = parserSupplier;
        this.generatorSupplier = generatorSupplier;
    }

    /**
     * Default constructor
     */
    public ProcessDescriptionGeneratorImpl() {
        this(() -> ParserFactory.getInstance().getAllParsers(), () -> GeneratorFactory.getInstance().getAllGenerators());
    }

    @Override
    public ProcessDescriptionsDocument generateProcessDescription(final IConfiguration configuration) {

        final ProcessDescriptionsDocument result = ProcessDescriptionsDocument.Factory.newInstance();
        final ProcessDescriptionsDocument.ProcessDescriptions processDescriptions = result.addNewProcessDescriptions();
        processDescriptions.setLang("en-US");
        processDescriptions.setService("WPS");
        processDescriptions.setVersion("1.0.0");
        final ProcessDescriptionType processDescriptionType = processDescriptions.addNewProcessDescription();

        final CodeType processIdentifier = processDescriptionType.addNewIdentifier();
        processIdentifier.setStringValue(configuration.getFullQualifiedIdentifier());

        final LanguageStringType processTitle = processDescriptionType.addNewTitle();
        processTitle.setStringValue(configuration.getIdentifier());

        processDescriptionType.setStatusSupported(true);
        processDescriptionType.setStoreSupported(true);
        processDescriptionType.setProcessVersion("1.0.0");

        final List<IIdentifierWithBinding> inputIdentifiers = configuration.getInputIdentifiers();
        if(! inputIdentifiers.isEmpty()) {
            final ProcessDescriptionType.DataInputs dataInputs = processDescriptionType.addNewDataInputs();

            for (final IIdentifierWithBinding input : configuration.getInputIdentifiers()) {
                final InputDescriptionType inputDescriptionType = dataInputs.addNewInput();
                inputDescriptionType.setMinOccurs(BigInteger.ONE);
                inputDescriptionType.setMaxOccurs(BigInteger.ONE);

                final CodeType inputIdentifier = inputDescriptionType.addNewIdentifier();
                inputIdentifier.setStringValue(input.getIdentifer());

                final LanguageStringType inputTitle = inputDescriptionType.addNewTitle();
                inputTitle.setStringValue(input.getIdentifer());

                final Class<? extends IData> inputDataTypeClass = input.getBindingClass();
                final List<Class<?>> interfaces = findInterfaces(inputDataTypeClass);

                if(interfaces.stream().anyMatch(ILiteralData.class::equals)) {
                    final LiteralInputType literalData = inputDescriptionType.addNewLiteralData();
                    final Constructor<?>[] constructors = inputDataTypeClass.getConstructors();

                    final Optional<String> inputClassType = findSimpleNameOfFirstConstructorParameter(constructors);

                    if(inputClassType.isPresent()) {
                        final DomainMetadataType datatype = literalData.addNewDataType();
                        datatype.setReference("xs:" + inputClassType.get().toLowerCase());

                        final Optional<List<String>> optionalAllowedValues = input.getAllowedValues();
                        if(optionalAllowedValues.isPresent()) {
                            final AllowedValuesDocument.AllowedValues allowedValues = literalData.addNewAllowedValues();
                            for(final String allowedValue : optionalAllowedValues.get()) {
                                allowedValues.addNewValue().setStringValue(allowedValue);
                            }
                        } else {
                            literalData.addNewAnyValue();
                        }

                        final Optional<String> optionalDefaultValue = input.getDefaultValue();
                        optionalDefaultValue.ifPresent(literalData::setDefaultValue);

                    }
                } else if(interfaces.stream().anyMatch(IBBOXData.class::equals)) {
                    final SupportedCRSsType bboxData = inputDescriptionType.addNewBoundingBoxData();
                    final Optional<List<String>> optionalSupportedCrsList = input.getSupportedCRSForBBox();
                    if(optionalSupportedCrsList.isPresent()) {
                        final List<String> supportedCrsList = optionalSupportedCrsList.get();
                        for(int i = 0; i < supportedCrsList.size(); i++) {
                            final String supportedCrs = supportedCrsList.get(i);
                            if (i == 0) {
                                final SupportedCRSsType.Default defaultCRS = bboxData.addNewDefault();
                                defaultCRS.setCRS(supportedCrs);
                            } else if(i == 1) {
                                final CRSsType supportedCRS = bboxData.addNewSupported();
                                supportedCRS.addCRS(supportedCrs);
                            } else {
                                bboxData.getSupported().addCRS(supportedCrs);
                            }
                        }
                    }
                } else if(interfaces.stream().anyMatch(IComplexData.class::equals)) {
                    final SupportedComplexDataInputType complexData = inputDescriptionType.addNewComplexData();
                    final List<IParser> parsers = parserSupplier.get();
                    final List<IParser> foundParsers = findParser(parsers, inputDataTypeClass);
                    addInputFormats(complexData, foundParsers, input.getSchema().orElse("null"));
                }
            }
        }

        final ProcessDescriptionType.ProcessOutputs processOutputs = processDescriptionType.addNewProcessOutputs();
        for(final IIdentifierWithBinding output : configuration.getOutputIdentifiers()) {
            final OutputDescriptionType outputDescriptionType = processOutputs.addNewOutput();

            final CodeType outputIdentifier = outputDescriptionType.addNewIdentifier();
            outputIdentifier.setStringValue(output.getIdentifer());

            final LanguageStringType outputTitle = outputDescriptionType.addNewTitle();
            outputTitle.setStringValue(output.getIdentifer());

            final Class<?> outputDataTypeClass = output.getBindingClass();
            final List<Class<?>> interfaces = findInterfaces(outputDataTypeClass);

            if(interfaces.stream().anyMatch(ILiteralData.class::equals)) {
                final LiteralOutputType literalOutputType = outputDescriptionType.addNewLiteralOutput();
                final Constructor<?>[] constructors = outputDataTypeClass.getConstructors();

                final Optional<String> outputClassType = findSimpleNameOfFirstConstructorParameter(constructors);
                outputClassType.ifPresent(
                        classType -> literalOutputType.addNewDataType().setReference("xs:" + classType.toLowerCase()));

            } else if(interfaces.stream().anyMatch(IBBOXData.class::equals)) {
                final SupportedCRSsType bboxData = outputDescriptionType.addNewBoundingBoxOutput();
                final Optional<List<String>> optionalSupportedCrsList = output.getSupportedCRSForBBox();
                boolean isFirst = true;
                if(optionalSupportedCrsList.isPresent()) {
                    for (final String supportedCrs : optionalSupportedCrsList.get()) {
                        if (isFirst) {
                            final SupportedCRSsType.Default defaultCRS = bboxData.addNewDefault();
                            defaultCRS.setCRS(supportedCrs);
                            final CRSsType supportedCRS = bboxData.addNewSupported();
                            supportedCRS.addCRS(supportedCrs);
                            isFirst = false;
                        } else {
                            bboxData.getSupported().addCRS(supportedCrs);
                        }
                    }
                }
            } else if(interfaces.stream().anyMatch(IComplexData.class::equals)) {
                final SupportedComplexDataType complexData = outputDescriptionType.addNewComplexOutput();
                final List<IGenerator> generators = generatorSupplier.get();
                final List<IGenerator> foundGenerators = findGenerators(generators, outputDataTypeClass);
                addOutputFormats(complexData, foundGenerators, output.getSchema().orElse("null"));
            }
        }
        return result;
    }

    /*
     * takes all the constructors and searchs for one constructor getting a single element
     * the class of this element is the name to search for.
     *
     * Example:
     * I search in the constructors of LiteralStringBinding
     * There is a constructor using a single string argument
     * --> the result is string
     *
     * Same for LiteralDoubleBinding and so on
     */
    private Optional<String> findSimpleNameOfFirstConstructorParameter(final Constructor<?>[] constructors) {
        Optional<String> result = Optional.empty();
        for(final Constructor<?> constructor : constructors) {
            final Class<?>[] supportedClasses = constructor.getParameterTypes();
            if(supportedClasses.length == 1) {
                result = Optional.of(supportedClasses[0].getSimpleName());
            }
        }
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
    private void addInputFormats(final SupportedComplexDataInputType complexData, final List<IParser> foundParsers,
                                 final String optionalSchema) {
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
                } else if(optionalSchema != null && "text/xml".equals(format.getMimeType())) {
                    supportedFormat.setSchema(optionalSchema);
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
                } else if(optionalSchema != null && "text.xml".equals(format.getMimeType())) {
                    supportedFormat.setSchema(optionalSchema);
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
    private void addOutputFormats(SupportedComplexDataType complexData, List<IGenerator> foundGenerators, final String optionalSchema) {
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
                } else if(optionalSchema != null && "text/xml".equals(format.getMimeType())) {
                    supportedFormat.setSchema(optionalSchema);
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
                } else if(optionalSchema != null && "text/xml".equals(format.getMimeType())) {
                    supportedFormat.setSchema(optionalSchema);
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
