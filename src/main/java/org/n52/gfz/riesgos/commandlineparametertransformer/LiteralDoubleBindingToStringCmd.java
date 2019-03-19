package org.n52.gfz.riesgos.commandlineparametertransformer;

import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LiteralDoubleBindingToStringCmd implements IConvertIDataToCommandLineParameter {

    private final Optional<String> defaultFlag;
    public LiteralDoubleBindingToStringCmd(final Optional<String> defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    @Override
    public List<String> convertToCommandLineParameter(final IData iData) {
        final List<String> result = new ArrayList<>();

        defaultFlag.ifPresent(result::add);

        if(iData instanceof LiteralDoubleBinding) {
            final LiteralDoubleBinding binding = (LiteralDoubleBinding) iData;
            final double value = binding.getPayload();
            result.add(String.valueOf(value));
        }
        return result;
    }


}
