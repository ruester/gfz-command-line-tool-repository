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
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.InputParameterFactory;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sub implementation for parsing a single input input.
 */
public class ParseJsonForInputImpl extends AbstractParseJsonForInAndOutput {



    /**
     * Constant with the field attribute for useAs.
     */
    private static final String USE_AS = "useAs";

    /**
     * Constant with the field attribute for comamndLineFlag.
     */
    private static final String COMMNAD_LINE_FLAG = "commandLineFlag";

    /**
     * Constant with the field attribute for default.
     */
    private static final String DEFAULT = "default";

    /**
     * Constant with the field attribute for allowed.
     */
    private static final String ALLOWED = "allowed";

    /**
     * Constant with the field attribute for crs.
     */
    private static final String CRS = "crs";

    /**
     * Map with ToCommandLineArgumentOption by data type name.
     */
    private final Map<String, ToCommandLineArgumentOption>
            optionsToUseAsCommandLineArgument;
    /**
     * Map with ToStdinOption by data type name.
     */
    private final Map<String, ToStdinInputOption>
            optionsToUseAsStdinInput;
    /**
     * Map with ToFileInputOption by data type name.
     */
    private final Map<String, ToFileInputOption>
            optionsToUseAsFileInput;

    /**
     * Default constructor.
     */
    public ParseJsonForInputImpl() {
        optionsToUseAsCommandLineArgument =
                getOptionsToUseAsCommandLineArgument();
        optionsToUseAsStdinInput =
                getOptionsToUseAsStdinInput();
        optionsToUseAsFileInput =
                getOptionsToUseAsFileInput();
    }

    /**
     *
     * @return Map with ToCommandLineArgumentOption-Enums by data type name
     */
    private Map<String, ToCommandLineArgumentOption>
            getOptionsToUseAsCommandLineArgument() {
        return Stream.of(ToCommandLineArgumentOption.values())
                .collect(
                        Collectors.toMap(
                                ToCommandLineArgumentOption::getDataType,
                                Function.identity()
        ));
    }

    /**
     *
     * @return Map with ToStdinInputOption-Enums by data type name
     */
    private Map<String, ToStdinInputOption> getOptionsToUseAsStdinInput() {
        return Stream.of(ToStdinInputOption.values()).collect(Collectors.toMap(
                ToStdinInputOption::getDataType, Function.identity()
        ));
    }

    /**
     *
     * @return Map with ToFileInputOption-Enums by data type name
     */
    private Map<String, ToFileInputOption> getOptionsToUseAsFileInput() {
        return Stream.of(ToFileInputOption.values()).collect(Collectors.toMap(
                ToFileInputOption::getDataType, Function.identity()
        ));
    }

