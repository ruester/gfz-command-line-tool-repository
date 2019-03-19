package org.n52.gfz.riesgos.algorithm;

import org.n52.gfz.riesgos.cmdexecution.IExecutionContext;
import org.n52.gfz.riesgos.cmdexecution.IExecutionContextManager;
import org.n52.gfz.riesgos.cmdexecution.IExecutionRun;
import org.n52.gfz.riesgos.cmdexecution.IExecutionRunResult;
import org.n52.gfz.riesgos.cmdexecution.docker.DockerContainerExecutionContextManagerImpl;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.IIdentifierWithBinding;
import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;
import org.n52.gfz.riesgos.exceptions.NonZeroExitValueException;
import org.n52.gfz.riesgos.exceptions.WriteToStdinException;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IConvertStderrToIData;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;
import org.n52.gfz.riesgos.functioninterfaces.IConvertStdoutToIData;
import org.n52.gfz.riesgos.functioninterfaces.IWriteToStdin;
import org.n52.wps.io.data.IData;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;
import org.n52.wps.server.ExceptionReport;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Abstract base class that can be used for an more
 * configuration like approach for a branch of command line
 * applications that run inside of docker.
 */
public abstract class AbstractGfzRiesgosProcess extends AbstractSelfDescribingAlgorithm {

    private final IConfiguration configuration;
    private final Logger logger;
    private final List<IIdentifierWithBinding> inputIdentifiers;
    private final List<IIdentifierWithBinding> outputIdentifiers;
    private final Map<String, Class<?>> mapInputDataTypes;
    private final Map<String, Class<?>> mapOutputDataTypes;


    /**
     * Constructor that only gets a configuration and a logger
     * @param configuration configuration to use for the executable
     * @param logger logger to log some messages
     */
    public AbstractGfzRiesgosProcess(final IConfiguration configuration, final Logger logger) {
        this.configuration = configuration;
        this.logger = logger;

        this.inputIdentifiers = configuration.getInputIdentifiers();
        this.outputIdentifiers = configuration.getOutputIdentifiers();
        this.mapInputDataTypes = extractMapInputDataTypes(configuration);
        this.mapOutputDataTypes = extractMapOutputDataTypes(configuration);
    }

    /*
     * transforms the input data to a map to lookup the types in a predefined fast way
     */
    private Map<String, Class<?>> extractMapInputDataTypes(final IConfiguration configuration) {
        return configuration
                .getInputIdentifiers()
                .stream()
                .collect(Collectors.toMap(IIdentifierWithBinding::getIdentifer, IIdentifierWithBinding::getBindingClass));
    }

    /*
     * transforms the output data to a map to lookup the types in a predefined fast way
     */
    private Map<String, Class<?>> extractMapOutputDataTypes(final IConfiguration configuration) {
        return configuration
                .getOutputIdentifiers()
                .stream()
                .collect(Collectors.toMap(IIdentifierWithBinding::getIdentifer, IIdentifierWithBinding::getBindingClass));
    }

    /**
     *
     * @return List with the names of the input identifiers
     */
    @Override
    public List<String> getInputIdentifiers() {
        return inputIdentifiers.stream().map(IIdentifierWithBinding::getIdentifer).collect(Collectors.toList());
    }

    /**
     *
     * @return List with the names of the output identifiers
     */
    @Override
    public List<String> getOutputIdentifiers() {
        return outputIdentifiers.stream().map(IIdentifierWithBinding::getIdentifer).collect(Collectors.toList());
    }

    /**
     * method for all the work of the algorithm
     * extracts the input data
     * runs the executable
     * returns the output data
     * @param inputDataFromMethod input data from the wps service
     * @return Map with IData as results
     * @throws ExceptionReport maybe a ExceptionReport is thrown to handle errors in the service
     */
    @Override
    public Map<String, IData> run(final Map<String, List<IData>> inputDataFromMethod) throws ExceptionReport {
        final InnerRunContext innerRunContext = new InnerRunContext(inputDataFromMethod);
        return innerRunContext.run();

    }

