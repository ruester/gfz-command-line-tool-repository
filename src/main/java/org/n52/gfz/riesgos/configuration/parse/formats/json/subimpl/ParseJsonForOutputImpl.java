package org.n52.gfz.riesgos.configuration.parse.formats.json.subimpl;

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
import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.configuration.parse.formats.json.AbstractParseJson;
import org.n52.gfz.riesgos.configuration.parse.output.exitvalue.FromExitValueOption;
import org.n52.gfz.riesgos.configuration.parse.output.file.FromFilesOption;
import org.n52.gfz.riesgos.configuration.parse.output.stderr.FromStderrOption;
import org.n52.gfz.riesgos.configuration.parse.output.stdout.FromStdoutOption;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

import java.util.Map;
import java.util.Optional;

/**
 * Sub implementation for parsing a single input input.
 */
public class ParseJsonForOutputImpl extends AbstractParseJson {

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

        this.optionsToReadFromStdout = FromStdoutOption.asMap();
        this.optionsToReadFromFiles = FromFilesOption.asMap();
        this.optionsToReadFromStderr = FromStderrOption.asMap();
        this.optionsToReadFromExitValue = FromExitValueOption.asMap();
    }

    /**
     * Parses the sub json object to an IdentifierWithBinding.
     * @param json sub json with the output parameter data
     * @return IIdentifierWithBinding with all the data for the output parameter
     * @throws ParseConfigurationException if a field is missing that is
     * necessary a ParseConfigurationException will be thrown
     */
    public IOutputParameter parseOutput(
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
                            isOptional,
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
                                isOptional,
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
                                isOptional,
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
                                isOptional,
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

}
