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

import java.util.List;
import java.util.Optional;

/**
 * Interface for all the data for the process description generation.
 */
public interface IProcessDescriptionGeneratorData {

    /**
     * Identifier of the process.
     * @return identifier of the process
     */
    String getIdentifier();

    /**
     * Full qualified identifier of the process (something like
     * a package name + class name).
     * @return full qualified identifier of the process
     */
    String getFullQualifiedIdentifier();

    /**
     * Optional text with the abstract for the process.
     * @return optional text with the abstract of the process.
     */
    Optional<String> getProcessAbstract();

    /**
     * List with inputs.
     * @return list with inputs
     */
    List<IProcessDescriptionGeneratorInputData> getInputData();

    /**
     * List with outputs.
     * @return list with outputs.
     */
    List<IProcessDescriptionGeneratorOutputData> getOutputData();

}