    /**
     * Parses the sub json object to an IdentifierWithBinding.
     * @param json sub json with the input parameter data
     * @return IIdentifierWithBinding with all the data for the input parameter
     * @throws ParseConfigurationException if a field is missing that is
     * necessary a ParseConfigurationException will be thrown
     */
    public IInputParameter parseInput(
            final JSONObject json)
            throws ParseConfigurationException {
        final String identifier = getString(json, TITLE);
        final Optional<String> optionalAbstract =
                getOptionalString(json, ABSTRACT);
        final String useAs = getString(json, USE_AS);
        final String type = getString(json, TYPE);
        final boolean isOptional = getOptionalBoolean(json, OPTIONAL, false);

        final Optional<String> optionalDefaultCommandLineFlag =
                getOptionalString(json, COMMNAD_LINE_FLAG);
        final Optional<String> optionalDefaultValue =
                getOptionalString(json, DEFAULT);
        final Optional<List<String>> optionalAllowedValues =
                getOptionalListOfStrings(json, ALLOWED);
        final Optional<List<String>> optionalSupportedCrs =
                getOptionalListOfStrings(json, CRS);
        final Optional<String> optionalSchema = getOptionalString(json, SCHEMA);

        if (ToCommandLineArgumentOption.useAs().equals(useAs)) {
            if (optionsToUseAsCommandLineArgument.containsKey(type)) {
                return optionsToUseAsCommandLineArgument
                        .get(type)
                        .getFactory()
                        .create(
                            identifier,
                            isOptional,
                            optionalAbstract.orElse(null),
                            optionalDefaultCommandLineFlag.orElse(null),
                            optionalDefaultValue.orElse(null),
                            optionalAllowedValues.orElse(null),
                            optionalSupportedCrs.orElse(null),
                            optionalSchema.orElse(null));
            } else {
                throw new ParseConfigurationException(
                        "Not supported type value '" + type + "'");
            }
        } else if (ToStdinInputOption.useAs().equals(useAs)) {
            if (optionsToUseAsStdinInput.containsKey(type)) {
                return optionsToUseAsStdinInput
                        .get(type)
                        .getFactory()
                        .create(
                            identifier,
                            isOptional,
                            optionalAbstract.orElse(null),
                            optionalDefaultValue.orElse(null),
                            optionalAllowedValues.orElse(null),
                            optionalSchema.orElse(null));
            } else {
                throw new ParseConfigurationException(
                        "Not supported type value '" + type + "'");
            }
        } else if (ToFileInputOption.useAs().equals(useAs)) {
            if (optionsToUseAsFileInput.containsKey(type)) {

                final String path = getString(json, PATH);

                return optionsToUseAsFileInput.get(type).getFactory().create(
                        identifier,
                        isOptional,
                        optionalAbstract.orElse(null),
                        path,
                        optionalSchema.orElse(null)
                );
            } else {
                throw new ParseConfigurationException(
                        "Not supported type value '" + type + "'");
            }
        } else {
            throw new ParseConfigurationException(
                    "Not supported useAs value: '" + useAs + "'");
        }
    }


    /**
     * Interface for a factory to create the identifiers for
     * the supported types that will be used as a command line argument.
     */
    @FunctionalInterface
    private interface IAsCommandLineArgumentFactory {
        /**
         * Factory method to create the identifier with the given data.
         * Not all implementations support app of this arguments.
         *
         * @param identifier identifier of the data
         * @param optionalAbstract optional description of the parameter
         * @param defaultCommandLineFlag optional default command line flag
         * @param defaultValue optional default value
         * @param allowedValues optional list with allowed values
         * @param supportedCrs optional list with supported crs
         * @param schema optional schema
         * @return IIdentifierWithBinding
         * @throws ParseConfigurationException exception that will be thrown
         * if an unsupported argument is given to the implementation.
         */
        IInputParameter create(
                String identifier,
                final boolean isOptional,
                String optionalAbstract,
                String defaultCommandLineFlag,
                String defaultValue,
                List<String> allowedValues,
                List<String> supportedCrs,
                String schema)
                throws ParseConfigurationException;
    }


