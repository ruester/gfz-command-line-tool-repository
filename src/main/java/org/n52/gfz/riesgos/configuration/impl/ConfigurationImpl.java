package org.n52.gfz.riesgos.configuration.impl;

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

import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation for the IConfiguration interface.
 */
public final class ConfigurationImpl implements IConfiguration {

    /**
     * String with the identifier.
     */
    private final String identifier;
    /**
     * String with the abstract of the process.
     */
    private final String optionalAbstract;
    /**
     * Stirng with the image if (either a tag or the real image id,
     * just something that docker create can handle).
     */
    private final String imageId;
    /**
     * The working directory (in the docker image) where
     * the executable will run and all the file paths are
     * relative to this directory).
     */
    private final String workingDirectory;
    /**
     * This is the command to execute (as a list).
     * For example:
     * ["python3", "examplescript.py"]
     */
    private final List<String> commandToExecute;

    /**
     * This is a list of default command line flags that should
     * always be included.
     *
     * So out of
     * ["python3", "examplescript.py", "cmdarg1", "cmdarg2"]
     *
     * it makes this with a list of ["-v"]:
     *
     * ["python3", "examplescript.py", "-v", "cmdarg1", "cmdarg2"]
     */
    private final List<String> defaultCommandLineFlags;
    /**
     * List with all the input identifiers.
     */
    private final List<IInputParameter> inputIdentifier;
    /**
     * List with all the output identifiers.
     */
    private final List<IOutputParameter> outputIdentifier;
    /**
     * Handler for the stderr text.
     * This can be specific for python for example (so that tracebacks
     * are handled, but no warnings).
     */
    private final IStderrHandler stderrHandler;
    /**
     * Handler for the exit value.
     */
    private final IExitValueHandler exitValueHandler;
    /**
     * Handler for the stdout text.
     */
    private final IStdoutHandler stdoutHandler;

