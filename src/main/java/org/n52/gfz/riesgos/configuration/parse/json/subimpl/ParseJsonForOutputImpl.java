package org.n52.gfz.riesgos.configuration.parse.json.subimpl;

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

import org.json.simple.JSONObject;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.IdentifierWithBindingFactory;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

import java.net.ProxySelector;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sub implementation for parsing a single input input
 */
public class ParseJsonForOutputImpl {

    final Map<String, FromStdoutOption> optionsToReadFromStdout;

    public ParseJsonForOutputImpl() {

        this.optionsToReadFromStdout = getOptionsToReadFromStdout();
    }

    private Map<String, FromStdoutOption> getOptionsToReadFromStdout() {
        return Stream.of(FromStdoutOption.values()).collect(Collectors.toMap(
                FromStdoutOption::getDataType, Function.identity()
        ));
    }

    public IIdentifierWithBinding parseOutput(final JSONObject json) throws ParseConfigurationException {
        final String identifier = getString(json, "title");
        final String readFrom = getString(json, "readFrom");

        if("stdout".equals(readFrom)) {
            final String type = getString(json, "type");
            if(optionsToReadFromStdout.containsKey(type)) {
                return optionsToReadFromStdout.get(type).getFactory().create(identifier);

            } else {
                throw new ParseConfigurationException("Not supported type value");
            }
        } else {
            throw new ParseConfigurationException("Not supported readFrom value");
        }
    }

    private String getString(final JSONObject json, final String key) throws ParseConfigurationException {
        if(! json.containsKey(key)) {
            throw new ParseConfigurationException("Missing element '" + key + "'");
        }
        final Object rawValue = json.get(key);
        if(! (rawValue instanceof String)) {
            throw new ParseConfigurationException("Wrong type for element '" + key + "', expected a String");
        }
        return (String) rawValue;
    }

    @FunctionalInterface
    private interface IStdoutOutputFactory {
        IIdentifierWithBinding create(final String identifier);
    }

    private enum FromStdoutOption {
        STRING("string", IdentifierWithBindingFactory::createStdoutString);

        private final String dataType;
        private final IStdoutOutputFactory factory;

        FromStdoutOption(final String dataType, final IStdoutOutputFactory factory) {
            this.dataType = dataType;
            this.factory = factory;
        }

        public String getDataType() {
            return dataType;
        }

        public IStdoutOutputFactory getFactory() {
            return factory;
        }
    }
}
