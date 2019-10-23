package org.n52.gfz.riesgos.configuration.parse.formats.json;

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
 */

import org.apache.tools.ant.types.Commandline;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.configuration.impl.ConfigurationImpl;
import org.n52.gfz.riesgos.configuration.parse.IParseConfiguration;
import org.n52.gfz.riesgos.configuration.parse.exitvaluehandler.ExitValueHandlerOption;
import org.n52.gfz.riesgos.configuration.parse.formats.json.subimpl.ParseJsonForInputImpl;
import org.n52.gfz.riesgos.configuration.parse.formats.json.subimpl.ParseJsonForOutputImpl;
import org.n52.gfz.riesgos.configuration.parse.stderrhandler.StderrHandlerOption;
import org.n52.gfz.riesgos.configuration.parse.stdouthandler.StdoutHandlerOption;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Implementation that parses a json configuration.
 */
public class ParseJsonConfigurationImpl
        extends AbstractParseJson
        implements IParseConfiguration {

    /**
     * Sub parser for the input elements.
     */
    private final ParseJsonForInputImpl inputElementParser;
    /**
     * Sub parser for the output elements.
     */
    private final ParseJsonForOutputImpl outputElementParser;

    /**
     * Map with the stderr handler options by name.
     */
    private final Map<String, StderrHandlerOption> optionsForStderrHandler;
    /**
     * Map with the exit value handlers by name.
     */
    private final Map<String, ExitValueHandlerOption>
            optionsForExitValueHandler;
    /**
     * Map with the stdout handlers by name.
     */
    private final Map<String, StdoutHandlerOption> optionsForStdoutHandler;

    /**
     * Default constructor.
     */
    public ParseJsonConfigurationImpl() {
        this.inputElementParser = new ParseJsonForInputImpl();
        this.outputElementParser = new ParseJsonForOutputImpl();

        this.optionsForStderrHandler = StderrHandlerOption.asMap();
        this.optionsForExitValueHandler = ExitValueHandlerOption.asMap();
        this.optionsForStdoutHandler = StdoutHandlerOption.asMap();
    }

    /**
     * Parses the configuration.
     * @param inputText input text with a json object
     * @return IConfiguration configuration of the input text
     * @throws ParseConfigurationException exception indicating that there
     * is problem on parsing the input
     */
    @Override
    public IConfiguration parse(final String inputText)
            throws ParseConfigurationException {
        try {
            if (inputText == null) {
                throw new ParseConfigurationException("No input text");
            }
            final Object parsed = new JSONParser().parse(inputText);
            if (parsed instanceof JSONObject) {
                final JSONObject json = (JSONObject) parsed;

                final String identifier = getString(json, "title");
                final String optionalAbstract =
                        getOptionalString(json, "abstract")
                                .orElse(null);
                final String imageId = getString(json, "imageId");
                final String workingDirectory =
                        getString(json, "workingDirectory");

                final List<String> commandToExecute = getCommandToExecute(json);

                final List<String> defaultCommandLineFlags =
                        getDefaultCommandLineFlags(json);

                final List<IInputParameter> inputData = parseInputs(json);
                final List<IOutputParameter> outputData = parseOutputs(json);

                final IStderrHandler stderrHandler = parseStderrHandler(json);
                final IExitValueHandler exitValueHandler =
                        parseExitValueHandler(json);
                final IStdoutHandler stdoutHandler = parseStdoutHandler(json);

                return new ConfigurationImpl.Builder(
                        identifier,
                        optionalAbstract,
                        imageId,
                        workingDirectory,
                        commandToExecute)
                    .withAddedDefaultCommandLineFlags(defaultCommandLineFlags)
                    .withAddedInputIdentifiers(inputData)
                    .withAddedOutputIdentifiers(outputData)
                    .withStderrHandler(stderrHandler)
                    .withExitValueHandler(exitValueHandler)
                    .withStdoutHandler(stdoutHandler)
                    .build();

            } else {
                throw new ParseConfigurationException(
                        "There must be a json document (as json object)");
            }
        } catch (final ParseException parseException) {
            throw new ParseConfigurationException(parseException);
        }
    }

    /**
     *
     * @param jsonObject json object to search in for commandToExecute
     * @return command as a list as for ["ls", "-la"]
     * @throws ParseConfigurationException exception that is thrown
     * if the element is not in the json object
     */
    private List<String> getCommandToExecute(final JSONObject jsonObject)
            throws ParseConfigurationException {
        // command to execute must use a list
        // -> use some class from the ant library
        // they know how to split it into several parts
        return Arrays.asList(
                Commandline.translateCommandline(
                        getString(jsonObject, "commandToExecute")));
    }

    /**
     *
     * @param jsonObject json object to search in
     * @return list with input parameters
     * @throws ParseConfigurationException exception that is thrown if there
     * is a problem on parsing the input parameters
     */
    private List<IInputParameter> parseInputs(final JSONObject jsonObject)
            throws ParseConfigurationException {
        final List<IInputParameter> result = new ArrayList<>();
        final String key = "input";
        if (jsonObject.containsKey(key)) {
            final Object rawList = jsonObject.get(key);
            if (!(rawList instanceof JSONArray)) {
                throw new ParseConfigurationException(
                        "Wrong type for key '" + key + "', expected an Array");
            }
            final JSONArray list = (JSONArray) rawList;
            for (final Object element : list) {
                if (!(element instanceof JSONObject)) {
                    throw new ParseConfigurationException(
                            "Wrong type for element in '"
                                    + key
                                    + "', expected an JSONObject");
                }
                final JSONObject jsonIdentifierObject = (JSONObject) element;
                result.add(
                        inputElementParser.parseInput(jsonIdentifierObject));
            }
        }
        return result;
    }

    /**
     *
     * @param jsonObject json object to search in
     * @return lsit with output parameters
     * @throws ParseConfigurationException exception that is thrown if htere
     * is a problem on parsing the output parameters
     */
    private List<IOutputParameter> parseOutputs(final JSONObject jsonObject)
            throws ParseConfigurationException {
        final List<IOutputParameter> result = new ArrayList<>();
        final String key = "output";
        if (jsonObject.containsKey(key)) {
            final Object rawList = jsonObject.get(key);
            if (!(rawList instanceof JSONArray)) {
                throw new ParseConfigurationException(
                        "Wrong type for key '" + key + "', expected an Array");
            }
            final JSONArray list = (JSONArray) rawList;
            for (final Object element : list) {
                if (!(element instanceof JSONObject)) {
                    throw new ParseConfigurationException(
                            "Wrong type for element in '"
                                    + key
                                    + "', expected an JSONObject");
                }
                final JSONObject jsonIdentifierObject = (JSONObject) element;
                result.add(
                        outputElementParser.parseOutput(jsonIdentifierObject));
            }
        }
        return result;
    }

    /**
     *
     * @param json json object to search in
     * @return list with the default command line flags
     * @throws ParseConfigurationException exception if there is a
     * key in the json object but the wrong type.
     */
    private List<String> getDefaultCommandLineFlags(
            final JSONObject json) throws ParseConfigurationException {
        final List<String> result = new ArrayList<>();

        final String key = "defaultCommandLineFlags";
        if (json.containsKey(key)) {
            final Object rawList = json.get(key);
            if (!(rawList instanceof JSONArray)) {
                throw new ParseConfigurationException(
                        "Wrong type for key '" + key + "', expected an Array");
            }
            final JSONArray list = (JSONArray) rawList;
            for (final Object element : list) {
                if (!(element instanceof String)) {
                    throw new ParseConfigurationException(
                            "Wrong type for element in "
                                    + key
                                    + ", expected a String");
                }
                final String defaultCommandLineFlag = (String) element;
                result.add(defaultCommandLineFlag);
            }
        }
        return result;
    }

    /**
     *
     * @param jsonObject json object to search in
     * @return stderr handler
     * @throws ParseConfigurationException exception that is thrown
     * if there is a not supported error handler or the type if the
     * value for the stderr handler is wrong
     */
    private IStderrHandler parseStderrHandler(
            final JSONObject jsonObject)
            throws ParseConfigurationException {

        final IStderrHandler result;
        final String key = StderrHandlerOption.getHandler();
        if (jsonObject.containsKey(key)) {
            final Object rawValue = jsonObject.get(key);
            if (!(rawValue instanceof String)) {
                throw new ParseConfigurationException(
                        "Wrong type for key '" + key + "', expected a String");
            }
            final String value = (String) rawValue;
            if (optionsForStderrHandler.containsKey(value)) {
                result = optionsForStderrHandler
                        .get(value)
                        .getFactory()
                        .create();
            } else {
                throw new ParseConfigurationException(
                        "Unsupported value '"
                                + value
                                + "' for key '" + key + "'");
            }
        } else {
            result = null;
        }
        return result;
    }

    /**
     *
     * @param jsonObject json object to search in
     * @return exit value handler
     * @throws ParseConfigurationException exception that is thrown if
     * the value for the exit value handler is not supported or have
     * a wrong type
     */
    private IExitValueHandler parseExitValueHandler(
            final JSONObject jsonObject)
            throws ParseConfigurationException {
        final IExitValueHandler result;
        final String key = ExitValueHandlerOption.getHandler();
        if (jsonObject.containsKey(key)) {
            final Object rawValue = jsonObject.get(key);
            if (!(rawValue instanceof  String)) {
                throw new ParseConfigurationException(
                        "Wrong type for key '" + key + "', expected a String");
            }
            final String value = (String) rawValue;
            if (optionsForExitValueHandler.containsKey(value)) {
                result = optionsForExitValueHandler
                        .get(value)
                        .getFactory()
                        .create();
            } else {
                throw new ParseConfigurationException(
                        "Unsupported value '"
                                + value
                                + "' for key '" + key + "'");
            }
        } else {
            result = null;
        }

        return result;
    }

    /**
     *
     * @param jsonObject json object to search in
     * @return stdout handler
     * @throws ParseConfigurationException exception that is thrown if the
     * value for the stdout handler is not not supported or
     * the type of the value is wrong
     */
    private IStdoutHandler parseStdoutHandler(
            final JSONObject jsonObject)
            throws ParseConfigurationException {
        final IStdoutHandler result;
        final String key = StdoutHandlerOption.getHandler();
        if (jsonObject.containsKey(key)) {
            final Object rawValue = jsonObject.get(key);
            if (!(rawValue instanceof String)) {
                throw new ParseConfigurationException(
                        "Wrong type for key '" + key + "', expected a String");
            }
            final String value = (String) rawValue;
            if (optionsForStdoutHandler.containsKey(value)) {
                result = optionsForStdoutHandler
                        .get(value)
                        .getFactory()
                        .create();
            } else {
                throw new ParseConfigurationException(
                        "Unsupported value '"
                                + value
                                + "' for key '" + key + "'");
            }
        } else {
            result = null;
        }
        return result;
    }


}
