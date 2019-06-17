package org.n52.gfz.riesgos.configuration;

import org.n52.gfz.riesgos.exceptions.ParseConfigurationException;

import java.util.List;

public interface IInputParameterAsCommandLineArgumentFactory {
    IInputParameter create(
            String identifier,
            boolean isOptional,
            String optionalAbstract,
            String defaultCommandLineArgument,
            String defaultValue,
            List<String> allowedValues,
            List<String> supportedCrs,
            String schema
    ) throws ParseConfigurationException;
}
