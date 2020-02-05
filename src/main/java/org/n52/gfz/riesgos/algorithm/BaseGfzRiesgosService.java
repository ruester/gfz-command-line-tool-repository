package org.n52.gfz.riesgos.algorithm;

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

import net.opengis.wps.x100.ProcessDescriptionsDocument;
import net.opengis.wps.x20.OutputDefinitionType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.n52.gfz.riesgos.cache.DataWithRecreatorTuple;
import org.n52.gfz.riesgos.cache.ICacher;
import org.n52.gfz.riesgos.cache.IDataRecreator;
import org.n52.gfz.riesgos.cache.RecreateFromByteArray;
import org.n52.gfz.riesgos.cache.RecreateFromExitValue;
import org.n52.gfz.riesgos.cache.hash.IHasher;
import org.n52.gfz.riesgos.cmdexecution.IExecutionContext;
import org.n52.gfz.riesgos.cmdexecution.IExecutionContextManager;
import org.n52.gfz.riesgos.cmdexecution.IExecutionRun;
import org.n52.gfz.riesgos.cmdexecution.IExecutionRunResult;
import org.n52.gfz.riesgos.cmdexecution.util.IExecutionContextManagerFactory;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IInputParameter;
import org.n52.gfz.riesgos.configuration.IOutputParameter;
import org.n52.gfz.riesgos.exceptions.ConvertToBytesException;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.exceptions.ConvertToStringCmdException;
import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;
import org.n52.gfz.riesgos.exceptions.NonZeroExitValueException;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.gfz.riesgos.functioninterfaces.IReadIDataFromFiles;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.functioninterfaces.IWriteIDataToFiles;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGenerator;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorData;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGeneratorOutputData;
import org.n52.gfz.riesgos.processdescription.impl.ProcessDescriptionGeneratorDataConfigImpl;
import org.n52.gfz.riesgos.processdescription.impl.ProcessDescriptionGeneratorImpl;
import org.n52.gfz.riesgos.util.Tuple;
import org.n52.wps.commons.context.ExecutionContext;
import org.n52.wps.commons.context.ExecutionContextFactory;
import org.n52.wps.commons.context.OutputTypeWrapper;
import org.n52.wps.io.data.IData;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;
import org.n52.wps.server.ExceptionReport;
import org.n52.wps.server.ProcessDescription;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base class that can be used for an more
 * configuration like approach for a branch of command line
 * applications that run inside of docker.
 *
 * The processes should be created by creating an instance of this class.
 */
