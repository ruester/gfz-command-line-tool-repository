package org.n52.gfz.riesgos.configuration;

import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.wps.io.data.IData;

import java.util.List;
import java.util.Optional;


/**
 * Configuration of an input or output parameter.
 *
 * This is a very general interface.
 * Depending on that values are provided via the Optionals
 * the data can be written to files, read from files, converted to
 * command line parameter,
 * written to stdin, or read from stdout / stderr or from the exit value.
 */
public interface IIOParameter {

    /**
     *
     * @return identifier for the value
     */
    String getIdentifier();

    /**
     *
     * @return Binding class (LiteralStringBinding, GenericXMLDataBinding, ...)
     */
    Class<? extends IData> getBindingClass();

    /**
     *
     * @return optional abstract of the input/output identifier
     */
    Optional<String> getAbstract();

    /**
     *
     * @return optional Validator to prove that input and output values
     * have the right data
     */
    Optional<ICheckDataAndGetErrorMessage> getValidator();

    /**
     *
     * @return Path to read or write a file (relative to the
     * working directory)
     */
    Optional<String> getPathToWriteToOrReadFromFile();

    /**
     * Only used if the type is a GenericXMLDataBinding to provide a
     * more specific schema,
     * but for still using the parser and generator for the generic case.
     * @return schema for xml data
     */
    Optional<String> getSchema();

    /**
     * Only used if the type is a bbox type.
     * @return list with supported CRSs
     */
    Optional<List<String>> getSupportedCRSForBBox();

    /**
     * Specifies if the parameter is optional or not.
     * At the moment only meaningful for input parameters.
     *
     * @return true if the parameter is optional
     */
    boolean isOptional();
}
