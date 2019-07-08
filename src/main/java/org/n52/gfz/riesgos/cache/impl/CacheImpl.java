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

package org.n52.gfz.riesgos.cache.impl;

import org.n52.gfz.riesgos.cache.IInputParameterCacheKey;
import org.n52.gfz.riesgos.cache.ICacher;
import org.n52.gfz.riesgos.cache.dockerimagehandling.IDockerImageIdLookup;
import org.n52.gfz.riesgos.cache.inputparametercachekey.InputParameterCacheKeyByException;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.util.Tuple;
import org.n52.wps.io.data.IData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the caching mechanism.
 *
 * TODO: Things that are not supported at the moment:
 * - The full mechanism of how to read the data (at the moment
 *   the mechanism is the same for elements that are read from files
 *   and elements that are read from files but given to the program
 *   as command line arguments)
 * - The output data mechanism can't handle temporary files at the moment.
 *   This is especially true for the current handling of shapefiles.
 * - The whole mechanism should be permanent (so store the data in a database).
 * - Test with optional input parameters must be written.
 */
public class CacheImpl implements ICacher {

    /**
     * Instance to lookup the docker image id.
     */
    private final IDockerImageIdLookup imageIdLookup;

    /**
     * Map to save the data.
     */
    private final Map<CacheKey, Map<String, IData>> cache;


    /**
     * Constructor with the function to lookup the image id.
     * @param aImageIdLookup function to look up the image id
     */
    public CacheImpl(final IDockerImageIdLookup aImageIdLookup) {

        this.imageIdLookup = aImageIdLookup;
        cache = new HashMap<>();
    }

    /**
     * This is the method to ask the caching system about if it has data in it.
     * If there is no data for in the caching system, than it will just give
     * back Optional.empty
     *
     * @param configuration configuration that is used to process the data
     * @param inputData     input data
     * @return optional map with output data
     */
    @Override
    public Optional<Map<String, IData>> getCachedResult(
            final IConfiguration configuration,
            final Map<String, List<IData>> inputData) {

        final CacheKey key = generateCacheKey(configuration, inputData);

        if (cache.containsKey(key)) {
            return Optional.ofNullable(cache.get(key));
        }
        return Optional.empty();
    }

    /**
     * This is the method to call once the algorithm is done and the
     * result should be included in the caching system.
     *
     * @param configuration configuration used to process the data
     * @param inputData     input data
     * @param outputData    resulting data to store
     */
    @Override
    public void insertResultIntoCache(
            final IConfiguration configuration,
            final Map<String, List<IData>> inputData,
            final Map<String, IData> outputData) {

        final CacheKey key = generateCacheKey(configuration, inputData);
        cache.put(key, outputData);
    }

    /**
     * Function to generate a key for storing data in a map.
     * @param configuration configuration that is used
     * @param inputData map with input data
     * @return CacheKey for querying and storing data in the cache map.
     */
    private CacheKey generateCacheKey(
            final IConfiguration configuration,
            final Map<String, List<IData>> inputData) {


        return new CacheKey(
                configuration,
                imageIdLookup.lookUpImageId(configuration.getImageId()),
                inputData);
    }

    /**
     * Class for storing the data as keys.
     */
    private static class CacheKey {


        /**
         * Full qualified indentifier of the data.
         */
        private final String fullQualifiedIdentifier;

        /**
         * Image id to run ("real" image id, not the labeled one
         * anymore).
         */
        private final String imageId;

        /**
         * Working directory of the run.
         */
        private final String workingDirectory;

        /**
         * Command of the run.
         */
        private final List<String> commandToExecute;

        /**
         * List of default command line parameters.
         */
        private final List<String> defaultCommandLineFlags;

        /**
         * Optional exit value handler.
         */
        private final IExitValueHandler exitValueHandler;
        /**
         * Optional stderr text handler.
         */
        private final IStderrHandler stderrHandler;

        /**
         * Optional stdout text handler.
         */
        private final IStdoutHandler stdoutHandler;

