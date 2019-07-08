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
public class InputParameterImpl<T extends IData> implements IInputParameter {

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
    private final ICheckDataAndGetErrorMessage<T> validator;
    private final IConvertIDataToCommandLineParameter<T> functionToTransformToCmd;
    private final String path;
    private final IWriteIDataToFiles<T> functionToWriteToFiles;
    private final IConvertIDataToByteArray<T> functionToWriteToStdin;
    private final List<String> allowedValues;
    private final String defaultValue;
    private final List<String> supportedCRSForBBox;
    private final String schema;
    private final FormatEntry defaultFormat;

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
    public Optional<IConvertIDataToCommandLineParameter> getFunctionToTransformToCmd() {
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
        if(functionToWriteToStdin != null) {
            return new GenerateCacheKeyByConvertToByteArray<>(functionToWriteToStdin);
        } else if(functionToWriteToFiles != null) {
            // the way that the write to files function is used before
            // the transform th cmd it is not necessary
            // to care about the file name that is used if the file is used
            // as a command line argument
            return new GenerateCacheKeyByWriteToFiles<>(functionToWriteToFiles);
        } else if(functionToTransformToCmd != null) {
            return new GenerateCacheKeyByTransformToCmd<>(functionToTransformToCmd);
        } else {
            throw new RuntimeException("There must be a mechanism to handle the input parameter");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InputParameterImpl that = (InputParameterImpl) o;
        return isOptional == that.isOptional &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(bindingClass, that.bindingClass) &&
                Objects.equals(optionalAbstract, that.optionalAbstract) &&
                Objects.equals(validator, that.validator) &&
                Objects.equals(functionToTransformToCmd, that.functionToTransformToCmd) &&
                Objects.equals(path, that.path) &&
                Objects.equals(functionToWriteToFiles, that.functionToWriteToFiles) &&
                Objects.equals(functionToWriteToStdin, that.functionToWriteToStdin) &&
                Objects.equals(allowedValues, that.allowedValues) &&
                Objects.equals(defaultValue, that.defaultValue) &&
                Objects.equals(supportedCRSForBBox, that.supportedCRSForBBox) &&
                Objects.equals(schema, that.schema) &&
                Objects.equals(defaultFormat, that.defaultFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, bindingClass, isOptional, optionalAbstract, validator, functionToTransformToCmd, path, functionToWriteToFiles, functionToWriteToStdin, allowedValues, defaultValue, supportedCRSForBBox, schema, defaultFormat);
    }

    public static class Builder<T extends IData> {
        private final String identifier;
        private final Class<T> bindingClass;
        private final boolean isOptional;
        private final String optionalAbstract;

        private ICheckDataAndGetErrorMessage<T> validator;
        private IConvertIDataToCommandLineParameter<T> functionToTransformToCmd;
        private String path;
        private IWriteIDataToFiles<T> functionToWriteToFiles;
        private IConvertIDataToByteArray<T> functionToWriteToStdin;

        private List<String> allowedValues;
        private String defaultValue;
        private List<String> supportedCRSForBBox;
        private String schema;
        private FormatEntry defaultFormat;

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

        public Builder withValidator(final ICheckDataAndGetErrorMessage<T> aValidator) {
            this.validator = aValidator;
            return this;
        }

        public Builder withFunctionToTransformToCmd(final IConvertIDataToCommandLineParameter<T> aFunctionToTransformToCmd) {
            this.functionToTransformToCmd = aFunctionToTransformToCmd;
            return this;
        }

        public Builder withPath(final String aPath) {
            this.path = aPath;
            return this;
        }

        public Builder withFunctionToWriteToFiles(final IWriteIDataToFiles<T> aFunctionToWriteToFiles) {
            this.functionToWriteToFiles = aFunctionToWriteToFiles;
            return this;
        }

        public Builder withFunctionToWriteToStdin(final IConvertIDataToByteArray<T> aFunctionToWriteToStdin) {
            this.functionToWriteToStdin = aFunctionToWriteToStdin;
            return this;
        }

        public Builder withAllowedValues(final List<String> aAllowedValues) {
            this.allowedValues = aAllowedValues;
            return this;
        }

        public Builder withDefaultValue(final String aDefaultValue) {
            this.defaultValue = aDefaultValue;
            return this;
        }

        public Builder withSupportedCRSForBBox(final List<String> aSupportedCRSForBBox) {
            this.supportedCRSForBBox = aSupportedCRSForBBox;
            return this;
        }

        public Builder withSchema(final String aSchema) {
            this.schema = aSchema;
            return this;
        }

        public Builder withDefaultFormat(final FormatEntry aDefaultFormat) {
            this.defaultFormat = aDefaultFormat;
            return this;
        }

        public IInputParameter build() {
            return new InputParameterImpl<>(this);
        }
    }
}
