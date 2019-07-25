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

import java.io.PrintStream;

/**
 * Interface for a running process to provide.
 */
public interface IExecutionRun {

    /**
     * Returns the stdin stream of the running process.
     * @return stdin stream
     */
    PrintStream getStdin();

    /**
     * Blocks until the process completed.
     * Gives a result back to access stderr and stdout text.
     *
     * @return result of the process with access to the
     * exit value and stderr and stdout text
     * @throws InterruptedException there maybe is an interrupted exception
     * on waiting for the process to complete
     */
    IExecutionRunResult waitForCompletion() throws InterruptedException;

}
