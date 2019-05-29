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
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ConfigurationImpl implements IConfiguration {

    private final String identifier;
    private final String imageId;
    private final String workingDirectory;
    private final List<String> commandToExecute;
    private final List<String> defaultCommandLineFlags;
    private final List<IIdentifierWithBinding> inputIdentifier;
    private final List<IIdentifierWithBinding> outputIdentifier;
    private final IStderrHandler stderrHandler;
    private final IExitValueHandler exitValueHandler;
    private final IStdoutHandler stdoutHandler;

    private ConfigurationImpl(
            final String identifier,
            final String imageId,
            final String workingDirectory,
            final List<String> commandToExecute,
            final List<String> defaultCommandLineFlags,
            final List<IIdentifierWithBinding> inputIdentifier,
            final List<IIdentifierWithBinding> outputIdentifier,
            final IStderrHandler stderrHandler,
            final IExitValueHandler exitValueHandler,
            final IStdoutHandler stdoutHandler) {
        this.identifier = identifier;
        this.imageId = imageId;
        this.workingDirectory = workingDirectory;
        this.commandToExecute = commandToExecute;
        this.defaultCommandLineFlags = defaultCommandLineFlags;
        this.inputIdentifier = inputIdentifier;
        this.outputIdentifier = outputIdentifier;
        this.stderrHandler = stderrHandler;
        this.exitValueHandler = exitValueHandler;
        this.stdoutHandler = stdoutHandler;
    }

    @Override
    public String getIdentifier() {
        return identifier;
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
    public List<IIdentifierWithBinding> getInputIdentifiers() {
        return inputIdentifier;
    }

    @Override
    public List<IIdentifierWithBinding> getOutputIdentifiers() {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfigurationImpl that = (ConfigurationImpl) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(imageId, that.imageId) &&
                Objects.equals(workingDirectory, that.workingDirectory) &&
                Objects.equals(commandToExecute, that.commandToExecute) &&
                Objects.equals(defaultCommandLineFlags, that.defaultCommandLineFlags) &&
                Objects.equals(inputIdentifier, that.inputIdentifier) &&
                Objects.equals(outputIdentifier, that.outputIdentifier) &&
                Objects.equals(stderrHandler, that.stderrHandler) &&
                Objects.equals(exitValueHandler, that.exitValueHandler) &&
                Objects.equals(stdoutHandler, that.stdoutHandler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, imageId, workingDirectory, commandToExecute, defaultCommandLineFlags, inputIdentifier, outputIdentifier, stderrHandler, exitValueHandler, stdoutHandler);
    }

    /**
     * The builder class helps to build an identifier
     */
    public static class Builder {
        private final String identifier;
        private final String imageId;
        private final String workingDirectory;
        private final List<String> commandToExecute;

        private final List<String> defaultCommandLineFlags;
        private final List<IIdentifierWithBinding> inputIdentifier;
        private final List<IIdentifierWithBinding> outputIdentifier;

        private IStderrHandler stderrHandler;
        private IExitValueHandler exitValueHandler;
        private IStdoutHandler stdoutHandler;


        /**
         * Constructor
         * @param identifier identifier of the process
         * @param imageId image id for docker
         * @param workingDirectory working directory to run the process in (inside docker)
         * @param commandToExecute command to execute (inside docker)
         */
        public Builder(final String identifier, final String imageId, final String workingDirectory, final List<String> commandToExecute) {
            this.identifier = identifier;
            this.imageId = imageId;
            this.workingDirectory = workingDirectory;
            this.commandToExecute = commandToExecute;

            this.defaultCommandLineFlags = new ArrayList<>();
            this.inputIdentifier = new ArrayList<>();
            this.outputIdentifier = new ArrayList<>();
        }

        /**
         * Set additional command line flags
         * @param defaultCommandLineFlags flags that should always be included on running the command line program
         * @return builder instance
         */
        public Builder withAddedDefaultCommandLineFlags(final List<String> defaultCommandLineFlags) {
            this.defaultCommandLineFlags.addAll(defaultCommandLineFlags);
            return this;
        }

        /**
         * Set the input identifiers
         * @param inputIdentifiers identifiers for input
         * @return builder instance
         */
        public Builder withAddedInputIdentifiers(final List<IIdentifierWithBinding> inputIdentifiers) {
            this.inputIdentifier.addAll(inputIdentifiers);
            return this;
        }

        /**
         * Adds some input identifiers
         * @param inputIdentifier identifiers for input to add
         * @return builder instance
         */
        public Builder withAddedInputIdentifier(final IIdentifierWithBinding inputIdentifier) {
            this.inputIdentifier.add(inputIdentifier);
            return this;
        }

        /**
         * Set the output identifiers
         * @param outputIdentifiers identifiers for output
         * @return builder instance
         */
        public Builder withAddedOutputIdentifiers(final List<IIdentifierWithBinding> outputIdentifiers) {
            this.outputIdentifier.addAll(outputIdentifiers);
            return this;
        }

        /**
         * Adds some output identifiers
         * @param outputIdentifier identifiers for output to add
         * @return builder instance
         */
        public Builder withAddedOutputIdentifier(final IIdentifierWithBinding outputIdentifier) {
            this.outputIdentifier.add(outputIdentifier);
            return this;
        }

        /**
         * Sets the stderr handler
         * @param stderrHandler handler for standard error stream
         * @return builder instance
         */
        public Builder withStderrHandler(final IStderrHandler stderrHandler) {
            this.stderrHandler = stderrHandler;
            return this;
        }

        /**
         * Sets the stdout handler
         * @param stdoutHandler handler for standard output stream
         * @return builder instance
         */
        public Builder withStdoutHandler(final IStdoutHandler stdoutHandler) {
            this.stdoutHandler = stdoutHandler;
            return this;
        }

        /**
         * Sets the exit value handler
         * @param exitValueHandler handler for exit value
         * @return builder instance
         */
        public Builder withExitValueHandler(final IExitValueHandler exitValueHandler) {
            this.exitValueHandler = exitValueHandler;
            return this;
        }

        /**
         * Creates the new configuration
         * @return IConfiguration created by the builder
         */
        public IConfiguration build() {
            return new ConfigurationImpl(
                    identifier,
                    imageId,
                    workingDirectory,
                    commandToExecute,
                    defaultCommandLineFlags,
                    inputIdentifier,
                    outputIdentifier,
                    stderrHandler,
                    exitValueHandler,
                    stdoutHandler);
        }
    }
}