    /**
     * Function to create a command line argument with an int.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createCommandLineArgumentInt(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if (listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for int types");
        }
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for int types");
        }
        return InputParameterFactory.INSTANCE.createCommandLineArgumentInt(
                identifier,
                isOptional, optionalAbstract,
                flag,
                defaultValue,
                allowedValues
        );
    }

    /**
     * Function to create a command line argument with a double.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createCommandLineArgumentDouble(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if (listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for double types");
        }
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for double types");
        }
        return InputParameterFactory.INSTANCE.createCommandLineArgumentDouble(
                identifier,
                isOptional, optionalAbstract,
                flag,
                defaultValue,
                allowedValues
        );
    }

    /**
     * Function to create a command line argument with a string.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createCommandLineArgumentString(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if (listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for string types");
        }
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for string types");
        }
        return InputParameterFactory.INSTANCE.createCommandLineArgumentString(
                identifier,
                isOptional, optionalAbstract,
                flag,
                defaultValue,
                allowedValues
        );
    }

    /**
     * Function to create a command line argument with boolean.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createCommandLineArgumentBoolean(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if (listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for boolean types");
        }
        if (listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowed values are not supported for booleans");
        }
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for booleans");
        }
        if (strHasNoValue(flag)) {
            throw new ParseConfigurationException(
                    "flag is necessary for boolean type");
        }
        return InputParameterFactory.INSTANCE.createCommandLineArgumentBoolean(
                identifier,
                isOptional, optionalAbstract,
                flag,
                defaultValue
        );
    }

    /**
     * Function to create a command line argument with a bounding box.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createCommandLineArgumentBBox(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {

        if (strHasValue(flag)) {
            throw new ParseConfigurationException(
                    "commandLineFlag is not supported for bbox");
        }

        if (strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "default is not supported for bbox");
        }

        if (listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowed values are not supported for bbox");
        }

        if (listHasNoValues(supportedCrs)) {
            throw new ParseConfigurationException(
                    "The element 'crs' for is necessary for bbox");
        }

        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for bbox");
        }

        return InputParameterFactory.INSTANCE.createCommandLineArgumentBBox(
                identifier,
                isOptional, optionalAbstract,
                supportedCrs
        );
    }

    /**
     * Function to create a command line argument with a xml file.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createCommandLineArgumentXmlFile(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "default is not supported for xml");
        }
        if (listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowed values are not supported for xml");
        }
        if (listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for xml");
        }
        return InputParameterFactory
                .INSTANCE
                .createCommandLineArgumentXmlFileWithSchema(
                        identifier,
                        isOptional, optionalAbstract,
                        schema,
                        flag
                );
    }


    /**
     * Function to create a command line argument with a quakeml file.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createCommandLineArgumentQuakeML(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            @SuppressWarnings({"unused"})
            final String schema) throws ParseConfigurationException {
        if (strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "default is not supported for quakeml");
        }
        if (listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowed values are not supported for quakemml");
        }
        if (listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for quakeml");
        }
        // ignore schema
        return InputParameterFactory.INSTANCE.createCommandLineArgumentQuakeML(
                identifier,
                isOptional, optionalAbstract,
                flag
        );
    }

    /**
     * Function to create a command line argument with a geotiff file.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createCommandLineArgumentGeotiffFile(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "default is not supported for geotiff");
        }
        if (listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowed values are not supported for geotiff");
        }
        if (listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for geotiff");
        }
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for geotiff");
        }
        return InputParameterFactory.INSTANCE.createCommandLineArgumentGeotiff(
                identifier,
                isOptional, optionalAbstract,
                flag
        );
    }

    /**
     * Function to create a command line argument with a geojson file.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createCommandLineArgumentGeojsonFile(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "default is not supported for geojson");
        }
        if (listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowed values are not supported for geojson");
        }
        if (listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for geojson");
        }
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for geojson");
        }
        return InputParameterFactory.INSTANCE.createCommandLineArgumentGeojson(
                identifier,
                isOptional, optionalAbstract,
                flag
        );
    }

    /**
     * Function to create a command line argument with a shapefile.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createCommandLineArgumentShapefile(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "default is not supported for shapefile");
        }
        if (listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowed values are not supported for shapefile");
        }
        if (listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for shapefile");
        }
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for shapefile");
        }
        return InputParameterFactory.INSTANCE.createCommandLineArgumentShapeFile(
                identifier,
                isOptional, optionalAbstract,
                flag
        );
    }

    /**
     * Function to create a command line argument with a generic file.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createCommandLineArgumentGenericFile(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "default is not supported for file");
        }
        if (listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowed values are not supported for file");
        }
        if (listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for file");
        }
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for file");
        }
        return InputParameterFactory.INSTANCE.createCommandLineArgumentFile(
                identifier,
                isOptional, optionalAbstract,
                flag
        );
    }

    /**
     * Function to create a command line argument with a json file.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param supportedCrs optional list with supported crs
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createCommandLineArgumentJson(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "default is not supported for json");
        }
        if (listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowed values are not supported for json");
        }
        if (listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(
                    "crs are not supported for json");
        }
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for json");
        }
        return InputParameterFactory.INSTANCE.createCommandLineArgumentJson(
                identifier,
                isOptional, optionalAbstract,
                flag
        );
    }

    /**
     * Enums with the supported factories to use data as command line arguments.
     */
    private enum ToCommandLineArgumentOption {
        /**
         * This is the enum to use an int as command line argument.
         */
        INT("int",
                ParseJsonForInputImpl::createCommandLineArgumentInt),
        /**
         * This is the enum to use a double as command line argument.
         */
        DOUBLE("double",
                ParseJsonForInputImpl::createCommandLineArgumentDouble),
        /**
         * This is the enum to use a boolean as command line argument.
         */
        BOOLEAN("boolean",
                ParseJsonForInputImpl::createCommandLineArgumentBoolean),
        /**
         * This is the enum to use a stringas command line argument.
         */
        STRING("string",
                ParseJsonForInputImpl::createCommandLineArgumentString),
        /**
         * This is the enum to use a bounding box as command line argument.
         */
        BBOX("bbox",
                ParseJsonForInputImpl::createCommandLineArgumentBBox),
        /**
         * This is the enum to use an xml file as command line argument.
         */
        XML("xml",
                ParseJsonForInputImpl::createCommandLineArgumentXmlFile),
        /**
         * This is the enum to use a geotiff file as command line argument.
         */
        GEOFITT("geotiff",
                ParseJsonForInputImpl::createCommandLineArgumentGeotiffFile),
        /**
         * This is the enum to use a geojson file as command line argument.
         */
        GEOJSON("geojson",
                ParseJsonForInputImpl::createCommandLineArgumentGeojsonFile),
        /**
         * This is the enum to use a shapefile as command line argument.
         */
        SHAPEFILE("shapefile",
                ParseJsonForInputImpl::createCommandLineArgumentShapefile),
        /**
         * This is the enum to use a generic file as command line argument.
         */
        GENERIC_FILE("file",
                ParseJsonForInputImpl::createCommandLineArgumentGenericFile),
        /**
         * This is the enum to use a quakeml file as command line argument.
         */
        QUAKEML("quakeml",
                ParseJsonForInputImpl::createCommandLineArgumentQuakeML),
        /**
         * This is the enum to use a json file as command line argument.
         */
        JSON("json",
                ParseJsonForInputImpl::createCommandLineArgumentJson);

