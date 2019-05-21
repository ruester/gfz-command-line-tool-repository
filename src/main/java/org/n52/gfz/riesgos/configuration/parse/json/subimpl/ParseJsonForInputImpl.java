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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.IdentifierWithBindingFactory;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sub implementation for parsing a single input input
 */
public class ParseJsonForInputImpl {

    private final Map<String, ToCommandLineArgumentOption> optionsToUseAsCommandLineArgument;
    private final Map<String, ToStdinInputOption> optionsToUseAsStdinInput;
    private final Map<String, ToFileInputOption> optionsToUseAsFileInput;

    public ParseJsonForInputImpl() {
        optionsToUseAsCommandLineArgument = getOptionsToUseAsCommandLineArgument();
        optionsToUseAsStdinInput = getOptionsToUseAsStdinInput();
        optionsToUseAsFileInput = getOptionsToUseAsFileInput();
    }

    private Map<String, ToCommandLineArgumentOption> getOptionsToUseAsCommandLineArgument() {
        return Stream.of(ToCommandLineArgumentOption.values()).collect(Collectors.toMap(
                ToCommandLineArgumentOption::getDataType, Function.identity()
        ));
    }

    private Map<String, ToStdinInputOption> getOptionsToUseAsStdinInput() {
        return Stream.of(ToStdinInputOption.values()).collect(Collectors.toMap(
                ToStdinInputOption::getDataType, Function.identity()
        ));
    }

    private Map<String, ToFileInputOption> getOptionsToUseAsFileInput() {
        return Stream.of(ToFileInputOption.values()).collect(Collectors.toMap(
                ToFileInputOption::getDataType, Function.identity()
        ));
    }

    public IIdentifierWithBinding parseInput(final JSONObject json) throws ParseConfigurationException {
        final String identifier = getString(json, "title");
        final String useAs = getString(json, "useAs");
        final String type = getString(json, "type");

        final Optional<String> optionalDefaultCommandLineFlag = getOptionalString(json, "commandLineFlag");
        final Optional<String> optionalDefaultValue = getOptionalString(json, "default");
        final Optional<List<String>> optionalAllowedValues = getOptionalListOfStrings(json, "allowed");
        final Optional<List<String>> optionalSupportedCrs = getOptionalListOfStrings(json, "crs");
        final Optional<String> optionalSchema = getOptionalString(json, "schema");

        if("commandLineArgument".equals(useAs)) {
            if (optionsToUseAsCommandLineArgument.containsKey(type)) {
                return optionsToUseAsCommandLineArgument.get(type).getFactory().create(
                        identifier,
                        optionalDefaultCommandLineFlag.orElse(null),
                        optionalDefaultValue.orElse(null),
                        optionalAllowedValues.orElse(null),
                        optionalSupportedCrs.orElse(null),
                        optionalSchema.orElse(null));
            } else {
                throw new ParseConfigurationException("Not supported type value '" + type + "'");
            }
        } else if("stdin".equals(useAs)) {
            if (optionsToUseAsStdinInput.containsKey(type)) {
                return optionsToUseAsStdinInput.get(type).getFactory().create(
                        identifier,
                        optionalDefaultValue.orElse(null),
                        optionalAllowedValues.orElse(null),
                        optionalSchema.orElse(null)

                );
            } else {
                throw new ParseConfigurationException("Not supported type value '" + type + "'");
            }
        } else if("file".equals(useAs))
            if(optionsToUseAsFileInput.containsKey(type)) {

                final String path = getString(json, "path");

                return optionsToUseAsFileInput.get(type).getFactory().create(
                        identifier,
                        path,
                        optionalSchema.orElse(null)
                );
            } else {
                throw new ParseConfigurationException("Not supported type value '" + type + "'");
        } else {
            throw new ParseConfigurationException("Not supported useAs value: '" + useAs + "'");
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

    private Optional<String> getOptionalString(final JSONObject json, final String key) throws ParseConfigurationException {
        final Optional<String> result;
        if(json.containsKey(key)) {
            final Object rawValue = json.get(key);
            if(! (rawValue instanceof String)) {
                throw new ParseConfigurationException("Wrong type for element '" + key + "', expected a String");
            }
            result = Optional.of((String) rawValue);
        } else {
            result = Optional.empty();
        }
        return result;
    }

    private Optional<List<String>> getOptionalListOfStrings(final JSONObject json, final String key) throws ParseConfigurationException {
        final Optional<List<String>> result;
        if(json.containsKey(key)) {
            final Object rawValue = json.get(key);
            if(! (rawValue instanceof JSONArray)) {
                throw new ParseConfigurationException("Wrong type for element '" + key + "', expected a JSON array");
            }
            final List<String> list = new ArrayList<>();
            for(final Object element : (JSONArray) rawValue) {
                if(element instanceof String) {
                    list.add((String) element);
                } else if(element instanceof Double || element instanceof Integer) {
                    list.add(String.valueOf(element));
                } else {
                    throw new ParseConfigurationException("Wrong type for element in '" + key + "', expected a String");
                }
            }
            result = Optional.of(list);
        } else {
            result = Optional.empty();
        }
        return result;
    }

    @FunctionalInterface
    private interface IAsCommandLineArgumentFactory {
        IIdentifierWithBinding create(final String identifier,
                                      final String defaultCommandLineFlag,
                                      final String defaultValue,
                                      final List<String> allowedValues,
                                      final List<String> supportedCrs,
                                      final String schema) throws ParseConfigurationException;
    }

    private static boolean strHasValue(final String str) {
        return str != null && (! str.isEmpty());
    }

    private static boolean strHasNoValue(final String str) {
        return ! strHasValue(str);
    }

    private static boolean listHasValue(final List<String> list) {
        return list != null && (! list.isEmpty());
    }

    private static boolean listHasNoValues(final List<String> list) {
        return ! listHasValue(list);
    }

    private static IIdentifierWithBinding createCommandLineArgumentInt(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if(listHasValue(supportedCrs)) {
            throw new ParseConfigurationException("crs are not supported for int types");
        }
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for int types");
        }
        return IdentifierWithBindingFactory.createCommandLineArgumentInt(identifier, flag, defaultValue, allowedValues);
    }

    private static IIdentifierWithBinding createCommandLineArgumentDouble(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if(listHasValue(supportedCrs)) {
            throw new ParseConfigurationException("crs are not supported for double types");
        }
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for double types");
        }
        return IdentifierWithBindingFactory.createCommandLineArgumentDouble(identifier, flag, defaultValue, allowedValues);
    }

