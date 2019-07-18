package org.n52.gfz.riesgos.functioninterfaces;

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

import org.n52.gfz.riesgos.exceptions.ConvertFormatException;

/**
 * Interface for any kind of format converting process
 * @param <X> format of the input data
 * @param <Y> format of the output data
 */
@FunctionalInterface
public interface IConvertFormat<X, Y>  {

    /**
     * Converts data from one format to another
     * @param x data to convert
     * @return converted data
     * @throws ConvertFormatException exception that is used on this conversion
     */
    Y convert(X x) throws ConvertFormatException;
}
