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
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.parse.formats.json.AbstractParseJson;
import org.n52.gfz.riesgos.configuration.parse.input.commandlineargument.ToCommandLineArgumentOption;
import org.n52.gfz.riesgos.configuration.parse.input.file.ToFileInputOption;
import org.n52.gfz.riesgos.configuration.parse.input.stdin.ToStdinInputOption;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Sub implementation for parsing a single input input.
 */
public class ParseJsonForInputImpl extends AbstractParseJson {



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
        optionsToUseAsCommandLineArgument = ToCommandLineArgumentOption.asMap();
        optionsToUseAsStdinInput = ToStdinInputOption.asMap();
        optionsToUseAsFileInput = ToFileInputOption.asMap();
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
}
