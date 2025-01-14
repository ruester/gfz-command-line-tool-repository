package org.n52.gfz.riesgos.commandlineparametertransformer;

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

import org.n52.gfz.riesgos.exceptions.ConvertToStringCmdException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.IData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Class to convert file to string.
 * @param <T> Type of the data
 */
public final class FileToStringCmd<T extends IData>
    implements IConvertIDataToCommandLineParameter<T> {

    /**
     * Filename.
     */
    private final String filename;

    /**
     * Default flag.
     */
    private final String defaultCommandLineFlag;

    /**
     * Constructor with filename and flag.
     * @param argFilename filename
     * @param argDefaultCommandLineFlag default flag
     */
    public FileToStringCmd(
        final String argFilename,
        final String argDefaultCommandLineFlag
    ) {
        this.filename = argFilename;
        this.defaultCommandLineFlag = argDefaultCommandLineFlag;
    }

    /**
     * Constructor with filename only.
     * @param argFilename filname
     */
    public FileToStringCmd(final String argFilename) {
        this(argFilename, null);
    }

    @Override
    public List<String> convertToCommandLineParameter(final T iData)
            throws ConvertToStringCmdException {
        final List<String> result = new ArrayList<>();
        Optional.ofNullable(defaultCommandLineFlag).ifPresent(result::add);
        if (filename == null) {
            throw new ConvertToStringCmdException("There is no filename");
        }
        result.add(filename);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileToStringCmd that = (FileToStringCmd) o;
        return Objects.equals(filename, that.filename)
            && Objects.equals(
                defaultCommandLineFlag,
                that.defaultCommandLineFlag
            );
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, defaultCommandLineFlag);
    }
}
