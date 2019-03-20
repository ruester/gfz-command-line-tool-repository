package org.n52.gfz.riesgos.processdescription;

import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.gfz.riesgos.configuration.IConfiguration;

public interface IProcessDescriptionGenerator {

    public ProcessDescriptionsDocument generateProcessDescription(final IConfiguration configuration);
}
