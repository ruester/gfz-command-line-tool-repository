package org.n52.gfz.riesgos.configuration.parse.json.subimpl;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractParseJsonForInAndOutput {

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
     * @throws ParseConfigurationException
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

    /**
     * Function to check if a string has a value and is not empty.
     * @param str string to check
     * @return true if there is text inside of the string
     */
    protected static boolean strHasValue(final String str) {
        return str != null && (!str.isEmpty());
    }

    /**
     * Function to check if a string is null or is empty.
     * @param str string to check
     * @return true if there is no text inside of the string
     * (or it is null)
     */
    protected static boolean strHasNoValue(final String str) {
        return !strHasValue(str);
    }


    /**
     * Function to check if a list has entries.
     * @param list list to check
     * @return true if there is a value inside of the list
     */
    protected static boolean listHasValue(final List<String> list) {
        return list != null && (!list.isEmpty());
    }

    /**
     * Function to check if a list is null or empty.
     * @param list list to check
     * @return true if the list is null or empty
     */
    protected static boolean listHasNoValues(final List<String> list) {
        return !listHasValue(list);
    }

}
