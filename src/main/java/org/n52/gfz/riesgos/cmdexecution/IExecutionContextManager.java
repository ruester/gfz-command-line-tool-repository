package org.n52.gfz.riesgos.cmdexecution;

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

import java.util.List;

/**
 * Interface for an execution context manager
 * (for creating an execution context).
 */
public interface IExecutionContextManager {

    /**
     * Creates an execution context
     * (maybe inside of docker, depending on the implementation).
     * @param workingDirectory directory to run the code inside
     * @param cmd string list with the command to execute
     *            (for example ["python3", "script.py", "arg1", "arg2"]
     * @return execution context to start the process
     */
    IExecutionContext createExecutionContext(
            String workingDirectory, List<String> cmd);
}
