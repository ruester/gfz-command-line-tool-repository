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


package org.n52.gfz.riesgos.configuration.impl;

import org.n52.gfz.riesgos.cache.generateinputcachekey.GenerateCacheKeyByConvertToByteArray;
import org.n52.gfz.riesgos.cache.generateinputcachekey.GenerateCacheKeyByTransformToCmd;
import org.n52.gfz.riesgos.cache.generateinputcachekey.GenerateCacheKeyByWriteToFiles;
import org.n52.gfz.riesgos.cache.IFunctionToGenerateCacheKey;
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IWriteIDataToFiles;
import org.n52.wps.io.data.IData;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the IInputParameter interface.
 * This implementation is generic to realize problems
 * on compsing the functions on compile time.
 *
 * @param <T> type that extends IData
 */
public final class InputParameterImpl<T extends IData>
        implements IInputParameter {

    /**
     * Identifier of the data.
     */
    private final String identifier;
    /**
     * Binding class of the data (class that extends
     * the IData interface).
     */
    private final Class<T> bindingClass;
    /**
     * Variable to hold if the input is optional (true) or not.
     */
    private final boolean isOptional;
    /**
     * String to contain the abstract / description of the
     * input parameter.
     */
    private final String optionalAbstract;
    /**
     * Validator to check the data.
     */
    private final ICheckDataAndGetErrorMessage<T> validator;
    /**
     * Function to transform the data to a command line argument.
     */
    private final IConvertIDataToCommandLineParameter<T>
            functionToTransformToCmd;
    /**
     * Path to put a file into.
     */
    private final String path;
    /**
     * Function to write the data to a files.
     */
    private final IWriteIDataToFiles<T> functionToWriteToFiles;
    /**
     * Function to write the data to stdin.
     */
    private final IConvertIDataToByteArray<T> functionToWriteToStdin;
    /**
     * List with allowed values.
     */
    private final List<String> allowedValues;
    /**
     * Default value.
     */
    private final String defaultValue;
    /**
     * List with supported crs for bbox.
     */
    private final List<String> supportedCRSForBBox;
    /**
     * Schema for XML.
     */
    private final String schema;
    /**
     * Default format.
     */
    private final FormatEntry defaultFormat;

    /**
     * Default constructor. Private. Use the builder
     * to create instances.
     * @param builder builder with the data
     */
    private InputParameterImpl(final Builder<T> builder) {
        this.identifier = builder.identifier;
        this.bindingClass = builder.bindingClass;
        this.isOptional = builder.isOptional;
        this.optionalAbstract = builder.optionalAbstract;
        this.validator = builder.validator;
        this.functionToTransformToCmd = builder.functionToTransformToCmd;
        this.path = builder.path;
        this.functionToWriteToFiles = builder.functionToWriteToFiles;
        this.functionToWriteToStdin = builder.functionToWriteToStdin;
        this.allowedValues = builder.allowedValues;
        this.defaultValue = builder.defaultValue;
        this.supportedCRSForBBox = builder.supportedCRSForBBox;
        this.schema = builder.schema;
        this.defaultFormat = builder.defaultFormat;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Class<? extends IData> getBindingClass() {
        return bindingClass;
    }

    @Override
    public boolean isOptional() {
        return isOptional;
    }

    @Override
    public Optional<String> getAbstract() {
        return Optional.ofNullable(optionalAbstract);
    }

    @Override
    public Optional<ICheckDataAndGetErrorMessage> getValidator() {
        return Optional.ofNullable(validator);
    }

    @Override
    public Optional<IConvertIDataToCommandLineParameter>
    getFunctionToTransformToCmd() {
        return Optional.ofNullable(functionToTransformToCmd);
    }


    @Override
    public Optional<String> getPathToWriteToOrReadFromFile() {
        return Optional.ofNullable(path);
    }

    @Override
    public Optional<IWriteIDataToFiles> getFunctionToWriteIDataToFiles() {
        return Optional.ofNullable(functionToWriteToFiles);
    }

    @Override
    public Optional<IConvertIDataToByteArray> getFunctionToWriteToStdin() {
        return Optional.ofNullable(functionToWriteToStdin);
    }

    @Override
    public Optional<List<String>> getAllowedValues() {
        return Optional.ofNullable(allowedValues);
    }

    @Override
    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    @Override
    public Optional<List<String>> getSupportedCRSForBBox() {
        return Optional.ofNullable(supportedCRSForBBox);
    }

    @Override
    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

    @Override
    public Optional<FormatEntry> getDefaultFormat() {
        return Optional.ofNullable(defaultFormat);
    }

    @Override
    public IFunctionToGenerateCacheKey<T> getFunctionToGenerateCacheKey() {
        if (functionToWriteToStdin != null) {
            return new GenerateCacheKeyByConvertToByteArray<>(
                    functionToWriteToStdin, path, isOptional);
        } else if (functionToWriteToFiles != null) {
            // this is necessary for files that should be written to files
            // and should be mentioned as command line arguments
            // --> those have random file names to give them to the program
            // --> path should not be included
            if (functionToTransformToCmd != null) {
                return new GenerateCacheKeyByWriteToFiles<>(
                        functionToWriteToFiles, null, isOptional);
            }

            return new GenerateCacheKeyByWriteToFiles<>(
                    functionToWriteToFiles, path, isOptional);
        } else if (functionToTransformToCmd != null) {
            return new GenerateCacheKeyByTransformToCmd<>(
                    functionToTransformToCmd, isOptional);
        } else {
            throw new RuntimeException(
                    "There must be a mechanism to handle the input parameter");
        }
    }

    /**
     * Tests equality.
     * @param o other object
     * @return true if both are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InputParameterImpl that = (InputParameterImpl) o;
        return isOptional == that.isOptional
                && Objects.equals(identifier, that.identifier)
                && Objects.equals(bindingClass, that.bindingClass)
                && Objects.equals(optionalAbstract, that.optionalAbstract)
                && Objects.equals(validator, that.validator)
                && Objects.equals(
                        functionToTransformToCmd, that.functionToTransformToCmd)
                && Objects.equals(path, that.path)
                && Objects.equals(
                        functionToWriteToFiles, that.functionToWriteToFiles)
                && Objects.equals(
                        functionToWriteToStdin, that.functionToWriteToStdin)
                && Objects.equals(allowedValues, that.allowedValues)
                && Objects.equals(defaultValue, that.defaultValue)
                && Objects.equals(supportedCRSForBBox, that.supportedCRSForBBox)
                && Objects.equals(schema, that.schema)
                && Objects.equals(defaultFormat, that.defaultFormat);
    }

    /**
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(identifier, bindingClass, isOptional,
                optionalAbstract, validator, functionToTransformToCmd, path,
                functionToWriteToFiles, functionToWriteToStdin, allowedValues,
                defaultValue, supportedCRSForBBox, schema, defaultFormat);
    }

    /**
     * Builder for the InputParameterImpl.
     * @param <T> binding class
     */
    public static class Builder<T extends IData> {
        /**
         * Identifier of the input.
         */
        private final String identifier;
        /**
         * Binding class for the input.
         */
        private final Class<T> bindingClass;
        /**
         * Flag if the input is optional.
         */
        private final boolean isOptional;
        /**
         * Abstract of the input.
         */
        private final String optionalAbstract;

        /**
         * Validator for the data.
         */
        private ICheckDataAndGetErrorMessage<T> validator;
        /**
         * Function to transform the input data to a command line argument.
         */
        private IConvertIDataToCommandLineParameter<T> functionToTransformToCmd;
        /**
         * Path to write files to.
         */
        private String path;
        /**
         * Function to write files.
         */
        private IWriteIDataToFiles<T> functionToWriteToFiles;
        /**
         * Function to write to stdin.
         */
        private IConvertIDataToByteArray<T> functionToWriteToStdin;

        /**
         * List with allowed values.
         */
        private List<String> allowedValues;
        /**
         * Default value.
         */
        private String defaultValue;
        /**
         * List with supported crs for bbox.
         */
        private List<String> supportedCRSForBBox;
        /**
         * Schema for xml.
         */
        private String schema;
        /**
         * Default format.
         */
        private FormatEntry defaultFormat;

        /**
         * Default constructor.
         * @param aIdentifier identifier of the input
         * @param aBindingClass binding class of the input
         * @param aIsOptional flag if the input is optional
         * @param aOptionalAbstract abstract of the input
         */
        public Builder(
                final String aIdentifier,
                final Class<T> aBindingClass,
                final boolean aIsOptional,
                final String aOptionalAbstract
        ) {
            this.identifier = aIdentifier;
            this.bindingClass = aBindingClass;
            this.isOptional = aIsOptional;
            this.optionalAbstract = aOptionalAbstract;
        }

        /**
         * Adds / Sets the validator.
         * @param aValidator validator to validate the data
         * @return builder
         */
        public Builder withValidator(
                final ICheckDataAndGetErrorMessage<T> aValidator) {
            this.validator = aValidator;
            return this;
        }

        /**
         * Adds / Sets the function to transform the data to a command line.
         * argument.
         * @param aFunctionToTransformToCmd function to transform the data to
         *                                  cmd argument
         * @return builder
         */
        public Builder withFunctionToTransformToCmd(
                final IConvertIDataToCommandLineParameter<T>
                        aFunctionToTransformToCmd) {
            this.functionToTransformToCmd = aFunctionToTransformToCmd;
            return this;
        }

        /**
         * Sets the path to write to.
         * @param aPath path to write files to.
         * @return builder
         */
        public Builder withPath(final String aPath) {
            this.path = aPath;
            return this;
        }

        /**
         * Sets the function to write to files.
         * @param aFunctionToWriteToFiles function to write to files
         * @return builder
         */
        public Builder withFunctionToWriteToFiles(
                final IWriteIDataToFiles<T> aFunctionToWriteToFiles) {
            this.functionToWriteToFiles = aFunctionToWriteToFiles;
            return this;
        }

        /**
         * Sets the function to write to stdin.
         * @param aFunctionToWriteToStdin function to write to stdin
         * @return builder
         */
        public Builder withFunctionToWriteToStdin(
                final IConvertIDataToByteArray<T> aFunctionToWriteToStdin) {
            this.functionToWriteToStdin = aFunctionToWriteToStdin;
            return this;
        }

        /**
         * Sets the allowed values.
         * @param aAllowedValues allowed values
         * @return builder
         */
        public Builder withAllowedValues(final List<String> aAllowedValues) {
            this.allowedValues = aAllowedValues;
            return this;
        }

        /**
         * Sets the default value.
         * @param aDefaultValue default value
         * @return builder
         */
        public Builder withDefaultValue(final String aDefaultValue) {
            this.defaultValue = aDefaultValue;
            return this;
        }

        /**
         * Sets the supported crs for bbox.
         * @param aSupportedCRSForBBox supported crs for bbox
         * @return builder
         */
        public Builder withSupportedCRSForBBox(
                final List<String> aSupportedCRSForBBox) {
            this.supportedCRSForBBox = aSupportedCRSForBBox;
            return this;
        }

        /**
         * Sets the schema.
         * @param aSchema schema for xml
         * @return builder
         */
        public Builder withSchema(final String aSchema) {
            this.schema = aSchema;
            return this;
        }

        /**
         * Sets the default format.
         * @param aDefaultFormat default format for complex outputs
         * @return builder
         */
        public Builder withDefaultFormat(final FormatEntry aDefaultFormat) {
            this.defaultFormat = aDefaultFormat;
            return this;
        }

        /**
         * Builds the IInputParameter.
         * @return IInputParameter
         */
        public IInputParameter build() {
            return new InputParameterImpl<>(this);
        }
    }
}
