package org.n52.gfz.riesgos.cmdexecution.common;

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

import org.n52.gfz.riesgos.cmdexecution.IExecutionRunResult;

/**
 * Implementation of the result of running a process.
 */
public class ExecutionRunResultImpl implements IExecutionRunResult {

    /**
     * Exit value of the run.
     */
    private final int exitValue;
    /**
     * Stderr output of the run.
     */
    private final String stderr;
    /**
     * Stdout output of the run.
     */
    private final String stdout;

    /**
     * Default constructor.
     * @param aExitValue exit value of the process
     * @param aStderr joined stderr text
     * @param aStdout joined stdout text
     */
    ExecutionRunResultImpl(
            final int aExitValue,
            final String aStderr,
            final String aStdout) {
        this.exitValue = aExitValue;
        this.stderr = aStderr;
        this.stdout = aStdout;
    }

    /**
     *
     * @return exit value of the run
     */
    @Override
    public int getExitValue() {
        return exitValue;
    }

    /**
     *
     * @return stderr output of the run
     */
    @Override
    public String getStderrResult() {
        return stderr;
    }

    /**
     *
     * @return stdout output of the run
     */
    @Override
    public String getStdoutResult() {
        return stdout;
    }
}