    private static IIdentifierWithBinding createCommandLineArgumentString(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if(listHasValue(supportedCrs)) {
            throw new ParseConfigurationException(("crs are not supported for string types"));
        }
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for string types");
        }
        return IdentifierWithBindingFactory.createCommandLineArgumentString(identifier, flag, defaultValue, allowedValues);
    }

    private static IIdentifierWithBinding createCommandLineArgumentBoolean(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if(listHasValue(supportedCrs)) {
            throw new ParseConfigurationException("crs are not supported for boolean types");
        }
        if(listHasValue(allowedValues)) {
            throw new ParseConfigurationException("allowed values are not supported for booleans");
        }
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for booleans");
        }
        if(strHasNoValue(flag)) {
            throw new ParseConfigurationException("flag is necessary for boolean type");
        }
        return IdentifierWithBindingFactory.createCommandLineArgumentBoolean(identifier, flag, defaultValue);
    }

    private static IIdentifierWithBinding createCommandLineArgumentBBox(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {

        if(strHasValue(flag)) {
            throw new ParseConfigurationException("commandLineFlag is not supported for bbox");
        }

        if(strHasValue(defaultValue)) {
            throw new ParseConfigurationException("default is not supported for bbox");
        }

        if(listHasValue(allowedValues)) {
            throw new ParseConfigurationException("allowed values are not supported for bbox");
        }

        if(listHasNoValues(supportedCrs)) {
            throw new ParseConfigurationException("The element 'crs' for is necessary for bbox");
        }

        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for bbox");
        }

        return IdentifierWithBindingFactory.createCommandLineArgumentBBox(identifier, supportedCrs);
    }

    private static IIdentifierWithBinding createCommandLineArgumentXmlFile(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(defaultValue)) {
            throw new ParseConfigurationException("default is not supported for xml");
        }
        if(listHasValue(allowedValues)) {
            throw new ParseConfigurationException("allowed values are not supported for xml");
        }
        if(listHasValue(supportedCrs)) {
            throw new ParseConfigurationException("crs are not supported for xml");
        }
        return IdentifierWithBindingFactory.createCommandLineArgumentXmlFileWithSchema(identifier, schema, flag);
    }

    private static IIdentifierWithBinding createCommandLineArgumentXmlFileWithoutHeader(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(defaultValue)) {
            throw new ParseConfigurationException("default is not supported for xml");
        }
        if(listHasValue(allowedValues)) {
            throw new ParseConfigurationException("allowed values are not supported for xml");
        }
        if(listHasValue(supportedCrs)) {
            throw new ParseConfigurationException("crs are not supported for xml");
        }
        return IdentifierWithBindingFactory.createCommandLineArgumentXmlFileWithSchemaWithoutHeader(identifier, schema, flag);
    }

    private static IIdentifierWithBinding createCommandLineArgumentGeotiffFile(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(defaultValue)) {
            throw new ParseConfigurationException("default is not supported for geotiff");
        }
        if(listHasValue(allowedValues)) {
            throw new ParseConfigurationException("allowed values are not supported for geotiff");
        }
        if(listHasValue(supportedCrs)) {
            throw new ParseConfigurationException("crs are not supported for geotiff");
        }
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for geotiff");
        }
        return IdentifierWithBindingFactory.createCommandLineArgumentGeotiff(identifier, flag);
    }

    private static IIdentifierWithBinding createCommandLineArgumentGeojsonFile(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(defaultValue)) {
            throw new ParseConfigurationException("default is not supported for geojson");
        }
        if(listHasValue(allowedValues)) {
            throw new ParseConfigurationException("allowed values are not supported for geojson");
        }
        if(listHasValue(supportedCrs)) {
            throw new ParseConfigurationException("crs are not supported for geojson");
        }
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for geojson");
        }
        return IdentifierWithBindingFactory.createCommandLineArgumentGeojson(identifier, flag);
    }

    private static IIdentifierWithBinding createCommandLineArgumentShapefile(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(defaultValue)) {
            throw new ParseConfigurationException("default is not supported for shapefile");
        }
        if(listHasValue(allowedValues)) {
            throw new ParseConfigurationException("allowed values are not supported for shapefile");
        }
        if(listHasValue(supportedCrs)) {
            throw new ParseConfigurationException("crs are not supported for shapefile");
        }
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for shapefile");
        }
        return IdentifierWithBindingFactory.createCommandLineArgumentShapeFile(identifier, flag);
    }

    private static IIdentifierWithBinding createCommandLineArgumentGenericFile(
            final String identifier,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final List<String> supportedCrs,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(defaultValue)) {
            throw new ParseConfigurationException("default is not supported for file");
        }
        if(listHasValue(allowedValues)) {
            throw new ParseConfigurationException("allowed values are not supported for file");
        }
        if(listHasValue(supportedCrs)) {
            throw new ParseConfigurationException("crs are not supported for file");
        }
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for file");
        }
        return IdentifierWithBindingFactory.createCommandLineArgumentFile(identifier, flag);
    }

    private enum ToCommandLineArgumentOption {
        INT("int", ParseJsonForInputImpl::createCommandLineArgumentInt),
        DOUBLE("double", ParseJsonForInputImpl::createCommandLineArgumentDouble),
        BOOLEAN("boolean", ParseJsonForInputImpl::createCommandLineArgumentBoolean),
        STRING("string", ParseJsonForInputImpl::createCommandLineArgumentString),
        BBOX("bbox", ParseJsonForInputImpl::createCommandLineArgumentBBox),
        XML("xml", ParseJsonForInputImpl::createCommandLineArgumentXmlFile),
        XML_WITHOUT_HEADER("xmlWithoutHeader", ParseJsonForInputImpl::createCommandLineArgumentXmlFileWithoutHeader),
        GEOFITT("geotiff", ParseJsonForInputImpl::createCommandLineArgumentGeotiffFile),
        GEOJSON("geojson", ParseJsonForInputImpl::createCommandLineArgumentGeojsonFile),
        SHAPEFILE("shapefile", ParseJsonForInputImpl::createCommandLineArgumentShapefile),
        GENERIC_FILE("file", ParseJsonForInputImpl::createCommandLineArgumentGenericFile);

        private final String dataType;
        private final IAsCommandLineArgumentFactory factory;

        ToCommandLineArgumentOption(final String dataType, final IAsCommandLineArgumentFactory factory) {
            this.dataType = dataType;
            this.factory = factory;
        }

        public String getDataType() {
            return dataType;
        }

        public IAsCommandLineArgumentFactory getFactory() {
            return factory;
        }
    }

    @FunctionalInterface
    private interface IAsStdinInputFactory {
        IIdentifierWithBinding create(final String identifier,
                                      final String defaultValue,
                                      final List<String> allowedValues,
                                      final String schema) throws ParseConfigurationException;
    }

    private static IIdentifierWithBinding createStdinString(
            final String identifier,
            final String defaultValue,
            final List<String> allowedValues,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for string");
        }
        return IdentifierWithBindingFactory.createStdinString(identifier, defaultValue, allowedValues);
    }

    private enum ToStdinInputOption {
        STRING("string", ParseJsonForInputImpl::createStdinString);

        private final String dataType;
        private final IAsStdinInputFactory factory;

        ToStdinInputOption(final String dataType, final IAsStdinInputFactory factory) {
            this.dataType = dataType;
            this.factory = factory;
        }

        public String getDataType() {
            return dataType;
        }

        public IAsStdinInputFactory getFactory() {
            return factory;
        }
    }

    @FunctionalInterface
    private interface IAsFileInputFactory {
        IIdentifierWithBinding create(final String identifier,
                                      final String path,
                                      final String schema) throws ParseConfigurationException;
    }

    private static IIdentifierWithBinding createFileInputGeotiff(
            final String identifier,
            final String path,
            final String schema) throws ParseConfigurationException {
        if (strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for geotiff");
        }
        return IdentifierWithBindingFactory.createFileInGeotiff(identifier, path);
    }

    private static IIdentifierWithBinding createFileInputGeojson(
            final String identifier,
            final String path,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for geojson");
        }
        return IdentifierWithBindingFactory.createFileInGeojson(identifier, path);
    }

    private static IIdentifierWithBinding createFileInputGeneric(
            final String identifier,
            final String path,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for file");
        }
        return IdentifierWithBindingFactory.createFileInGeneric(identifier, path);
    }

    private static IIdentifierWithBinding createFileInputShapefile(
            final String identifier,
            final String path,
            final String schema) throws ParseConfigurationException {
        if(strHasValue(schema)) {
            throw new ParseConfigurationException("schema is not supported for shapefile");
        }
        return IdentifierWithBindingFactory.createFileInShapeFile(identifier, path);
    }

    private static IIdentifierWithBinding createFileInQuakeML(
            final String identifier,
            final String path,
            @SuppressWarnings({"unused"})
            final String schema) {

        // schema is ignored here
        // takes the schema for quakeml
        return IdentifierWithBindingFactory.createFileInQuakeML(identifier, path);
    }

    private static IIdentifierWithBinding createFileInShakemap(
            final String identifier,
            final String path,
             @SuppressWarnings({"unused"})
            final String schema) {
        // schema is ignored here
        // takes the schema for shakemap
        return IdentifierWithBindingFactory.createFileInShakemap(identifier, path);
    }

    private enum ToFileInputOption {
        GEOTIFF("geotiff", ParseJsonForInputImpl::createFileInputGeotiff),
        GEOJSON("geojson", ParseJsonForInputImpl::createFileInputGeojson),
        SHAPEFILE("shapefile", ParseJsonForInputImpl::createFileInputShapefile),
        GENERIC_FILE("file", ParseJsonForInputImpl::createFileInputGeneric),
        QUAKEML("quakeml", ParseJsonForInputImpl::createFileInQuakeML),
        SHAKEMAP("shakemap", ParseJsonForInputImpl::createFileInShakemap);

        private final String dataType;
        private final IAsFileInputFactory factory;

        ToFileInputOption(final String dataType, final IAsFileInputFactory factory) {
            this.dataType = dataType;
            this.factory = factory;
        }

        public String getDataType() {
            return dataType;
        }

        public IAsFileInputFactory getFactory() {
            return factory;
        }
    }



}
