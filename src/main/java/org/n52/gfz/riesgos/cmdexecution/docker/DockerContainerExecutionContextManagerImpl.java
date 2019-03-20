package org.n52.gfz.riesgos.cmdexecution.docker;

import org.n52.gfz.riesgos.cmdexecution.IExecutionContext;
import org.n52.gfz.riesgos.cmdexecution.IExecutionContextManager;
import org.n52.gfz.riesgos.cmdexecution.IExecutionRun;
import org.n52.gfz.riesgos.cmdexecution.IExecutionRunResult;
import org.n52.gfz.riesgos.cmdexecution.common.ExecutionRunImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DockerContainerExecutionContextManagerImpl implements IExecutionContextManager {

    private final String imageId;

    public DockerContainerExecutionContextManagerImpl(final String imageId) {
        this.imageId = imageId;
    }

    @Override
    public IExecutionContext createExecutionContext(String workingDirectory, List<String> cmd) {
        final String containerId = runCreateContainerProcess(workingDirectory, cmd);
        return new DockerExecutionContextImpl(containerId);
    }

    private String runCreateContainerProcess(final String workingDirectory, final List<String> cmd) {
        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(createCommand(workingDirectory, cmd));

        try {
            final Process process = processBuilder.start();
            final IExecutionRun run = new ExecutionRunImpl(process);

            final IExecutionRunResult result = run.waitForCompletion();

            final String errorText = result.getStderrResult();
            if(! errorText.isEmpty()) {
                throw new RuntimeException("Can't create the container:"  + errorText);
            }
            final int exitValue = result.getExitValue();
            if(exitValue != 0) {
                throw new RuntimeException("Can't create the container. Exit value != 0: " + exitValue);
            }

            return result.getStdoutResult().trim();

        } catch(final IOException ioException) {
            throw new RuntimeException(ioException);
        } catch(final InterruptedException interruptedException) {
            throw new RuntimeException(interruptedException);
        }
    }

    private List<String> createCommand(final String workingDirectory, final List<String> cmd) {
        final List<String> result = new ArrayList<>();

        result.add("docker");
        result.add("container");
        result.add("create");
        result.add("--attach");
        result.add("STDOUT");
        result.add("--attach");
        result.add("STDERR");
        result.add("--interactive");
        result.add("--workdir");
        result.add(workingDirectory);
        result.add("--restart");
        result.add("no");

        for(final String cap : Arrays.asList("chown", "dac_override", "fowner", "fsetid", "kill", "setgid", "setuid", "setpcap",
                "net_bind_service", "net_raw", "sys_chroot", "mknod", "audit_write", "setfcap")) {
            result.add("--cap-drop");
            result.add(cap);
        }

        result.add(imageId);

        result.addAll(cmd);

        return result;
    }
}
