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

import java.io.Serializable;

/**
 * This is an interface to recreate the idata.
 * It is meant for cases on which the idata refers
 * to a temporary file and the content is read from
 * the caching system, so that the temporary file
 * is not there anymore.
 */
public interface IDataRecreator extends Serializable {

    /**
     * Recreates the idata.
     * @return recreated idata
     */
    IData recreate();

    /**
     * Returns the binding class that will be recreated.
     * This is meant to check the chache if there is content
     * for a specifc class.
     * @return binding class
     */
    Class<? extends IData> getBindingClassToRecreate();

    /**
     * Get the size of this object in bytes.
     * @return the size of the object in bytes
     */
    int getSizeInBytes();
}
