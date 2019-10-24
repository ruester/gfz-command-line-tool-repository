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

import java.io.Serializable;

/**
 * Handler for stdout text.
 */
@FunctionalInterface
public interface IStdoutHandler extends Serializable {

    /**
     * Handles the text on stdout.
     * @param stdout text from stdout
     */
    void handleStdout(String stdout);
}
