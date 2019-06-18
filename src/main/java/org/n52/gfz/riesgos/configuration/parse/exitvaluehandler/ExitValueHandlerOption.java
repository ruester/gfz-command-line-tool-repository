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

package org.n52.gfz.riesgos.configuration.parse.exitvaluehandler;

import org.n52.gfz.riesgos.exitvaluehandler.ExceptionIfExitValueIsNotEmptyHandler;
import org.n52.gfz.riesgos.exitvaluehandler.LogExitValueHandler;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enums for the exit value handlers.
 */
public enum ExitValueHandlerOption {

    /**
     * ignore will create a solution where the exit value will not be handled
     * at all.
     */
    IGNORE("ignore", () -> null),

    /**
     * logging will create a handler that just logs the values.
     */
    LOG("logging", LogExitValueHandler::new),

    /**
     * errorIfNotZero will create a handler that will throw an exception
     * if the value is not zero.
     */
    ERROR("errorIfNotZero", ExceptionIfExitValueIsNotEmptyHandler::new);

    /**
     * String with the attribute name for exit value handlers.
     */
    private static final String HANDLER = "exitValueHandler";

    /**
     * Key with the name to lookup.
     */
    private final String key;
    /**
     * Factory to create the exit value handler.
     */
    private final IExitValueHandlerFactory factory;

    /**
     * Default constructor with a key and a factory.
     * @param aKey lookup name for the handler option
     * @param aFactory factory to create the handler
     */
    ExitValueHandlerOption(
            final String aKey,
            final IExitValueHandlerFactory aFactory) {
        this.key = aKey;
        this.factory = aFactory;
    }

    /**
     *
     * @return key of the option (lookup name)
     */
    public String getKey() {
        return key;
    }

    /**
     *
     * @return factory to create the error handler
     */
    public IExitValueHandlerFactory getFactory() {
        return factory;
    }

    /**
     *
     * @return map with the lookup names as keys
     */
    public static Map<String, ExitValueHandlerOption> asMap() {
        return Stream.of(ExitValueHandlerOption.values())
                .collect(Collectors.toMap(
                        ExitValueHandlerOption::getKey,
                        Function.identity()));
    }

    /**
     *
     * @return string with the attribute name for exit value handlers
     */
    public static String getHandler() {
        return HANDLER;
    }
}
