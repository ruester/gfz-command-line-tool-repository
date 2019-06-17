package org.n52.gfz.riesgos.configuration;

import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IWriteIDataToFiles;

import java.util.List;
import java.util.Optional;

/**
 * Configuration of an input parameter.
 *
 */
public interface IInputParameter extends IIOParameter {

    /**
     *
     * @return Function to transform the value of the parameter to a command
     * line argument (string, boolean flag, file, ...)
     */
    Optional<IConvertIDataToCommandLineParameter> getFunctionToTransformToCmd();

    /**
     *
     * @return function to write the iData to files
     */
    Optional<IWriteIDataToFiles> getFunctionToWriteIDataToFiles();

    /**
     *
     * @return function to convert the content of the value into a byte array
     * so that it can be written to stdin
     */
    Optional<IConvertIDataToByteArray> getFunctionToWriteToStdin();

    /**
     * Only used if the type is a literal type (for example
     * LiteralStringBinding).
     * @return optional list with allowed values.
     */
    Optional<List<String>> getAllowedValues();

    /**
     * Only used if the type is a literal type (for example
     * LiteralStringBinding).
     * @return optional default value
     */
    Optional<String> getDefaultValue();
}
