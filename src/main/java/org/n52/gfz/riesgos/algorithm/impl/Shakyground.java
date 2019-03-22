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

package org.n52.gfz.riesgos.algorithm.impl;

import org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.IdentifierWithBindingFactory;
import org.n52.gfz.riesgos.exitvaluehandler.LogExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.stderrhandler.LogStderrHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Shakyground extends BaseGfzRiesgosService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Shakyground.class);

    public Shakyground(final String imageId) {
        super(createShakygroundConfig(imageId), LOGGER);
    }

    private static IConfiguration createShakygroundConfig(final String imageId) {
        return new ShakygroundConfig(imageId);
    }

    private static class ShakygroundConfig implements IConfiguration {
        private final String imageId;

        public ShakygroundConfig(final String imageId) {
            this.imageId = imageId;
        }

        @Override
        public String getIdentifier() {
            return "ShakygroundTest";
        }

        @Override
        public List<IIdentifierWithBinding> getInputIdentifiers() {
            return Collections.singletonList(
                    IdentifierWithBindingFactory.createCommandLineArgumentXmlFileWithSchemaWithoutHeader(
                            "quakeMLFile", "http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd")
            );
        }

        @Override
        public List<IIdentifierWithBinding> getOutputIdentifiers() {
            return Collections.singletonList(
                    IdentifierWithBindingFactory.createStdoutXmlWithSchema(
                            "shakeMapFile", "http://earthquake.usgs.gov/eqcenter/shakemap")
            );
        }

        @Override
        public String getImageId() {
            return imageId;
        }

        @Override
        public String getWorkingDirectory() {
            return "/usr/share/git/shakyground";
        }

        @Override
        public List<String> getCommandToExecute() {
            return Arrays.asList("python3", "service.py");
        }

        @Override
        public List<String> getDefaultCommandLineFlags() {
            return Collections.emptyList();
        }

        @Override
        public Optional<IStderrHandler> getStderrHandler() {
            return Optional.of(new LogStderrHandler(LOGGER::debug));
        }

        @Override
        public Optional<IExitValueHandler> getExitValueHandler() {
            return Optional.of(new LogExitValueHandler(LOGGER::debug));
        }

        @Override
        public Optional<IStdoutHandler> getStdoutHandler() {
            return Optional.empty();
        }
    }
}
