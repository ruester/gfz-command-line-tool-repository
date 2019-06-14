package org.n52.gfz.riesgos.repository.modules;

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

import org.apache.commons.io.IOUtils;
import org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService;
import org.n52.gfz.riesgos.algorithm.TransformDataFormatProcess;
import org.n52.gfz.riesgos.configuration.ConfigurationFactory;
import org.n52.gfz.riesgos.configuration.IConfiguration;
import org.n52.gfz.riesgos.configuration.parse.IParseConfiguration;
import org.n52.gfz.riesgos.configuration.parse.json.ParseJsonConfigurationImpl;
import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;
import org.n52.gfz.riesgos.formats.IMimeTypeAndSchemaConstants;
import org.n52.gfz.riesgos.formats.quakeml.binding.QuakeMLXmlDataBinding;
import org.n52.gfz.riesgos.formats.shakemap.binding.ShakemapXmlDataBinding;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.repository.GfzRiesgosRepository;
import org.n52.gfz.riesgos.validators.XmlBindingWithAllowedSchema;
import org.n52.wps.io.data.IComplexData;
import org.n52.wps.server.IAlgorithm;
import org.n52.wps.server.ProcessDescription;
import org.n52.wps.webapp.api.AlgorithmEntry;
import org.n52.wps.webapp.api.ClassKnowingModule;
import org.n52.wps.webapp.api.ConfigurationCategory;
import org.n52.wps.webapp.api.FormatEntry;
import org.n52.wps.webapp.api.types.ConfigurationEntry;
import org.n52.wps.webapp.api.types.StringConfigurationEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Configuration module for the gfz riesgos repository.
 *
 * The aim is to provide all the information about the process
 * configurations here, so it should be possible to
 * add a process description on runtime and to execute it immediately.
 */
public class GfzRiesgosRepositoryCM extends ClassKnowingModule {

    /**
     * The key (the name) for the fields that can be configured.
     * In this case the json_configuration folder
     * for putting the js configurations in.
     */
    private static final String CONFIG_KEY = "json_configuration";

    /**
     * Logger for the class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GfzRiesgosRepositoryCM.class);

    /**
     * This is the default folder that is used to
     * read the json configuration files from.
     */
    private static final String DEFAULT_CONFIGURATION_FOLDER =
            "/usr/share/riesgos/json-configurations";

    /**
     * Name of the repository.
     */
    private static final String MODULE_NAME =
            "GFZ RIESGOS Configuration Module";
    /**
     * Name of the class of the repository that will be configured here.
     */
    private static final String CLASS_NAME_OF_REPOSITORY_TO_CONFIG =
            GfzRiesgosRepository.class.getName();
    /**
     * The category of this configuration module
     * is for a wps algorithm repository.
     */
    private static final ConfigurationCategory CATEGORY =
            ConfigurationCategory.REPOSITORY;

    /**
     * List with all of the configuration entries.
     */
    private final List<? extends ConfigurationEntry<?>> configurationEntries;

    /**
     * Wrapper around the string to store
     * the path for the configuration files.
     */
    private final ConfigurationEntry<String> jsonConfigurationFolder;

    /**
     * Boolean to indicate if this repository is active
     * or not.
     */
    private boolean isActive;

    /**
     * Default constructor that takes no arguments.
     */
    public GfzRiesgosRepositoryCM() {
        isActive = true;
        jsonConfigurationFolder = new StringConfigurationEntry(
                CONFIG_KEY,
                "JSON Process Configuration Folder",
                "Folder that contains the json files to add "
                + "/ remove / configure the wps processes that use the "
                + "skeleton to run command line processes in docker.",
                true, DEFAULT_CONFIGURATION_FOLDER);
        configurationEntries = Collections.singletonList(
                jsonConfigurationFolder);
    }

    /**
     *
     * @return name of the repository class to configure.
     */
    @Override
    public String getClassName() {
        return CLASS_NAME_OF_REPOSITORY_TO_CONFIG;
    }

    /**
     *
     * @return Name of the configuration module itself.
     */
    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    /**
     *
     * @return true if the configuration module is active.
     */
    @Override
    public boolean isActive() {
        return isActive;
    }

    /**
     * Set whether the repository should be active.
     * @param active the status active=true/false
     */
    @Override
    public void setActive(final boolean active) {
        isActive = active;
    }

    /**
     *
     * @return the category of the configuration
     * module
     */
    @Override
    public ConfigurationCategory getCategory() {
        return CATEGORY;
    }

    /**
     *
     * @return list with all the configuration entries
     */
    @Override
    public List<? extends ConfigurationEntry<?>> getConfigurationEntries() {
        return configurationEntries;
    }

    /**
     *
     * @return list with all the algorithm entries
     */
    @Override
    public List<AlgorithmEntry> getAlgorithmEntries() {
        return parseConfigToAlgorithmEntries().stream()
                .map(AlgorithmData::toAlgorithmEntry)
                .collect(Collectors.toList());
    }

    /**
     * This is not a configuration module for formats,
     * so no format entries will be returned.
     * @return null
     */
    @Override
    public List<FormatEntry> getFormatEntries() {
        return null;
    }

