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
 */

package org.n52.gfz.riesgos.formats.json.binding;

import org.n52.wps.io.data.IComplexData;

/**
 * Binding class that contains a simple json object.
 */
public class JsonDataBinding implements IComplexData {

    private static final long serialVersionUID = 8386437107877117360L;

    /**
     * Inner json object.
     */
    private final JsonObjectOrArray jsonObject;

    /**
     * Default constructor for JsonDataBinding.
     * @param aJsonObject jsonObject to wrap
     */
    public JsonDataBinding(final JsonObjectOrArray aJsonObject) {
        this.jsonObject = aJsonObject;
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
    public JsonObjectOrArray getPayload() {
        return jsonObject;
    }

    /**
     *
     * @return supported class for the data binding
     */
    @Override
    public Class<?> getSupportedClass() {
        return JsonObjectOrArray.class;
    }
}
