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

package org.n52.gfz.riesgos.cache.hash;

import org.apache.commons.codec.binary.Hex;
import org.n52.gfz.riesgos.cache.IInputParameterCacheKey;
import org.n52.gfz.riesgos.cache.dockerimagehandling.IDockerImageIdLookup;
import org.n52.gfz.riesgos.cache.inputparametercachekey.InputParameterCacheKeyByException;
import org.n52.gfz.riesgos.cache.wpsversionhandling.IWpsVersionHandler;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.util.Tuple;
import org.n52.wps.io.data.IData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of the hasher, that takes the input
 * data, the configuration and the docker image id and versions
 * into account.
 */
public class HasherImpl implements IHasher {

    /**
     * MD5 algorithm to compute hashes.
     */
    private static final MessageDigest MESSAGE_DIGEST = getMd5();

    /**
     * Function to get the md5 algorithm.
     * @return MessageDigest with md5 algorithm
     */
    private static MessageDigest getMd5() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (final NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Lookup function for the image id and the docker version.
     */
    private final IDockerImageIdLookup imageIdLookup;
    /**
     * Lookup function for the wps and repository version.
     */
    private final IWpsVersionHandler wpsVersionHandler;

    /**
     * Constructor with some handlers for docker images and server versions.
     * @param aImageIdLookup handler for asking for real image ids
     * @param aWpsVersionHandler handler for asking for versions
     */
    public HasherImpl(final IDockerImageIdLookup aImageIdLookup,
                      final IWpsVersionHandler aWpsVersionHandler) {
        this.imageIdLookup = aImageIdLookup;
        this.wpsVersionHandler = aWpsVersionHandler;
    }

    /**
     * Creates a hash from the configuration and the input data.
     * @param configuration configuration used for the process
     * @param inputData input data for the process
     * @param requestedParameters output parameters that the user requested
     * @return hash for the overall input environment and the
     * output handling
     */
    @Override
    public String hash(
            final IConfiguration configuration,
            final Map<String, List<IData>> inputData,
            final Set<String> requestedParameters) {

        final CacheKey key = new CacheKey(
                configuration,
                imageIdLookup.lookUpImageId(configuration.getImageId()),
                inputData,
                requestedParameters,
                imageIdLookup.getDockerVersion(),
                wpsVersionHandler.getRepositoryVersion(),
                wpsVersionHandler.getWpsVersion());

        final ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream =
                     new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(key);

            final byte[] md5 =
                    MESSAGE_DIGEST.digest(byteArrayOutputStream.toByteArray());

            MESSAGE_DIGEST.reset();
            return Hex.encodeHexString(md5);

        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }



    /**
     * Class for storing the data as keys.
     */
    private static class CacheKey implements Serializable {

        private static final long serialVersionUID = -3303094796676261450L;

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
         * A set with the output parameters that the user requested.
         */
        private final Set<String> requestedParameters;
        /**
         * The version of docker.
         */
        private final String dockerVersion;

        /**
         * Version of the wps server.
         */
        private final String wpsVersion;
        /**
         * Version of our repository.
         */
        private final String repositoryVersion;

        /**
         * List with the keys for caching the input data.
         * This includes the mechanism (how the data is read)
         * and the data itself together with the identifier.
         * <p>
         * Having a list instead of a map allows us to care
         * about the ordering (important for the command line arguments).
         * <p>
         * (the mechanism cares a bit about it for how to
         * read the data in which is included in reading the content
         * to create the cache key)
         */
        private final List<Tuple<String, IInputParameterCacheKey>>
                inputCacheKeyMap;

        /**
         * Constructor with the configuration, a image id and the
         * input data.
         *
         * @param configuration configuration to use for caching
         * @param aImageId      real image id to use for running the code
         * @param inputData     input data to execute the code with
         * @param aRequestedParameters set of output parameters that the user
         *                             requested
         * @param aDockerVersion docker version of the docker daemon
         * @param aWpsVersion version of the wps server
         * @param aRepositoryVersion version of the repository
         */
        CacheKey(final IConfiguration configuration,
                 final String aImageId,
                 final Map<String, List<IData>> inputData,
                 final Set<String> aRequestedParameters,
                 final String aDockerVersion,
                 final String aWpsVersion,
                 final String aRepositoryVersion) {


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
            this.requestedParameters = aRequestedParameters;

            inputCacheKeyMap = new ArrayList<>();

            for (final IInputParameter inputParameter
                    : configuration.getInputIdentifiers()) {
                try {
                    final List<IData> iDataList =
                            inputData.get(inputParameter.getIdentifier());
                    final IData iData = iDataList.get(0);

                    @SuppressWarnings("unchecked")
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
                                    exception,
                                    inputParameter
                                            .getPathToWriteToOrReadFromFile()
                                            .orElse(null),
                                    inputParameter.isOptional())));
                }
            }

            this.dockerVersion = aDockerVersion;
            this.wpsVersion = aWpsVersion;
            this.repositoryVersion = aRepositoryVersion;
        }

        /**
         * Tests equality.
         *
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
                    && Objects.equals(requestedParameters,
                    cacheKey.requestedParameters)
                    && Objects.equals(inputCacheKeyMap,
                    cacheKey.inputCacheKeyMap)
                    && Objects.equals(dockerVersion, cacheKey.dockerVersion)
                    && Objects.equals(wpsVersion, cacheKey.wpsVersion)
                    && Objects.equals(repositoryVersion,
                    cacheKey.repositoryVersion);
        }

        /**
         * Generates the hash code.
         *
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
                    requestedParameters,
                    inputCacheKeyMap,
                    dockerVersion,
                    wpsVersion,
                    repositoryVersion);
        }
    }
}
