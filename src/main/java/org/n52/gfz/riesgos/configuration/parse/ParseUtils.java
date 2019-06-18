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

package org.n52.gfz.riesgos.configuration.parse;

import java.util.List;

/**
 * Utility class with some functions for several parsers.
 */
public final class ParseUtils {

    /**
     * The class should be used as static.
     */
    private ParseUtils() {
        // static
    }

    /**
     * Function to check if a string has a value and is not empty.
     * @param str string to check
     * @return true if there is text inside of the string
     */
    public static boolean strHasValue(final String str) {
        return str != null && (!str.isEmpty());
    }

    /**
     * Function to check if a string is null or is empty.
     * @param str string to check
     * @return true if there is no text inside of the string
     * (or it is null)
     */
    public static boolean strHasNoValue(final String str) {
        return !strHasValue(str);
    }


    /**
     * Function to check if a list has entries.
     * @param list list to check
     * @return true if there is a value inside of the list
     */
    public static boolean listHasValue(final List<String> list) {
        return list != null && (!list.isEmpty());
    }

    /**
     * Function to check if a list is null or empty.
     * @param list list to check
     * @return true if the list is null or empty
     */
    public static boolean listHasNoValues(final List<String> list) {
        return !listHasValue(list);
    }
}
