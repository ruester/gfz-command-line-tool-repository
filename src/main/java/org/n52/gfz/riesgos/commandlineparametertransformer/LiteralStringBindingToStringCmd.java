package org.n52.gfz.riesgos.commandlineparametertransformer;

import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Function to convert an IData to a String.
 * Used to add it as a command line argument.
 * Can also handle default flags.
 */
public class LiteralStringBindingToStringCmd implements IConvertIDataToCommandLineParameter {

    private final String defaultFlag;

    /**
     * Constructor with a default flag
     * @param defaultFlag flag that is before the element (for example --etype before an expert type)
     */
    public LiteralStringBindingToStringCmd(final String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    /**
     * Constructor without a default flag
     */
    public LiteralStringBindingToStringCmd() {
        this(null);
    }

    @Override
    public List<String> convertToCommandLineParameter(final IData iData) {
        final List<String> result = new ArrayList<>();

        Optional.ofNullable(defaultFlag).ifPresent(result::add);

        if(iData instanceof LiteralStringBinding) {
            final LiteralStringBinding binding = (LiteralStringBinding) iData;
            final String value = binding.getPayload();
            result.add(value);
        }
        return result;
    }
}
