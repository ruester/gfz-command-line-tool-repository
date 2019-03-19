package org.n52.gfz.riesgos.algorithm.impl;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.algorithm.AbstractGfzRiesgosProcess;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.impl.IdentifierWithBindingImpl;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class Quakeledger extends AbstractGfzRiesgosProcess {

    public Quakeledger() {
        super(createQuakeledgerConfig());
    }

    private static IConfiguration createQuakeledgerConfig() {
        return new QuakeledgerConfig();
    }

    private static class QuakeledgerConfig implements IConfiguration {
        @Override
        public List<IIdentifierWithBinding> getInputIdentifiers() {
            return Arrays.asList(
                    new IdentifierWithBindingImpl("lonmin",
                            LiteralDoubleBinding.class,
                            Optional.empty(),
                            Optional.of(new DoubleBindingToString(Optional.empty())),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()),
                    new IdentifierWithBindingImpl(
                            "lonmax",
                            LiteralDoubleBinding.class,
                            Optional.empty(),
                            Optional.of(new DoubleBindingToString(Optional.empty())),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()),
                    new IdentifierWithBindingImpl(
                            "latmin",
                            LiteralDoubleBinding.class,
                            Optional.empty(),
                            Optional.of(new DoubleBindingToString(Optional.empty())),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()),
                    new IdentifierWithBindingImpl(
                            "latmax",
                            LiteralDoubleBinding.class,
                            Optional.empty(),
                            Optional.of(new DoubleBindingToString(Optional.empty())),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()),
                    new IdentifierWithBindingImpl("mmin",
                            LiteralDoubleBinding.class,
                            Optional.empty(),
                            Optional.of(new DoubleBindingToString(Optional.empty())),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()),
                    new IdentifierWithBindingImpl(
                            "mmax",
                            LiteralDoubleBinding.class,
                            Optional.empty(),
                            Optional.of(new DoubleBindingToString(Optional.empty())),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()),
                    new IdentifierWithBindingImpl(
                            "zmin",
                            LiteralDoubleBinding.class,
                            Optional.empty(),
                            Optional.of(new DoubleBindingToString(Optional.empty())),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()),
                    new IdentifierWithBindingImpl(
                            "zmax",
                            LiteralDoubleBinding.class,
                            Optional.empty(),
                            Optional.of(new DoubleBindingToString(Optional.empty())),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()),
                    new IdentifierWithBindingImpl(
                            "p",
                            LiteralDoubleBinding.class,
                            Optional.empty(),
                            Optional.of(new DoubleBindingToString(Optional.empty())),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()),
                    new IdentifierWithBindingImpl(
                            "etype",
                            LiteralStringBinding.class,
                            Optional.of(new IsStringAndInList("observed", "deaggregated", "stochastic", "expert")),
                            Optional.of(new StringBindingToString(Optional.empty())),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()),
                    new IdentifierWithBindingImpl(
                            "tlon",
                            LiteralDoubleBinding.class,
                            Optional.empty(),
                            Optional.of(new DoubleBindingToString(Optional.empty())),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()),
                    new IdentifierWithBindingImpl(
                            "tlat",
                            LiteralDoubleBinding.class,
                            Optional.empty(),
                            Optional.of(new DoubleBindingToString(Optional.empty())),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty())
            );
        }

        @Override
        public List<IIdentifierWithBinding> getOutputIdentifiers() {
            return Arrays.asList(
                    new IdentifierWithBindingImpl(
                            "selectedRows",
                            GenericXMLDataBinding.class,
                            Optional.empty(),
                            Optional.empty(),
                            Optional.of("test.xml"),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.of(new ConvertBytesToXmlBinding()))
            );
        }

        @Override
        public String getImageId() {
            return "sha256:71b93ade61bf41da8d68419bec12ec1e274eae28b36bc64cc156e1be33294821";
        }

        @Override
        public String getWorkingDirectory() {
            return "/usr/share/git/quakeledger";
        }

        @Override
        public List<String> getCommandToExecute() {
            return Arrays.asList("python3", "eventquery.py");
        }

        @Override
        public List<String> getDefaultCommandLineFlags() {
            return Collections.emptyList();
        }

        @Override
        public Optional<IStderrHandler> getStderrHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<IExitValueHandler> getExitValueHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<IStdoutHandler> getStdoutHandler() {
            return Optional.empty();
        }
    }

    private static class IsStringAndInList implements ICheckDataAndGetErrorMessage {

        private final Set<String> allowedValues;

        public IsStringAndInList(final String... values) {
            allowedValues = new HashSet<>();
            Stream.of(values).forEach(allowedValues::add);
        }

        @Override
        public Optional<String> check(final IData iData) {
            final Optional<String> error;
            if(iData instanceof LiteralStringBinding) {
                final LiteralStringBinding wrappedStr = (LiteralStringBinding) iData;
                final String str = wrappedStr.getPayload();
                if(allowedValues.contains(str)) {
                    error = Optional.empty();
                } else {
                    error = Optional.of("Input is non of the allowed values");
                }
            } else {
                error = Optional.of("Unexpected input type");
            }

            return error;
        }
    }

    private static class DoubleBindingToString implements IConvertIDataToCommandLineParameter {

        private final Optional<String> defaultFlag;
        public DoubleBindingToString(final Optional<String> defaultFlag) {
            this.defaultFlag = defaultFlag;
        }

        @Override
        public List<String> convertToCommandLineParameter(final IData iData) {
            final List<String> result = new ArrayList<>();

            defaultFlag.ifPresent(result::add);

            if(iData instanceof LiteralDoubleBinding) {
                final LiteralDoubleBinding binding = (LiteralDoubleBinding) iData;
                final double value = binding.getPayload();
                result.add(String.valueOf(value));
            }
            return result;
        }
    }

    private static class StringBindingToString implements IConvertIDataToCommandLineParameter {
        private final Optional<String> defaultFlag;
        public StringBindingToString(final Optional<String> defaultFlag) {
            this.defaultFlag = defaultFlag;
        }

        @Override
        public List<String> convertToCommandLineParameter(final IData iData) {
            final List<String> result = new ArrayList<>();

            defaultFlag.ifPresent(result::add);

            if(iData instanceof LiteralStringBinding) {
                final LiteralStringBinding binding = (LiteralStringBinding) iData;
                final String value = binding.getPayload();
                result.add(value);
            }
            return result;
        }
    }

    private static class ConvertBytesToXmlBinding implements IConvertByteArrayToIData {
        @Override
        public IData convertToIData(final byte[] content) throws ConvertToIDataException {
            try(final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content)) {
                return new GenericXMLDataBinding(XmlObject.Factory.parse(byteArrayInputStream));
            } catch(final XmlException xmlException) {
                throw new ConvertToIDataException(xmlException);
            } catch(final IOException ioException) {
                throw new ConvertToIDataException(ioException);
            }
        }
    }
}
