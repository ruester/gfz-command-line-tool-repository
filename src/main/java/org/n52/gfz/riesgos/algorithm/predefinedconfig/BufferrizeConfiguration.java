package org.n52.gfz.riesgos.algorithm.predefinedconfig;

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


import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.configuration.IdentifierWithBindingFactory;
import org.n52.gfz.riesgos.exitvaluehandler.LogExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.stderrhandler.LogStderrHandler;



import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Configuration for a test process that takes a shape file
 * buffers the features and gives them back (all shp-Files).
 *
 * This is an example configuration that is not going to be a productive service.
 * The aim was just to have an test for writing and reading shape files to the
 * underlying container.
 */
public class BufferrizeConfiguration implements IConfiguration {

    @Override
    public String getIdentifier() {
        return "Bufferize";
    }

    @Override
    public String getImageId() {
        return "a8e4c481daa3";
    }

    @Override
    public String getWorkingDirectory() {
        return "/usr/share/git/bufferize/";
    }

    @Override
    public List<String> getCommandToExecute() {
        return Arrays.asList("python3", "bufferize.py");
    }

    @Override
    public List<String> getDefaultCommandLineFlags() {
        return Collections.emptyList();
    }

    @Override
    public List<IIdentifierWithBinding> getInputIdentifiers() {
        return Arrays.asList(
                IdentifierWithBindingFactory.createCommandLineArgumentShapeFile("shapefile-input"),
                IdentifierWithBindingFactory.createCommandLineArgumentDoubleWithDefaultValue("radius", 3.0)
        );
    }

    @Override
    public List<IIdentifierWithBinding> getOutputIdentifiers() {
        return Collections.singletonList(
                IdentifierWithBindingFactory.createFileOutShapeFile("shapefile-output", "buffered.shp")
        );
    }

    @Override
    public Optional<IStderrHandler> getStderrHandler() {
        return Optional.of(new LogStderrHandler());
    }

    @Override
    public Optional<IExitValueHandler> getExitValueHandler() {
        return Optional.of(new LogExitValueHandler());
    }

    @Override
    public Optional<IStdoutHandler> getStdoutHandler() {
        return Optional.empty();
    }
}