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
 * Sub implementation for parsing a single input input
 */
public class ParseJsonForOutputImpl {

    private final Map<String, FromStdoutOption> optionsToReadFromStdout;
    private final Map<String, FromFilesOption> optionsToReadFromFiles;
    private final Map<String, FromStderrOption> optionsToReadFromStderr;
    private final Map<String, FromExitValueOption> optionsToReadFromExitValue;

    public ParseJsonForOutputImpl() {

        this.optionsToReadFromStdout = getOptionsToReadFromStdout();
        this.optionsToReadFromFiles = getOptionsToReadFromFiles();
        this.optionsToReadFromStderr = getOptionsToReadFromStderr();
        this.optionsToReadFromExitValue = getOptionsToReadFromExitValue();
    }

    private Map<String, FromStdoutOption> getOptionsToReadFromStdout() {
        return Stream.of(FromStdoutOption.values()).collect(Collectors.toMap(
                FromStdoutOption::getDataType, Function.identity()
        ));
    }

    private Map<String, FromFilesOption> getOptionsToReadFromFiles() {
        return Stream.of(FromFilesOption.values()).collect(Collectors.toMap(
                FromFilesOption::getDataType, Function.identity()
        ));
    }

    private Map<String, FromStderrOption> getOptionsToReadFromStderr() {
        return Stream.of(FromStderrOption.values()).collect(Collectors.toMap(
                FromStderrOption::getDataType, Function.identity()
        ));
    }

    private Map<String, FromExitValueOption> getOptionsToReadFromExitValue() {
        return Stream.of(FromExitValueOption.values()).collect(Collectors.toMap(
                FromExitValueOption::getDataType, Function.identity()
        ));
    }

    public IIdentifierWithBinding parseOutput(final JSONObject json) throws ParseConfigurationException {
        final String identifier = getString(json, "title");
        final String readFrom = getString(json, "readFrom");
        final String type = getString(json, "type");

        final Optional<String> optionalSchema = getOptionalSchemaString(json);

        if("stdout".equals(readFrom)) {
            if (optionsToReadFromStdout.containsKey(type)) {
                return optionsToReadFromStdout.get(type).getFactory().create(identifier, optionalSchema.orElse(null));

            } else {
                throw new ParseConfigurationException("Not supported type value");
            }
        } else if("stderr".equals(readFrom)) {
            if (optionsToReadFromStderr.containsKey(type)) {
                return optionsToReadFromStderr.get(type).getFactory().create(identifier);
            } else {
                throw new ParseConfigurationException("Not supported type value");
            }
        } else if("exitValue".equals(readFrom)) {
            if(optionsToReadFromExitValue.containsKey(type)) {
                return optionsToReadFromExitValue.get(type).getFactory().create(identifier);
            } else {
                throw new ParseConfigurationException("Not supported type value");
            }
        } else if("file".equals(readFrom)) {

            final String path = getString(json, "path");

            if(optionsToReadFromFiles.containsKey(type)) {
                return optionsToReadFromFiles.get(type).getFactory().create(identifier, path, optionalSchema.orElse(null));
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

    private Optional<String> getOptionalSchemaString(final JSONObject json) throws ParseConfigurationException {
        final String key = "schema";
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


    @FunctionalInterface
    private interface IStdoutOutputFactory {
        IIdentifierWithBinding create(final String identifier, final String schema);
    }

    private enum FromStdoutOption {
        STRING("string", (identifier, schema) -> IdentifierWithBindingFactory.createStdoutString(identifier)),
        XML("xml", IdentifierWithBindingFactory::createStdoutXmlWithSchema),
        QUAKEML("quakeml", (identifier, schema) -> IdentifierWithBindingFactory.createStdoutQuakeML(identifier)),
        SHAKEMAP("shakemap", (identifier, schema) -> IdentifierWithBindingFactory.createStdoutShakemap(identifier)),
        JSON("json", (identifier, schema) -> IdentifierWithBindingFactory.createStdoutJson(identifier));

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

    @FunctionalInterface
    private interface IStderrOutputFactory {
        IIdentifierWithBinding create(final String identifier);
    }

    private enum FromStderrOption {
        STRING("string", IdentifierWithBindingFactory::createStderrString),
        JSON("json", IdentifierWithBindingFactory::createStderrJson);

        private final String dataType;
        private final IStderrOutputFactory factory;

        FromStderrOption(final String dataType, final IStderrOutputFactory factory) {
            this.dataType = dataType;
            this.factory = factory;
        }

        public String getDataType() {
            return dataType;
        }

        public IStderrOutputFactory getFactory() {
            return factory;
        }
    }

    @FunctionalInterface
    private interface IExitValueOutputFactory {
        IIdentifierWithBinding create(final String identifier);
    }

    private enum FromExitValueOption {
        INT("int", IdentifierWithBindingFactory::createExitValueInt);

        private final String dataType;
        private final IExitValueOutputFactory factory;

        FromExitValueOption(final String dataType, final IExitValueOutputFactory factory) {
            this.dataType = dataType;
            this.factory = factory;
        }

        public String getDataType() {
            return dataType;
        }

        public IExitValueOutputFactory getFactory() {
            return factory;
        }
    }

    @FunctionalInterface
    private interface IFileOutputFactory {
        IIdentifierWithBinding create(final String identifier, final String path, final String schema);
    }

    private enum FromFilesOption {
        XML("xml", IdentifierWithBindingFactory::createFileOutXmlWithSchema),
        FILE("file", (identifier, path, schema) -> IdentifierWithBindingFactory.createFileOutGeneric(identifier, path)),
        GEOJSON("geojson", (identifier, path, schema) -> IdentifierWithBindingFactory.createFileOutGeojson(identifier, path)),
        GEOTIFF("geotiff", (identifier, path, schema) -> IdentifierWithBindingFactory.createFileOutGeotiff(identifier, path)),
        SHP("shapefile", (identifier, path, schema) -> IdentifierWithBindingFactory.createFileOutShapeFile(identifier, path)),
        QUAKEML("quakeml", (identifier, path, schema) -> IdentifierWithBindingFactory.createFileOutQuakeMLFile(identifier, path)),
        SHAKEMAP("shakemap", (identifier, path, schema) -> IdentifierWithBindingFactory.createFileOutShakemap(identifier, path)),
        JSON("json", (identifier, path, schema) -> IdentifierWithBindingFactory.createFileOutJson(identifier, path));

        private final String dataType;
        private final IFileOutputFactory factory;

        FromFilesOption(final String dataType, final IFileOutputFactory factory) {
            this.dataType = dataType;
            this.factory = factory;
        }

        public String getDataType() {
            return dataType;
        }

        public IFileOutputFactory getFactory() {
            return factory;
        }
    }
}
