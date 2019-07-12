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
 * Implementation of the recreator
 * that just contains the idata itself.
 *
 * This is only meant for cases in which there
 * is no link to a tempoary file (so for literal strings
 * or xml contents or feature collections).
 */
public class RecreateFromBindingClass implements IDataRecreator {

    private static final long serialVersionUID = 4607639669235423099L;
    /**
     * Idata to give back.
     */
    private final IData data;

    /**
     * Creates the recreator from an existing idata.
     * @param aData idata to store in
     */
    public RecreateFromBindingClass(final IData aData) {
        this.data = aData;
    }

    /**
     *
     * @return the given idata
     */
    @Override
    public IData recreate() {
        return data;
    }

    /**
     *
     * @return class of the given idata
     */
    @Override
    public Class<? extends IData> getBindingClassToRecreate() {
        return data.getClass();
    }
}
