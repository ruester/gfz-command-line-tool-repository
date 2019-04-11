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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.impl.ConfigurationImpl;
import org.n52.gfz.riesgos.configuration.parse.IParseConfiguration;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation that parses a json configuration.
 */
public class ParseJsonConfigurationImpl implements IParseConfiguration {

    /**
     * Parses the configuration
     * @param inputText input text with a json object
     * @return IConfiguratation
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
                final String imageId = getStringEntry(json, "imageId");
                final String workingDirectory = getStringEntry(json, "workingDirectory");

                // command to execute must use a list
                // -> use some class from the ant library
                // they know how to split it into several parts
                final String rawCommandToExecute = getStringEntry(json, "commandToExecute");
                final List<String> commandToExecute = Arrays.asList(Commandline.translateCommandline(rawCommandToExecute));

                return new ConfigurationImpl.Builder(identifier, imageId, workingDirectory, commandToExecute)
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
}