    /**
     * Lookup for the binding class of the input data
     * @param id identifier of the input dataset
     * @return binding class (for example GenericXMLBinding or LiteralStringBinding)
     */
    @Override
    public Class<?> getInputDataType(final String id) {
        if(mapInputDataTypes.containsKey(id)) {
            return mapInputDataTypes.get(id);
        }
        return null;
    }

    /**
     * Lookup for the binding class for the output data
     * @param id identifier of the output dataset
     * @return binding class (for example GenericXMLBinding or LiteralStringBinding)
     */
    @Override
    public Class<?> getOutputDataType(final String id) {
        if(mapOutputDataTypes.containsKey(id)) {
            return mapOutputDataTypes.get(id);
        }
        return null;
    }

    /**
     * InnerRunContext
     * here are the Maps with the input and output data for each run
     */
    private class InnerRunContext {

        private final Map<String, IData> inputData;
        private final Map<String, IData> outputData;

        /**
         * Constructor with the original input data
         * @param originalInputData Map with input data from the service
         * @throws ExceptionReport maybe a ExceptionReport is thrown to handle the errors in the process
         */
        private InnerRunContext(final Map<String, List<IData>> originalInputData) throws ExceptionReport {
            this.inputData = getInputFields(originalInputData);
            this.outputData = new HashMap<>();
        }

        /**
         * Set the input fields and runs the script
         * @return Map with IData as the results
         * @throws ExceptionReport maybe a ExceptionReport is thrown to handle the errors in the process
         */
        private Map<String, IData> run() throws ExceptionReport {
            logger.debug("Start run");
            runExecutable();
            return outputData;
        }

        private Map<String, IData> getInputFields(final Map<String, List<IData>> originalInputData) throws ExceptionReport {

            logger.debug("Start getInputFields");
            final Map<String, IData> result = new HashMap<>();

            for(final IIdentifierWithBinding inputValue : inputIdentifiers) {
                final String identifier = inputValue.getIdentifer();
                if(! originalInputData.containsKey(identifier)) {
                    throw new ExceptionReport("There is no data for the identifier '" + identifier + "'", ExceptionReport.MISSING_PARAMETER_VALUE);
                }
                final List<IData> list = originalInputData.get(inputValue.getIdentifer());
                if(list.isEmpty()) {
                    throw new ExceptionReport("There is just an empty list for the identifier '" + identifier + "'", ExceptionReport.MISSING_PARAMETER_VALUE);
                }
                final IData firstElement = list.get(0);

                final Class<? extends IData> bindingClass = inputValue.getBindingClass();
                if(! bindingClass.isInstance(firstElement)) {
                    throw new ExceptionReport("There is not the expected Binding class for the identifier '" + identifier + "'", ExceptionReport.INVALID_PARAMETER_VALUE);
                }
                final Optional<ICheckDataAndGetErrorMessage> optionalValidator = inputValue.getValidator();
                if(optionalValidator.isPresent()) {
                    final ICheckDataAndGetErrorMessage validator = optionalValidator.get();
                    final Optional<String> errorMessage = validator.check(firstElement);
                    if(errorMessage.isPresent()) {
                        throw new ExceptionReport("There is invalid input for the identifier '" + identifier + "'. " + errorMessage.get(), ExceptionReport.INVALID_PARAMETER_VALUE);
                    }
                }

                result.put(identifier, firstElement);
            }

            return result;
        }

        private void runExecutable() throws ExceptionReport {

            final String imageId = configuration.getImageId();
            final String workingDirectory = configuration.getWorkingDirectory();
            final List<String> cmd = createCommandToExecute();

            final IExecutionContextManager contextManager = new DockerContainerExecutionContextManagerImpl(imageId);

            try(final IExecutionContext context = contextManager.createExecutionContext(workingDirectory, cmd)) {
                logger.debug("Docker container created");
                runExecutableInContext(context);
            }
            logger.debug("Docker container removed");
        }

