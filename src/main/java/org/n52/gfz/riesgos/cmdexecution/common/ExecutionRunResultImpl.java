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
 * Implementation of the result of running a process
 */
public class ExecutionRunResultImpl implements IExecutionRunResult {

    private final int exitValue;
    private final String stderr;
    private final String stdout;

    /**
     *
     * @param exitValue exit value of the process
     * @param stderr joined stderr text
     * @param stdout joined stdout text
     */
    ExecutionRunResultImpl(final int exitValue, final String stderr, final String stdout) {
        this.exitValue = exitValue;
        this.stderr = stderr;
        this.stdout = stdout;
    }

    @Override
    public int getExitValue() {
        return exitValue;
    }

    @Override
    public String getStderrResult() {
        return stderr;
    }

    @Override
    public String getStdoutResult() {
        return stdout;
    }
}
