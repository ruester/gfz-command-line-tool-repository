/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.n52.gfz.riesgos.processdescription.impl;

import org.junit.Test;
import org.n52.gfz.riesgos.cache.IFunctionToGenerateCacheKey;
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IWriteIDataToFiles;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorInputData;
import org.n52.wps.io.data.IData;
import org.n52.wps.webapp.api.FormatEntry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Test class for the InputDataConfig for the process description generator
 */
public class TestProcessDescriptionGeneratorInputDataConfigImpl {

    @Test
    public void testSupportedCrs() {
        final List<String> supportedCrs = Arrays.asList("ESGP:4326", "EPSG:4328");

        final IInputParameter inputParameter = new IInputParameter() {
            @Override
            public Optional<IConvertIDataToCommandLineParameter> getFunctionToTransformToCmd() {
                return Optional.empty();
            }

            @Override
            public Optional<IWriteIDataToFiles> getFunctionToWriteIDataToFiles() {
                return Optional.empty();
            }

            @Override
            public Optional<IConvertIDataToByteArray> getFunctionToWriteToStdin() {
                return Optional.empty();
            }

            @Override
            public Optional<List<String>> getAllowedValues() {
                return Optional.empty();
            }

            @Override
            public Optional<String> getDefaultValue() {
                return Optional.empty();
            }

            @Override
            public IFunctionToGenerateCacheKey getFunctionToGenerateCacheKey() {
                return null;
            }

            @Override
            public String getIdentifier() {
                return null;
            }

            @Override
            public Class<? extends IData> getBindingClass() {
                return null;
            }

            @Override
            public Optional<String> getAbstract() {
                return Optional.empty();
            }

            @Override
            public Optional<ICheckDataAndGetErrorMessage> getValidator() {
                return Optional.empty();
            }

            @Override
            public Optional<String> getPathToWriteToOrReadFromFile() {
                return Optional.empty();
            }

            @Override
            public Optional<String> getSchema() {
                return Optional.empty();
            }

            @Override
            public Optional<List<String>> getSupportedCRSForBBox() {
                return Optional.of(supportedCrs);
            }

            @Override
            public boolean isOptional() {
                return false;
            }

            @Override
            public Optional<FormatEntry> getDefaultFormat() {
                return Optional.empty();
            }
        };

        final IProcessDescriptionGeneratorInputData inputData = new ProcessDescriptionGeneratorInputDataConfigImpl(inputParameter);

        final Optional<List<String>> optionalSupportedCrs = inputData.getSupportedCrs();

        assertTrue("There are supported crs present", optionalSupportedCrs.isPresent());

        assertEquals("And that are the same values as given in", supportedCrs, optionalSupportedCrs.get());
    }

}