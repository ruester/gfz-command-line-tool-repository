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

/**
 * Tuple class with the idata and the recreator.
 * @param <T> explicit binding class
 */
public class DataWithRecreatorTuple<T extends IData> {

    /**
     * Data itself.
     */
    private final T iData;
    /**
     * Recreator for that data.
     */
    private final IDataRecreator recreator;

    /**
     * Creates a new Tuple with the data and its recreator.
     * @param aData data itself
     * @param aRecreator recreator for that data
     */
    public DataWithRecreatorTuple(
            final T aData,
            final IDataRecreator aRecreator) {
        this.iData = aData;
        this.recreator = aRecreator;
    }

    /**
     * Returns the data itself.
     * @return data itself
     */
    public T getData() {
        return iData;
    }

    /**
     * Returns the recreator.
     * @return recreator for the data
     */
    public IDataRecreator getRecreator() {
        return recreator;
    }
}
