package org.n52.gfz.riesgos.processdescription.impl;

import net.opengis.ows.x11.CodeType;
import net.opengis.ows.x11.DomainMetadataType;
import net.opengis.ows.x11.LanguageStringType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.LiteralInputType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionsDocument;
import net.opengis.wps.x100.ValuesReferenceType;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.functioninterfaces.IAddTypeIntoInputDescriptionType;
import org.n52.gfz.riesgos.functioninterfaces.IAddTypeIntoOutputDescriptionType;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGenerator;

import java.math.BigInteger;
import java.util.Optional;

public class ProcessDescriptionGeneratorImpl implements IProcessDescriptionGenerator {

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

        final ProcessDescriptionType.DataInputs dataInputs = processDescriptionType.addNewDataInputs();
        for(final IIdentifierWithBinding input : configuration.getInputIdentifiers()) {
            final InputDescriptionType inputDescriptionType = dataInputs.addNewInput();
            inputDescriptionType.setMinOccurs(BigInteger.ONE);
            inputDescriptionType.setMaxOccurs(BigInteger.ONE);

            final CodeType inputIdentifier = inputDescriptionType.addNewIdentifier();
            inputIdentifier.setStringValue(input.getIdentifer());

            final LanguageStringType inputTitle = inputDescriptionType.addNewTitle();
            inputTitle.setStringValue(input.getIdentifer());

            final Optional<IAddTypeIntoInputDescriptionType> optionalAddTypeIntoInputDescriptionType = input.getFunctionToAddInputDescriptionType();
            // must be there
            final IAddTypeIntoInputDescriptionType addTypeIntoOutputDescriptionType = optionalAddTypeIntoInputDescriptionType.get();
            addTypeIntoOutputDescriptionType.addType(inputDescriptionType);
        }
        final ProcessDescriptionType.ProcessOutputs processOutputs = processDescriptionType.addNewProcessOutputs();
        for(final IIdentifierWithBinding output : configuration.getOutputIdentifiers()) {
            final OutputDescriptionType outputDescriptionType = processOutputs.addNewOutput();

            final CodeType outputIdentifier = outputDescriptionType.addNewIdentifier();
            outputIdentifier.setStringValue(output.getIdentifer());

            final LanguageStringType outputTitle = outputDescriptionType.addNewTitle();
            outputTitle.setStringValue(output.getIdentifer());

            final Optional<IAddTypeIntoOutputDescriptionType> optionalAddTypeIntoOutputDescriptionType = output.getFunctionToAddOutputDescriptionType();
            // must be there
            final IAddTypeIntoOutputDescriptionType addTypeIntoOutputDescriptionType = optionalAddTypeIntoOutputDescriptionType.get();
            addTypeIntoOutputDescriptionType.addType(outputDescriptionType);
        }

        return result;
    }
}
