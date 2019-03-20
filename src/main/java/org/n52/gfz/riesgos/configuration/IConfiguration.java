package org.n52.gfz.riesgos.configuration;

import net.opengis.wps.x100.ProcessDescriptionsDocument;
import org.n52.gfz.riesgos.functioninterfaces.IExitValueHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;
import org.n52.gfz.riesgos.functioninterfaces.IStdoutHandler;

import java.util.List;
import java.util.Optional;

public interface IConfiguration {

    public String getIdentifier();

    public default String getFullQualifiedIdentifier() {
        return "org.n52.gfz.riesgos.algorithm.impl." + getIdentifier();
    }

    public String getImageId();

    public String getWorkingDirectory();

    public List<String> getCommandToExecute();

    public List<String> getDefaultCommandLineFlags();

    public List<IIdentifierWithBinding> getInputIdentifiers();

    public List<IIdentifierWithBinding> getOutputIdentifiers();

    public Optional<IStderrHandler> getStderrHandler();

    public Optional<IExitValueHandler> getExitValueHandler();

    public Optional<IStdoutHandler> getStdoutHandler();
}
