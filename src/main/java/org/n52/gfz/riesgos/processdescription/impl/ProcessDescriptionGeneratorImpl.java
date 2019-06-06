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
import org.n52.wps.io.GeneratorFactory;
import org.n52.wps.io.IGenerator;
import org.n52.wps.io.IParser;
import org.n52.wps.io.ParserFactory;
import org.n52.wps.io.data.IBBOXData;
import org.n52.wps.io.data.IComplexData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.ILiteralData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implementation of the process description generation
 */
public class ProcessDescriptionGeneratorImpl extends AbstractProcessDescriptionGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDescriptionGeneratorImpl.class);

    private final IConfiguration configuration;

    private final Supplier<List<IParser>> parserSupplier;
    private final Supplier<List<IGenerator>> generatorSupplier;

    /**
     * Constructor (for testing purpose)
     * @param configuration the configuration for which the process description should be generated
     * @param parserSupplier supplier for getting all the parsers
     * @param generatorSupplier supplier for getting all the generators
     */
    public ProcessDescriptionGeneratorImpl(
            final IConfiguration configuration,
            final Supplier<List<IParser>> parserSupplier,
            final Supplier<List<IGenerator>> generatorSupplier) {
        this.configuration = configuration;
        this.parserSupplier = parserSupplier;
        this.generatorSupplier = generatorSupplier;
    }

    /**
     *
     * @param configuration configuration for which the description should be generated
     */
    public ProcessDescriptionGeneratorImpl(final IConfiguration configuration) {
        this(configuration, () -> ParserFactory.getInstance().getAllParsers(), () -> GeneratorFactory.getInstance().getAllGenerators());
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
        processIdentifier.setStringValue(configuration.getFullQualifiedIdentifier());

        final LanguageStringType processTitle = processDescriptionType.addNewTitle();
        processTitle.setStringValue(configuration.getIdentifier());

        final Optional<String> optionalAbstract = configuration.getAbstract();
        if(optionalAbstract.isPresent()) {
            final LanguageStringType abstractType = processDescriptionType.addNewAbstract();
            abstractType.setStringValue(optionalAbstract.get());
        }

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
                inputIdentifier.setStringValue(input.getIdentifier());

                final LanguageStringType inputTitle = inputDescriptionType.addNewTitle();
                inputTitle.setStringValue(input.getIdentifier());

                final Optional<String> inputAbstract = input.getAbstract();
                if(inputAbstract.isPresent()) {
                    final LanguageStringType inputAbstractType = inputDescriptionType.addNewAbstract();
                    inputAbstractType.setStringValue(inputAbstract.get());
                }

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
                    addInputFormats(complexData, foundParsers);
                }
            }
        }

        final ProcessDescriptionType.ProcessOutputs processOutputs = processDescriptionType.addNewProcessOutputs();
        for(final IIdentifierWithBinding output : configuration.getOutputIdentifiers()) {
            final OutputDescriptionType outputDescriptionType = processOutputs.addNewOutput();

            final CodeType outputIdentifier = outputDescriptionType.addNewIdentifier();
            outputIdentifier.setStringValue(output.getIdentifier());

            final LanguageStringType outputTitle = outputDescriptionType.addNewTitle();
            outputTitle.setStringValue(output.getIdentifier());

            final Optional<String> outputAbstract = output.getAbstract();
            if(outputAbstract.isPresent()) {
                final LanguageStringType abstractType = outputDescriptionType.addNewAbstract();
                abstractType.setStringValue(outputAbstract.get());
            }

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
                addOutputFormats(complexData, foundGenerators);
            }
        }
        return result;
    }


}