    /**
     *
     * @return list with the predefined configurations
     * that are integrated by default
     */
    private List<IConfiguration> createPredefinedConfigurations() {
        return Arrays.asList(
                ConfigurationFactory.createQuakeledger(),
                ConfigurationFactory.createShakyground(),
                ConfigurationFactory.createFlooddamage()
        );
    }

    /**
     *
     * @return list with algoritm data with all
     * configurations (the predefined ones, the
     * ones for transforming formats and the user defined
     * ones).
     */
    private List<AlgorithmData> parseConfigToAlgorithmEntries() {

        final List<AlgorithmData> result = new ArrayList<>();

        // first, insert all the data format transformation processes
        addAlgorithmsOfFormatTransformations(result::add);

        // then load all the configurations for the custom processes
        // using this approach the predefined services
        // can be overwritten by improved ones on server runtime
        final Map<String, IConfiguration> configurationProcesses =
                new HashMap<>();

        // step 1: the predefined ones
        for (final IConfiguration predefinedConfig
                : createPredefinedConfigurations()) {
            configurationProcesses.put(
                    predefinedConfig.getIdentifier(),
                    predefinedConfig
            );
        }

        // others can be added by using the folder
        addConfigurationsFromFolder(
                this::getFileNamesFromConfig,
                configurationProcesses::put);

        // than add all to the result
        configurationProcesses.values().stream()
                .map(this::configurationToAlgorithm)
                .forEach(result::add);


        return result;
    }

    /**
     *
     * @param adder consumer (mostly the add method of a list) to consume the
     *              class transformation processes
     */
    private void addAlgorithmsOfFormatTransformations(
            final Consumer<AlgorithmData> adder) {
        for (ClassTransformationProcess transformationProcess
                : Arrays.asList(
                    new ClassTransformationProcess(
                        QuakeMLXmlDataBinding.class,
                        "QuakeMLTransformationProcess",
                        new XmlBindingWithAllowedSchema(
                                IMimeTypeAndSchemaConstants.SCHEMA_QUAKE_ML),
                        "Process to transform quakeml between various formats"),
                    new ClassTransformationProcess(
                        ShakemapXmlDataBinding.class,
                        "ShakemapTransformationProcess",
                        new XmlBindingWithAllowedSchema(
                                IMimeTypeAndSchemaConstants.SCHEMA_SHAKEMAP),
                        "Process to transform shakemaps between "
                            + "various formats")
        )) {
            final String processName = transformationProcess.getProcessName();
            final AlgorithmData algorithmData = new AlgorithmData(
                    processName,
                    new TransformDataFormatProcess(
                            processName,
                            transformationProcess.getClazz(),
                            LoggerFactory.getLogger(processName),
                            transformationProcess.getValidator(),
                            transformationProcess.getOptionalAbstract()));
            adder.accept(algorithmData);
        }
    }

    /**
     * Function to wrap the configurations into a algorithm data.
     * @param configuration configuration to transform into an
     *                      algorithm data object
     * @return algorithm data
     */
    private AlgorithmData configurationToAlgorithm(
            final IConfiguration configuration) {
        return new AlgorithmData(configuration.getIdentifier(),
                new BaseGfzRiesgosService(configuration,
                        LoggerFactory.getLogger(
                                configuration.getFullQualifiedIdentifier())));
    }

    /**
     *
     * @return list of json files in the given
     * configuration folder
     */
    private Collection<String> getFileNamesFromConfig() {
        return getFileNamesFrom(
                new File(jsonConfigurationFolder.getValue()),
                this::filterJsonFiles);
    }

    /**
     *
     * @param file file to test
     * @return true if the file has an json ending
     */
    private boolean filterJsonFiles(final File file) {
        return file.isFile()
            && file.getAbsolutePath()
                        .toLowerCase()
                        .endsWith(".json");
    }

    /**
     *
     * @param folder folder to scan
     * @param fileFilter file filter to only process specifc
     *                   files
     * @return collection of file names from the directory
     */
    private Collection<String> getFileNamesFrom(
            final File folder,
            final FileFilter fileFilter) {
        if (!folder.exists()) {
            LOGGER.error("The folder '"
                    + folder.getAbsolutePath()
                    + "' does not exist.");
            return Collections.emptyList();
        } else if (!folder.isDirectory()) {
            LOGGER.error("The file '"
                    + folder.getAbsolutePath()
                    + "' is not a folder.");
            return Collections.emptyList();
        }

        final File[] files = folder.listFiles(fileFilter);
        final File[] filesNotNull = Optional.ofNullable(files)
                .orElseGet(this::emptyFileArray);

        return Stream.of(filesNotNull)
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
    }

    /**
     * This is used as a fallback mode.
     * @return empty file array
     */
    private File[] emptyFileArray() {
        return new File[]{};
    }

