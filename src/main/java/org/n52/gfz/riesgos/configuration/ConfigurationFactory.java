package org.n52.gfz.riesgos.configuration;

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

import org.apache.commons.io.IOUtils;
import org.n52.gfz.riesgos.configuration.parse.IParseConfiguration;
import org.n52.gfz.riesgos.configuration.parse.json.ParseJsonConfigurationImpl;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Factory class for providing predefined configurations.
 */
public final class ConfigurationFactory {

    /**
     * This is a private constructor.
     * We don't want to have an instance
     * of this for accessing the static methods.
     */
    private ConfigurationFactory() {
        // static
    }

    /**
     * Creates the configuration for Quakeledger.
     * It uses a predefined docker image (quakeledger:latest)
     * @return IConfiguration
     */
    public static IConfiguration createQuakeledger() {
        try {
            final InputStream inputStream = ConfigurationFactory
                    .class
                    .getClassLoader()
                    .getResourceAsStream(
                        "org/n52/gfz/riesgos/configuration/quakeledger.json");
            if (inputStream == null) {
                throw new IOException("Input stream is null");
            }
            final String content = new String(IOUtils.toByteArray(inputStream));
            final IParseConfiguration parser = new ParseJsonConfigurationImpl();
            return parser.parse(content);
        } catch (final IOException | ParseConfigurationException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Creates the configuration for shakyground.
     * It uses a predefined docker image (shakyground:latest)
     * @return IConfiguration
     */
    public static IConfiguration createShakyground() {
        try {
            final InputStream inputStream = ConfigurationFactory
                    .class
                    .getClassLoader()
                    .getResourceAsStream(
                        "org/n52/gfz/riesgos/configuration/shakyground.json");
            if (inputStream == null) {
                throw new IOException("Input stream is null");
            }
            final String content = new String(IOUtils.toByteArray(inputStream));
            final IParseConfiguration parser = new ParseJsonConfigurationImpl();
            return parser.parse(content);
        } catch (final IOException | ParseConfigurationException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Creates the configuration for flooddmage.
     * It uses a predefined docker image (flooddamage:latest)
     * @return IConfiguration
     */
    public static IConfiguration createFlooddamage() {
        try {
            final InputStream inputStream = ConfigurationFactory
                    .class
                    .getClassLoader()
                    .getResourceAsStream(
                        "org/n52/gfz/riesgos/configuration/flooddamage.json");
            if (inputStream == null) {
                throw new IOException("Input stream is null");
            }
            final String content = new String(IOUtils.toByteArray(inputStream));
            final IParseConfiguration parser = new ParseJsonConfigurationImpl();
            return parser.parse(content);
        } catch (final IOException | ParseConfigurationException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Creates the configuration for shakyground.
     * It uses a predefined docker image (assetmaster:latest)
     * @return IConfiguration
     */
    public static IConfiguration createAssetmaster() {
        try {
            final InputStream inputStream = ConfigurationFactory
                    .class
                    .getClassLoader()
                    .getResourceAsStream(
                            "org/n52/gfz/riesgos/configuration/assetmaster.json");
            if (inputStream == null) {
                throw new IOException("Input stream is null");
            }
            final String content = new String(IOUtils.toByteArray(inputStream));
            final IParseConfiguration parser = new ParseJsonConfigurationImpl();
            return parser.parse(content);
        } catch (final IOException | ParseConfigurationException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Creates the configuration for modelprop.
     * It uses a predefined docker image (modelprop:latest)
     * @return IConfiguration
     */
    public static IConfiguration createModelprop() {
        try {
            final InputStream inputStream = ConfigurationFactory
                    .class
                    .getClassLoader()
                    .getResourceAsStream(
                            "org/n52/gfz/riesgos/configuration/modelprop.json");
            if (inputStream == null) {
                throw new IOException("Input stream is null");
            }
            final String content = new String(IOUtils.toByteArray(inputStream));
            final IParseConfiguration parser = new ParseJsonConfigurationImpl();
            return parser.parse(content);
        } catch (final IOException | ParseConfigurationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
