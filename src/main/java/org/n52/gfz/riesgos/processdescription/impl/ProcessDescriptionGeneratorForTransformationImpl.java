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
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import net.opengis.wps.x100.SupportedComplexDataInputType;
import net.opengis.wps.x100.SupportedComplexDataType;
import org.n52.wps.io.GeneratorFactory;
import org.n52.wps.io.IGenerator;
import org.n52.wps.io.IParser;
import org.n52.wps.io.ParserFactory;
import org.n52.wps.io.data.IComplexData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Supplier;

/**
 * Implementation of the process description generation
 * for those processes that just takes one complexdata input and transforms it into another format
 * (using parsers and generators only).
 */
public class ProcessDescriptionGeneratorForTransformationImpl extends AbstractProcessDescriptionGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDescriptionGeneratorForTransformationImpl.class);

    private final String identifier;
    private final String fullQualifiedIdentifier;
    private final Class<? extends IComplexData> bindingClass;
    private final String optionalAbstract;
    private final String inputIdentifier;
    private final String optionalInputAbstract;
    private final String outputIdentifier;
    private final String optionalOutputAbstract;

    private final Supplier<List<IParser>> parserSupplier;
    private final Supplier<List<IGenerator>> generatorSupplier;

    /**
     * Constructor (for testing purpose)
     * @param identifier the identifier (title) of the process
     * @param fullQualifiedIdentifier the full qualified identifier of the data (to simulate a package path)
     * @param bindingClass the class to use for the binding
     * @param optionalAbstract optional description of the process
     * @param inputIdentifier identifier for the input value
     * @param optionalInputAbstract optional description for the input parameter
     * @param outputIdentifier identifier for the output value
     * @param optionalOutputAbstract optional description for the output parameter
     * @param parserSupplier supplier for getting all the parsers
     * @param generatorSupplier supplier for getting all the generators
     */
    public ProcessDescriptionGeneratorForTransformationImpl(
            final String identifier,
            final String fullQualifiedIdentifier,
            final Class<? extends IComplexData> bindingClass,
            final String optionalAbstract,
            final String inputIdentifier,
            final String optionalInputAbstract,
            final String outputIdentifier,
            final String optionalOutputAbstract,
            final Supplier<List<IParser>> parserSupplier,
            final Supplier<List<IGenerator>> generatorSupplier) {
        this.identifier = identifier;
        this.fullQualifiedIdentifier = fullQualifiedIdentifier;
        this.bindingClass = bindingClass;
        this.optionalAbstract = optionalAbstract;
        this.inputIdentifier = inputIdentifier;
        this.optionalInputAbstract = optionalInputAbstract;
        this.outputIdentifier = outputIdentifier;
        this.optionalOutputAbstract = optionalOutputAbstract;

        this.parserSupplier = parserSupplier;
        this.generatorSupplier = generatorSupplier;
    }

    /**
     *
     * @param identifier the identifier (title) of the process
     * @param fullQualifiedIdentifier the full qualified identifier of the data (to simulate a package path)
     * @param bindingClass the class to use for the binding
     * @param optionalAbstract optional description of the process
     * @param inputIdentifier identifier for the input value
     * @param optionalInputIdentifier optional description for the input parameter
     * @param outputIdentifier identifier for the output value
     * @param optionalOutputIdentifier optional description for the output parameter
     */
    public ProcessDescriptionGeneratorForTransformationImpl(
            final String identifier,
            final String fullQualifiedIdentifier,
            final Class<? extends IComplexData> bindingClass,
            final String optionalAbstract,
            final String inputIdentifier,
            final String optionalInputIdentifier,
            final String outputIdentifier,
            final String optionalOutputIdentifier) {
        this(identifier, fullQualifiedIdentifier, bindingClass,
                optionalAbstract,
                inputIdentifier, optionalInputIdentifier,
                outputIdentifier, optionalOutputIdentifier,
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

        if(optionalAbstract != null) {
            final LanguageStringType abstractType = processDescriptionType.addNewAbstract();
            abstractType.setStringValue(optionalAbstract);
        }

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

        if(optionalInputAbstract != null) {
            final LanguageStringType inputAbstract = inputDescriptionType.addNewAbstract();
            inputAbstract.setStringValue(optionalInputAbstract);
        }

        final SupportedComplexDataInputType complexDataInputData = inputDescriptionType.addNewComplexData();
        final List<IParser> parsers = parserSupplier.get();
        final List<IParser> foundParsers = findParser(parsers, bindingClass);
        addInputFormats(complexDataInputData, foundParsers);


        final ProcessDescriptionType.ProcessOutputs processOutputs = processDescriptionType.addNewProcessOutputs();
        final OutputDescriptionType outputDescriptionType = processOutputs.addNewOutput();

        final CodeType outputIdentifier = outputDescriptionType.addNewIdentifier();
        outputIdentifier.setStringValue(this.outputIdentifier);

        final LanguageStringType outputTitle = outputDescriptionType.addNewTitle();
        outputTitle.setStringValue(this.outputIdentifier);

        if(optionalOutputAbstract != null) {
            final LanguageStringType outputAbstract = outputDescriptionType.addNewAbstract();
            outputAbstract.setStringValue(optionalOutputAbstract);
        }

        final SupportedComplexDataType complexDataOutputData = outputDescriptionType.addNewComplexOutput();
        final List<IGenerator> generators = generatorSupplier.get();
        final List<IGenerator> foundGenerators = findGenerators(generators, bindingClass);
        addOutputFormats(complexDataOutputData, foundGenerators);

        return result;
    }
}
