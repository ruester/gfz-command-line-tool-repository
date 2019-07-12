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

package org.n52.gfz.riesgos.cache.generateinputcachekey;

import org.n52.gfz.riesgos.cache.IFunctionToGenerateCacheKey;
import org.n52.gfz.riesgos.cache.inputparametercachekey.InputParameterCacheKeyByCmdArguments;
import org.n52.gfz.riesgos.cache.inputparametercachekey.InputParameterCacheKeyByException;
import org.n52.gfz.riesgos.cache.IInputParameterCacheKey;
import org.n52.gfz.riesgos.exceptions.ConvertToStringCmdException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.IData;

import java.util.List;

/**
 * Implementation that generates the cache keys for input parameters
 * that are given as command line arguments.
 * @param <T> class that extends IData
 */
public class GenerateCacheKeyByTransformToCmd<T extends IData>
        implements IFunctionToGenerateCacheKey<T> {

    /**
     * Inner function to convert the iData to command line parameters.
     */
    private final IConvertIDataToCommandLineParameter<T>
            convertIDataToCommandLineParameter;

    /**
     * True if the input value is optional.
     */
    private final boolean isOptional;

    /**
     * Constructor with a function to convert idata to command line
     * arguments.
     * @param aConvertIDataToCommandLineParameter converter function
     * @param aIsOptional boolean value that tells it the input is optional
     */
    public GenerateCacheKeyByTransformToCmd(
            final IConvertIDataToCommandLineParameter<T>
                    aConvertIDataToCommandLineParameter,
            final boolean aIsOptional) {
        this.convertIDataToCommandLineParameter =
                aConvertIDataToCommandLineParameter;
        this.isOptional = aIsOptional;
    }

    /**
     *
     * @param idata data to compute a cache key for
     * @return InputParameterCacheKeyByCmdArguments
     */
    @Override
    public IInputParameterCacheKey generateCacheKey(final T idata) {

        try {
            final List<String> cmds =
                    convertIDataToCommandLineParameter
                            .convertToCommandLineParameter(idata);
            return new InputParameterCacheKeyByCmdArguments(
                    cmds, isOptional);
        } catch (final ConvertToStringCmdException exception) {
            return new InputParameterCacheKeyByException(
                    exception, null, isOptional);
        }
    }
}
