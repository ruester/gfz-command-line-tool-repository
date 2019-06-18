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

import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.wps.io.data.IData;

/**
 * Interface for converting the exit value to an IData element
 */
public interface IConvertExitValueToIData<T extends IData> {

    /**
     * converts the exit value to an IData element
     * @param exitValue integer value to convert
     * @return IData element
     * @throws ConvertToIDataException exception if there is an internal error / exception on conversion
     */
    T convertToIData(final int exitValue) throws ConvertToIDataException;
}
