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

package org.n52.gfz.riesgos.cmdexecution.docker;

import org.n52.gfz.riesgos.cmdexecution.IExecutionContextManager;
import org.n52.gfz.riesgos.cmdexecution.util.IExecutionContextManagerFactory;
import org.n52.gfz.riesgos.configuration.IConfiguration;

/**
 * Factory for the IExecutionContextManager (that creates the image in case
 * of docker).
 */
public class DockerExecutionContextManagerFactory
        implements IExecutionContextManagerFactory {

    /**
     * Creates a DockerContainerExecutionContextManager (so a class
     * that can create a new docker container).
     * @param configuration configuration for the creation
     * @return DockerContainerExecutionContextManagerImpl
     */
    @Override
    public IExecutionContextManager createExecutionContext(
            final IConfiguration configuration) {
        return new DockerContainerExecutionContextManagerImpl(
                configuration.getImageId());
    }
}
