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

package org.n52.gfz.riesgos.formats.jsonfile.binding;

import org.n52.wps.io.data.IComplexData;

/**
 * Binding class that contains a JSON file.
 */
public class JsonFileBinding implements IComplexData {

    private static final long serialVersionUID = 838643710787714343L;

    /**
     * The json file data.
     */
    private final JsonFileData jsonFileData;

    /**
     * Default constructor for JsonFileBinding.
     * @param aJsonFileData JSON file data
     */
    public JsonFileBinding(final JsonFileData aJsonFileData) {
        this.jsonFileData = aJsonFileData;
    }

    /**
     * Disposes the data binding.
     */
    @Override
    public void dispose() {
        // do nothing
    }

    /**
     *
     * @return the content of the data binding
     */
    @Override
    public JsonFileData getPayload() {
        return jsonFileData;
    }

    /**
     *
     * @return supported class for the data binding
     */
    @Override
    public Class<?> getSupportedClass() {
        return JsonFileData.class;
    }
}
