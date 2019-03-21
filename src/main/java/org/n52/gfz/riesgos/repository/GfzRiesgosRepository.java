package org.n52.gfz.riesgos.repository;

import org.n52.gfz.riesgos.repository.modules.GfzRiesgosRepositoryCM;
import org.n52.wps.commons.WPSConfig;
import org.n52.wps.server.IAlgorithm;
import org.n52.wps.server.ITransactionalAlgorithmRepository;
import org.n52.wps.server.ProcessDescription;
import org.n52.wps.webapp.api.ConfigurationCategory;
import org.n52.wps.webapp.api.ConfigurationModule;

import java.util.Collection;

/**
 * Repository for the algorithms for the gfz in riesgos using the generation of services
 * by providing configurations to call command line tools in docker.
 */
public class GfzRiesgosRepository implements ITransactionalAlgorithmRepository  {

    private final GfzRiesgosRepositoryCM configurationModule;

    /**
     * Default constructor
     */
    public GfzRiesgosRepository() {

        // implementation detail
        // -> reads the configuration out of a database
        final ConfigurationModule cm = WPSConfig.getInstance().getConfigurationModuleForClass(getClass().getName(), ConfigurationCategory.REPOSITORY);
        if(cm instanceof GfzRiesgosRepositoryCM) {
            configurationModule = (GfzRiesgosRepositoryCM) cm;
        } else {
            throw new RuntimeException("Configuration Module has wrong type");
        }
    }

    @Override
    public boolean addAlgorithm(final Object processIdentifier) {
        throw new UnsupportedOperationException("The repository is only configured via the json config. Adding an algorithm via java code is not supported");
    }

    @Override
    public boolean removeAlgorithm(final Object processIdentifier) {
        throw new UnsupportedOperationException("The repository is only configured via the json config. Removing an algorithm via java code is not supported");
    }

    @Override
    public Collection<String> getAlgorithmNames() {
        return configurationModule.getAlgortihmNames();
    }

    @Override
    public IAlgorithm getAlgorithm(String processIdentifier) {
        return configurationModule.getAlgorithm(processIdentifier);
    }

    @Override
    public ProcessDescription getProcessDescription(String processIdentifier) {
        return configurationModule.getProcessDescription(processIdentifier);
    }

    @Override
    public boolean containsAlgorithm(String processIdentifier) {
        return getAlgorithmNames().contains(processIdentifier);
    }

    @Override
    public void shutdown() {
        // nothing to do
    }
}
