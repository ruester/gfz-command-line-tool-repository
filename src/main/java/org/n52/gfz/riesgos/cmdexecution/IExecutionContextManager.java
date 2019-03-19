package org.n52.gfz.riesgos.cmdexecution;

import java.util.List;

public interface IExecutionContextManager {

    public IExecutionContext createExecutionContext(final String workingDirectory, final List<String> cmd);
}
