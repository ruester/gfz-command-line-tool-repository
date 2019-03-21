package org.n52.gfz.riesgos.configuration;

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

import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.wps.io.data.IData;

import java.util.List;
import java.util.Optional;

/**
 * Configuration of an input or output parameter.
 *
 * This is a very generel interface.
 * Depending on that values are provided via the Optionals
 * the data can be written to files, read from files, converted to command line parameter,
 * written to stdin, or read from stdout / stderr or from the exit value.
 */
public interface IIdentifierWithBinding {

    /**
     *
     * @return identifier for the value
     */
    String getIdentifer();

    /**
     *
     * @return Binding class (LiteralStringBinding, GenericXMLDataBinding, ...)
     */
    Class<? extends IData> getBindingClass();

    /**
     *
     * @return optional Validator to prove that input and output values have the right data
     */
    Optional<ICheckDataAndGetErrorMessage> getValidator();

    /**
     *
     * @return Function to transform the value of the parameter to a command line argument (string, boolean flag,
     * file, ...)
     */
    Optional<IConvertIDataToCommandLineParameter> getFunctionToTransformToCmd();

    /**
     *
     * @return Path to read or write a file (relative to the working directory)
     */
    Optional<String> getPathToWriteToOrReadFromFile();

    /**
     *
     * @return function to convert the content of the value into a byte array
     * so that it can be written to files
     */
    Optional<IConvertIDataToByteArray> getFunctionToGetBytesToWrite();

    /**
     *
     * @return function to convert the content of the value into a byte array
     * so that it can be written to stdin
     */
    Optional<IConvertIDataToByteArray> getFunctionToWriteToStdin();

    /**
     *
     * @return function to convert a byte array to the value; used to convert from stderr
     */
    Optional<IConvertByteArrayToIData> getFunctionToHandleStderr();

    /**
     *
     * @return function to convert a integer exit value to the value used to value; u
     */
    Optional<IConvertExitValueToIData> getFunctionToHandleExitValue();

    /**
     *
     * @return function to convert a byte array to the value; used to convert from stdout
     */
    Optional<IConvertByteArrayToIData> getFunctionToHandleStdout();

    /**
     *
     * @return function to convert a byte array to the value; used to read from output files
     */
    Optional<IConvertByteArrayToIData> getFunctionToReadFromBytes();

    /**
     * Only used if the type is a literal type (for example LiteralStringBinding)
     * @return optional list with allowed values.
     */
    Optional<List<String>> getAllowedValues();

    /**
     * Only used if the type is a literal type (for example LiteralStringBinding)
     * @return optional default value
     */
    Optional<String> getDefaultValue();

    /**
     * Only used if the type is a bbox type
     * @return list with supported CRSs
     */
    Optional<List<String>> getSupportedCRSForBBox();

    /**
     * Only used if the type is a GenericXMLDataBinding to provide a more specific schema,
     * but for still using the parser and generator for the generic case.
     * @return schema for xml data
     */
    Optional<String> getSchema();
}
