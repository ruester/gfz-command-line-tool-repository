package org.n52.gfz.riesgos.commandlineparametertransformer;

import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LiteralStringBindingToStringCmd implements IConvertIDataToCommandLineParameter {

    private final Optional<String> defaultFlag;
    public LiteralStringBindingToStringCmd(final Optional<String> defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    @Override
    public List<String> convertToCommandLineParameter(final IData iData) {
        final List<String> result = new ArrayList<>();

        defaultFlag.ifPresent(result::add);

        if(iData instanceof LiteralStringBinding) {
            final LiteralStringBinding binding = (LiteralStringBinding) iData;
            final String value = binding.getPayload();
            result.add(value);
        }
        return result;
    }
}
