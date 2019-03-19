package org.n52.gfz.riesgos.cmdexecution;

public interface IExecutionRunResult  {

    public int getExitValue();

    public String getStderrResult();

    public String getStdoutResult();
}
