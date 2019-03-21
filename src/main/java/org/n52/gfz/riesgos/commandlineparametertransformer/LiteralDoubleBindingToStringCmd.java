package org.n52.gfz.riesgos.commandlineparametertransformer;

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

import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Function to convert an IData to a String.
 * Used to add it as a command line argument.
 * Can also handle default flags.
 */
public class LiteralDoubleBindingToStringCmd implements IConvertIDataToCommandLineParameter {

    private final String defaultFlag;

    /**
     * Constructor with a default flag
     * @param defaultFlag flag that is before the element (for example --lat before a latitude)
     */
    private LiteralDoubleBindingToStringCmd(final String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    /**
     * Constructor without a default flag
     */
    public LiteralDoubleBindingToStringCmd() {
        this(null);
    }

    @Override
    public List<String> convertToCommandLineParameter(final IData iData) {
        final List<String> result = new ArrayList<>();

        Optional.ofNullable(defaultFlag).ifPresent(result::add);

        if(iData instanceof LiteralDoubleBinding) {
            final LiteralDoubleBinding binding = (LiteralDoubleBinding) iData;
            final Double value = binding.getPayload();
            result.add(String.valueOf(value));
        }
        return result;
    }


}