public class BaseGfzRiesgosService
        extends AbstractSelfDescribingAlgorithm
        implements ICachableProcess {

    /**
     * The hasher to compute a hash for saving it in the cache.
     */
    private final IHasher hasher;
    /**
     * The cache to save data inside, so that there is no need
     * to run the process inside again.
     */
    private final ICacher cache;

    /**
     * Factory to create an execution manager for the
     * command line process.
     * The most common way is the use of docker.
     */
    private final IExecutionContextManagerFactory executionContextFactory;

    /**
     * Configuration to run the process.
     */
    private final IConfiguration configuration;

    /**
     * Logger for this service.
     * Not static to differentiating between several instances of this
     * class.
     */
    private final Logger logger;

    /**
     * A list of input parameters from the configuration.
     */
    private final List<IInputParameter> inputIdentifiers;

    /**
     * A list of output parameters form the configuration.
     */
    private final List<IOutputParameter> outputIdentifiers;

    /**
     * A map to look up the binding classes for the inputs
     * by input identifier.
     */
    private final Map<String, Class<?>> mapInputDataTypes;

    /**
     * A map to look up the binding classes for the outputs
     * by output identifier.
     */
    private final Map<String, Class<?>> mapOutputDataTypes;


    /**
     * Constructor that  gets a configuration, a logger,
     * a hasher, a cache and a execution context factory.
     * @param aConfiguration configuration to use for the executable
     * @param aLogger logger to log some messages
     * @param aHasher function to compute stable hashes for the inputs and
     *               configuration
     * @param aCache implementation of the cache
     * @param aExecutionContextFactory factory for creating execution contexts
     *                                (like running in docker or not)
     */
    public BaseGfzRiesgosService(
            final IConfiguration aConfiguration,
            final Logger aLogger,
            final IHasher aHasher,
            final ICacher aCache,
            final IExecutionContextManagerFactory aExecutionContextFactory) {

        this.hasher = aHasher;
        this.cache = aCache;
        this.executionContextFactory = aExecutionContextFactory;

        this.configuration = aConfiguration;
        this.logger = aLogger;

        this.inputIdentifiers = configuration.getInputIdentifiers();
        this.outputIdentifiers = configuration.getOutputIdentifiers();
        this.mapInputDataTypes = extractMapInputDataTypes(configuration);
        this.mapOutputDataTypes = extractMapOutputDataTypes(configuration);
    }

    /**
     * To be able to serve a process that gives back all the
     * output from a cache-key we provide access to the used cache.
     *
     * For the productive services the cache is shared for
     * the whole system.
     * @return cache of the process
     */
    @Override
    public ICacher getCache() {
        return cache;
    }

    /**
     * To be able to serve a process that gives back all the
     * output from a cache-key we give the data for
     * the process description here.
     * @return list with output parameter data
     */
    @Override
    public List<IProcessDescriptionGeneratorOutputData>
    getOutputDataForProcessGeneration() {
        return toProcessDescriptionGeneratorData().getOutputData();
    }

    /**
     * Helper method to have the creation of the
     * IProcessDescriptionGeneratorData only at one point.
     * @return IProcessDescriptionGeneratorData
     */
    private IProcessDescriptionGeneratorData
    toProcessDescriptionGeneratorData() {
        return new ProcessDescriptionGeneratorDataConfigImpl(configuration);
    }


    /**
     * Transforms the input data to a map to lookup the types
     * in a predefined fast way.
     *
     * @param aConfiguration configuration to use
     * @return map with the binding classes by identifier
     */
    private Map<String, Class<?>> extractMapInputDataTypes(
            final IConfiguration aConfiguration) {
        return aConfiguration
                .getInputIdentifiers()
                .stream()
                .collect(Collectors.toMap(
                        IInputParameter::getIdentifier,
                        IInputParameter::getBindingClass
                ));
    }

    /**
     * Transforms the output data to a map to lookup the
     * types in a predefined fast way.
     * @param aConfiguration configuration to use
     * @return map with the binding classes by identifier
     */
    private Map<String, Class<?>> extractMapOutputDataTypes(
            final IConfiguration aConfiguration) {
        return aConfiguration
                .getOutputIdentifiers()
                .stream()
                .collect(Collectors.toMap(
                        IOutputParameter::getIdentifier,
                        IOutputParameter::getBindingClass
                ));
    }

    /**
     *
     * @return List with the names of the input identifiers
     */
    @Override
    public List<String> getInputIdentifiers() {
        return inputIdentifiers.stream()
                .map(IInputParameter::getIdentifier)
                .collect(Collectors.toList());
    }

    /**
     *
     * @return List with the names of the output identifiers
     */
    @Override
    public List<String> getOutputIdentifiers() {
        return outputIdentifiers.stream()
                .map(IOutputParameter::getIdentifier)
                .collect(Collectors.toList());
    }

    /**
     * Method for all the work of the algorithm.
     * - extracts the input data
     * - runs the executable
     * - returns the output data
     *
     * Now it also computes hashes and looks up and stores
     * in a caching system.
     *
     * @param inputDataFromMethod input data from the wps service
     * @return Map with IData as results
     * @throws ExceptionReport maybe a ExceptionReport is thrown to
     * handle errors in the service
     */
    @Override
    public Map<String, IData> run(
            final Map<String, List<IData>> inputDataFromMethod)
            throws ExceptionReport {

        final Set<String> requestedParameters = getSetWithRequestedOutputIds();
        final String hash = hasher.hash(
                configuration,
                inputDataFromMethod,
                requestedParameters);

        logger.info("Cache-Hash: " + hash);

        final Optional<Map<String, IDataRecreator>> cachedResult =
                cache.getCachedResult(hash);

        if (cachedResult.isPresent()) {
            logger.info("Read the results from cache");

            return cachedResult.get().entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().recreate()
            ));
        }

        logger.info("There is no result in the cache");

        final InnerRunContext innerRunContext =
                new InnerRunContext(inputDataFromMethod);
        final Map<String, Tuple<IData, IDataRecreator>> innerResult =
                innerRunContext.run();

        final Map<String, IDataRecreator> dataToStoreInCache =
                innerResult.entrySet().stream()
                        .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().getSecond()
        ));

        cache.insertResultIntoCache(hash, dataToStoreInCache);

        return innerResult.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getFirst()
        ));
    }

    /**
     * This returns a set of the identifiers (as strinds) that the user
     * requests.
     * @return Set with output parameter identifiers.
     */
    private Set<String> getSetWithRequestedOutputIds() {
        final Set<String> requestedParameters = new HashSet<>();

        final ExecutionContext wpsExecutionContext =
                ExecutionContextFactory.getContext();
        final OutputTypeWrapper outputTypeWrapper =
                wpsExecutionContext.getOutputs();

        if (outputTypeWrapper.isWPS100Execution()) {
            outputTypeWrapper.getWps100OutputDefinitionTypes().stream().map(
                    outputType -> outputType.getIdentifier().getStringValue()
            ).forEach(requestedParameters::add);
        } else {
            outputTypeWrapper.getWps200OutputDefinitionTypes().stream().map(
                    OutputDefinitionType::getId
            ).forEach(requestedParameters::add);
        }
        return requestedParameters;
    }

    /**
     * Lookup for the binding class of the input data.
     * @param id identifier of the input dataset
     * @return binding class (for example GenericXMLBinding
     * or LiteralStringBinding)
     */
    @Override
    public Class<?> getInputDataType(final String id) {
        if (mapInputDataTypes.containsKey(id)) {
            return mapInputDataTypes.get(id);
        }
        return null;
    }

    /**
     * Lookup for the binding class for the output data.
     * @param id identifier of the output dataset
     * @return binding class (for example GenericXMLBinding
     * or LiteralStringBinding)
     */
    @Override
    public Class<?> getOutputDataType(final String id) {
        if (mapOutputDataTypes.containsKey(id)) {
            return mapOutputDataTypes.get(id);
        }
        return null;
    }

    /**
     * Generates the process description by using the configuration.
     * @return ProcessDescription of the process (xml)
     */
    @Override
    public ProcessDescription getDescription() {

        final IProcessDescriptionGenerator generator =
                new ProcessDescriptionGeneratorImpl(
                    toProcessDescriptionGeneratorData());
        final ProcessDescriptionsDocument description =
                generator.generateProcessDescription();
        ProcessDescription processDescription = new ProcessDescription();
        processDescription.addProcessDescriptionForVersion(
                description
                        .getProcessDescriptions()
                        .getProcessDescriptionArray(0),
                "1.0.0");
        return processDescription;
    }

    /**
     * This is the inner run context.
     * Here are the Maps with the input and output data for each run
     */
    private final class InnerRunContext {

        /**
         * Map with the input data.
         */
        private final Map<String, IData> inputData;
        /**
         * Map with the output data to fill in.
         */
        private final Map<String, Tuple<IData, IDataRecreator>> outputData;

        /**
         * Constructor with the original input data.
         * @param originalInputData Map with input data from the service
         * @throws ExceptionReport maybe a ExceptionReport is thrown
         * to handle the errors in the process
         */
        private InnerRunContext(
                final Map<String, List<IData>> originalInputData)
                throws ExceptionReport {
            this.inputData = getInputFields(originalInputData);
            this.outputData = new HashMap<>();
        }

        /**
         * Set the input fields and runs the script.
         * @return Map with IData as the results
         * @throws ExceptionReport maybe a ExceptionReport is thrown
         * to handle the errors in the process
         */
        private Map<String, Tuple<IData, IDataRecreator>> run()
                throws ExceptionReport {
            logger.debug("Start run");
            runExecutable();
            return outputData;
        }

        /**
         * Extracts the map with the input data.
         * @param originalInputData original input data from the base service
         * @return Map only with Strings as keys and the idatas as values
         * @throws ExceptionReport exception that is thrown if there
         * is a need to handle errors in the process
         */
        private Map<String, IData> getInputFields(
                final Map<String, List<IData>> originalInputData)
                throws ExceptionReport {

            logger.debug("Start getInputFields");
            final Map<String, IData> result = new HashMap<>();

            for (final IInputParameter inputValue : inputIdentifiers) {
                final String identifier = inputValue.getIdentifier();
                if (!originalInputData.containsKey(identifier)) {
                    if (inputValue.isOptional()) {
                        continue;
                    } else {
                        throw new ExceptionReport(
                                "There is no data for the identifier '"
                                        + identifier
                                        + "'",
                                ExceptionReport.MISSING_PARAMETER_VALUE);
                    }
                }
                final List<IData> list = originalInputData.get(
                        inputValue.getIdentifier());
                if (list.isEmpty()) {
                    if (inputValue.isOptional()) {
                        // if the value is optional it is fine if there is none
                        continue;
                    } else {
                        throw new ExceptionReport(
                                "There is just an empty list "
                                        + "for the identifier '"
                                        + identifier
                                        + "'",
                                ExceptionReport.MISSING_PARAMETER_VALUE);
                    }
                }
                final IData firstElement = list.get(0);

                final Class<? extends IData> bindingClass =
                        inputValue.getBindingClass();
                if (!bindingClass.isInstance(firstElement)) {
                    throw new ExceptionReport(
                            "There is not the expected "
                                    + "binding class for the identifier '"
                                    + identifier
                                    + "'",
                            ExceptionReport.INVALID_PARAMETER_VALUE);
                }
                final Optional<ICheckDataAndGetErrorMessage> optionalValidator =
                        inputValue.getValidator();
                if (optionalValidator.isPresent()) {
                    final ICheckDataAndGetErrorMessage validator =
                            optionalValidator.get();
                    @SuppressWarnings("unchecked")
                    final Optional<String> errorMessage =
                            validator.check(firstElement);
                    if (errorMessage.isPresent()) {
                        throw new ExceptionReport(
                                "There is invalid input for the identifier '"
                                        + identifier
                                        + "'. "
                                        + errorMessage.get(),
                                ExceptionReport.INVALID_PARAMETER_VALUE);
                    }
                }

                result.put(identifier, firstElement);
            }

            return result;
        }

        /**
         * Runs the executable inside a context / container.
         * @throws ExceptionReport may throw this exception in case
         * of an error
         */
        private void runExecutable() throws ExceptionReport {

            final String workingDirectory = configuration.getWorkingDirectory();
            final List<String> cmd = createCommandToExecute();

            logger.debug("List with cmd-arguments: " + cmd);

            final IExecutionContextManager contextManager =
                    executionContextFactory.createExecutionContext(
                            configuration);

            try (IExecutionContext context =
                        contextManager.createExecutionContext(
                                workingDirectory, cmd)) {
                logger.debug("Context container created");
                runExecutableInContext(context);
            }
            logger.debug("Context container removed");
        }

        /**
         * Creates a list of the executable and the arguments.
         * Uses a list and not a single string argument to make
         * things more safe.
         *
         * @return list with command line
         * @throws ExceptionReport exception that is thrown in case of an
         * error
         */
        private List<String> createCommandToExecute() throws ExceptionReport {
            final List<String> result = new ArrayList<>();
            result.addAll(configuration.getCommandToExecute());

            result.addAll(configuration.getDefaultCommandLineFlags());

            try {
                for (final IInputParameter inputValue : inputIdentifiers) {
                    final Optional<IConvertIDataToCommandLineParameter>
                            functionToTransformToCmd =
                            inputValue.getFunctionToTransformToCmd();
                    if (functionToTransformToCmd.isPresent()
                            && inputData.containsKey(
                                    inputValue.getIdentifier())) {
                        @SuppressWarnings("unchecked")
                        final List<String> args =
                                functionToTransformToCmd
                                        .get().convertToCommandLineParameter(
                                            inputData.get(
                                                inputValue.getIdentifier()));
                        result.addAll(args);
                    }
                }
            } catch (final ConvertToStringCmdException exception) {
                throw new ExceptionReport(
                        "It is not valid to "
                                + "use the command line arguments",
                        ExceptionReport.INVALID_PARAMETER_VALUE,
                        exception);
            }

            return result;
        }

        /**
         * Runs the process and handles input and output.
         * @param context execution context to start the run.
         * @throws ExceptionReport exception that is thrown in case of
         * an error
         */
        private void runExecutableInContext(final IExecutionContext context)
                throws ExceptionReport {
            copyInput(context);
            logger.debug("Files copied into container");

            try {
                final IExecutionRun run = context.run();
                final PrintStream stdinStreamToWrite = run.getStdin();
                logger.debug("Executable started");

                writeToStdin(stdinStreamToWrite);

                stdinStreamToWrite.close();

                try {
                    final IExecutionRunResult result = run.waitForCompletion();

                    logger.debug("Executable finished");

                    handleStderr(result.getStderrResult());
                    handleExitValue(result.getExitValue());
                    handleStdout(result.getStdoutResult());

                    logger.debug(
                            "Handling of stderr/exitValue/stdout finished");

                    readFromOutputFiles(context);

                    logger.debug(
                            "Getting files out of the container finished");
                } catch (final InterruptedException interruptedException) {
                    throw new ExceptionReport(
                            "Can't wait for process termination",
                            ExceptionReport.REMOTE_COMPUTATION_ERROR,
                            interruptedException);
                }
            } catch (final IOException ioException) {
                throw new ExceptionReport(
                        "Can't handle input and output",
                        ExceptionReport.REMOTE_COMPUTATION_ERROR,
                        ioException);
            }
        }

        /**
         * Copies all the input files into the context / container.
         * @param context exeuction context / container
         * @throws ExceptionReport exception that is thrown in case
         * of an error
         */
        private void copyInput(
                final IExecutionContext context)
                throws ExceptionReport {

            try {
                for (final IInputParameter inputValue : inputIdentifiers) {
                    // if there is no data for that identifier it was optional
                    // so no need to copy the input
                    if (inputData.containsKey(inputValue.getIdentifier())) {
                        final Optional<String> optionalPath =
                                inputValue.getPathToWriteToOrReadFromFile();
                        final Optional<IWriteIDataToFiles>
                                optionalWriteIDataToFiles =
                                inputValue.getFunctionToWriteIDataToFiles();

                        if (optionalPath.isPresent()
                                && optionalWriteIDataToFiles.isPresent()) {
                            final String path = optionalPath.get();
                            final IWriteIDataToFiles writeIDataToFiles =
                                    optionalWriteIDataToFiles.get();
                            //noinspection unchecked
                            writeIDataToFiles.writeToFiles(
                                    inputData.get(
                                            inputValue.getIdentifier()),
                                    context,
                                    configuration.getWorkingDirectory(),
                                    path);
                        }
                    }
                }
            } catch (final IOException ioException) {
                throw new ExceptionReport(
                        "Files could not be copied to the "
                                + "working directory",
                        ExceptionReport.REMOTE_COMPUTATION_ERROR,
                        ioException);
            } catch (final ConvertToBytesException convertToBytesException) {
                throw new ExceptionReport(
                        "Data could not be "
                                + "converted to an input file",
                        ExceptionReport.REMOTE_COMPUTATION_ERROR,
                        convertToBytesException);
            }
        }

        /**
         * Writes the input to the stdin stream.
         * @param stdin stdin stream
         * @throws ExceptionReport exception that is thrown in case of
         * an error
         */
        private void writeToStdin(final PrintStream stdin)
                throws ExceptionReport {
            try {
                for (final IInputParameter inputValue : inputIdentifiers) {
                    if (inputData.containsKey(inputValue.getIdentifier())) {
                        final Optional<IConvertIDataToByteArray>
                                optionalFunctionToWriteToStdin =
                                inputValue.getFunctionToWriteToStdin();
                        if (optionalFunctionToWriteToStdin.isPresent()) {
                            final IConvertIDataToByteArray
                                    functionToWriteToStdin =
                                    optionalFunctionToWriteToStdin.get();
                            @SuppressWarnings("unchecked")
                            final byte[] content =
                                    functionToWriteToStdin
                                        .convertToBytes(
                                            inputData.get(
                                                inputValue.getIdentifier()));
                            IOUtils.write(content, stdin);
                        }
                    }
                }
            } catch (final IOException exception) {
                throw new ExceptionReport(
                        "Can't write to stdin",
                        ExceptionReport.REMOTE_COMPUTATION_ERROR,
                        exception);
            } catch (final ConvertToBytesException convertToBytesException) {
                throw new ExceptionReport(
                        "Data could not be converted to an text for stdin",
                        ExceptionReport.REMOTE_COMPUTATION_ERROR,
                        convertToBytesException);
            }
        }

        /**
         * Handes the stderr stream output (error, logging, use as output, ...).
         * @param stderr stderr text
         * @throws ExceptionReport exception in case of an error
         */
        private void handleStderr(final String stderr) throws ExceptionReport {

            final Optional<IStderrHandler> mainStderrHandler =
                    configuration.getStderrHandler();
            if (mainStderrHandler.isPresent()) {
                try {
                    mainStderrHandler.get().handleStderr(stderr, logger::debug);
                } catch (final NonEmptyStderrException exception) {
                    logger.error("Error on handling stderr", exception);
                    throw new ExceptionReport(
                        "There is an error on stderr: "
                        + exception.getMessage()
                        + ExceptionUtils.getStackTrace(exception),
                        ExceptionReport.REMOTE_COMPUTATION_ERROR,
                        exception
                    );
                }
            }
            try {
                for (final IOutputParameter outputValue : outputIdentifiers) {
                    final Optional<IConvertByteArrayToIData> stderrHandler =
                            outputValue.getFunctionToHandleStderr();

                    try {
                        if (stderrHandler.isPresent()) {
                            final byte[] bytes = stderr.getBytes();
                            final IConvertByteArrayToIData converter =
                                    stderrHandler.get();

                            final IData iData = converter.convertToIData(bytes);
                            putIntoOutput(outputValue,
                                    iData,
                                    new RecreateFromByteArray(
                                            bytes,
                                            converter,
                                            outputValue.getBindingClass()));
                        }
                    } catch (final ConvertToIDataException convertException) {
                        if (outputValue.isOptional()) {
                            logger.info("Can't read from stderr.");
                            logger.info("But since '"
                                    + outputValue.getIdentifier()
                                    + "' is optional, it can be ignored.");
                        } else {
                            throw convertException;
                        }
                    }
                }
            } catch (final ConvertToIDataException convertException) {
                throw new ExceptionReport(
                        "Can't read from stderr",
                        ExceptionReport.REMOTE_COMPUTATION_ERROR,
                        convertException);
            }

        }

        /*
         * insert the data into the output map
         * if there is an validator, then it is used here
         */

        /**
         * Inserts the data to the output map.
         * If there is a validator, then it is used here.
         * @param outputValue output parameter for which the data should be
         *                    inserted in the result map
         * @param iData data that should be inserted in the result map
         * @param dataRecreator recreator for the data (for the caching)
         * @throws ExceptionReport exception in case of an error
         */
        private void putIntoOutput(
                final IOutputParameter outputValue,
                final IData iData,
                final IDataRecreator dataRecreator)

                throws ExceptionReport {

            final Optional<ICheckDataAndGetErrorMessage> optionalValidator =
                    outputValue.getValidator();
            if (optionalValidator.isPresent()) {
                final ICheckDataAndGetErrorMessage validator =
                        optionalValidator.get();

                @SuppressWarnings("unchecked")
                final Optional<String> optionalErrorMessage =
                        validator.check(iData);
                if (optionalErrorMessage.isPresent()) {
                    final String errorMessage = optionalErrorMessage.get();
                    throw new ExceptionReport(
                            "The output for '"
                                    + outputValue.getIdentifier()
                                    + "' is not valid:\n"
                                    + errorMessage,
                            ExceptionReport.REMOTE_COMPUTATION_ERROR);
                }
            }
            outputData.put(
                    outputValue.getIdentifier(),
                    new Tuple<>(iData, dataRecreator));
        }

        /**
         * Handling of the exit value (error, logging, use as output).
         * @param exitValue exit value from the cmd program
         * @throws ExceptionReport exception that may be thrown
         * in case the exitValue indicates an error
         */
        private void handleExitValue(final int exitValue)
                throws ExceptionReport {
            final Optional<IExitValueHandler> mainExitValueHandler =
                    configuration.getExitValueHandler();
            if (mainExitValueHandler.isPresent()) {
                try {
                    mainExitValueHandler
                            .get()
                            .handleExitValue(exitValue, logger::debug);
                } catch (final NonZeroExitValueException exception) {
                    throw new ExceptionReport(
                            "There is a non empty exit value",
                            ExceptionReport.REMOTE_COMPUTATION_ERROR,
                            exception);
                }
            }
            try {
                for (final IOutputParameter outputValue : outputIdentifiers) {
                    try {
                        final Optional<IConvertExitValueToIData>
                                exitValueHandler =
                                outputValue.getFunctionToHandleExitValue();
                        if (exitValueHandler.isPresent()) {
                            final IConvertExitValueToIData converter =
                                    exitValueHandler.get();
                            final IData iData =
                                    converter.convertToIData(exitValue);
                            putIntoOutput(
                                    outputValue,
                                    iData,
                                    new RecreateFromExitValue(
                                            exitValue,
                                            converter,
                                            outputValue.getBindingClass()));
                        }
                    } catch (final ConvertToIDataException convertException) {
                        if (outputValue.isOptional()) {
                            logger.info("Can't read from exit value.");
                            logger.info("But since '"
                                    + outputValue.getIdentifier()
                                    + "' is optional, it can be ignored.");
                        } else {
                            throw convertException;
                        }
                    }

                }
            } catch (final ConvertToIDataException convertException) {
                throw new ExceptionReport(
                        "Can't read from exit value",
                        ExceptionReport.REMOTE_COMPUTATION_ERROR,
                        convertException);
            }
        }

        /**
         * Handles stdout stream output (logging, use as output).
         * @param stdout text from stdout
         * @throws ExceptionReport Exception that may be thrown in case of
         * an error
         */
        private void handleStdout(final String stdout) throws ExceptionReport {
            final Optional<IStdoutHandler> mainStdoutHandler
                    = configuration.getStdoutHandler();
            mainStdoutHandler.ifPresent(
                    handler -> handler.handleStdout(stdout));

            try {
                for (final IOutputParameter outputValue : outputIdentifiers) {
                    try {
                        final Optional<IConvertByteArrayToIData> stdoutHandler
                                = outputValue.getFunctionToHandleStdout();
                        if (stdoutHandler.isPresent()) {
                            final byte[] bytes = stdout.getBytes();
                            final IConvertByteArrayToIData converter =
                                    stdoutHandler.get();
                            final IData iData = converter.convertToIData(bytes);
                            putIntoOutput(
                                    outputValue,
                                    iData,
                                    new RecreateFromByteArray(
                                            bytes,
                                            converter,
                                            outputValue.getBindingClass()));
                        }
                    } catch (final ConvertToIDataException convertException) {
                        if (outputValue.isOptional()) {
                            logger.info("Can't read from stdout.");
                            logger.info("But since '"
                                    + outputValue.getIdentifier()
                                    + "' is optional, it can be ignored.");
                        } else {
                            throw convertException;
                        }
                    }
                }
            } catch (final ConvertToIDataException convertException) {
                throw new ExceptionReport("Can't read from stdout",
                        ExceptionReport.REMOTE_COMPUTATION_ERROR,
                        convertException);
            }
        }


        /**
         * Reads the output files from the context / container.
         * @param context execution context / container with the output files
         * @throws ExceptionReport exception that is thrown in case of an
         * error
         */
        private void readFromOutputFiles(
                final IExecutionContext context) throws ExceptionReport {

            final Set<String> requestedParameters =
                    getSetWithRequestedOutputIds();
            try {
                for (final IOutputParameter outputValue : outputIdentifiers) {
                    if (requestedParameters.contains(
                            outputValue.getIdentifier())
                    ) {
                        try {
                            final Optional<String> optionalPath =
                                outputValue.getPathToWriteToOrReadFromFile();
                            final Optional<IReadIDataFromFiles>
                                optionalFunctionToReadFromFiles =
                                outputValue.getFunctionToReadIDataFromFiles();
                            if (optionalPath.isPresent()
                                && optionalFunctionToReadFromFiles.isPresent()
                            ) {

                                final String path = optionalPath.get();
                                final IReadIDataFromFiles
                                        functionToReadFromFiles =
                                        optionalFunctionToReadFromFiles.get();
                                final DataWithRecreatorTuple readResult =
                                        functionToReadFromFiles.readFromFiles(
                                            context,
                                            configuration.getWorkingDirectory(),
                                            path);
                                putIntoOutput(
                                        outputValue,
                                        readResult.getData(),
                                        readResult.getRecreator());
                            }
                        } catch (final IOException
                                | ConvertToIDataException exception) {
                            if (outputValue.isOptional()) {
                                logger.info("Can't read from output file.");
                                logger.info("But since '"
                                        + outputValue.getIdentifier()
                                        + "' is optional, it can be ignored.");
                            } else {
                                throw exception;
                            }
                        }
                    }
                }
            } catch (final IOException ioException) {
                logger.error("Files could not be read", ioException);
                throw new ExceptionReport(
                        "Files could not be read"
                        + ioException.getMessage()
                        + ExceptionUtils.getStackTrace(ioException),
                        ExceptionReport.REMOTE_COMPUTATION_ERROR,
                        ioException);
            } catch (final ConvertToIDataException convertException) {
                logger.error("Data could not be converted", convertException);
                throw new ExceptionReport(
                        "Data could not be converted"
                        + convertException.getMessage()
                        + ExceptionUtils.getStackTrace(convertException),
                        ExceptionReport.REMOTE_COMPUTATION_ERROR,
                        convertException);
            }
        }
    }
}
