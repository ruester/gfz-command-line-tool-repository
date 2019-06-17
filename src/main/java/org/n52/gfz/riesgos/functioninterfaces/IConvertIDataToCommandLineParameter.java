package org.n52.gfz.riesgos.functioninterfaces;

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

import org.n52.gfz.riesgos.exceptions.ConvertToStringCmdException;
import org.n52.wps.io.data.IData;

import java.util.List;

/**
 * Interface to convert in IData to a string for using it as a
 * command line argument.
 */
@FunctionalInterface
public interface IConvertIDataToCommandLineParameter<T extends IData> {

    /**
     * Converts the IData to a list of command line arguments.
     * @param iData element to convert
     * @return list of strings
     * @throws ConvertToStringCmdException exception if the input can't be
     * handled by the function
     */
    List<String> convertToCommandLineParameter(
            final T iData)
            throws ConvertToStringCmdException;
}