        /**
         * This is the constant of the useAs value
         * for which we will use this enums.
         */
        private static final String USE_AS_CMD = "commandLineArgument";

        /**
         * String with the name of the data type to look it up.
         */
        private final String dataType;
        /**
         * Factory that is used to create the identifiers.
         */
        private final IAsCommandLineArgumentFactory factory;

        /**
         * This is the default constructor for the enum.
         * @param aDataType string with the data type name
         * @param aFactory factory to create the identifier with this type
         */
        ToCommandLineArgumentOption(
                final String aDataType,
                final IAsCommandLineArgumentFactory aFactory) {
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
        public IAsCommandLineArgumentFactory getFactory() {
            return factory;
        }

        /**
         *
         * @return setting for useAs that is supported
         * by this types
         */
        public static String useAs() {
            return USE_AS_CMD;
        }
    }

    /**
     * Interface for a factory to create the identifiers for
     * the supported types that will be used as text for stdin.
     */
    @FunctionalInterface
    private interface IAsStdinInputFactory {
        /**
         * Factory method to create the identifier with the given data.
         * Not all implementations support app of this arguments.
         *
         * @param identifier identifier of the data
         * @param optionalAbstract optional description of the parameter
         * @param defaultValue optional default value
         * @param allowedValues optional list with allowed values
         * @param schema optional schema
         * @return IIdentifierWithBinding
         * @throws ParseConfigurationException exception that will be thrown
         * if an unsupported argument is given to the implementation.
         */
        IInputParameter create(
                String identifier,
                boolean isOptional,
                String optionalAbstract,
                String defaultValue,
                List<String> allowedValues,
                String schema)
                throws ParseConfigurationException;
    }

