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

import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;

import java.util.List;
import java.util.Optional;

/**
 * Interface for the configuration of the services that access command line tools inside of docker
 */
public interface IConfiguration {

    String PATH_FULL_QUALIFIED = "org.n52.gfz.riesgos.algorithm.impl.";

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
        return PATH_FULL_QUALIFIED + getIdentifier();
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
