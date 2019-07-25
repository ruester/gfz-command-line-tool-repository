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

package org.n52.gfz.riesgos.processdescription;

import java.util.Optional;

/**
 * Named tuple with an identifier
 * and an optional abstract (description).
 */
public class IdentifierAbstractTuple {

    /**
     * Identifier for an element.
     */
    private final String identifier;
    /**
     * Optional abstrat for an element.
     */
    private final String optionalAbstract;

    /**
     * Creates a new named tuple.
     * @param aIdentifier the identifier of the element
     * @param aOptionalAbstract the optional abstract of the element
     */
    public IdentifierAbstractTuple(
            final String aIdentifier,
            final String aOptionalAbstract
    ) {
        this.identifier = aIdentifier;
        this.optionalAbstract = aOptionalAbstract;
    }

    /**
     *
     * @return identifier of the element.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     *
     * @return optional abstract of the element
     */
    public Optional<String> getAbstract() {
        return Optional.ofNullable(optionalAbstract);
    }
}
