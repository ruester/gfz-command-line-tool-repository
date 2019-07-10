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

import org.junit.Ignore;
import org.junit.Test;
import org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService;
import org.n52.gfz.riesgos.cache.dockerimagehandling.DockerImageIdLookup;
import org.n52.gfz.riesgos.cache.impl.CacheImpl;
import org.n52.gfz.riesgos.configuration.ConfigurationFactory;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.bbox.BoundingBoxData;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.server.ExceptionReport;
import org.n52.wps.server.IAlgorithm;
import org.slf4j.LoggerFactory;
import sun.misc.Cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static junit.framework.TestCase.fail;

/**
 * This is a test that can only run on a system with
 * docker and with all the docker images to run
 * the quakeledger configuration.
 *
 * It will create a list of runs that run simultaneously
 * and try to reproduce a problem on reading the container ids
 * on their creation.
 *
 * As some of the fails are in other threads this test
 * may be displayed as succeeded even if there is a fail,
 * so this test is for debugging only.
 */
@Ignore("Only for debugging purpose")
public class TestContainerIdCreation {

    /**
     * Runs the queries.
     */
    @Test
    public void runNQueries() {
        final int n = 100;

        final List<Thread> threads = IntStream.range(0, n).mapToObj(this::createRunThread).collect(Collectors.toList());

        threads.forEach(Thread::start);

        final Optional<InterruptedException> optionalInterruptedException =
                threads.stream()
                .map(this::join)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        if(optionalInterruptedException.isPresent()) {
            optionalInterruptedException.get().printStackTrace();
            fail("There should be no interrupted exception");
        }
    }

    private Thread createRunThread(@SuppressWarnings("unused") final int i) {
        return new Thread(new StartQueryRunnable());
    }

    private Optional<InterruptedException> join(final Thread t) {
        try {
            t.join();
            return Optional.empty();
        } catch (final InterruptedException interruptedException) {
            return Optional.of(interruptedException);
        }
    }

    private static class StartQueryRunnable implements Runnable {
        @Override
        public void run() {
            final IConfiguration conf = ConfigurationFactory.createQuakeledger();

            final IAlgorithm algorithm = new BaseGfzRiesgosService(conf, LoggerFactory.getLogger(TestContainerIdCreation.class),
                    // caching should not be involved in this test
                    // but the docker image id lookup can be done
                    new CacheImpl(new DockerImageIdLookup()), new DockerExecutionContextManagerFactory());

            final Map<String, List<IData>> inputData = new HashMap<>();

            inputData.put("input-boundingbox", Collections.singletonList(new BoundingBoxData(new double[] {-71.8, -33.2}, new double[]{ -71.4, -33.0}, "EPSG:4326")));
            inputData.put("mmin", Collections.singletonList(new LiteralDoubleBinding(6.6)));
            inputData.put("mmax", Collections.singletonList(new LiteralDoubleBinding(8.5)));
            inputData.put("zmin", Collections.singletonList(new LiteralDoubleBinding(5.0)));
            inputData.put("zmax", Collections.singletonList(new LiteralDoubleBinding(140.0)));
            inputData.put("p", Collections.singletonList(new LiteralDoubleBinding(0.1)));
            inputData.put("etype", Collections.singletonList(new LiteralStringBinding("deaggregation")));
            inputData.put("tlon", Collections.singletonList(new LiteralDoubleBinding(-71.5730623712764)));
            inputData.put("tlat", Collections.singletonList(new LiteralDoubleBinding(-33.1299174879672)));

            try {
                algorithm.run(inputData);
            } catch(final ExceptionReport exceptionReport) {
                exceptionReport.printStackTrace();

                fail("There should be no exception on running the process");
            }
        }
    }
}
