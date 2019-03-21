package org.n52.gfz.riesgos.repository.modules;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.n52.gfz.riesgos.algorithm.impl.Quakeledger;
import org.n52.gfz.riesgos.repository.GfzRiesgosRepository;
import org.n52.wps.server.IAlgorithm;
import org.n52.wps.server.ProcessDescription;
import org.n52.wps.webapp.api.AlgorithmEntry;
import org.n52.wps.webapp.api.ClassKnowingModule;
import org.n52.wps.webapp.api.ConfigurationCategory;
import org.n52.wps.webapp.api.FormatEntry;
import org.n52.wps.webapp.api.types.ConfigurationEntry;
import org.n52.wps.webapp.api.types.StringConfigurationEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Configuration module for the gfz riesgos repository.
 *
 * The aim is to provide all the informations about the process configurations here, so it should be possible to
 * add a process description on runtime and to execute it immediatly.
 */
public class GfzRiesgosRepositoryCM extends ClassKnowingModule {

    private static final String CONFIG_KEY = "json_configuration";

    /*
     * a default configuration to insert the Quakeledger process with a given image id
     *
     * it is already intended to provide a JSON configuration for handling the processes
     * The actual format here and (of course) the content will change.
     *
     */
    private static final String DEFAULT_CONFIGURATION =
            "[{\"title\": \"Quakeledger\", \"imageId\": \"sha256:71b93ade61bf41da8d68419bec12ec1e274eae28b36bc64cc156e1be33294821\"}]";

    private static final String MODULE_NAME = "GFZ RIESGOS Configuration Module";
    private static final String CLASS_NAME_OF_REPOSITORY_TO_CONFIG = GfzRiesgosRepository.class.getName();
    private static final ConfigurationCategory CATEGORY = ConfigurationCategory.REPOSITORY;


    private final List<? extends ConfigurationEntry<?>> configurationEntries;
    private ConfigurationEntry<String> jsonConfiguration;
    private boolean isActive;

    /**
     * Default constructor
     */
    public GfzRiesgosRepositoryCM() {
        isActive = true;
        jsonConfiguration = new StringConfigurationEntry(CONFIG_KEY, "JSON Process Configuration", "JSON description of the processes",
                true, DEFAULT_CONFIGURATION);
        configurationEntries = Collections.singletonList(jsonConfiguration);
    }

    @Override
    public String getClassName() {
        return CLASS_NAME_OF_REPOSITORY_TO_CONFIG;
    }

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public ConfigurationCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public List<? extends ConfigurationEntry<?>> getConfigurationEntries() {
        return configurationEntries;
    }

    @Override
    public List<AlgorithmEntry> getAlgorithmEntries() {
        return parseConfigToAlgorithmEntries().stream().map(AlgorithmData::toAlgorithmEntry).collect(Collectors.toList());
    }

    @Override
    public List<FormatEntry> getFormatEntries() {
        // this is no configuration module for formats
        return null;
    }

    private List<AlgorithmData> parseConfigToAlgorithmEntries() {

        final List<AlgorithmData> result = new ArrayList<>();

        /*
         * The code here should be improved to provide user specific configurations.
         * This implementation is just a working state to provide access to the Quakeledger process
         * and to specify the image id yourself (because on each time you build the docker image for
         * Quakeledger using the file in the assistance folder the imageId changes).
         */

        try {
            final JSONParser parser = new JSONParser();
            final JSONArray arrayOfProcesses = (JSONArray) parser.parse(jsonConfiguration.getValue());

            for(final Object pureProcess : arrayOfProcesses) {
                final JSONObject process = (JSONObject) pureProcess;
                final String title = (String) process.get("title");
                final String imageId = (String) process.get("imageId");
                if("Quakeledger".equals(title)) {
                    final IAlgorithm algorithm = new Quakeledger(imageId);
                    final AlgorithmData data = new AlgorithmData(title, algorithm);
                    result.add(data);
                }
            }
        } catch(final ParseException parseException) {
            // error handling must be improved too
            throw new RuntimeException(parseException);
        }

        return result;
    }

    /**
     *
     * @return Set with all the algorithm names
     */
    public Set<String> getAlgortihmNames() {
        return getAlgorithmEntries().stream().map(AlgorithmEntry::getAlgorithm).collect(Collectors.toSet());
    }

    /**
     * Lookup of the algorithms
     *
     * This just works with the algorithms that are generated by using the configuration
     *
     * @param processIdentifier identifier to access the algorithm
     * @return IAlgorithm
     */
    public IAlgorithm getAlgorithm(final String processIdentifier) {
        final Map<String, AlgorithmData> map = parseConfigToAlgorithmEntries().stream().collect(Collectors.toMap(AlgorithmData::getAlgorithmName, Function.identity()));
        if(map.containsKey(processIdentifier)) {
            return map.get(processIdentifier).getAlgorithm();
        }
        return null;
    }

    /**
     * Lookup of the process description
     *
     * This just works with the algorithms that are generated by using the configuration
     * @param processIdentifier identifier to access the algorithm
     * @return ProcessDescription
     */
    public ProcessDescription getProcessDescription(final String processIdentifier) {
        final Map<String, AlgorithmData> map = parseConfigToAlgorithmEntries().stream().collect(Collectors.toMap(AlgorithmData::getAlgorithmName, Function.identity()));
        if(map.containsKey(processIdentifier)) {
            return map.get(processIdentifier).getAlgorithm().getDescription();
        }
        return null;
    }


    /*
     * Inner class to access the name and the algorithm.
     * All the generated algorithms are active.
     */
    private static class AlgorithmData {
        private final String algorithmName;
        private final IAlgorithm algorithm;

        AlgorithmData(final String algorithmName, final IAlgorithm algorithm) {
            // strConfiguration is at the moment only the Name of the class
            this.algorithmName = algorithmName;
            this.algorithm = algorithm;
        }

        AlgorithmEntry toAlgorithmEntry() {
            return new AlgorithmEntry(algorithmName, true);
        }

        String getAlgorithmName() {
            return algorithmName;
        }

        IAlgorithm getAlgorithm() {
            return algorithm;
        }
    }
}
