package org.n52.gfz.riesgos.processdescription;

import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.gfz.riesgos.configuration.IConfiguration;

/**
 * Interface to generate the process description out of the configuration
 */
public interface IProcessDescriptionGenerator {

    /**
     * Generates the process description
     * @param configuration configuration to use for the description
     * @return process description (xml) for the service based on the configuration
     */
    ProcessDescriptionsDocument generateProcessDescription(final IConfiguration configuration);
}
