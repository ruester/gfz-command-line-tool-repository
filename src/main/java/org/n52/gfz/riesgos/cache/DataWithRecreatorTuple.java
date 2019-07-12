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

package org.n52.gfz.riesgos.cache;

import org.n52.wps.io.data.IData;

public class DataWithRecreatorTuple<T extends IData> {

    private final T iData;
    private final IDataRecreator recreator;

    public DataWithRecreatorTuple(
            final T aData,
            final IDataRecreator aRecreator) {
        this.iData = aData;
        this.recreator = aRecreator;
    }

    public T getData() {
        return iData;
    }

    public IDataRecreator getRecreator() {
        return recreator;
    }
}
