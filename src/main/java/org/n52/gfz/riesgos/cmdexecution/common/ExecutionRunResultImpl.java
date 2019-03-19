package org.n52.gfz.riesgos.cmdexecution.common;

import org.n52.gfz.riesgos.cmdexecution.IExecutionRunResult;

public class ExecutionRunResultImpl implements IExecutionRunResult {

    private final int exitValue;
    private final String stderr;
    private final String stdout;

    public ExecutionRunResultImpl(final int exitValue, final String stderr, final String stdout) {
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
