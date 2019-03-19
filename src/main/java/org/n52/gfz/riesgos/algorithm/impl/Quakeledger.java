package org.n52.gfz.riesgos.algorithm.impl;

import org.n52.gfz.riesgos.algorithm.AbstractGfzRiesgosProcess;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGenericXMLDataBinding;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralDoubleBindingToStringCmd;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.impl.IdentifierWithBindingImpl;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.validators.LiteralStringBindingWithAllowedValues;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Quakeledger is one implementation of a abstract riesgos service.
 * It is used to query a python script to provide a list of
 * earth quake events using quakeml
 *
 */
public class Quakeledger extends AbstractGfzRiesgosProcess {

    /**
     * Default constructor
     */
    public Quakeledger() {
        super(createQuakeledgerConfig(), LoggerFactory.getLogger(Quakeledger.class));
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
                            Optional.of(new LiteralDoubleBindingToStringCmd()),
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
                            Optional.of(new LiteralDoubleBindingToStringCmd()),
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
                            Optional.of(new LiteralDoubleBindingToStringCmd()),
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
                            Optional.of(new LiteralDoubleBindingToStringCmd()),
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
                            Optional.of(new LiteralDoubleBindingToStringCmd()),
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
                            Optional.of(new LiteralDoubleBindingToStringCmd()),
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
                            Optional.of(new LiteralDoubleBindingToStringCmd()),
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
                            Optional.of(new LiteralDoubleBindingToStringCmd()),
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
                            Optional.of(new LiteralDoubleBindingToStringCmd()),
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
                            Optional.of(new LiteralStringBindingWithAllowedValues("observed", "deaggregated", "stochastic", "expert")),
                            Optional.of(new LiteralDoubleBindingToStringCmd()),
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
                            Optional.of(new LiteralDoubleBindingToStringCmd()),
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
                            Optional.of(new LiteralDoubleBindingToStringCmd()),
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
                            Optional.of(new ConvertBytesToGenericXMLDataBinding()))
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
}
