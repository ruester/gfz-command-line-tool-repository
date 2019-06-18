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

package org.n52.gfz.riesgos.configuration.parse.stdouthandler;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum with the options for stdout handling.
 */
public enum StdoutHandlerOption {
    /**
     * ignore will ignore any text on stdout.
     */
    IGNORE("ignore", () -> null);

    /**
     * Text attribute to search in.
     */
    private static final String HANLDER = "stdoutHandler";

    /**
     * Key for the lookup of the option.
     */
    private final String key;
    /**
     * Factory to create the stdout handler.
     */
    private final IStdoutHandlerFactory factory;


    /**
     * Default constructor.
     * @param aKey key for the lookup
     * @param aFactory factory to create the stdout handler
     */
    StdoutHandlerOption(
            final String aKey,
            final IStdoutHandlerFactory aFactory) {
        this.key = aKey;
        this.factory = aFactory;
    }

    /**
     *
     * @return key for the lookup of the options
     */
    public String getKey() {
        return key;
    }

    /**
     *
     * @return factory to create the stdout handler
     */
    public IStdoutHandlerFactory getFactory() {
        return factory;
    }

    /**
     *
     * @return Map with the options by lookup key
     */
    public static Map<String, StdoutHandlerOption> asMap() {
        return Stream.of(StdoutHandlerOption.values())
                .collect(Collectors.toMap(
                        StdoutHandlerOption::getKey,
                        Function.identity()));
    }

    /**
     *
     * @return text attribute to search in
     */
    public static String getHandler() {
        return HANLDER;
    }
}
