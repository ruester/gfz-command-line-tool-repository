package org.n52.gfz.riesgos.cmdexecution.docker;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.n52.gfz.riesgos.cmdexecution.IExecutionContext;
import org.n52.gfz.riesgos.cmdexecution.IExecutionRun;
import org.n52.gfz.riesgos.cmdexecution.IExecutionRunResult;
import org.n52.gfz.riesgos.cmdexecution.common.ExecutionRunImpl;
import org.n52.gfz.riesgos.cmdexecution.util.ThreadedStreamStringReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class DockerExecutionContextImpl implements IExecutionContext {

    private final String containerId;

    public DockerExecutionContextImpl(final String containerId) {
        this.containerId = containerId;
    }

    @Override
    public void close() {
        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(createRemoveCommand());
        try {
            final Process process = processBuilder.start();
            final IExecutionRun run = new ExecutionRunImpl(process);
            final IExecutionRunResult result = run.waitForCompletion();

            final String errorText = result.getStderrResult();
            final int exitValue = result.getExitValue();

            if(! errorText.isEmpty()) {
                throw new RuntimeException("The command to remove the docker container failed:\n" + errorText);
            }
            if(exitValue != 0) {
                throw new RuntimeException("The command to remove the docker container failed with exit value " + exitValue);
            }

        } catch(final InterruptedException interruptedException) {
            throw new RuntimeException(interruptedException);
        } catch(final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private List<String> createRemoveCommand() {
        return Arrays.asList("docker", "container", "rm", containerId);
    }

    @Override
    public IExecutionRun run() throws IOException {
        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(createRunCommand());
        final Process process = processBuilder.start();
        return new ExecutionRunImpl(process);
    }

    private List<String> createRunCommand() {
        return Arrays.asList("docker", "container", "start", "--interactive", "--attach", containerId);
    }

    @Override
    public byte[] readFromFile(String path) throws IOException {
        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(createCopyToHostCommand(path));

        final Process process = processBuilder.start();
        final InputStream stdout = process.getInputStream();

        final ThreadedStreamStringReader stderr = new ThreadedStreamStringReader(process.getErrorStream());
        stderr.start();

        try(final TarArchiveInputStream tarInputStream = new TarArchiveInputStream(stdout)) {
            tarInputStream.getNextEntry();
            final byte[] result = IOUtils.toByteArray(tarInputStream);

            final int exitValue = process.waitFor();
            process.destroy();
            stdout.close();
            tarInputStream.close();

            final String errorText = stderr.getResult();

            if(! errorText.isEmpty()) {
                throw new IOException(errorText);
            }
            if(exitValue != 0) {
                throw new IOException("Exit value for copying to host is not zero: " + exitValue);
            }

            return result;
        } catch(final InterruptedException interruptedException) {
            throw new IOException(interruptedException);
        }
    }

    private List<String> createCopyToHostCommand(final String path) {
        final String src = containerId + ":" + path;
        return Arrays.asList("docker", "container", "cp", src, "-");
    }

    @Override
    public void writeToFile(byte[] content, String workingDir, String fileName) throws IOException {
        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(createCopyFromContainerCommand(workingDir));

        final Process process = processBuilder.start();
        final OutputStream stdin = process.getOutputStream();
        final ThreadedStreamStringReader stderr = new ThreadedStreamStringReader(process.getErrorStream());
        stderr.start();

        try(final TarArchiveOutputStream tarOutputStream = new TarArchiveOutputStream(stdin)) {
            final TarArchiveEntry entry = new TarArchiveEntry(fileName);
            entry.setSize((long) content.length);
            tarOutputStream.putArchiveEntry(entry);
            IOUtils.write(content, tarOutputStream);

            tarOutputStream.closeArchiveEntry();
            tarOutputStream.finish();

            tarOutputStream.close();
            stdin.close();

            final int exitValue = process.waitFor();
            process.destroy();

            final String errorText = stderr.getResult();

            if(! errorText.isEmpty()) {
                throw new IOException(errorText);
            }
            if(exitValue != 0) {
                throw new IOException("Exit value for copying to container is not zero " + exitValue);
            }
        } catch(final InterruptedException interruptedException) {
            throw new IOException(interruptedException);
        }
    }

    private List<String> createCopyFromContainerCommand(final String path) {
        final String dest = containerId + ":" + path;
        return Arrays.asList("docker", "container", "cp", "-", dest);
    }
}