    /**
     * Function to create a stdin input with a string.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createStdinString(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String defaultValue,
            final List<String> allowedValues,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for string");
        }
        return InputParameterFactory.INSTANCE.createStdinString(
                identifier,
                isOptional, optionalAbstract,
                defaultValue,
                allowedValues
        );
    }

    /**
     * Function to create a stdin input with a json.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param defaultValue optional default value
     * @param allowedValues optional allowed values
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createStdinJson(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String defaultValue,
            final List<String> allowedValues,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for json");
        }
        if (strHasValue(defaultValue)) {
            throw new ParseConfigurationException(
                    "defaultValue is not supported for json");
        }
        if (listHasValue(allowedValues)) {
            throw new ParseConfigurationException(
                    "allowedValues are not supported for json");
        }
        return InputParameterFactory.INSTANCE.createStdinJson(
                identifier,
                isOptional, optionalAbstract
        );
    }


    /**
     * Enums with the supported factories to use data as stdin input.
     */
    private enum ToStdinInputOption {
        /**
         * This is the enum to use the value as string on stdin.
         */
        STRING("string", ParseJsonForInputImpl::createStdinString),
        /**
         * This is the enum to use the value as json on stdin.
         */
        JSON("json", ParseJsonForInputImpl::createStdinJson);

        /**
         * This is the constant of the useAs value
         * for which we will use this enums.
         */
        private static final String USE_AS_STDIN = "stdin";

        /**
         * String with the name of the data type to look it up.
         */
        private final String dataType;

        /**
         * Factory that is used to create the identifiers.
         */
        private final IAsStdinInputFactory factory;

        /**
         * This is default constructor for the enum.
         * @param aDataType string with the data type name
         * @param aFactory factory to create the identifier with this type
         */
        ToStdinInputOption(
                final String aDataType,
                final IAsStdinInputFactory aFactory) {
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
        public IAsStdinInputFactory getFactory() {
            return factory;
        }

        /**
         *
         * @return setting for useAs that is supported
         * by this types
         */
        public static String useAs() {
            return USE_AS_STDIN;
        }
    }

    /**
     * Interface for a factory to create the identifiers for
     * the supported types that will be used as input files.
     */
    @FunctionalInterface
    private interface IAsFileInputFactory {
        /**
         * Factory method to create the identifier with the given data.
         * Not all implementations support app of this arguments.
         *
         * @param identifier identifier of the data
         * @param optionalAbstract optional description of the parameter
         * @param path path to the file
         * @param schema optional schema
         * @return IIdentifierWithBinding
         * @throws ParseConfigurationException exception that will be thrown
         * if an unsupported argument is given to the implementation.
         */
        IInputParameter create(
                String identifier,
                boolean isOptional,
                String optionalAbstract,
                String path,
                String schema)
                throws ParseConfigurationException;
    }

    /**
     * Function to create a file input with a geotiff.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path to the file
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createFileInputGeotiff(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String path,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for geotiff");
        }
        return InputParameterFactory.INSTANCE.createFileInGeotiff(
                identifier,
                isOptional, optionalAbstract,
                path
        );
    }

    /**
     * Function to create a file input with geojson.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path to the file
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createFileInputGeojson(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String path,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for geojson");
        }
        return InputParameterFactory.INSTANCE.createFileInGeojson(
                identifier,
                isOptional, optionalAbstract,
                path
        );
    }

    /**
     * Function to create a file input with a generic file.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path to the file
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createFileInputGeneric(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String path,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for file");
        }
        return InputParameterFactory.INSTANCE.createFileInGeneric(
                identifier,
                isOptional, optionalAbstract,
                path
        );
    }

    /**
     * Function to create a file input with a shapefile.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path to the file
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createFileInputShapefile(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String path,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for shapefile");
        }
        return InputParameterFactory.INSTANCE.createFileInShapeFile(
                identifier,
                isOptional, optionalAbstract,
                path
        );
    }

    /**
     * Function to create a file input with quakeml.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path to the file
     * @param schema optional schema
     * @return IIdentifierWithBinding
     */
    private static IInputParameter createFileInputQuakeML(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String path,
            @SuppressWarnings({"unused"})
            final String schema) {

        // schema is ignored here
        // takes the schema for quakeml
        return InputParameterFactory.INSTANCE.createFileInQuakeML(
                identifier,
                isOptional, optionalAbstract,
                path
        );
    }

