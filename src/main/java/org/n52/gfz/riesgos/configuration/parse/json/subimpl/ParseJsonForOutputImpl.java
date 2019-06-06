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

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sub implementation for parsing a single input input.
 */
public class ParseJsonForOutputImpl {

    /**
     * Constant with the field attribute for title.
     */
    private static final String TITLE = "title";
    /**
     * Constant with the field attribute for abstract.
     */
    private static final String ABSTRACT = "abstract";
    /**
     * Constant with the field attribute for readFrom.
     */
    private static final String READ_FROM = "readFrom";
    /**
     * Constant with the field attribute for type.
     */
    private static final String TYPE = "type";
    /**
     * Constant with the field attribute for schema.
     */
    private static final String SCHEMA = "schema";
    /**
     * Constant with the field attribute for path.
     */
    private static final String PATH = "path";

    /**
     *
     * Map with FromStdoutOption-Enums by data type name.
     */
    private final Map<String, FromStdoutOption> optionsToReadFromStdout;
    /**
     *
     * Map with FromFilesOption-Enums by data type name.
     */
    private final Map<String, FromFilesOption> optionsToReadFromFiles;
    /**
     *
     * Map with FromStderrOption-Enums by data type name.
     */
    private final Map<String, FromStderrOption> optionsToReadFromStderr;
    /**
     *
     * Map with FromExitValueOption-Enums by data type name.
     */
    private final Map<String, FromExitValueOption> optionsToReadFromExitValue;

    /**
     * Default constructor.
     */
    public ParseJsonForOutputImpl() {

        this.optionsToReadFromStdout = getOptionsToReadFromStdout();
        this.optionsToReadFromFiles = getOptionsToReadFromFiles();
        this.optionsToReadFromStderr = getOptionsToReadFromStderr();
        this.optionsToReadFromExitValue = getOptionsToReadFromExitValue();
    }

    /**
     *
     * @return Map with FromStdoutOption-Enums by data type name
     */
    private Map<String, FromStdoutOption> getOptionsToReadFromStdout() {
        return Stream.of(FromStdoutOption.values()).collect(Collectors.toMap(
                FromStdoutOption::getDataType, Function.identity()
        ));
    }

    /**
     *
     * @return Map with FromFilesOption-Enums by data type name
     */
    private Map<String, FromFilesOption> getOptionsToReadFromFiles() {
        return Stream.of(FromFilesOption.values()).collect(Collectors.toMap(
                FromFilesOption::getDataType, Function.identity()
        ));
    }

    /**
     *
     * @return Map with FromStderrOption-Enums by data type name
     */
    private Map<String, FromStderrOption> getOptionsToReadFromStderr() {
        return Stream.of(FromStderrOption.values()).collect(Collectors.toMap(
                FromStderrOption::getDataType, Function.identity()
        ));
    }

    /**
     *
     * @return Map with FromExitValueOption-Enums by data type name
     */
    private Map<String, FromExitValueOption> getOptionsToReadFromExitValue() {
        return Stream.of(FromExitValueOption.values()).collect(Collectors.toMap(
                FromExitValueOption::getDataType, Function.identity()
        ));
    }

