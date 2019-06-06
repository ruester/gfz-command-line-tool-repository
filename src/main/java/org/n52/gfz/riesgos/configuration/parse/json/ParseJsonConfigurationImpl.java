package org.n52.gfz.riesgos.configuration.parse.json;

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

import org.apache.tools.ant.types.Commandline;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.impl.ConfigurationImpl;
import org.n52.gfz.riesgos.configuration.parse.IParseConfiguration;
import org.n52.gfz.riesgos.configuration.parse.json.subimpl.ParseJsonForInputImpl;
import org.n52.gfz.riesgos.configuration.parse.json.subimpl.ParseJsonForOutputImpl;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.gfz.riesgos.exitvaluehandler.ExceptionIfExitValueIsNotEmptyHandler;
import org.n52.gfz.riesgos.exitvaluehandler.LogExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.stderrhandler.ExceptionIfStderrIsNotEmptyHandler;
import org.n52.gfz.riesgos.stderrhandler.LogStderrHandler;
import org.n52.gfz.riesgos.stderrhandler.PythonTracebackStderrHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation that parses a json configuration.
 */
public class ParseJsonConfigurationImpl implements IParseConfiguration {

    private final ParseJsonForInputImpl inputElementParser;
    private final ParseJsonForOutputImpl outputElementParser;

    private final Map<String, StderrHandlerOption> optionsForStderrHandler;
    private final Map<String, ExitValueHandlerOption> optionsForExitValueHandler;
    private final Map<String, StdoutHandlerOption> optionsForStdoutHandler;

    public ParseJsonConfigurationImpl() {
        this.inputElementParser = new ParseJsonForInputImpl();
        this.outputElementParser = new ParseJsonForOutputImpl();

        this.optionsForStderrHandler = getOptionsForStderrHandler();
        this.optionsForExitValueHandler = getOptionsForExitValueHandler();
        this.optionsForStdoutHandler = getOptionsForStdoutHandler();
    }

    private Map<String, StderrHandlerOption> getOptionsForStderrHandler() {
        return Stream.of(StderrHandlerOption.values())
                .collect(Collectors.toMap(
                        StderrHandlerOption::getKey,
                        Function.identity()));
    }

    private Map<String, ExitValueHandlerOption> getOptionsForExitValueHandler() {
        return Stream.of(ExitValueHandlerOption.values())
                .collect(Collectors.toMap(
                        ExitValueHandlerOption::getKey,
                        Function.identity()));
    }

    private Map<String, StdoutHandlerOption> getOptionsForStdoutHandler() {
        return Stream.of(StdoutHandlerOption.values())
                .collect(Collectors.toMap(
                        StdoutHandlerOption::getKey,
                        Function.identity()));
    }


    /**
     * Parses the configuration
     * @param inputText input text with a json object
     * @return IConfiguration
     * @throws ParseConfigurationException exception indicating that there is problem on parsing the input
     */
    @Override
    public IConfiguration parse(String inputText) throws ParseConfigurationException {
        try {
            if(inputText == null) {
                throw new ParseConfigurationException("No input text");
            }
            final Object parsed = new JSONParser().parse(inputText);
            if(parsed instanceof JSONObject) {
                final JSONObject json = (JSONObject) parsed;

                final String identifier = getStringEntry(json, "title");
                final String optionalAbstract = getOptionalStringEntry(json, "abstract");
                final String imageId = getStringEntry(json, "imageId");
                final String workingDirectory = getStringEntry(json, "workingDirectory");

                final List<String> commandToExecute = getCommandToExecute(json);

                final List<String> defaultCommandLineFlags = getDefaultCommandLineFlags(json);

                final List<IIdentifierWithBinding> inputData = parseInputs(json);
                final List<IIdentifierWithBinding> outputData = parseOutputs(json);

                final IStderrHandler stderrHandler = parseStderrHandler(json);
                final IExitValueHandler exitValueHandler = parseExitValueHandler(json);
                final IStdoutHandler stdoutHandler = parseStdoutHandler(json);

                return new ConfigurationImpl.Builder(identifier, optionalAbstract, imageId, workingDirectory, commandToExecute)
                        .withAddedDefaultCommandLineFlags(defaultCommandLineFlags)
                        .withAddedInputIdentifiers(inputData)
                        .withAddedOutputIdentifiers(outputData)
                        .withStderrHandler(stderrHandler)
                        .withExitValueHandler(exitValueHandler)
                        .withStdoutHandler(stdoutHandler)
                        .build();

            } else {
                throw new ParseConfigurationException("There must be a json document (as json object)");
            }
        } catch(final ParseException parseException) {
            throw new ParseConfigurationException(parseException);
        }
    }

