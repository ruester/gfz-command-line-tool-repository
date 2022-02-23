/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.n52.gfz.riesgos.commandlineparametertransformer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.binding.literal.LiteralDateTimeBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Function to convert an DateTime Binding to a string.
 * Used tot add it as a command line argument.
 */
public class LiteralDateTimeBindingToStringCmd
        implements IConvertIDataToCommandLineParameter<LiteralDateTimeBinding> {
    /**
     * Internal default flag (for example '--starttime').
     */
    private final String defaultFlag;

    /**
     * Constructor with a default flag.
     * @param aDefaultFlag flag that is before the element
     *                     (for example --start-time before the time value)
     */
    public LiteralDateTimeBindingToStringCmd(final String aDefaultFlag) {
        this.defaultFlag = aDefaultFlag;
    }

    /**
     * Constructor without a default flag.
     */
    public LiteralDateTimeBindingToStringCmd() {
        this.defaultFlag = null;
    }

    /**
     * Converts the IData to a list of arguments.
     * @param binding element to convert
     * @return list of strings
     */
    @Override
    public List<String> convertToCommandLineParameter(
            final LiteralDateTimeBinding binding) {

        final List<String> result = new ArrayList<>();
        Optional.ofNullable(defaultFlag).ifPresent(result::add);

        final Date date = binding.getPayload();
        final DateTime dateTime =
                new DateTime(date).toDateTime(DateTimeZone.UTC);

        final String dateTimeAsString = dateTime.toString();
        result.add(dateTimeAsString);

        return result;
    }

    /**
     *
     * @param o other object
     * @return true if this object equals the other one
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LiteralDateTimeBindingToStringCmd that =
                (LiteralDateTimeBindingToStringCmd) o;
        return Objects.equals(defaultFlag, that.defaultFlag);
    }

    /**
     *
     * @return hashcode of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(defaultFlag);
    }
}
