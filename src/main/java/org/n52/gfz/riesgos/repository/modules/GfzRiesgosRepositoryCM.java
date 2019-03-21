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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GfzRiesgosRepositoryCM extends ClassKnowingModule {

    private static final String CONFIG_KEY = "json_configuration";


    private static final String DEFAULT_CONFIGURATION = "[{\"title\": \"Quakeledger\", \"imageId\": \"sha256:71b93ade61bf41da8d68419bec12ec1e274eae28b36bc64cc156e1be33294821\"}]";

    private static final String MODULE_NAME = "GFZ RIESGOS Configuration Module";
    private static final String CLASS_NAME_OF_REPOSITORY_TO_CONFIG = GfzRiesgosRepository.class.getName();
    private static final ConfigurationCategory CATEGORY = ConfigurationCategory.REPOSITORY;


    private final List<? extends ConfigurationEntry<?>> configurationEntries;
    private ConfigurationEntry<String> jsonConfiguration;
    private boolean isActive;

    public GfzRiesgosRepositoryCM() {
        isActive = true;
        jsonConfiguration = new StringConfigurationEntry(CONFIG_KEY, "JSON Process Configuration", "JSON description of the processes",
                true, DEFAULT_CONFIGURATION);
        configurationEntries = Arrays.asList(jsonConfiguration);
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

    public List<AlgorithmData> parseConfigToAlgorithmEntries() {

        final List<AlgorithmData> result = new ArrayList<>();

        // TODO

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
            // TODO
            // improve error handling
            throw new RuntimeException(parseException);
        }

        return result;
    }

    public Set<String> getAlgortihmNames() {
        return getAlgorithmEntries().stream().map(AlgorithmEntry::getAlgorithm).collect(Collectors.toSet());
    }

    public IAlgorithm getAlgorithm(final String processIdentifier) {
        final Map<String, AlgorithmData> map = parseConfigToAlgorithmEntries().stream().collect(Collectors.toMap(AlgorithmData::getAlgorithmName, Function.identity()));
        if(map.containsKey(processIdentifier)) {
            return map.get(processIdentifier).getAlgorithm();
        }
        return null;
    }

    public ProcessDescription getProcessDescription(final String processIdentifier) {
        final Map<String, AlgorithmData> map = parseConfigToAlgorithmEntries().stream().collect(Collectors.toMap(AlgorithmData::getAlgorithmName, Function.identity()));
        if(map.containsKey(processIdentifier)) {
            return map.get(processIdentifier).getAlgorithm().getDescription();
        }
        return null;
    }


    private static class AlgorithmData {
        private final String algorithmName;
        private final IAlgorithm algorithm;

        public AlgorithmData(final String algorithmName, final IAlgorithm algorithm) {
            // strConfiguration is at the moment only the Name of the class
            this.algorithmName = algorithmName;
            this.algorithm = algorithm;
        }

        public AlgorithmEntry toAlgorithmEntry() {
            return new AlgorithmEntry(algorithmName, true);
        }

        public String getAlgorithmName() {
            return algorithmName;
        }

        public IAlgorithm getAlgorithm() {
            return algorithm;
        }
    }
}