        private List<String> createCommandToExecute() {
            final List<String> result = new ArrayList<>();
            result.addAll(configuration.getCommandToExecute());

            result.addAll(configuration.getDefaultCommandLineFlags());

            for(final IIdentifierWithBinding inputValue : inputIdentifiers) {
                final Optional<IConvertIDataToCommandLineParameter> functionToTransformToCmd = inputValue.getFunctionToTransformToCmd();
                if(functionToTransformToCmd.isPresent()) {
                    final List<String> args = functionToTransformToCmd.get().convertToCommandLineParameter(inputData.get(inputValue.getIdentifer()));
                    result.addAll(args);
                }
            }

            return result;
        }

        private void runExecutableInContext(final IExecutionContext context) throws ExceptionReport {
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

                    logger.debug("Handling of stderr/exitvalue/stdout finished");

                    readFromOutputFiles(context);

                    logger.debug("Getting files out of the container finished");
                } catch(final InterruptedException interruptedException) {
                    throw new ExceptionReport("Can't wait for process termination", ExceptionReport.REMOTE_COMPUTATION_ERROR, interruptedException);
                }
            } catch(final IOException ioException) {
                throw new ExceptionReport("Can't handle input and output", ExceptionReport.REMOTE_COMPUTATION_ERROR, ioException);
            }
        }

        private void copyInput(final IExecutionContext context) throws ExceptionReport {

            try {
                for (final IIdentifierWithBinding inputValue : inputIdentifiers) {
                    final Optional<String> optionalPath = inputValue.getPathToWriteToOrReadFromFile();
                    final Optional<IConvertIDataToByteArray> optionalFunctionToGetBytesToWrite = inputValue.getFunctionToGetBytesToWrite();

                    if (optionalPath.isPresent() && optionalFunctionToGetBytesToWrite.isPresent()) {
                        final String path = optionalPath.get();
                        final IConvertIDataToByteArray functionToGetBytesToWrite = optionalFunctionToGetBytesToWrite.get();
                        final byte[] content = functionToGetBytesToWrite.convertToBytes(inputData.get(inputValue.getIdentifer()));
                        context.writeToFile(content, configuration.getWorkingDirectory(), path);

                    }
                }
            } catch(final IOException ioException) {
                throw new ExceptionReport("Files could not be copied to the working directory", ExceptionReport.REMOTE_COMPUTATION_ERROR, ioException);
            }
        }

        private void writeToStdin(final PrintStream stdin) throws ExceptionReport {
            try {
                for (final IIdentifierWithBinding inputValue : inputIdentifiers) {
                    final Optional<IWriteToStdin> optionalFunctionToWriteToStdin = inputValue.getFunctionToWriteToStdin();
                    if (optionalFunctionToWriteToStdin.isPresent()) {
                        final IWriteToStdin functionToWriteToStdin = optionalFunctionToWriteToStdin.get();
                        functionToWriteToStdin.writeToStdin(stdin, inputData.get(inputValue.getIdentifer()));
                    }
                }
            } catch(final WriteToStdinException exception) {
                throw new ExceptionReport("Can't write to stdin", ExceptionReport.REMOTE_COMPUTATION_ERROR, exception);
            }
        }

        private void handleStderr(final String stderr) throws ExceptionReport {

            final Optional<IStderrHandler> mainStderrHandler = configuration.getStderrHandler();
            if(mainStderrHandler.isPresent()) {
                try {
                    mainStderrHandler.get().handleSterr(stderr);
                } catch (final NonEmptyStderrException exception) {
                    throw new ExceptionReport("There is an error on stderr", ExceptionReport.REMOTE_COMPUTATION_ERROR, exception);
                }
            }
            try {
                for (final IIdentifierWithBinding outputValue : outputIdentifiers) {
                    final Optional<IConvertStderrToIData> stderrHandler = outputValue.getFunctionToHandleStderr();
                    if (stderrHandler.isPresent()) {
                        final IData iData = stderrHandler.get().convertToIData(stderr);
                        putIntoOutput(outputValue, iData);
                    }
                }
            } catch(final ConvertToIDataException convertException) {
                throw new ExceptionReport("Can't read from stderr", ExceptionReport.REMOTE_COMPUTATION_ERROR, convertException);
            }

        }

        private void putIntoOutput(final IIdentifierWithBinding outputValue, final IData iData) throws ExceptionReport {
            final Optional<ICheckDataAndGetErrorMessage> optionalValidator = outputValue.getValidator();
            if(optionalValidator.isPresent()) {
                final ICheckDataAndGetErrorMessage validator = optionalValidator.get();

                final Optional<String> optionalErrorMessage = validator.check(iData);
                if(optionalErrorMessage.isPresent()) {
                    final String errorMessage = optionalErrorMessage.get();
                    throw new ExceptionReport("The output for '" + outputValue.getIdentifer() + "' is not valid:\n" + errorMessage, ExceptionReport.REMOTE_COMPUTATION_ERROR);
                }
            }
            outputData.put(outputValue.getIdentifer(), iData);
        }

        private void handleExitValue(final int exitValue) throws ExceptionReport {
            final Optional<IExitValueHandler> mainExitValueHandler = configuration.getExitValueHandler();
            if (mainExitValueHandler.isPresent()) {
                try {
                    mainExitValueHandler.get().handleExitValue(exitValue);
                } catch( final NonZeroExitValueException exception){
                    throw new ExceptionReport("There is a non empty exit value", ExceptionReport.REMOTE_COMPUTATION_ERROR, exception);
                }
            }
            try {
                for (final IIdentifierWithBinding outputValue : outputIdentifiers) {
                    final Optional<IConvertExitValueToIData> exitValueHandler = outputValue.getFunctionToHandleExitValue();
                    if (exitValueHandler.isPresent()) {
                        final IData iData = exitValueHandler.get().convertToIData(exitValue);
                        putIntoOutput(outputValue, iData);
                    }
                }
            } catch(final ConvertToIDataException convertException) {
                throw new ExceptionReport("Can't read from exit value", ExceptionReport.REMOTE_COMPUTATION_ERROR, convertException);
            }
        }

        private void handleStdout(final String stdout) throws ExceptionReport {
            final Optional<IStdoutHandler> mainStdoutHandler = configuration.getStdoutHandler();
            mainStdoutHandler.ifPresent(handler -> handler.handleStdout(stdout));

            try {
                for (final IIdentifierWithBinding outputValue : outputIdentifiers) {
                    final Optional<IConvertStdoutToIData> stdoutHandler = outputValue.getFunctionToHandleStdout();
                    if (stdoutHandler.isPresent()) {
                        final IData iData = stdoutHandler.get().convertToIData(stdout);
                        putIntoOutput(outputValue, iData);
                    }
                }
            } catch(final ConvertToIDataException convertException) {
                throw new ExceptionReport("Can't read from stdout", ExceptionReport.REMOTE_COMPUTATION_ERROR, convertException);
            }
        }

        private void readFromOutputFiles(final IExecutionContext context) throws ExceptionReport {

            try {
                for(final IIdentifierWithBinding outputValue : outputIdentifiers) {
                    final Optional<String> optionalPath = outputValue.getPathToWriteToOrReadFromFile();
                    final Optional<IConvertByteArrayToIData> optionalFunctionToReadFromBytes = outputValue.getFunctionToReadFromBytes();
                    if(optionalPath.isPresent() && optionalFunctionToReadFromBytes.isPresent()) {
                        final String path = optionalPath.get();
                        final IConvertByteArrayToIData functionToReadFromBytes = optionalFunctionToReadFromBytes.get();
                        final byte[] content = context.readFromFile(path);
                        final IData iData = functionToReadFromBytes.convertToIData(content);
                        putIntoOutput(outputValue, iData);
                    }
                }
            } catch(final IOException ioException) {
                throw new ExceptionReport("Files could not be read", ExceptionReport.REMOTE_COMPUTATION_ERROR, ioException);
            } catch(final ConvertToIDataException convertException) {
                throw new ExceptionReport("Data could not be converted", ExceptionReport.REMOTE_COMPUTATION_ERROR, convertException);
            }
        }
    }



}
