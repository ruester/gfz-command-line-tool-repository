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

import org.n52.gfz.riesgos.exceptions.ConvertToIDataException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertByteArrayToIData;
import org.n52.wps.io.data.IData;

public class RecreateFromByteArray implements IDataRecreator {

    private final byte[] content;
    private final IConvertByteArrayToIData converter;
    private final Class<? extends IData> bindingClass;


    public RecreateFromByteArray(
            final byte[] aContent,
            final IConvertByteArrayToIData aConverter,
            final Class<? extends IData> aBindingClass) {
        this.content = aContent;
        this.converter = aConverter;
        this.bindingClass = aBindingClass;
    }

    @Override
    public IData recreate() {
        try {
            return converter.convertToIData(content);
        } catch (final ConvertToIDataException exception) {
            // this is only meant to recreate from content
            // not for trying it the first time!
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Class<? extends IData> getBindingClassToRecreate() {
        return bindingClass;
    }
}
