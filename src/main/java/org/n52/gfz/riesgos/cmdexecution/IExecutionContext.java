package org.n52.gfz.riesgos.cmdexecution;

import java.io.IOException;

public interface IExecutionContext extends AutoCloseable {

    public void close();

    public IExecutionRun run() throws IOException;

    public byte[] readFromFile(final String path) throws IOException;

    public void writeToFile(final byte[] content, final String workingDir, final String fileName) throws IOException;
}
