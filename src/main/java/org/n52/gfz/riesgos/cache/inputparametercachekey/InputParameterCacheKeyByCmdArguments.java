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

package org.n52.gfz.riesgos.cache.inputparametercachekey;

import org.n52.gfz.riesgos.cache.IInputParameterCacheKey;

import java.util.List;
import java.util.Objects;

/**
 * Cache key implementation that works with the command line arguments.
 */
public class InputParameterCacheKeyByCmdArguments
        implements IInputParameterCacheKey {

    private static final long serialVersionUID = 6286290863841381454L;

    /**
     * List with command line arguments.
     */
    private final List<String> cmds;

    /**
     * Constructor with a list of command line arguments.
     * @param aCmds list of command line arguments
     */
    public InputParameterCacheKeyByCmdArguments(
            final List<String> aCmds) {
        this.cmds = aCmds;
    }

    /**
     * Tests equality.
     * @param o other object
     * @return true if both are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InputParameterCacheKeyByCmdArguments that =
                (InputParameterCacheKeyByCmdArguments) o;
        return Objects.equals(cmds, that.cmds);

    }

    /**
     *
     * @return hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(cmds);
    }
}