    /**
     * Function to traverse all the files and to
     * parse them to configurations.
     * @param fileProvider supplier with a collection of file names
     * @param adder bi consumer to add the configurations
     *              with a given name and the configuration
     */
    private void addConfigurationsFromFolder(
            final Supplier<Collection<String>> fileProvider,
            final BiConsumer<String, IConfiguration> adder) {
        final IParseConfiguration parser = new ParseJsonConfigurationImpl();

        for (final String fileName : fileProvider.get()) {
            try (FileInputStream inputStream =
                        new FileInputStream(fileName)) {
                final String content = new String(
                        IOUtils.toByteArray(inputStream));
                final IConfiguration configuration = parser.parse(content);

                adder.accept(configuration.getIdentifier(), configuration);

            } catch (final IOException ioException) {
                LOGGER.error("Can't read the content from file '"
                        + fileName
                        + "': "
                        + ioException);
            } catch (final ParseConfigurationException parseConfigException) {
                LOGGER.error("Can't parse the content of file '"
                        + fileName
                        + "': "
                        + parseConfigException);
            }
        }
    }

    /**
     *
     * @return Set with all the algorithm names
     */
    public Set<String> getAlgorithmNames() {
        return getAlgorithmEntries()
                .stream()
                .map(AlgorithmEntry::getAlgorithm)
                .collect(Collectors.toSet());
    }

    /**
     * Lookup of the algorithms.
     *
     * This just works with the algorithms that are
     * generated by using the configuration.
     *
     * @param processIdentifier identifier to access the algorithm
     * @return IAlgorithm
     */
    public IAlgorithm getAlgorithm(final String processIdentifier) {
        final Map<String, AlgorithmData> map = parseConfigToAlgorithmEntries()
                .stream()
                .collect(
                    Collectors.toMap(
                        AlgorithmData::getAlgorithmName,
                        Function.identity()));
        if (map.containsKey(processIdentifier)) {
            return map.get(processIdentifier).getAlgorithm();
        }
        return null;
    }

    /**
     * Lookup of the process description.
     *
     * This just works with the algorithms that are
     * generated by using the configuration
     * @param processIdentifier identifier to access the algorithm
     * @return ProcessDescription
     */
    public ProcessDescription getProcessDescription(
            final String processIdentifier) {
        final Map<String, AlgorithmData> map = parseConfigToAlgorithmEntries()
                .stream()
                .collect(
                    Collectors.toMap(
                        AlgorithmData::getAlgorithmName,
                            Function.identity()));
        if (map.containsKey(processIdentifier)) {
            return map.get(processIdentifier)
                    .getAlgorithm()
                    .getDescription();
        }
        return null;
    }


    /**
     * Inner class to access the name and the algorithm.
     * All the generated algorithms are active.
     */
    private static class AlgorithmData {
        /**
         * Name of the algorithm.
         */
        private final String algorithmName;
        /**
         * Algorithm itself.
         */
        private final IAlgorithm algorithm;

        /**
         * Constructor with the algorithm and
         * its name.
         * @param aAlgorithmName name for the algorithm
         * @param aAlgorithm the algorithm for the processing
         */
        AlgorithmData(
                final String aAlgorithmName,
                final IAlgorithm aAlgorithm) {
            // strConfiguration is at the moment only the Name of the class
            this.algorithmName = aAlgorithmName;
            this.algorithm = aAlgorithm;
        }

        /**
         *
         * @return the algorithm as a algorithm entry
         */
        AlgorithmEntry toAlgorithmEntry() {
            return new AlgorithmEntry(algorithmName, true);
        }

        /**
         *
         * @return name of the algorithm
         */
        String getAlgorithmName() {
            return algorithmName;
        }

        /**
         *
         * @return the algorithm iteself
         */
        IAlgorithm getAlgorithm() {
            return algorithm;
        }
    }

    /**
     * Wrapper class for all the transformation
     * processes (for example quakeml into
     * varios formats).
     */
    private static class ClassTransformationProcess {
        /**
         * The binding class for which the transformer
         * should work.
         */
        private final Class<? extends IComplexData> clazz;

        /**
         * The name that the transformation process
         * should have.
         */
        private final String processName;
        /**
         *  A validator for the binding class.
         */
        private final ICheckDataAndGetErrorMessage validator;
        /**
         * A description of the transformation process.
         */
        private final String optionalAbstract;

        /**
         * Cnostructor with the class, the processname,
         * the validator and a description.
         * @param aClazz binding class for the transformation
         * @param aProcessName process name for the transformation
         * @param aValidator validator for the data
         * @param aOptionalAbstract description
         */
        ClassTransformationProcess(
                final Class<? extends IComplexData> aClazz,
                final String aProcessName,
                final ICheckDataAndGetErrorMessage aValidator,
                final String aOptionalAbstract) {
            this.clazz = aClazz;
            this.processName = aProcessName;
            this.validator = aValidator;
            this.optionalAbstract = aOptionalAbstract;
        }

        /**
         *
         * @return binding class
         */
        Class<? extends IComplexData> getClazz() {
            return clazz;
        }

        /**
         *
         * @return process name
         */
        String getProcessName() {
            return processName;
        }

        /**
         *
         * @return validator for the binding class
         */
        ICheckDataAndGetErrorMessage getValidator() {
            return validator;
        }

        /**
         *
         * @return description (abstract) of the process
         */
        String getOptionalAbstract() {
            return optionalAbstract;
        }
    }
}
