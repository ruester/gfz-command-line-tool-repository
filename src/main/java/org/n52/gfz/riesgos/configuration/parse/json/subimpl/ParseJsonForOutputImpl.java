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
public class ParseJsonForOutputImpl extends AbstractParseJsonForInAndOutput {

    /**
     * Constant with the field attribute for readFrom.
     */
    private static final String READ_FROM = "readFrom";

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

        final boolean isOptional = getOptionalBoolean(json, OPTIONAL, false);

        if (FromStdoutOption.readFrom().equals(readFrom)) {
            if (optionsToReadFromStdout.containsKey(type)) {
                return optionsToReadFromStdout
                        .get(type)
                        .getFactory()
                        .create(
                            identifier,
                            optionalAbstract.orElse(null),
                            optionalSchema.orElse(null),
                            isOptional);

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
                                optionalAbstract.orElse(null),
                                isOptional);
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
                                optionalAbstract.orElse(null),
                                isOptional);
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
                                optionalSchema.orElse(null),
                                isOptional);
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
                String schema,
                boolean isOptional);
    }

    /**
     * Enums with the supported factories to read the types from stdout.
     */
    private enum FromStdoutOption {
        /**
         * This is the enum to read a string from stdout.
         */
        STRING("string",
                (identifier, optionalAbstract, schema, isOptional) ->
                        IdentifierWithBindingFactory.createStdoutString(
                                identifier, optionalAbstract, isOptional)),
        /**
         * This is the enum to read generic xml from stdout.
         */
        XML("xml", IdentifierWithBindingFactory::createStdoutXmlWithSchema),
        /**
         * This is the enum to read xml quakeml from stdout.
         */
        QUAKEML("quakeml",
                (identifier, optionalAbstract, schema, isOptional) ->
                        IdentifierWithBindingFactory.createStdoutQuakeML(
                                identifier, optionalAbstract, isOptional)),
        /**
         * This is the enum to read xml shakemaps from stdout.
         */
        SHAKEMAP("shakemap",
                (identifier, optionalAbstract, schema, isOptional) ->
                        IdentifierWithBindingFactory.createStdoutShakemap(
                                identifier, optionalAbstract, isOptional)),
        /**
         * This is the enum to read json from stdout.
         */
        JSON("json",
                (identifier, optionalAbstract, schema, isOptional) ->
                        IdentifierWithBindingFactory.createStdoutJson(
                                identifier, optionalAbstract, isOptional));

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
         * @param aFactory factory to create the identifier with this type
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
                String optionalAbstract,
                boolean isOptional);
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
         * @param aFactory factory to create the identifier with this type
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
                String optionalAbstract,
                boolean isOptional);
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
         * @param aFactory factory to create the identifier with this type
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
                String schema,
                boolean isOptional);
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
                (identifier, optionalAbstract, path, schema, isOptional) ->
                        IdentifierWithBindingFactory.createFileOutGeneric(
                                identifier, optionalAbstract, path, isOptional)),
        /**
         * This is the enum to read geojson from files.
         */
        GEOJSON("geojson",
                (identifier, optionalAbstract, path, schema, isOptional) ->
                        IdentifierWithBindingFactory.createFileOutGeojson(
                                identifier, optionalAbstract, path, isOptional)),
        /**
         * This is the enum to read geotiff rasters from files.
         */
        GEOTIFF("geotiff",
                (identifier, optionalAbstract, path, schema, isOptional) ->
                        IdentifierWithBindingFactory.createFileOutGeotiff(
                                identifier, optionalAbstract, path, isOptional)),
        /**
         * This is the enum to read shapefiles from files.
         */
        SHP("shapefile",
                (identifier, optionalAbstract, path, schema, isOptional) ->
                        IdentifierWithBindingFactory.createFileOutShapeFile(
                                identifier, optionalAbstract, path, isOptional)),
        /**
         * This is the enum to read xml quakeml from files.
         */
        QUAKEML("quakeml",
                (identifier, optionalAbstract, path, schema, isOptional) ->
                        IdentifierWithBindingFactory.createFileOutQuakeMLFile(
                                identifier, optionalAbstract, path, isOptional)),
        /**
         * This is the enum to read xml shakemap from files.
         */
        SHAKEMAP("shakemap",
                (identifier, optionalAbstract, path, schema, isOptional) ->
                        IdentifierWithBindingFactory.createFileOutShakemap(
                                identifier, optionalAbstract, path, isOptional)),
        /**
         * This is the enum to read json from files.
         */
        JSON("json",
                (identifier, optionalAbstract, path, schema, isOptional) ->
                        IdentifierWithBindingFactory.createFileOutJson(
                                identifier, optionalAbstract, path, isOptional));

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
         * @param aFactory factory to create the identifier with this type
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
