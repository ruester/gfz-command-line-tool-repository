package org.n52.gfz.riesgos.exitvaluetoidataconverter;

/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 *
 *
 */

import org.n52.gfz.riesgos.functioninterfaces.IConvertExitValueToIData;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;

import java.util.Objects;

public class ConvertExitValueToLiteralIntBinding implements IConvertExitValueToIData<LiteralIntBinding> {

    @Override
    public LiteralIntBinding convertToIData(int exitValue) {
        return new LiteralIntBinding(exitValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName());
    }
}
