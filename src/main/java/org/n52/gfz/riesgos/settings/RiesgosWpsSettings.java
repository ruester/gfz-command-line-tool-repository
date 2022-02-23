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

package org.n52.gfz.riesgos.settings;

import java.util.Map;

/**
 * Singleton class to handle our riesgos wps config in java.
 * It is meant to work with env variables as we can set them
 * in an easier way compard to the config modules that the
 * current WPS implementation handles.
 */
public enum RiesgosWpsSettings {
    /**
     * Singleton instance.
     */
    INSTANCE;

    /**
     * We want to use a default workspace in case the user doesn't
     * give it via env variable.
     */
    private static final String DEFAULT_WORKSPACE = "riesgos";

    /**
     * Url that we can use to send our files
     * to a geoserver.
     *
     * It should look like:
     * http://localhost:8080/geoserver
     *
     * The idea to use a base url is that we can
     * set the protocol, the hostname & the port ino
     * one single config entry.
     *
     * It can be different to the access base url, as it doesn't
     * need to go over an reverse proxy for example (if wps & geoserver
     * run on the same machine (same application server, or different
     * containers).
     */
    private final String geoserverSendBaseUrl;

    /**
     * Url to access the geoservers layers.
     * It should look like:
     * http://localhost:8080/geoserver
     *
     * As mentioned before, this can be different from the send
     * base url.
     * The access base url is the one that we use to create
     * urls that should be used by external clients (as openlayers wms
     * for example), so this one must be queryable from the outside.
     */
    private final String geoserverAccessBaseUrl;

    /**
     * Username to access the geoserver.
     */
    private final String geoserverUsername;

    /**
     * Password to access the geoserver.
     */
    private final String geoserverPassword;

    /**
     * Workspace that we want to use in the geoserver.
     * Normally something like "N52" was used in the riesgos project.
     */
    private final String geoserverWorkspace;

    /**
     * Init the config by env variables.
     */
    RiesgosWpsSettings() {
        final Map<String, String> env = System.getenv();
        geoserverSendBaseUrl = env.getOrDefault(
                "RIESGOS_GEOSERVER_SEND_BASE_URL", ""
        );
        geoserverAccessBaseUrl = env.getOrDefault(
                "RIESGOS_GEOSERVER_ACCESS_BASE_URL", ""
        );

        geoserverUsername = env.getOrDefault(
                "RIESGOS_GEOSERVER_USERNAME", ""
        );
        geoserverPassword = env.getOrDefault(
                "RIESGOS_GEOSERVER_PASSWORD", ""
        );
        geoserverWorkspace = env.getOrDefault(
                "RIESGOS_GEOSERVER_WORKSPACE", DEFAULT_WORKSPACE
        );
    }


    /**
     * Getter for the geoserver send base url.
     * @return something like http://localhost:8080/geoserver
     */
    public String getGeoserverSendBaseUrl() {
        return geoserverSendBaseUrl;
    }

    /**
     * Getter for the geoserver access base url.
     * @return something like http://localhost:8080/geoserver
     */
    public String getGeoserverAccessBaseUrl() {
        return geoserverAccessBaseUrl;
    }

    /**
     * Getter for the username to access the geoserver.
     * @return username, for example admin
     */
    public String getGeoserverUsername() {
        return geoserverUsername;
    }

    /**
     * Getter for the password to access the geoserver.
     * @return the password from the env variables
     */
    public String getGeoserverPassword() {
        return geoserverPassword;
    }

    /**
     * Getter for the workspace that we want to use with the geoserver.
     * @return for example N52
     */
    public String getGeoserverWorkspace() {
        return geoserverWorkspace;
    }
}
