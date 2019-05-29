package org.n52.gfz.riesgos.formats.quakeml;

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

import java.util.List;
import java.util.Optional;

/**
 * Interface to provide quakeml data
 */
public interface IQuakeMLDataProvider {

    /**
     *
     * @return list with quakeml events
     */
    List<IQuakeMLEvent> getEvents();

    /**
     *
     * @return Returns a public ID (something like quakeml:quakeledger/0
     */
    Optional<String > getPublicId();
}
