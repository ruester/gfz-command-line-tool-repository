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

import org.n52.gfz.riesgos.cmdexecution.IExecutionRun;
import org.n52.gfz.riesgos.cmdexecution.IExecutionRunResult;
import org.n52.gfz.riesgos.cmdexecution.util.ThreadedStreamStringReader;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Implementation that provides stdin, exit value
 * and provides the results of sterr and stdout streams
 */
public class ExecutionRunImpl implements IExecutionRun {

    private final Process process;

    private final PrintStream stdin;
    private final ThreadedStreamStringReader stderr;
    private final ThreadedStreamStringReader stdout;

    /**
     *
     * @param process the process to wrap
     */
    public ExecutionRunImpl(final Process process) {
        this.process = process;

        stdin = new PrintStream(process.getOutputStream());
        stderr = new ThreadedStreamStringReader(process.getErrorStream());
        stdout = new ThreadedStreamStringReader(process.getInputStream());
        stderr.start();
        stdout.start();
    }

    @Override
    public PrintStream getStdin() {
        return stdin;
    }

    @Override
    public IExecutionRunResult waitForCompletion() throws InterruptedException {
        stdin.close();

        final int exitValue = process.waitFor();
        process.destroy();

        final String stderrText = stderr.getResult();
        final String stdoutText = stdout.getResult();

        try {
            stderr.throwExceptionIfNecessary();
            stdout.throwExceptionIfNecessary();
        } catch(final IOException ioException) {
            throw new RuntimeException(ioException);
        }

        return new ExecutionRunResultImpl(exitValue, stderrText, stdoutText);
    }
}