    /**
     * Private constructor.
     * Use the builder.build instead.
     * @param builder builder with all the data
     */
    private ConfigurationImpl(
            final Builder builder) {
        this.identifier = builder.identifier;
        this.optionalAbstract = builder.optionalAbstract;
        this.imageId = builder.imageId;
        this.workingDirectory = builder.workingDirectory;
        this.commandToExecute = builder.commandToExecute;
        this.defaultCommandLineFlags = builder.defaultCommandLineFlags;
        this.inputIdentifier = builder.inputIdentifier;
        this.outputIdentifier = builder.outputIdentifier;
        this.stderrHandler = builder.stderrHandler;
        this.exitValueHandler = builder.exitValueHandler;
        this.stdoutHandler = builder.stdoutHandler;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Optional<String> getAbstract() {
        return Optional.ofNullable(optionalAbstract);
    }

    @Override
    public String getImageId() {
        return imageId;
    }

    @Override
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public List<String> getCommandToExecute() {
        return commandToExecute;
    }

    @Override
    public List<String> getDefaultCommandLineFlags() {
        return defaultCommandLineFlags;
    }

    @Override
    public List<IInputParameter> getInputIdentifiers() {
        return inputIdentifier;
    }

    @Override
    public List<IOutputParameter> getOutputIdentifiers() {
        return outputIdentifier;
    }

    @Override
    public Optional<IStderrHandler> getStderrHandler() {
        return Optional.ofNullable(stderrHandler);
    }

    @Override
    public Optional<IExitValueHandler> getExitValueHandler() {
        return Optional.ofNullable(exitValueHandler);
    }

    @Override
    public Optional<IStdoutHandler> getStdoutHandler() {
        return Optional.ofNullable(stdoutHandler);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfigurationImpl that = (ConfigurationImpl) o;
        return Objects.equals(identifier, that.identifier)
                && Objects.equals(imageId, that.imageId)
                && Objects.equals(workingDirectory, that.workingDirectory)
                && Objects.equals(commandToExecute, that.commandToExecute)
                && Objects.equals(defaultCommandLineFlags,
                that.defaultCommandLineFlags)
                && Objects.equals(inputIdentifier, that.inputIdentifier)
                && Objects.equals(outputIdentifier, that.outputIdentifier)
                && Objects.equals(stderrHandler, that.stderrHandler)
                && Objects.equals(exitValueHandler, that.exitValueHandler)
                && Objects.equals(stdoutHandler, that.stdoutHandler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, imageId, workingDirectory,
                commandToExecute, defaultCommandLineFlags, inputIdentifier,
                outputIdentifier, stderrHandler, exitValueHandler,
                stdoutHandler);
    }

    /**
     * The builder class helps to build an identifier.
     */
    public static class Builder {
        /**
         * Identifier of the process.
         */
        private final String identifier;
        /**
         * Abstract of the process.
         */
        private final String optionalAbstract;
        /**
         * ID / tag of the docker image.
         */
        private final String imageId;
        /**
         * Working directory for the executable.
         */
        private final String workingDirectory;
        /**
         * The command to execute (in the docker image).
         */
        private final List<String> commandToExecute;

        /**
         * List with default command line flags.
         * (To run a command always in verbose mode for example).
         */
        private final List<String> defaultCommandLineFlags;
        /**
         * List with the input identifiers.
         */
        private final List<IInputParameter> inputIdentifier;
        /**
         * List with the output identifiers.
         */
        private final List<IOutputParameter> outputIdentifier;

        /**
         * Handler for the stderr text.
         */
        private IStderrHandler stderrHandler;
        /**
         * Handler for the exit value.
         */
        private IExitValueHandler exitValueHandler;
        /**
         * Handler for the stdout text.
         */
        private IStdoutHandler stdoutHandler;


        /**
         * Constructor.
         * @param aIdentifier identifier of the process
         * @param aOptionalAbstract optional text with the abstract
         *                         of the process
         * @param aImageId image id for docker
         * @param aWorkingDirectory working directory to run the process in
         *                         (inside docker)
         * @param aCommandToExecute command to execute (inside docker)
         */
        public Builder(final String aIdentifier,
                       final String aOptionalAbstract,
                       final String aImageId,
                       final String aWorkingDirectory,
                       final List<String> aCommandToExecute) {
            this.identifier = aIdentifier;
            this.optionalAbstract = aOptionalAbstract;
            this.imageId = aImageId;
            this.workingDirectory = aWorkingDirectory;
            this.commandToExecute = aCommandToExecute;

            this.defaultCommandLineFlags = new ArrayList<>();
            this.inputIdentifier = new ArrayList<>();
            this.outputIdentifier = new ArrayList<>();
        }

        /**
         * Set additional command line flags.
         * @param aDefaultCommandLineFlags flags that should always be included
         *                                on running the command line program
         * @return builder instance
         */
        public Builder withAddedDefaultCommandLineFlags(
                final List<String> aDefaultCommandLineFlags) {
            this.defaultCommandLineFlags.addAll(aDefaultCommandLineFlags);
            return this;
        }

        /**
         * Set the input identifiers.
         * @param inputIdentifiers identifiers for input
         * @return builder instance
         */
        public Builder withAddedInputIdentifiers(
                final List<IInputParameter> inputIdentifiers) {
            this.inputIdentifier.addAll(inputIdentifiers);
            return this;
        }

        /**
         * Adds some input identifiers.
         * @param aInputIdentifier identifiers for input to add
         * @return builder instance
         */
        public Builder withAddedInputIdentifier(
                final IInputParameter aInputIdentifier) {
            this.inputIdentifier.add(aInputIdentifier);
            return this;
        }

        /**
         * Set the output identifiers.
         * @param outputIdentifiers identifiers for output
         * @return builder instance
         */
        public Builder withAddedOutputIdentifiers(
                final List<IOutputParameter> outputIdentifiers) {
            this.outputIdentifier.addAll(outputIdentifiers);
            return this;
        }

        /**
         * Adds some output identifiers.
         * @param aOutputIdentifier identifiers for output to add
         * @return builder instance
         */
        public Builder withAddedOutputIdentifier(
                final IOutputParameter aOutputIdentifier) {
            this.outputIdentifier.add(aOutputIdentifier);
            return this;
        }

        /**
         * Sets the stderr handler.
         * @param aStderrHandler handler for standard error stream
         * @return builder instance
         */
        public Builder withStderrHandler(
                final IStderrHandler aStderrHandler) {
            this.stderrHandler = aStderrHandler;
            return this;
        }

        /**
         * Sets the stdout handler.
         * @param aStdoutHandler handler for standard output stream
         * @return builder instance
         */
        public Builder withStdoutHandler(
                final IStdoutHandler aStdoutHandler) {
            this.stdoutHandler = aStdoutHandler;
            return this;
        }

        /**
         * Sets the exit value handler.
         * @param aExitValueHandler handler for exit value
         * @return builder instance
         */
        public Builder withExitValueHandler(
                final IExitValueHandler aExitValueHandler) {
            this.exitValueHandler = aExitValueHandler;
            return this;
        }

        /**
         * Creates the new configuration.
         * @return IConfiguration created by the builder
         */
        public IConfiguration build() {
            return new ConfigurationImpl(this);
        }
    }
}
