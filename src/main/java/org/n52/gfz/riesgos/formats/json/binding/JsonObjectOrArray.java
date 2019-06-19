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

package org.n52.gfz.riesgos.formats.json.binding;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.Optional;

/**
 * Wrapper to support both arrays and objects.
 */
public class JsonObjectOrArray implements Serializable {

    private static final long serialVersionUID = -3251367313463873184L;

    /**
     * This contains the json object if it used.
     */
    private final JSONObject jsonObject;
    /**
     * This contains the json array if it used.
     */
    private final JSONArray jsonArray;

    /**
     * This is the constructor with the json object.
     * @param aJsonObject json object to store
     */
    public JsonObjectOrArray(final JSONObject aJsonObject) {
        this.jsonObject = aJsonObject;
        this.jsonArray = null;
    }

    /**
     * This is the constructor with the json array.
     * @param aJsonArray json array to store.
     */
    public JsonObjectOrArray(final JSONArray aJsonArray) {
        this.jsonObject = null;
        this.jsonArray = aJsonArray;
    }

    /**
     *
     * @return optional json object
     */
    public Optional<JSONObject> getJsonObject() {
        return Optional.ofNullable(jsonObject);
    }

    /**
     *
     * @return optional json array
     */
    public Optional<JSONArray> getJsonArray() {
        return Optional.ofNullable(jsonArray);
    }
}
