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
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Function to convert an IData to a String.
 * Used to add it as a command line argument.
 * Can also handle default flags.
 */
public class LiteralStringBindingToStringCmd
        implements IConvertIDataToCommandLineParameter<LiteralStringBinding> {

    /**
     * Internal default flag (for example '--text').
     * Can be null.
     */
    private final String defaultFlag;

    /**
     * Constructor with a default flag.
     * @param aDefaultFlag flag that is before the element
     *                     (for example --etype before an expert type)
     */
    public LiteralStringBindingToStringCmd(final String aDefaultFlag) {
        this.defaultFlag = aDefaultFlag;
    }

    /**
     * Constructor without a default flag.
     */
    public LiteralStringBindingToStringCmd() {
        this(null);
    }

    /**
     * Converts the IData to a list of arguments.
     * @param binding element to convert
     * @return list of strings
     * handled by the function
     */
    @Override
    public List<String> convertToCommandLineParameter(
            final LiteralStringBinding binding) {
        final List<String> result = new ArrayList<>();

        Optional.ofNullable(defaultFlag).ifPresent(result::add);

        final String value = binding.getPayload();
        result.add(value);
        return result;
    }

    /**
     *
     * @param o other object
     * @return true if this object equals the other one
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LiteralStringBindingToStringCmd that =
                (LiteralStringBindingToStringCmd) o;
        return Objects.equals(defaultFlag, that.defaultFlag);
    }

    /**
     *
     * @return hashcode of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(defaultFlag);
    }
}
