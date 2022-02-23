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
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorData;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorInputData;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorOutputData;
import org.n52.gfz.riesgos.processdescription.impl.addformats.IAddTypeDataForInput;
import org.n52.gfz.riesgos.processdescription.impl.addformats.IAddTypeDataForOutput;
import org.n52.wps.io.GeneratorFactory;
import org.n52.wps.io.IGenerator;
import org.n52.wps.io.IParser;
import org.n52.wps.io.ParserFactory;
import org.n52.wps.io.data.IBBOXData;
import org.n52.wps.io.data.IComplexData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.ILiteralData;
import org.n52.wps.io.data.binding.literal.LiteralDateTimeBinding;
import org.n52.wps.webapp.api.FormatEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implementation of the process description generation.
 */
public class ProcessDescriptionGeneratorImpl
        extends AbstractProcessDescriptionGenerator {

    /**
     * Logger to log unexpected behaviour.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProcessDescriptionGeneratorImpl.class);

    /**
     * Data with the process description informations.
     */
    private final IProcessDescriptionGeneratorData data;

    /**
     * Supplier to get all the parsers it can use.
     */
    private final Supplier<List<IParser>> parserSupplier;
    /**
     * Supplier to get all the generators it can use.
     */
    private final Supplier<List<IGenerator>> generatorSupplier;

    /**
     * Constructor (for testing purpose).
     * @param aData the configuration for which the
     *             process description should be generated
     * @param aParserSupplier supplier for getting all the parsers
     * @param aGeneratorSupplier supplier for getting all the generators
     */
    public ProcessDescriptionGeneratorImpl(
            final IProcessDescriptionGeneratorData aData,
            final Supplier<List<IParser>> aParserSupplier,
            final Supplier<List<IGenerator>> aGeneratorSupplier) {
        this.data = aData;
        this.parserSupplier = aParserSupplier;
        this.generatorSupplier = aGeneratorSupplier;
    }

    /**
     * Constructor for productive use.
     * @param aData configuration for which the description should be generated
     */
    public ProcessDescriptionGeneratorImpl(
            final IProcessDescriptionGeneratorData aData) {
        this(aData,
                () -> ParserFactory.getInstance().getAllParsers(),
                () -> GeneratorFactory.getInstance().getAllGenerators());
    }

    /**
     * Generates the process description.
     * @return ProcessDescriptionsDocument
     */
    @Override
    public ProcessDescriptionsDocument generateProcessDescription() {

        final ProcessDescriptionsDocument result =
                ProcessDescriptionsDocument.Factory.newInstance();
        final ProcessDescriptionsDocument.ProcessDescriptions
                processDescriptions = result.addNewProcessDescriptions();
        processDescriptions.setLang("en-US");
        processDescriptions.setService("WPS");
        processDescriptions.setVersion("1.0.0");
        final ProcessDescriptionType processDescriptionType =
                processDescriptions.addNewProcessDescription();

        final CodeType processIdentifier =
                processDescriptionType.addNewIdentifier();
        processIdentifier.setStringValue(data.getFullQualifiedIdentifier());

        final LanguageStringType processTitle =
                processDescriptionType.addNewTitle();
        processTitle.setStringValue(data.getIdentifier());

        final Optional<String> optionalAbstract = data.getProcessAbstract();
        if (optionalAbstract.isPresent()) {
            final LanguageStringType abstractType =
                    processDescriptionType.addNewAbstract();
            abstractType.setStringValue(optionalAbstract.get());
        }

        processDescriptionType.setStatusSupported(true);
        processDescriptionType.setStoreSupported(true);
        processDescriptionType.setProcessVersion("1.0.0");

        final List<IProcessDescriptionGeneratorInputData> inputIdentifiers =
                data.getInputData();
        if (!inputIdentifiers.isEmpty()) {
            final ProcessDescriptionType.DataInputs dataInputs =
                    processDescriptionType.addNewDataInputs();

            for (final IProcessDescriptionGeneratorInputData input
                    : inputIdentifiers) {
                final InputDescriptionType inputDescriptionType =
                        dataInputs.addNewInput();
                if (input.isOptional()) {
                    inputDescriptionType.setMinOccurs(BigInteger.ZERO);
                } else {
                    inputDescriptionType.setMinOccurs(BigInteger.ONE);
                }

                inputDescriptionType.setMaxOccurs(BigInteger.ONE);

                final CodeType inputIdentifier =
                        inputDescriptionType.addNewIdentifier();
                inputIdentifier.setStringValue(input.getIdentifier());

                final LanguageStringType inputTitle =
                        inputDescriptionType.addNewTitle();
                inputTitle.setStringValue(input.getIdentifier());

                final Optional<String> inputAbstract = input.getAbstract();
                if (inputAbstract.isPresent()) {
                    final LanguageStringType inputAbstractType =
                            inputDescriptionType.addNewAbstract();
                    inputAbstractType.setStringValue(inputAbstract.get());
                }

                final IAddTypeDataForInput addTypeDataForInput =
                        dispatchAddInputType(input);
                addTypeDataForInput.addTypeData(inputDescriptionType);
            }
        }

        final ProcessDescriptionType.ProcessOutputs processOutputs =
                processDescriptionType.addNewProcessOutputs();
        for (final IProcessDescriptionGeneratorOutputData output
                : data.getOutputData()) {
            final OutputDescriptionType outputDescriptionType =
                    processOutputs.addNewOutput();

            final CodeType outputIdentifier =
                    outputDescriptionType.addNewIdentifier();
            outputIdentifier.setStringValue(output.getIdentifier());

            final LanguageStringType outputTitle =
                    outputDescriptionType.addNewTitle();
            outputTitle.setStringValue(output.getIdentifier());

            final Optional<String> outputAbstract = output.getAbstract();
            if (outputAbstract.isPresent()) {
                final LanguageStringType abstractType =
                        outputDescriptionType.addNewAbstract();
                abstractType.setStringValue(outputAbstract.get());
            }

            final IAddTypeDataForOutput addTypeDataForOutput =
                    dispatchAddOutputType(output);
            addTypeDataForOutput.addTypeData(outputDescriptionType);
        }
        return result;
    }


    /**
     * Adding the input format data for literal values.
     */
    private class AddTypeForInputLiteralImpl implements IAddTypeDataForInput {

        /**
         * Input parameter.
         */
        private final IProcessDescriptionGeneratorInputData inputParameter;

        /**
         * Default constructor.
         * @param aInputParameter input parameter to add as literal value
         */
        AddTypeForInputLiteralImpl(
                final IProcessDescriptionGeneratorInputData aInputParameter
        ) {
            this.inputParameter = aInputParameter;
        }

        /**
         *
         * @param inputDescriptionType xml with the data for the input
         */
        @Override
        public void addTypeData(
                final InputDescriptionType inputDescriptionType) {
            final LiteralInputType literalData =
                    inputDescriptionType.addNewLiteralData();
            final Constructor<?>[] constructors =
                    inputParameter.getBindingClass().getConstructors();

            final Optional<String> inputClassType =
                    findSimpleNameOfFirstConstructorParameter(constructors);

            if (inputClassType.isPresent()) {
                final DomainMetadataType datatype =
                        literalData.addNewDataType();
                String lowerCaseInputClassType =
                        inputClassType.get().toLowerCase();
                if (inputParameter.getBindingClass().equals(
                        LiteralDateTimeBinding.class
                )) {
                    // The literalDateTimeBinding class also supports time.
                    // It serializes to the long value of the
                    // number of milliseconds since January 1, 1970,
                    // 00:00:00 GMT.
                    //
                    // As the mechanism gets the constructor that gets
                    // a java Date object, the current mechanism
                    // dispatches
                    // to just 'date'.
                    // But in some of our cases we want to have
                    // datetime values.
                    lowerCaseInputClassType = "dateTime";
                }
                datatype.setReference("xs:"
                        + lowerCaseInputClassType);

                final Optional<List<String>> optionalAllowedValues =
                        inputParameter.getAllowedValues();
                if (optionalAllowedValues.isPresent()) {
                    final AllowedValuesDocument.AllowedValues allowedValues =
                            literalData.addNewAllowedValues();
                    for (final String allowedValue
                            : optionalAllowedValues.get()) {
                        allowedValues.addNewValue()
                                .setStringValue(allowedValue);
                    }
                } else {
                    literalData.addNewAnyValue();
                }

                final Optional<String> optionalDefaultValue =
                        inputParameter.getDefaultValue();
                optionalDefaultValue.ifPresent(literalData::setDefaultValue);

            }
        }
    }

    /**
     * Adding the input parameter as bounding box.
     */
    private class AddTypeForInputBBoxImpl implements IAddTypeDataForInput {
        /**
         * Input paramter to add.
         */
        private final IProcessDescriptionGeneratorInputData inputParameter;

        /**
         * Default constructor.
         * @param aInputParameter input paramter to add as bounding box
         */
        AddTypeForInputBBoxImpl(
                final IProcessDescriptionGeneratorInputData aInputParameter
        ) {
            this.inputParameter = aInputParameter;
        }

        /**
         *
         * @param inputDescriptionType xml with the data for the input
         */
        @Override
        public void addTypeData(
                final InputDescriptionType inputDescriptionType) {
            final SupportedCRSsType bboxData =
                    inputDescriptionType.addNewBoundingBoxData();
            final Optional<List<String>> optionalSupportedCrsList =
                    inputParameter.getSupportedCrs();
            if (optionalSupportedCrsList.isPresent()) {
                final List<String> supportedCrsList =
                        optionalSupportedCrsList.get();
                for (int i = 0; i < supportedCrsList.size(); i++) {
                    final String supportedCrs = supportedCrsList.get(i);
                    if (i == 0) {
                        final SupportedCRSsType.Default defaultCRS =
                                bboxData.addNewDefault();
                        defaultCRS.setCRS(supportedCrs);
                    } else if (i == 1) {
                        final CRSsType supportedCRS =
                                bboxData.addNewSupported();
                        supportedCRS.addCRS(supportedCrs);
                    } else {
                        bboxData.getSupported().addCRS(supportedCrs);
                    }
                }
            }
        }
    }

    /**
     * Adding the input parameter as complex input.
     */
    private class AddTypeForInputComplexImpl implements IAddTypeDataForInput {
        /**
         * Input parameter to add as complex input.
         */
        private final IProcessDescriptionGeneratorInputData inputParameter;

        /**
         * Default constructor.
         * @param aInputParameter input parameter to add as complex input
         */
        AddTypeForInputComplexImpl(
                final IProcessDescriptionGeneratorInputData aInputParameter
        ) {
            this.inputParameter = aInputParameter;
        }

        @Override
        public void addTypeData(
                final InputDescriptionType inputDescriptionType) {
            final SupportedComplexDataInputType complexData =
                    inputDescriptionType.addNewComplexData();
            final List<IParser> parsers = parserSupplier.get();
            final List<IParser> foundParsers =
                    findParser(parsers, inputParameter.getBindingClass());

            final List<FormatEntry> supportedFullFormats =
                    extractFormatsFromParsers(foundParsers);

            final FormatEntry defaultFormat = inputParameter.getDefaultFormat()
                    .orElse(supportedFullFormats.get(0));

            addFormats(complexData, defaultFormat, supportedFullFormats);
        }
    }

    /**
     * Unknown input type.
     */
    private class AddTypeForInputUnknownImpl implements IAddTypeDataForInput {

        /**
         * Input parameter that is unknown.
         */
        private final IProcessDescriptionGeneratorInputData inputParameter;

        /**
         * Default constructor.
         * @param aInputParameter input parameter
         */
        AddTypeForInputUnknownImpl(
                final IProcessDescriptionGeneratorInputData aInputParameter
        ) {
            this.inputParameter = aInputParameter;
        }

        /**
         *
         * @param inputDescriptionType xml with the data for the input
         */
        @Override
        public void addTypeData(
                final InputDescriptionType inputDescriptionType) {
            LOGGER.debug("Don't know how to add the the type data for "
                    + inputParameter.getBindingClass());
        }
    }

    /**
     * Dispatch for the input parameter type to add the format.
     * @param inputParameter input parameter to add
     * @return IAddTypeDataForInput
     */
    private IAddTypeDataForInput dispatchAddInputType(
            final IProcessDescriptionGeneratorInputData inputParameter) {
        final Class<? extends IData> inputDataTypeClass =
                inputParameter.getBindingClass();
        final List<Class<?>> interfaces = findInterfaces(inputDataTypeClass);

        if (interfaces.stream().anyMatch(ILiteralData.class::equals)) {
            return new AddTypeForInputLiteralImpl(inputParameter);
        } else if (interfaces.stream().anyMatch(IBBOXData.class::equals)) {
            return new AddTypeForInputBBoxImpl(inputParameter);
        } else if (interfaces.stream().anyMatch(IComplexData.class::equals)) {
            return new AddTypeForInputComplexImpl(inputParameter);
        } else {
            return new AddTypeForInputUnknownImpl(inputParameter);
        }
    }

    /**
     * Dispatch for the output parameter type to add the format.
     * @param outputParameter output parameter to add
     * @return IAddTypeDataForOutput
     */
    private IAddTypeDataForOutput dispatchAddOutputType(
            final IProcessDescriptionGeneratorOutputData outputParameter) {
        final Class<?> outputDataTypeClass = outputParameter.getBindingClass();
        final List<Class<?>> interfaces = findInterfaces(outputDataTypeClass);

        if (interfaces.stream().anyMatch(ILiteralData.class::equals)) {
            return new AddTypeForOutputLiteralImpl(outputParameter);
        } else if (interfaces.stream().anyMatch(IBBOXData.class::equals)) {
            return new AddTypeForOutputBBoxImpl(outputParameter);
        } else if (interfaces.stream().anyMatch(IComplexData.class::equals)) {
            return new AddTypeForOutputComplexImpl(outputParameter);
        } else {
            return new AddTypeForOutputUnknownImpl(outputParameter);
        }
    }

    /**
     * Adds the output as literal value.
     */
    private class AddTypeForOutputLiteralImpl implements IAddTypeDataForOutput {

        /**
         * Output parameter to add as a literal value.
         */
        private final IProcessDescriptionGeneratorOutputData outputParameter;

        /**
         * Default constructor.
         * @param aOutputParameter output parameter to add as literal value
         */
        AddTypeForOutputLiteralImpl(
                final IProcessDescriptionGeneratorOutputData aOutputParameter
        ) {
            this.outputParameter = aOutputParameter;
        }

        /**
         *
         * @param outputDescriptionType xml with the data for the output
         */
        @Override
        public void addTypeData(
                final OutputDescriptionType outputDescriptionType) {
            final LiteralOutputType literalOutputType =
                    outputDescriptionType.addNewLiteralOutput();
            final Constructor<?>[] constructors =
                    outputParameter.getBindingClass().getConstructors();

            final Optional<String> outputClassType =
                    findSimpleNameOfFirstConstructorParameter(constructors);
            outputClassType.ifPresent(
                    classType -> literalOutputType.addNewDataType()
                            .setReference("xs:" + classType.toLowerCase()));
        }
    }


    /**
     * Adds an output parameter as bbox.
     */
    private class AddTypeForOutputBBoxImpl implements IAddTypeDataForOutput {

        /**
         * Output parameter as bounding box.
         */
        private final IProcessDescriptionGeneratorOutputData outputParameter;

        /**
         * Default constructor.
         * @param aOutputParameter output parameter as bounding box
         */
        AddTypeForOutputBBoxImpl(
                final IProcessDescriptionGeneratorOutputData aOutputParameter
        ) {
            this.outputParameter = aOutputParameter;
        }
        @Override
        public void addTypeData(
                final OutputDescriptionType outputDescriptionType) {
            final SupportedCRSsType bboxData =
                    outputDescriptionType.addNewBoundingBoxOutput();
            final Optional<List<String>> optionalSupportedCrsList =
                    outputParameter.getSupportedCrs();
            boolean isFirst = true;
            if (optionalSupportedCrsList.isPresent()) {
                for (final String supportedCrs
                        : optionalSupportedCrsList.get()) {
                    if (isFirst) {
                        final SupportedCRSsType.Default defaultCRS =
                                bboxData.addNewDefault();
                        defaultCRS.setCRS(supportedCrs);
                        final CRSsType supportedCRS =
                                bboxData.addNewSupported();
                        supportedCRS.addCRS(supportedCrs);
                        isFirst = false;
                    } else {
                        bboxData.getSupported().addCRS(supportedCrs);
                    }
                }
            }

        }
    }

    /**
     * Adds an output parameter as complex output.
     */
    private class AddTypeForOutputComplexImpl implements IAddTypeDataForOutput {

        /**
         * Output parameter to add as complex output.
         */
        private final IProcessDescriptionGeneratorOutputData outputParameter;

        /**
         * Default constructor.
         * @param aOutputParameter output parameter to add as complex output
         */
        AddTypeForOutputComplexImpl(
                final IProcessDescriptionGeneratorOutputData aOutputParameter
        ) {
            this.outputParameter = aOutputParameter;
        }

        /**
         *
         * @param outputDescriptionType xml with the data for the output
         */
        @Override
        public void addTypeData(
                final OutputDescriptionType outputDescriptionType) {
            final SupportedComplexDataType complexData =
                    outputDescriptionType.addNewComplexOutput();
            final List<IGenerator> generators = generatorSupplier.get();
            final List<IGenerator> foundGenerators =
                    findGenerators(
                            generators, outputParameter.getBindingClass());

            final List<FormatEntry> supportedFullFormats =
                    extractFormatsFromGenerators(foundGenerators);

            final FormatEntry defaultFormat =
                    outputParameter.getDefaultFormat()
                            .orElse(supportedFullFormats.get(0));

            addFormats(complexData, defaultFormat, supportedFullFormats);
        }
    }

    /**
     * Output parameter of unknown type.
     */
    private static class AddTypeForOutputUnknownImpl
            implements IAddTypeDataForOutput {

        /**
         * Output parameter.
         */
        private final IProcessDescriptionGeneratorOutputData outputParameter;

        /**
         * Default constructor.
         * @param aOutputParameter output parameter of unknown type
         */
        AddTypeForOutputUnknownImpl(
                final IProcessDescriptionGeneratorOutputData aOutputParameter
        ) {
            this.outputParameter = aOutputParameter;
        }

        @Override
        public void addTypeData(
                final OutputDescriptionType outputDescriptionType) {
            LOGGER.debug("Don't know how to add the the type data for "
                    + outputParameter.getBindingClass());
        }
    }
}