    private String getStringEntry(final JSONObject jsonObject, final String key) throws ParseConfigurationException {
        if(! jsonObject.containsKey(key)) {
            throw new ParseConfigurationException("Missing key '"+ key + "'");
        }
        final Object value = jsonObject.get(key);
        if(! (value instanceof String)) {
            throw new ParseConfigurationException("Wrong type for key '" + key + "', expected a String");
        }
        return (String) value;
    }

    private String getOptionalStringEntry(final JSONObject jsonObject, final String key) throws ParseConfigurationException {
        if(! jsonObject.containsKey(key)) {
            return null;
        }
        final Object value = jsonObject.get(key);
        if(! (value instanceof String)) {
            throw new ParseConfigurationException("Wrong type for key '" + key + "', expected a String");
        }
        return (String) value;
    }

    private List<String> getCommandToExecute(final JSONObject jsonObject) throws ParseConfigurationException {
        // command to execute must use a list
        // -> use some class from the ant library
        // they know how to split it into several parts
        return Arrays.asList(Commandline.translateCommandline(getStringEntry(jsonObject, "commandToExecute")));
    }

    private List<IIdentifierWithBinding> parseInputs(final JSONObject jsonObject) throws ParseConfigurationException {
        return parseIdentifier(jsonObject, "input", inputElementParser::parseInput);
    }

    private List<IIdentifierWithBinding> parseOutputs(final JSONObject jsonObject) throws ParseConfigurationException {
        return parseIdentifier(jsonObject, "output", outputElementParser::parseOutput);
    }

    private List<IIdentifierWithBinding> parseIdentifier(final JSONObject jsonObject, final String key, final IParseIdentifier parser) throws ParseConfigurationException {
        final List<IIdentifierWithBinding> result = new ArrayList<>();
        if(jsonObject.containsKey(key)) {
            final Object rawList = jsonObject.get(key);
            if(! (rawList instanceof JSONArray)) {
                throw new ParseConfigurationException("Wrong type for key '" + key + "', expected an Array");
            }
            final JSONArray list = (JSONArray) rawList;
            for(final Object element : list) {
                if(! (element instanceof JSONObject)) {
                    throw new ParseConfigurationException("Wrong type for element in " + key + ", expected an JSONObject");
                }
                final JSONObject jsonIdentifierObject = (JSONObject) element;
                result.add(parser.parse(jsonIdentifierObject));
            }
        }
        return result;
    }

    @FunctionalInterface
    private interface IParseIdentifier {
        IIdentifierWithBinding parse(final JSONObject json) throws ParseConfigurationException;
    }

    private List<String> getDefaultCommandLineFlags(final JSONObject json) throws ParseConfigurationException {
        final List<String> result = new ArrayList<>();

        final String key = "defaultCommandLineFlags";
        if(json.containsKey(key)) {
            final Object rawList = json.get(key);
            if(! (rawList instanceof JSONArray)) {
                throw new ParseConfigurationException("Wrong type for key '" + key + "', expected an Array");
            }
            final JSONArray list = (JSONArray) rawList;
            for(final Object element : list) {
                if(! (element instanceof String)) {
                    throw new ParseConfigurationException("Wrong type for element in " + key + ", expected a String");
                }
                final String defaultCommandLineFlag = (String) element;
                result.add(defaultCommandLineFlag);
            }
        }
        return result;
    }