    /**
     * Parses the sub json object to an IdentifierWithBinding.
     * @param json sub json with the output parameter data
     * @return IIdentifierWithBinding with all the data for the output parameter
     * @throws ParseConfigurationException if a field is missing that is
     * necessary a ParseConfigurationException will be thrown
     */
    public IIdentifierWithBinding parseOutput(
            final JSONObject json)
            throws ParseConfigurationException {
        final String identifier = getString(json, TITLE);
        final Optional<String> optionalAbstract =
                getOptionalString(json, ABSTRACT);
        final String readFrom = getString(json, READ_FROM);
        final String type = getString(json, TYPE);

        final Optional<String> optionalSchema =
                getOptionalString(json, SCHEMA);

        if (FromStdoutOption.readFrom().equals(readFrom)) {
            if (optionsToReadFromStdout.containsKey(type)) {
                return optionsToReadFromStdout
                        .get(type)
                        .getFactory()
                        .create(
                            identifier,
                            optionalAbstract.orElse(null),
                            optionalSchema.orElse(null));

            } else {
                throw new ParseConfigurationException(
                        "Not supported type value");
            }
        } else if (FromStderrOption.readFrom().equals(readFrom)) {
            if (optionsToReadFromStderr.containsKey(type)) {
                return optionsToReadFromStderr
                        .get(type)
                        .getFactory()
                        .create(
                            identifier,
                                optionalAbstract.orElse(null));
            } else {
                throw new ParseConfigurationException(
                        "Not supported type value");
            }
        } else if (FromExitValueOption.readFrom().equals(readFrom)) {
            if (optionsToReadFromExitValue.containsKey(type)) {
                return optionsToReadFromExitValue
                        .get(type)
                        .getFactory()
                        .create(
                            identifier,
                                optionalAbstract.orElse(null));
            } else {
                throw new ParseConfigurationException(
                        "Not supported type value");
            }
        } else if (FromFilesOption.readFrom().equals(readFrom)) {

            final String path = getString(json, PATH);

            if (optionsToReadFromFiles.containsKey(type)) {
                return optionsToReadFromFiles
                        .get(type)
                        .getFactory()
                        .create(
                            identifier,
                                optionalAbstract.orElse(null),
                                path,
                                optionalSchema.orElse(null));
            } else {
                throw new ParseConfigurationException(
                        "Not supported type value");
            }
        } else {
            throw new ParseConfigurationException(
                    "Not supported readFrom value");
        }
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
    private String getString(
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
    private Optional<String> getOptionalString(
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
     * Interface for a factory to create the identifiers for
     * the supported types to read data from stdout.
     */
    @FunctionalInterface
    private interface IStdoutOutputFactory {
        /**
         * Factory method to create the identifier with the given data.
         * @param identifier identifier of the data
         * @param optionalAbstract optional description of the parameter
         * @param schema optional schema (for xml)
         * @return IIdentifierWithBinding
         */
        IIdentifierWithBinding create(
                String identifier,
                String optionalAbstract,
                String schema);
    }

    /**
     * Enums with the supported factories to read the types from stdout.
     */
    private enum FromStdoutOption {
        /**
         * This is the enum to read a string from stdout.
         */
        STRING("string",
                (identifier, optionalAbstract, schema) ->
                        IdentifierWithBindingFactory.createStdoutString(
                                identifier, optionalAbstract)),
        /**
         * This is the enum to read generic xml from stdout.
         */
        XML("xml", IdentifierWithBindingFactory::createStdoutXmlWithSchema),
        /**
         * This is the enum to read xml quakeml from stdout.
         */
        QUAKEML("quakeml",
                (identifier, optionalAbstract, schema) ->
                        IdentifierWithBindingFactory.createStdoutQuakeML(
                                identifier, optionalAbstract)),
        /**
         * This is the enum to read xml shakemaps from stdout.
         */
        SHAKEMAP("shakemap",
                (identifier, optionalAbstrat, schema) ->
                        IdentifierWithBindingFactory.createStdoutShakemap(
                                identifier, optionalAbstrat)),
        /**
         * This is the enum to read json from stdout.
         */
        JSON("json",
                (identifier, optionalAbstract, schema) ->
                        IdentifierWithBindingFactory.createStdoutJson(
                                identifier, optionalAbstract));

        /**
         * This is the constant of the readFrom value
         * for which we will use this enums.
         */
        private static final String STDOUT = "stdout";

        /**
         * String with the name of the data type to look it up.
         */
        private final String dataType;

        /**
         * Factory that is used to create the identifiers.
         */
        private final IStdoutOutputFactory factory;

        /**
         * This is default constructor for the enum.
         * @param aDataType string with the data type name
         * @param aFactory factory to crete the identifier with this type
         */
        FromStdoutOption(
                final String aDataType,
                final IStdoutOutputFactory aFactory) {
            this.dataType = aDataType;
            this.factory = aFactory;
        }

        /**
         *
         * @return name of the dataType
         */
        public String getDataType() {
            return dataType;
        }

        /**
         *
         * @return factory to create the identifier
         */
        public IStdoutOutputFactory getFactory() {
            return factory;
        }

        /**
         *
         * @return setting for readFrom that is supported
         * by this types
         */
        public static String readFrom() {
            return STDOUT;
        }
    }

    /**
     * Interface for a factory to create the identifiers for
     * the supported types to read data from stderr.
     */
    @FunctionalInterface
    private interface IStderrOutputFactory {
        /**
         * Factory method to create the identifier with the given data.
         * @param identifier identifier of the data
         * @param optionalAbstract optional description of the parameter
         * @return IIdentifierWithBinding
         */
        IIdentifierWithBinding create(
                String identifier,
                String optionalAbstract);
    }

    /**
     * Enums with the supported factories to read the types from stderr.
     */
    private enum FromStderrOption {
        /**
         * This is the enum to read a string from stderr.
         */
        STRING("string", IdentifierWithBindingFactory::createStderrString),
        /**
         * This is the enum to read json from stderr.
         */
        JSON("json", IdentifierWithBindingFactory::createStderrJson);

        /**
         * This is the constant of the readFrom value
         * for which we will use this enums.
         */
        private static final String STDERR = "stderr";

        /**
         * String with the name of the data type to look it up.
         */
        private final String dataType;

        /**
         * Factory that is used to create the identifiers.
         */
        private final IStderrOutputFactory factory;

        /**
         * This is default constructor for the enum.
         * @param aDataType string with the data type name
         * @param aFactory factory to crete the identifier with this type
         */
        FromStderrOption(
                final String aDataType,
                final IStderrOutputFactory aFactory) {
            this.dataType = aDataType;
            this.factory = aFactory;
        }

        /**
         *
         * @return name of the dataType
         */
        public String getDataType() {
            return dataType;
        }

        /**
         *
         * @return factory to create the identifier
         */
        public IStderrOutputFactory getFactory() {
            return factory;
        }

        /**
         *
         * @return setting for readFrom that is supported
         * by this types
         */
        public static String readFrom() {
            return STDERR;
        }
    }

    /**
     * Interface for a factory to create the identifiers for
     * the supported types to read data from the exit value.
     */
    @FunctionalInterface
    private interface IExitValueOutputFactory {
        /**
         * Factory method to create the identifier with the given data.
         * @param identifier identifier of the data
         * @param optionalAbstract optional description of the parameter
         * @return IIdentifierWithBinding
         */
        IIdentifierWithBinding create(
                String identifier,
                String optionalAbstract);
    }

    /**
     * Enums with the supported factories to read the types from the exit value.
     */
    private enum FromExitValueOption {
        /**
         * This is the enum to read ints from the exit value.
         */
        INT("int", IdentifierWithBindingFactory::createExitValueInt);

        /**
         * This is the constant of the readFrom value
         * for which we will use this enums.
         */
        private static final String EXIT_VALUE = "exitValue";

        /**
         * String with the name of the data type to look it up.
         */
        private final String dataType;

        /**
         * Factory that is used to create the identifiers.
         */
        private final IExitValueOutputFactory factory;

        /**
         * This is default constructor for the enum.
         * @param aDataType string with the data type name
         * @param aFactory factory to crete the identifier with this type
         */
        FromExitValueOption(
                final String aDataType,
                final IExitValueOutputFactory aFactory) {
            this.dataType = aDataType;
            this.factory = aFactory;
        }

        /**
         *
         * @return name of the dataType
         */
        public String getDataType() {
            return dataType;
        }

        /**
         *
         * @return factory to create the identifier
         */
        public IExitValueOutputFactory getFactory() {
            return factory;
        }

        /**
         *
         * @return setting for readFrom that is supported
         * by this types
         */
        public static String readFrom() {
            return EXIT_VALUE;
        }
    }

    /**
     * Interface for a factory to create the identifiers for
     * the supported types to read data from files.
     */
    @FunctionalInterface
    private interface IFileOutputFactory {
        /**
         * Factory methode to create the identifier with the given data.
         * @param identifier identifier of the data
         * @param optionalAbstract optional description of the parameter
         * @param path path to the file
         * @param schema optional schema
         * @return IIdentifierWithBinding
         */
        IIdentifierWithBinding create(
                String identifier,
                String optionalAbstract,
                String path,
                String schema);
    }

    /**
     * Enums with the supported factories to read the types from files.
     */
    private enum FromFilesOption {

        /**
         * This is the enum to read xml from files.
         */
        XML("xml", IdentifierWithBindingFactory::createFileOutXmlWithSchema),

        /**
         * This is the enum to read generic files.
         */
        FILE("file",
                (identifier, optionalAbstract, path, schema) ->
                        IdentifierWithBindingFactory.createFileOutGeneric(
                                identifier, optionalAbstract, path)),
        /**
         * This is the enum to read geojson from files.
         */
        GEOJSON("geojson",
                (identifier, optionalAbstract, path, schema) ->
                        IdentifierWithBindingFactory.createFileOutGeojson(
                                identifier, optionalAbstract, path)),
        /**
         * This is the enum to read geotiff rasters from files.
         */
        GEOTIFF("geotiff",
                (identifier, optionalAbstract, path, schema) ->
                        IdentifierWithBindingFactory.createFileOutGeotiff(
                                identifier, optionalAbstract, path)),
        /**
         * This is the enum to read shapefiles from files.
         */
        SHP("shapefile",
                (identifier, optionalAbstract, path, schema) ->
                        IdentifierWithBindingFactory.createFileOutShapeFile(
                                identifier, optionalAbstract, path)),
        /**
         * This is the enum to read xml quakeml from files.
         */
        QUAKEML("quakeml",
                (identifier, optionalAbstract, path, schema) ->
                        IdentifierWithBindingFactory.createFileOutQuakeMLFile(
                                identifier, optionalAbstract, path)),
        /**
         * This is the enum to read xml shakemap from files.
         */
        SHAKEMAP("shakemap",
                (identifier, optionalAbstract, path, schema) ->
                        IdentifierWithBindingFactory.createFileOutShakemap(
                                identifier, optionalAbstract, path)),
        /**
         * This is the enum to read json from files.
         */
        JSON("json",
                (identifier, optionalAbstract, path, schema) ->
                        IdentifierWithBindingFactory.createFileOutJson(
                                identifier, optionalAbstract, path));

        /**
         * This is the constant of the readFrom value
         * for which we will use this enums.
         */
        private static final String READ_FROM_FILE = "file";

        /**
         * String with the name of the data type to look it up.
         */
        private final String dataType;

        /**
         * Factory that is used to create the identifiers.
         */
        private final IFileOutputFactory factory;

        /**
         * This is default constructor for the enum.
         * @param aDataType string with the data type name
         * @param aFactory factory to crete the identifier with this type
         */
        FromFilesOption(
                final String aDataType,
                final IFileOutputFactory aFactory) {
            this.dataType = aDataType;
            this.factory = aFactory;
        }

        /**
         *
         * @return name of the dataType
         */
        public String getDataType() {
            return dataType;
        }

        /**
         *
         * @return factory to create the identifier
         */
        public IFileOutputFactory getFactory() {
            return factory;
        }

        /**
         *
         * @return setting for readFrom that is supported
         * by this types
         */
        public static String readFrom() {
            return READ_FROM_FILE;
        }
    }
}
