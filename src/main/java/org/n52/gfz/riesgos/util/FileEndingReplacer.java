package org.n52.gfz.riesgos.util;

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

/**
 * Utility class to replace file endings.
 */
public enum FileEndingReplacer {

    /**
     * Singleton.
     */
    INSTANCE;
    /**
     * Private constructor, so this class should only by used
     * from a static context.
     */
    FileEndingReplacer() {
        // static
    }

    /**
     * Replaces the file ending.
     *
     * @param filename filename that should be changed
     * @param endingToReplace ending that should be replaced
     * @param replacement replacement
     * @return String with a replaced ending
     */
    public String replaceFileEnding(
            final String filename,
            final String endingToReplace,
            final String replacement) {
        if (filename.endsWith(endingToReplace)) {
            return filename.replace(endingToReplace, replacement);
        }
        return filename + replacement;
    }
}
