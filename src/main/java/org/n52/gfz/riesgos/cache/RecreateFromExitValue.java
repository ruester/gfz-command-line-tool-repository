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

package org.n52.gfz.riesgos.cache;

import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.wps.io.data.IData;

public class RecreateFromExitValue implements IDataRecreator {

    private final int exitValue;
    private final IConvertExitValueToIData converter;

    public RecreateFromExitValue(
            final int exitValue,
            final IConvertExitValueToIData converter) {
        this.exitValue = exitValue;
        this.converter = converter;
    }


    @Override
    public IData recreate() {
        try {
            return converter.convertToIData(exitValue);
        } catch(final ConvertToIDataException exception) {
            // this class is only meant to recreate
            // objects
            throw new RuntimeException(exception);
        }
    }
}
