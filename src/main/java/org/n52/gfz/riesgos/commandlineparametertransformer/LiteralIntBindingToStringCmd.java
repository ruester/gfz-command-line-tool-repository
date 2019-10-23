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

package org.n52.gfz.riesgos.commandlineparametertransformer;

import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Function to convert an IData to a String.
 * Used to add a command line argument.
 * Can also handle default flags.
 */
public final class LiteralIntBindingToStringCmd
    implements IConvertIDataToCommandLineParameter<LiteralIntBinding> {

    private final String defaultFlag;

    /**
     * Constructor with a default flag
     * @param defaultFlag flag that is before the element
     *                    (for example --level before a level value)
     */
    public LiteralIntBindingToStringCmd(final String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public LiteralIntBindingToStringCmd() {
        this(null);
    }

    @Override
    public List<String> convertToCommandLineParameter(
        final LiteralIntBinding binding
    ) {
        final List<String> result = new ArrayList<>();

        Optional.ofNullable(defaultFlag).ifPresent(result::add);

        final Integer value = binding.getPayload();
        result.add(String.valueOf(value));

        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiteralIntBindingToStringCmd that = (LiteralIntBindingToStringCmd) o;
        return Objects.equals(defaultFlag, that.defaultFlag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultFlag);
    }
}
