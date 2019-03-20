package org.n52.gfz.riesgos.cmdexecution.common;

import org.n52.gfz.riesgos.cmdexecution.IExecutionRun;
import org.n52.gfz.riesgos.cmdexecution.IExecutionRunResult;
import org.n52.gfz.riesgos.cmdexecution.util.ThreadedStreamStringReader;

import java.io.PrintStream;

public class ExecutionRunImpl implements IExecutionRun {

    private final Process process;

    private final PrintStream stdin;
    private final ThreadedStreamStringReader stderr;
    private final ThreadedStreamStringReader stdout;

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

        return new ExecutionRunResultImpl(exitValue, stderrText, stdoutText);
    }
}
