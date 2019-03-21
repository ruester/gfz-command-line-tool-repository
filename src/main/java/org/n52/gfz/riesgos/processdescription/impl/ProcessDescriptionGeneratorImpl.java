package org.n52.gfz.riesgos.processdescription.impl;

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
import net.opengis.wps.x100.ValuesReferenceType;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.functioninterfaces.IAddTypeIntoInputDescriptionType;
import org.n52.gfz.riesgos.functioninterfaces.IAddTypeIntoOutputDescriptionType;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessDescriptionGeneratorImpl implements IProcessDescriptionGenerator {

    private final Supplier<List<IParser>> parserSupplier;
    private final Supplier<List<IGenerator>> generatorSupplier;


    public ProcessDescriptionGeneratorImpl(final Supplier<List<IParser>> parserSupplier, final Supplier<List<IGenerator>> generatorSupplier) {
        this.parserSupplier = parserSupplier;
        this.generatorSupplier = generatorSupplier;
    }

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

                final Optional<Class<?>> optionalLiteralDataClass = interfaces.stream().filter(ILiteralData.class::equals).findFirst();
                final Optional<Class<?>> optionalBboxDataClass = interfaces.stream().filter(IBBOXData.class::equals).findFirst();
                final Optional<Class<?>> optionalComplexDataClass = interfaces.stream().filter(IComplexData.class::equals).findFirst();

                if(optionalLiteralDataClass.isPresent()) {
                    final LiteralInputType literalData = inputDescriptionType.addNewLiteralData();
                    final Constructor<?>[] constructors = inputDataTypeClass.getConstructors();

                    String inputClassType = "";
                    for(final Constructor<?> constructor : constructors) {
                        final Class<?>[] supportedClasses = constructor.getParameterTypes();
                        if(supportedClasses.length == 1) {
                            inputClassType = supportedClasses[0].getSimpleName();
                        }
                    }

                    if(inputClassType.length() > 0) {
                        final DomainMetadataType datatype = literalData.addNewDataType();
                        datatype.setReference("xs:" + inputClassType.toLowerCase());

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
                        if(optionalDefaultValue.isPresent()) {
                            literalData.setDefaultValue(optionalDefaultValue.get());
                        }
                    }
                } else if(optionalBboxDataClass.isPresent()) {
                    final SupportedCRSsType bboxData = inputDescriptionType.addNewBoundingBoxData();
                    final Optional<List<String>> optionalSupportedCrsList = input.getSupportedCRSForBBox();
                    boolean isFirst = true;
                    for(final String supportedCrs : optionalSupportedCrsList.get()) {
                        if(isFirst) {
                            final SupportedCRSsType.Default defaultCRS = bboxData.addNewDefault();
                            defaultCRS.setCRS(supportedCrs);
                            final CRSsType supportedCRS = bboxData.addNewSupported();
                            supportedCRS.addCRS(supportedCrs);
                            isFirst = false;
                        } else {
                            bboxData.getSupported().addCRS(supportedCrs);
                        }
                    }
                } else if(optionalComplexDataClass.isPresent()) {
                    final SupportedComplexDataInputType complexData = inputDescriptionType.addNewComplexData();
                    final List<IParser> parsers = parserSupplier.get();
                    final List<IParser> foundParsers = findParser(parsers, inputDataTypeClass);
                    addInputFormats(complexData, foundParsers, input.getSchema());
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

            final Optional<Class<?>> optionalLiteralDataClass = interfaces.stream().filter(ILiteralData.class::equals).findFirst();
            final Optional<Class<?>> optionalBboxDataClass = interfaces.stream().filter(IBBOXData.class::equals).findFirst();
            final Optional<Class<?>> optionalComplexDataClass = interfaces.stream().filter(IComplexData.class::equals).findFirst();

            if(optionalLiteralDataClass.isPresent()) {
                final LiteralOutputType literalOutputType = outputDescriptionType.addNewLiteralOutput();

                final Constructor<?>[] constructors = outputDataTypeClass.getConstructors();

                String outputClassType = "";
                for(final Constructor<?> constructor : constructors) {
                    final Class<?>[] supportedClasses = constructor.getParameterTypes();
                    if(supportedClasses.length == 1) {
                        outputClassType = supportedClasses[0].getSimpleName();
                    }
                }

                if(outputClassType.length() > 0) {
                    literalOutputType.addNewDataType().setReference("xs:" + outputClassType.toLowerCase());
                }
            } else if(optionalBboxDataClass.isPresent()) {
                final SupportedCRSsType bboxData = outputDescriptionType.addNewBoundingBoxOutput();
                final Optional<List<String>> optionalSupportedCrsList = output.getSupportedCRSForBBox();
                boolean isFirst = true;
                for(final String supportedCrs : optionalSupportedCrsList.get()) {
                    if(isFirst) {
                        final SupportedCRSsType.Default defaultCRS = bboxData.addNewDefault();
                        defaultCRS.setCRS(supportedCrs);
                        final CRSsType supportedCRS = bboxData.addNewSupported();
                        supportedCRS.addCRS(supportedCrs);
                        isFirst = false;
                    } else {
                        bboxData.getSupported().addCRS(supportedCrs);
                    }
                }
            } else if(optionalComplexDataClass.isPresent()) {
                final SupportedComplexDataType complexData = outputDescriptionType.addNewComplexOutput();
                final List<IGenerator> generators = generatorSupplier.get();
                final List<IGenerator> foundGenerators = findGenerators(generators, outputDataTypeClass);
                addOutputFormats(complexData, foundGenerators, output.getSchema());
            }
        }

        /*
        ProcessDescriptionType.ProcessOutputs dataOutputs = processDescription.addNewProcessOutputs();
        List<String> outputIdentifiers = this.getOutputIdentifiers();
        Iterator var29 = outputIdentifiers.iterator();

        while(var29.hasNext()) {
            String identifier = (String)var29.next();
            OutputDescriptionType dataOutput = dataOutputs.addNewOutput();
            dataOutput.addNewIdentifier().setStringValue(identifier);
            dataOutput.addNewTitle().setStringValue(identifier);
            dataOutput.addNewAbstract().setStringValue(identifier);
            Class<?> outputDataTypeClass = this.getOutputDataType(identifier);
            Class<?>[] interfaces = outputDataTypeClass.getInterfaces();
            if (interfaces.length == 0) {
                interfaces = outputDataTypeClass.getSuperclass().getInterfaces();
            }

            Class[] var35 = interfaces;
            int var36 = interfaces.length;

            for(int var39 = 0; var39 < var36; ++var39) {
                Class<?> implementedInterface = var35[var39];
                if (implementedInterface.equals(ILiteralData.class)) {
                    LiteralOutputType literalData = dataOutput.addNewLiteralOutput();
                    String outputClassType = "";
                    Constructor<?>[] constructors = outputDataTypeClass.getConstructors();
                    Constructor[] var65 = constructors;
                    int var67 = constructors.length;

                    for(var22 = 0; var22 < var67; ++var22) {
                        Constructor<?> constructor = var65[var22];
                        Class<?>[] parameters = constructor.getParameterTypes();
                        if (parameters.length == 1) {
                            outputClassType = parameters[0].getSimpleName();
                        }
                    }

                    if (outputClassType.length() > 0) {
                        literalData.addNewDataType().setReference("xs:" + outputClassType.toLowerCase());
                    }
                } else if (implementedInterface.equals(IBBOXData.class)) {
                    SupportedCRSsType bboxData = dataOutput.addNewBoundingBoxOutput();
                    String[] supportedCRSAray = this.getSupportedCRSForBBOXOutput(identifier);

                    for(i = 0; i < supportedCRSAray.length; ++i) {
                        if (i == 0) {
                            SupportedCRSsType.Default defaultCRS = bboxData.addNewDefault();
                            defaultCRS.setCRS(supportedCRSAray[0]);
                            if (supportedCRSAray.length == 1) {
                                CRSsType supportedCRS = bboxData.addNewSupported();
                                supportedCRS.addCRS(supportedCRSAray[0]);
                            }
                        } else if (i == 1) {
                            CRSsType supportedCRS = bboxData.addNewSupported();
                            supportedCRS.addCRS(supportedCRSAray[1]);
                        } else {
                            bboxData.getSupported().addCRS(supportedCRSAray[i]);
                        }
                    }
                } else if (implementedInterface.equals(IComplexData.class)) {
                    SupportedComplexDataType complexData = dataOutput.addNewComplexOutput();
                    List<IGenerator> generators = GeneratorFactory.getInstance().getAllGenerators();
                    List<IGenerator> foundGenerators = new ArrayList();
                    Iterator var60 = generators.iterator();

                    while(var60.hasNext()) {
                        IGenerator generator = (IGenerator)var60.next();
                        supportedClasses = generator.getSupportedDataBindings();
                        Class[] var69 = supportedClasses;
                        int var71 = supportedClasses.length;

                        for(int var25 = 0; var25 < var71; ++var25) {
                            Class<?> clazz = var69[var25];
                            if (clazz.equals(outputDataTypeClass)) {
                                foundGenerators.add(generator);
                            }
                        }
                    }

                    this.addOutputFormats(complexData, foundGenerators);
                }
            }
        }
        */






        return result;
    }

    private List<Class<?>> findInterfaces(final Class<?> clazz) {
        final List<Class<?>> result = Arrays.asList(clazz.getInterfaces());
        final Class<?> superClass = clazz.getSuperclass();
        if(result.isEmpty() && superClass != null) {
            return findInterfaces(superClass);
        }
        return result;
    }

    private List<IParser> findParser(final List<IParser> allParsers, final Class<?> clazz) {
        return allParsers.stream().filter(new ParserSupportsClass(clazz)).collect(Collectors.toList());
    }

    private List<IGenerator> findGenerators(final List<IGenerator> allGenerators, final Class<?> clazz) {
        return allGenerators.stream().filter(new GeneratorSupportsClass(clazz)).collect(Collectors.toList());
    }

    private void addInputFormats(final SupportedComplexDataInputType complexData, final List<IParser> foundParsers, final Optional<String> optionalSchema) {
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
                } else if(optionalSchema.isPresent() && "text/xml".equals(format.getMimeType())) {
                    supportedFormat.setSchema(optionalSchema.get());
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
                } else if(optionalSchema.isPresent() && "text.xml".equals(format.getMimeType())) {
                    supportedFormat.setSchema(optionalSchema.get());
                }
            }
        }
    }

    private void addOutputFormats(SupportedComplexDataType complexData, List<IGenerator> foundGenerators, final Optional<String> optionalSchema) {
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
                } else if(optionalSchema.isPresent() && "text/xml".equals(format.getMimeType())) {
                    supportedFormat.setSchema(optionalSchema.get());
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
                } else if(optionalSchema.isPresent() && "text/xml".equals(format.getMimeType())) {
                    supportedFormat.setSchema(optionalSchema.get());
                }
            }
        }
    }

    private static class ParserSupportsClass implements Predicate<IParser> {
        private final Class<?> clazz;

        public ParserSupportsClass(final Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean test(final IParser parser) {
            return Stream.of(parser.getSupportedDataBindings()).anyMatch(clazz::equals);
        }
    }

    private static class GeneratorSupportsClass implements Predicate<IGenerator> {
        private final Class<?> clazz;

        public GeneratorSupportsClass(final Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean test(final IGenerator generator) {
            return Stream.of(generator.getSupportedDataBindings()).anyMatch(clazz::equals);
        }
    }
}
