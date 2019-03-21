package org.n52.gfz.riesgos.configuration;

import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;

import java.util.List;
import java.util.Optional;

/**
 * Interface for the configuration of the services that access command line tools inside of docker
 */
public interface IConfiguration {

    /**
     *
     * @return Identifier that will be displayed as the title of the process
     */
    String getIdentifier();

    /**
     *
     * @return full qualified class path of a - generated - service
     * just used for the generation of the process description
     */
    default String getFullQualifiedIdentifier() {
        return "org.n52.gfz.riesgos.algorithm.impl." + getIdentifier();
    }

    /**
     *
     * @return ID of the docker imaged used to create a container for running the process
     */
    String getImageId();

    /**
     *
     * @return working directory to run the executable inside of the container
     */
    String getWorkingDirectory();

    /**
     *
     * @return list with the command line command (for example ["python3", "script.py"]
     */
    List<String> getCommandToExecute();

    /**
     *
     * @return list with default command line flags that should always be provided
     */
    List<String> getDefaultCommandLineFlags();

    /**
     *
     * @return list with the configuration of all the input parameters
     */
    List<IIdentifierWithBinding> getInputIdentifiers();

    /**
     *
     * @return list with the configuration of all the output parameters
     */
    List<IIdentifierWithBinding> getOutputIdentifiers();

    /**
     *
     * @return handler for stderr (indicating errors, logging, ...)
     */
    Optional<IStderrHandler> getStderrHandler();

    /**
     *
     * @return handler for exit value (indicating errors, logging, ...)
     */
    Optional<IExitValueHandler> getExitValueHandler();

    /**
     *
     * @return handler for stdout (logging, ...)
     */
    Optional<IStdoutHandler> getStdoutHandler();
}
