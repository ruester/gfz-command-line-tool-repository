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

package org.n52.gfz.riesgos.commandlineparametertransformer;

import org.n52.gfz.riesgos.exceptions.ConvertToStringCmdException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.IData;
import java.util.Collections;
import java.util.List;

public class FileToStringCmd implements IConvertIDataToCommandLineParameter {

    private final String filename;

    public FileToStringCmd(final String filename) {
        this.filename = filename;
    }

    @Override
    public List<String> convertToCommandLineParameter(IData iData) throws ConvertToStringCmdException {
        if(filename != null) {
            return Collections.singletonList(filename);
        }
        throw new ConvertToStringCmdException("There is no filename");
    }
}
