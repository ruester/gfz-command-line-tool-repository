package org.n52.gfz.riesgos.exitvaluehandler;

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

import org.n52.gfz.riesgos.exceptions.NonZeroExitValueException;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.ILogger;

/**
 * Handler for the exit value that throws an exception on a non zero
 * exit value
 */
public class ExceptionIfExitValueIsNotEmptyHandler implements IExitValueHandler {

    @Override
    public void handleExitValue(int exitValue, final ILogger logger) throws NonZeroExitValueException {
        if(exitValue != 0) {
            throw new NonZeroExitValueException(exitValue);
        }
    }
}
