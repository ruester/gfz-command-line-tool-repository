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
 * This is an implementation of the docker image id lookup
 * that just takes the docker image id value as it is
 * and doesn't try to get the real image id from
 * the system.
 *
 * This implementation is meant for testing purposes!
 * Please make sure that you know why you use it
 * in productive code.
 */
public class NoDockerImageIdLookup implements IDockerImageIdLookup {

    /**
     * Dummy implementation of the docker image id
     * lookup.
     * Does nothing.
     *
     * @param imageIdWithLabel given image id (maybe with label)
     * @return the same image id that is the argument
     */
    @Override
    public String lookUpImageId(final String imageIdWithLabel) {
        return imageIdWithLabel;
    }
}
