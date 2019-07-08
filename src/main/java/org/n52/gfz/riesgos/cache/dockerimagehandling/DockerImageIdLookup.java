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

package org.n52.gfz.riesgos.cache.dockerimagehandling;

import org.n52.gfz.riesgos.cmdexecution.IExecutionRun;
import org.n52.gfz.riesgos.cmdexecution.IExecutionRunResult;
import org.n52.gfz.riesgos.cmdexecution.common.ExecutionRunImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This is a class to get the "real" image id that
 * is used for an image.
 *
 * It is meant to be used in case that there
 * is a an labeled image id as "quakeledger:latest"
 * so that we want to get the full image id (for example:
 * sha256:1f9dc932fdb0a41d2896c8d61056c44fb96c337fcdcb507632d575d6cc8922c2
 */
public class DockerImageIdLookup implements IDockerImageIdLookup {

    /**
     * Asks docker about the image id of the given label.
     * In case it is already a real image id than
     * docker will just return the id that was given (if
     * it exists on the system).
     * @param imageIdWithLabel given image id (maybe with label)
     * @return image id
     */
    @Override
    public String lookUpImageId(final String imageIdWithLabel) {


        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(createCommand(imageIdWithLabel));

        try {
            final Process process = processBuilder.start();
            final IExecutionRun run = new ExecutionRunImpl(process);

            final IExecutionRunResult result = run.waitForCompletion();

            final String errorText = result.getStderrResult();
            if (!errorText.isEmpty()) {
                throw new RuntimeException(
                        "Can't check the image id:"  + errorText);
            }
            final int exitValue = result.getExitValue();
            if (exitValue != 0) {
                throw new RuntimeException(
                        "Can't check the image id. Exit value != 0: "
                                + exitValue);
            }

            return result.getStdoutResult().trim();

        } catch (final IOException | InterruptedException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    /**
     * Creates a list of strings for a command to ask the
     * system about the real image id.
     * @param imageIdWithLabel given image id from the configuration
     * @return returns a list with the cmds to ask the system about
     * the real image id.
     */
    private List<String> createCommand(final String imageIdWithLabel) {
        return Arrays.asList(
                "docker",
                "image",
                "inspect",
                imageIdWithLabel,
                "--format",
                "{{.ID}}"
        );
    }
}
