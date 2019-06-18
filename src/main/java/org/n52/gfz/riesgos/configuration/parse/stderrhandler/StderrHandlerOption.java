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

package org.n52.gfz.riesgos.configuration.parse.stderrhandler;

import org.n52.gfz.riesgos.stderrhandler.ExceptionIfStderrIsNotEmptyHandler;
import org.n52.gfz.riesgos.stderrhandler.LogStderrHandler;
import org.n52.gfz.riesgos.stderrhandler.PythonTracebackStderrHandler;
import org.n52.gfz.riesgos.stderrhandler.RErrorMessageStderrHandler;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum with the options for the stderr error handlers.
 */
public enum StderrHandlerOption {
    /**
     * ignore will result in a error handling that does not care
     * about the stderr text at all.
     */
    IGNORE("ignore", () -> null),
    /**
     * logging will create a handler that will log all the text on stderr.
     */
    LOG("logging", LogStderrHandler::new),

    /**
     * errorIfNotEmpty will thrown an exception if the stderr text is not
     * empty.
     */
    ERROR("errorIfNotEmpty", ExceptionIfStderrIsNotEmptyHandler::new),
    /**
     * pythonTraceback will throw an exception if there is a python
     * traceback on stderr.
     */
    PYTHON_TRACEBACK("pythonTraceback", PythonTracebackStderrHandler::new),
    /**
     * rError will throw an exception if there is an R error on stderr.
     */
    R_ERROR("rError", RErrorMessageStderrHandler::new);

    /**
     * Text for the stderrHandler attribute.
     */
    private static final String HANDLER = "stderrHandler";

    /**
     * The key for the option.
     */
    private final String key;
    /**
     * The factory to create the error handler.
     */
    private final IStderrHandlerFactory factory;

    /**
     * Default constructor.
     * @param aKey key for the lookup
     * @param aFactory factory to create the error handler
     */
    StderrHandlerOption(
            final String aKey,
            final IStderrHandlerFactory aFactory) {
        this.key = aKey;
        this.factory = aFactory;
    }

    /**
     *
     * @return key to lookup the options for error handling
     */
    public String getKey() {
        return key;
    }

    /**
     *
     * @return factory to create the error handler
     */
    public IStderrHandlerFactory getFactory() {
        return factory;
    }

    /**
     *
     * @return map by the name of the options
     */
    public static Map<String, StderrHandlerOption> asMap() {
        return Stream.of(StderrHandlerOption.values())
                .collect(Collectors.toMap(
                        StderrHandlerOption::getKey,
                        Function.identity()));
    }

    /**
     *
     * @return text for the stderrHandler attribute
     */
    public static String getHandler() {
        return HANDLER;
    }
}