        /**
         * List with all the output parameters (that they are and
         * how the data should be read from the run).
         */
        private final List<IOutputParameter> outputParameters;

        /**
         * List with the keys for caching the input data.
         * This includes the mechanism (how the data is read)
         * and the data itself together with the identifier.
         *
         * Having a list instead of a map allows us to care
         * about the ordering (important for the command line arguments).
         *
         * TODO: Maybe this must be extended to care about the binding classes
         *       (the mechanism cares a bit about it for how to
         *       read the data in which is included in reading the content
         *       to create the cache key)
         */
        private final List<Tuple<String, IInputParameterCacheKey>>
                inputCacheKeyMap;

        /**
         * Constructor with the configuration, a image id and the
         * input data.
         * @param configuration configuration to use for caching
         * @param aImageId real image id to use for running the code
         * @param inputData input data to execute the code with
         */
        CacheKey(final IConfiguration configuration,
                 final String aImageId,
                 final Map<String, List<IData>> inputData) {


            this.fullQualifiedIdentifier =
                    configuration.getFullQualifiedIdentifier();
            this.imageId = aImageId;

            // abstract does not matter for the caching

            workingDirectory = configuration.getWorkingDirectory();
            commandToExecute = configuration.getCommandToExecute();
            defaultCommandLineFlags =
                    configuration.getDefaultCommandLineFlags();


            exitValueHandler = configuration.getExitValueHandler().orElse(null);
            stderrHandler = configuration.getStderrHandler().orElse(null);
            stdoutHandler = configuration.getStdoutHandler().orElse(null);

            // just to now the handling of the output
            outputParameters = configuration.getOutputIdentifiers();

            inputCacheKeyMap = new ArrayList<>();

            for (final IInputParameter inputParameter
                    : configuration.getInputIdentifiers()) {
                try {
                    final List<IData> iDataList =
                            inputData.get(inputParameter.getIdentifier());
                    final IData iData = iDataList.get(0);

                    final IInputParameterCacheKey cacheKey =
                            inputParameter
                                    .getFunctionToGenerateCacheKey()
                                    .generateCacheKey(
                                            iData);
                    inputCacheKeyMap.add(
                            new Tuple<>(
                                    inputParameter.getIdentifier(),
                                    cacheKey));
                } catch (final Exception exception) {
                    inputCacheKeyMap.add(
                            new Tuple<>(
                                    inputParameter.getIdentifier(),
                                    new InputParameterCacheKeyByException(
                                            exception)));
                }
            }
        }

        /**
         * Tests equality.
         * @param o other object
         * @return true if both are equal
         */
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CacheKey cacheKey = (CacheKey) o;
            return Objects.equals(fullQualifiedIdentifier,
                            cacheKey.fullQualifiedIdentifier)
                    && Objects.equals(imageId, cacheKey.imageId)
                    && Objects.equals(workingDirectory,
                            cacheKey.workingDirectory)
                    && Objects.equals(commandToExecute,
                            cacheKey.commandToExecute)
                    && Objects.equals(defaultCommandLineFlags,
                            cacheKey.defaultCommandLineFlags)
                    && Objects.equals(exitValueHandler,
                            cacheKey.exitValueHandler)
                    && Objects.equals(stderrHandler, cacheKey.stderrHandler)
                    && Objects.equals(stdoutHandler, cacheKey.stdoutHandler)
                    && Objects.equals(outputParameters,
                            cacheKey.outputParameters)
                    && Objects.equals(inputCacheKeyMap,
                    cacheKey.inputCacheKeyMap);
        }

        /**
         * Generates the hash code.
         * @return hash code of the object.
         */
        @Override
        public int hashCode() {
            return Objects.hash(
                    fullQualifiedIdentifier,
                    imageId,
                    workingDirectory,
                    commandToExecute,
                    defaultCommandLineFlags,
                    exitValueHandler,
                    stderrHandler,
                    stdoutHandler,
                    outputParameters,
                    inputCacheKeyMap);
        }
    }
}
