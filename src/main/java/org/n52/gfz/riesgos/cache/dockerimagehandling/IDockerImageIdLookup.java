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

package org.n52.gfz.riesgos.cache.dockerimagehandling;

/**
 * This is the interface to get the "real" image id
 * from a docker image as this input can also be
 * a label (for example quakeledger:latest).
 * For the caching we are interested on the real image id
 * to avoid given back results that the real image doesn't
 * produce anymore.
 */
public interface IDockerImageIdLookup {

    /**
     * Asks docker about the image id of the given label.
     * In case it is already a real image id than
     * docker will just return the id that was given (if
     * it exists on the system).
     * @param imageIdWithLabel given image id (maybe with label)
     * @return image id
     */
    String lookUpImageId(String imageIdWithLabel);
}
