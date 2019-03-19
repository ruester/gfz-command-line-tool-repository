package org.n52.gfz.riesgos.cmdexecution;

import java.io.PrintStream;

public interface IExecutionRun {

    public PrintStream getStdin();

    public IExecutionRunResult waitForCompletion() throws InterruptedException;

}