    /**
     * Function to create a file input with a shakemap.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path to the file
     * @param schema optional schema
     * @return IIdentifierWithBinding
     */
    private static IInputParameter createFileInputShakemap(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String path,
             @SuppressWarnings({"unused"})
            final String schema) {
        // schema is ignored here
        // takes the schema for shakemap
        return InputParameterFactory.INSTANCE.createFileInShakemap(
                identifier,
                isOptional, optionalAbstract,
                path
        );
    }

    /**
     * Function to create a file input with json.
     * Checks some attributes and delegates the creation.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path to the file
     * @param schema optional schema
     * @return IIdentifierWithBinding
     * @throws ParseConfigurationException exception that may be thrown
     * if a argument is used that is not supported for this type.
     */
    private static IInputParameter createFileInputJson(
            final String identifier,
            final boolean isOptional,
            final String optionalAbstract,
            final String path,
            final String schema)
            throws ParseConfigurationException {
        if (strHasValue(schema)) {
            throw new ParseConfigurationException(
                    "schema is not supported for json");
        }
        return InputParameterFactory.INSTANCE.createFileInJson(
                identifier,
                isOptional, optionalAbstract,
                path
        );
    }

    /**
     * Enums with the supported factories to use data as input files.
     */
    private enum ToFileInputOption {
        /**
         * This is a enum to read the input from a geotiff file.
         */
        GEOTIFF("geotiff", ParseJsonForInputImpl::createFileInputGeotiff),
        /**
         * This is a enum to read the input from a geojson file.
         */
        GEOJSON("geojson", ParseJsonForInputImpl::createFileInputGeojson),
        /**
         * This is a enum to read the input from a shapefile.
         */
        SHAPEFILE("shapefile", ParseJsonForInputImpl::createFileInputShapefile),
        /**
         * This is a enum to read the input from a generic file.
         */
        GENERIC_FILE("file", ParseJsonForInputImpl::createFileInputGeneric),
        /**
         * This is a enum to read the input from a quakeml xml file.
         */
        QUAKEML("quakeml", ParseJsonForInputImpl::createFileInputQuakeML),
        /**
         * This is a enum to read the input from a shakemap xml file.
         */
        SHAKEMAP("shakemap", ParseJsonForInputImpl::createFileInputShakemap),
        /**
         * This is a enum to read the input from a json file.
         */
        JSON("json", ParseJsonForInputImpl::createFileInputJson);

        /**
         * This is the constant of the useAs value
         * for which we will use this enums.
         */
        private static final String USE_AS_FILE = "file";

        /**
         * String with the name of the data type to look it up.
         */
        private final String dataType;

        /**
         * Factory that is used to create the identifiers.
         */
        private final IAsFileInputFactory factory;

        /**
         * This is default constructor for the enum.
         * @param aDataType string with the data type name
         * @param aFactory factory to create the identifier with this type
         */
        ToFileInputOption(
                final String aDataType,
                final IAsFileInputFactory aFactory) {
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
        public IAsFileInputFactory getFactory() {
            return factory;
        }

        /**
         *
         * @return setting for useAs that is supported
         * by this types
         */
        public static String useAs() {
            return USE_AS_FILE;
        }
    }
}
