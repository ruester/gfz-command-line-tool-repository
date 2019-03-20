package org.n52.gfz.riesgos.repository.modules;

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

    // TODO
    // must be a configuration (should be JSON)
    // at the moment just a test for creating a process on runtime
    private static final String DEFAULT_CONFIGURATION = "Quakeledger";

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
        final String[] classesToInstance = jsonConfiguration.getValue().split(",");
        for(final String classToInstance : classesToInstance) {
            final AlgorithmData data = new AlgorithmData(classToInstance);
            result.add(data);
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
        private final boolean isActive;
        private final IAlgorithm algorithm;

        public AlgorithmData(final String strConfiguration) {
            // strConfiguration is at the moment only the Name of the class
            algorithmName = strConfiguration;
            if(algorithmName.equals("Quakeledger")) {
                algorithm = new Quakeledger();
                isActive = true;
            } else {
                algorithm = null;
                isActive = false;
            }
        }

        public AlgorithmEntry toAlgorithmEntry() {
            return new AlgorithmEntry(algorithmName, isActive);
        }

        public String getAlgorithmName() {
            return algorithmName;
        }

        public IAlgorithm getAlgorithm() {
            return algorithm;
        }
    }
}
