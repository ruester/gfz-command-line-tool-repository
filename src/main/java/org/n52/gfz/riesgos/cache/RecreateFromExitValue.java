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

/**
 * Implementation of the recreation from an int value.
 */
public class RecreateFromExitValue implements IDataRecreator {

    private static final long serialVersionUID = -2531405430734671509L;

    /**
     * Exit value to convert.
     */
    private final int exitValue;

    /**
     * Function to do the conversion.
     */
    private final IConvertExitValueToIData converter;

    /**
     * Binding class of the recreated idata.
     */
    private final Class<? extends IData> bindingClass;

    /**
     * Creates a new Recreator for an exit value.
     * @param aExitValue int exit value
     * @param aConverter converter to idata
     * @param aBindingClass binding class of the result
     */
    public RecreateFromExitValue(
            final int aExitValue,
            final IConvertExitValueToIData aConverter,
            final Class<? extends IData> aBindingClass) {
        this.exitValue = aExitValue;
        this.converter = aConverter;
        this.bindingClass = aBindingClass;
    }


    /**
     *
     * @return recreted idata from the exit value
     */
    @Override
    public IData recreate() {
        try {
            return converter.convertToIData(exitValue);
        } catch (final ConvertToIDataException exception) {
            // this class is only meant to recreate
            // objects
            throw new RuntimeException(exception);
        }
    }

    /**
     *
     * @return binding class of the recreated idata
     */
    @Override
    public Class<? extends IData> getBindingClassToRecreate() {
        return bindingClass;
    }
}
