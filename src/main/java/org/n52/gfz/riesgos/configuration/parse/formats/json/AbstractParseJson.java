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

package org.n52.gfz.riesgos.configuration.parse.formats.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Abstract class for parsing json.
 */
public abstract class AbstractParseJson {

    /**
     * Constant with the field attribute for title.
     */
    protected static final String TITLE = "title";

    /**
     * Constant with the field attribute for abstract.
     */
    protected static final String ABSTRACT = "abstract";

    /**
     * Constant with the field attribute for type.
     */
    protected static final String TYPE = "type";

    /**
     * Constant with the field attribute for schema.
     */
    protected static final String SCHEMA = "schema";

    /**
     * Constant with the field attribute for path.
     */
    protected static final String PATH = "path";

    /**
     * Constant with the field attribute for optional.
     */
    protected static final String OPTIONAL = "optional";

    /**
     * Constant with the field attribute for default format.
     */
    protected static final String DEFAULT_FORMAT = "defaultFormat";

    /**
     * Searches for the key in the json object.
     * If the key is not there it returns an empty optional.
     * If the key is there but no string, than it throws an exception.
     * If the key is there and a string it returns the filled optional with the
     * value.
     * @param json json object that may contain the key
     * @param key field to search for
     * @return Optional with the string value
     * @throws ParseConfigurationException exception that is thrown if the key
     * is there but the value is not of type string
     */
    protected Optional<String> getOptionalString(
            final JSONObject json,
            final String key)
            throws ParseConfigurationException {
        final Optional<String> result;
        if (json.containsKey(key)) {
            final Object rawValue = json.get(key);
            if (!(rawValue instanceof String)) {
                throw new ParseConfigurationException(
                        "Wrong type for element '"
                                + key
                                + "', expected a String");
            }
            result = Optional.of((String) rawValue);
        } else {
            result = Optional.empty();
        }
        return result;
    }

    /**
     * Searches in the json object for a string.
     * @param json json object to search in
     * @param key key to search for
     * @return value of the key if it is of type string
     * @throws ParseConfigurationException exception that is thrown
     * if the key is not in the json object or the value of the key is not
     * a string
     */
    protected String getString(
            final JSONObject json,
            final String key)
            throws ParseConfigurationException {
        if (!json.containsKey(key)) {
            throw new ParseConfigurationException(
                    "Missing element '" + key + "'");
        }
        final Object rawValue = json.get(key);
        if (!(rawValue instanceof String)) {
            throw new ParseConfigurationException(
                    "Wrong type for element '" + key + "', expected a String");
        }
        return (String) rawValue;
    }

    /**
     * Searches for the key in the json object.
     * If the key is not there it returns the default value.
     * If the key is there but the value is not a boolean it throws
     * an exception.
     * If the key is there and a boolean than it returns the value.
     * @param json json object that may contain the the key
     * @param key key to search for
     * @param defaultValue value if the key is not in the json object
     * @return value of the key if in the json object else default value
     * @throws ParseConfigurationException exception that will be thrown if the
     * value in the json is not a boolean
     */
    protected boolean getOptionalBoolean(
            final JSONObject json,
            final String key,
            final boolean defaultValue)
            throws ParseConfigurationException {
        final boolean value;

        if (json.containsKey(key)) {
            final Object rawValue = json.get(key);
            if (!(rawValue instanceof Boolean)) {
                throw new ParseConfigurationException(
                        "Wrong type for element '"
                                + key
                                + "', expected a Boolean");

            }
            value = (Boolean) rawValue;
        } else {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Searches for an optional list of strings in the given json object.
     * @param json json object that may contain the key
     * @param key field to search for
     * @return optional value of the list for the given key
     * @throws ParseConfigurationException exception that is thrown if the
     * key is there but the value is not a list of strings.
     */
    protected Optional<List<String>> getOptionalListOfStrings(
            final JSONObject json,
            final String key)
            throws ParseConfigurationException {
        final Optional<List<String>> result;
        if (json.containsKey(key)) {
            final Object rawValue = json.get(key);
            if (!(rawValue instanceof JSONArray)) {
                throw new ParseConfigurationException(
                        "Wrong type for element '"
                                + key
                                + "', expected a JSON array");
            }
            final List<String> list = new ArrayList<>();
            for (final Object element : (JSONArray) rawValue) {
                if (element instanceof String) {
                    list.add((String) element);
                } else if (
                        element instanceof Double
                                || element instanceof Integer) {
                    list.add(String.valueOf(element));
                } else {
                    throw new ParseConfigurationException(
                            "Wrong type for element in '"
                                    + key
                                    + "', expected a String");
                }
            }
            result = Optional.of(list);
        } else {
            result = Optional.empty();
        }
        return result;
    }
}