    private IStderrHandler parseStderrHandler(final JSONObject jsonObject) throws ParseConfigurationException {
        final IStderrHandler result;
        final String key = "stderrHandler";
        if(jsonObject.containsKey(key)) {
            final Object rawValue = jsonObject.get(key);
            if(! (rawValue instanceof String)) {
                throw new ParseConfigurationException("Wrong type for key '" + key + "', expected a String");
            }
            final String value = (String) rawValue;
            if(optionsForStderrHandler.containsKey(value)) {
                result = optionsForStderrHandler.get(value).factory.get();
            } else {
                throw new ParseConfigurationException("Unsupported value '" + value + "' for key '" + key + "'");
            }
        } else {
            result = null;
        }
        return result;
    }

    private enum StderrHandlerOption {
        IGNORE("ignore", () -> null),
        LOG("logging", LogStderrHandler::new),
        ERROR("errorIfNotEmpty", ExceptionIfStderrIsNotEmptyHandler::new),
        PYTHON_TRACEBACK("pythonTraceback", PythonTracebackStderrHandler::new);

        private final String key;
        private final Supplier<IStderrHandler> factory;

        StderrHandlerOption(final String key, final Supplier<IStderrHandler> factory) {
            this.key = key;
            this.factory = factory;
        }

        public String getKey() {
            return key;
        }

        public Supplier<IStderrHandler> getFactory() {
            return factory;
        }
    }

    private IExitValueHandler parseExitValueHandler(final JSONObject jsonObject) throws ParseConfigurationException {
        final IExitValueHandler result;
        final String key = "exitValueHandler";
        if(jsonObject.containsKey(key)) {
            final Object rawValue = jsonObject.get(key);
            if(! (rawValue instanceof  String)) {
                throw new ParseConfigurationException("Wrong type for key '" + key + "', expected a String");
            }
            final String value = (String) rawValue;
            if(optionsForExitValueHandler.containsKey(value)) {
                result = optionsForExitValueHandler.get(value).getFactory().get();
            } else {
                throw new ParseConfigurationException("Unsupported value '" + value + "' for key '" + key + "'");
            }
        } else {
            result = null;
        }

        return result;
    }

    private enum ExitValueHandlerOption {
        IGNORE("ignore", () -> null),
        LOG("logging", LogExitValueHandler::new),
        ERROR("errorIfNotZero", ExceptionIfExitValueIsNotEmptyHandler::new);

        private final String key;
        private final Supplier<IExitValueHandler> factory;

        ExitValueHandlerOption(final String key, final Supplier<IExitValueHandler> factory) {
            this.key = key;
            this.factory = factory;
        }

        public String getKey() {
            return key;
        }

        public Supplier<IExitValueHandler> getFactory() {
            return factory;
        }
    }

    private IStdoutHandler parseStdoutHandler(final JSONObject jsonObject) throws ParseConfigurationException {
        final IStdoutHandler result;
        final String key = "stdoutHandler";
        if(jsonObject.containsKey(key)) {
            final Object rawValue = jsonObject.get(key);
            if (!(rawValue instanceof String)) {
                throw new ParseConfigurationException("Wrong type for key '" + key + "', expected a String");
            }
            final String value = (String) rawValue;
            if (optionsForStdoutHandler.containsKey(value)) {
                result = optionsForStdoutHandler.get(value).getFactory().get();
            } else {
                throw new ParseConfigurationException("Unsupported value '" + value + "' for key '" + key + "'");
            }
        } else {
            result = null;
        }
        return result;
    }

    private enum StdoutHandlerOption {
        IGNORE("ignore", () -> null);

        private final String key;
        private final Supplier<IStdoutHandler> factory;

        StdoutHandlerOption(final String key, final Supplier<IStdoutHandler> factory) {
            this.key = key;
            this.factory = factory;
        }

        public String getKey() {
            return key;
        }

        public Supplier<IStdoutHandler> getFactory() {
            return factory;
        }
    }
}
