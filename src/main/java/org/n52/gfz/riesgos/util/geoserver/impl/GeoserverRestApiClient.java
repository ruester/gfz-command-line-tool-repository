/*
 * Copyright (C) 2019-2022 GFZ German Research Centre for Geosciences
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

package org.n52.gfz.riesgos.util.geoserver.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.n52.gfz.riesgos.util.constants.Headers;
import org.n52.gfz.riesgos.util.constants.MimeTypes;
import org.n52.gfz.riesgos.util.geoserver.IGeoserverClient;
import org.n52.gfz.riesgos.util.geoserver.exceptions.UnableToCreateCoverageException;
import org.n52.gfz.riesgos.util.geoserver.exceptions.UnableToCreateWorkspaceException;
import org.n52.gfz.riesgos.util.geoserver.exceptions.UnableToUploadShpException;
import org.n52.gfz.riesgos.util.geoserver.exceptions.WorkspaceAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Rest API client for the geoserver.
 */
public class GeoserverRestApiClient implements IGeoserverClient {

    /**
     * Logger (if bad things happen...).
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GeoserverRestApiClient.class);

    /**
     * Ue a default content charset.
     * The constant is from org.apache.commons.httpclient.HttpConstants
     * (with is deprecated as a class).
     */
    private static final String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";

    /**
     * Base url to talk with the geoserver.
     * Should look like http://localhost:8080/geoserver
     */
    private final String baseUrl;
    /**
     * Credentials to authorize an user.
     */
    private final Credentials credentials;
    /**
     * The user value itself. (For situations in that need to handle
     * the credentials ourselves).
     */
    private final String user;
    /**
     * The password value itself. (For situations in that need to handle
     * the credentials ourselves).
     */
    private final String password;

    /**
     * Construct the client object.
     * @param aBaseUrl base url (like http://localhost:8080/geoserver)
     * @param aUsername a username to run the actions
     * @param aPassword a password to authenticate the user
     */
    public GeoserverRestApiClient(
            final String aBaseUrl,
            final String aUsername,
            final String aPassword
    ) {
        // baseUrl is something ala http://localhost:8080/geoserver
        this.baseUrl = aBaseUrl;
        this.user = aUsername;
        this.password = aPassword;
        this.credentials = new UsernamePasswordCredentials(
                aUsername, aPassword
        );
    }

    /**
     * Create a workspace on the geoserver.
     * @param workspace name of the workspace
     *
     * @throws UnableToCreateWorkspaceException In case we can't create the
     * workspace (for whatever reason), we throw that exception.
     * Also possible WorkspaceAlreadyExistsException: Very specific exception
     * in case the workspace exists already. Client can decide what to do then.
     */
    @Override
    public void createWorkspace(
            final String workspace
    ) throws UnableToCreateWorkspaceException {
        final String url = this.baseUrl + "/rest/workspaces";
        final String payload =
                "<workspace><name>" + workspace + "</name></workspace>";


        final HttpClient client = new HttpClient();
        final EntityEnclosingMethod requestMethod = new PostMethod(url);


        requestMethod.setRequestHeader(
                Headers.CONTENT_TYPE, MimeTypes.APPLICATION_XML);

        client.getState().setCredentials(
                new AuthScope(
                        AuthScope.ANY_HOST,
                        AuthScope.ANY_PORT,
                        AuthScope.ANY_REALM
                ),
                this.credentials
        );

        try {
            requestMethod.setRequestEntity(
                    new StringRequestEntity(
                            payload,
                            MimeTypes.APPLICATION_XML,
                            DEFAULT_CONTENT_CHARSET
                    )
            );

            final int statusCode = client.executeMethod(requestMethod);
            if (workspaceAlreadyExists(statusCode, requestMethod)) {
                final StatusLine statusLine = requestMethod.getStatusLine();
                final String errorMsg =
                        statusLine != null ? statusLine.toString() : null;
                throw new WorkspaceAlreadyExistsException(errorMsg);
            }
            if (!(
                    (statusCode == HttpStatus.SC_OK)
                    || (statusCode == HttpStatus.SC_CREATED)
            )) {
                LOGGER.error("Create workspace failed: {}",
                        requestMethod.getStatusLine());
                throw new UnableToCreateWorkspaceException();
            }

        } catch (IOException ioException) {
            throw new UnableToCreateWorkspaceException(ioException);
        }
    }

    /**
     * Check if the status code & the request body indicate that
     * the workspace does already exists.
     * @param statusCode http status code of the last request
     * @param requestMethod request object with responde
     * @return true if we realize that the workspace already exists.
     */
    private boolean workspaceAlreadyExists(
            final int statusCode,
            final EntityEnclosingMethod requestMethod
    ) {
        if (statusCode == HttpStatus.SC_CONFLICT) {
            return true;
        }
        if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            // ok we have a generic internal server error
            // this is not enough to check for an existing workspace
            try {
                final String responseText = new String(
                        requestMethod.getResponseBody()
                ).toLowerCase();
                // the response may look like:
                // :Workspace named 'riesgos' already exists.
                // But we transform to lowercase for our checks anyway.
                if (
                        responseText.contains("workspace named")
                        && responseText.contains("already exists")
                ) {
                    return true;
                }

            } catch (IOException ioException) {
                // In case we got an IOException we can't work with the
                // response. So we can't expect that issue is an already
                // existing workspace.
            }
        }
        return false;
    }

    /**
     * Create the coverage on the geoserver.
     * @param file Raster file that we want to send.
     * @param workspace workspace name in that the file should be added
     * @param layerName name of the layer to add.
     * @throws UnableToCreateCoverageException In case something goes
     * wrong when creating this coverage we throw this exception.
     */
    @Override
    public void createCoverage(
            final File file,
            final String workspace,
            final String layerName
    ) throws UnableToCreateCoverageException {
        try {

            File copyOfFile = new File(
                    System.getProperty("java.io.tmpdir")
                            + File.separatorChar
                            + layerName
            );
            LOGGER.debug(
                    "Copy {} -> {}",
                    file.getAbsoluteFile(),
                    copyOfFile.getAbsoluteFile()
            );
            FileUtils.copyFile(file, copyOfFile);

            final String url = this.baseUrl
                    + "/rest/workspaces/"
                    + workspace
                    + "/coveragestores/"
                    + layerName
                    + "/external.geotiff?configure=first&coverageName="
                    + layerName;

            // This here only works when the geoserver is local
            // it will not send the file to an external geoserver.
            final String payload = copyOfFile.getAbsolutePath().startsWith("/")
                    ? "file:" + copyOfFile.getAbsolutePath()
                    : "file:/" + copyOfFile.getAbsolutePath();

            final HttpClient client = new HttpClient();
            final EntityEnclosingMethod requestMethod = new PutMethod(url);
            requestMethod.setRequestHeader(
                    Headers.CONTENT_TYPE, MimeTypes.TEXT_PLAIN);
            requestMethod.setRequestEntity(
                    new StringRequestEntity(
                            payload,
                            MimeTypes.TEXT_PLAIN,
                            DEFAULT_CONTENT_CHARSET
                    )
            );
            client.getState().setCredentials(
                    new AuthScope(
                            AuthScope.ANY_HOST,
                            AuthScope.ANY_PORT,
                            AuthScope.ANY_REALM
                    ),
                    this.credentials
            );

            final int statusCode = client.executeMethod(requestMethod);

            if (!(
                    (statusCode == HttpStatus.SC_OK)
                    || (statusCode == HttpStatus.SC_CREATED)
            )) {
                LOGGER.error("Create coverage failed: {}",
                        requestMethod.getStatusLine());
                throw new UnableToCreateCoverageException(
                        new RuntimeException(
                                requestMethod.getResponseBodyAsString()
                        )
                );
            }

        } catch (IOException ioException) {
            throw new UnableToCreateCoverageException(ioException);
        }
    }

    /**
     * Upload a shape file to the geoserver.
     * @param file Shapefile that will be send
     * @param workspace workspace name in that the file should be added
     * @param storeName name of the store to add
     * @return response of the request
     * @throws UnableToUploadShpException if there is an error in the
     * communication
     * @throws IOException if writing data to disk failed
     */
    @Override
    public String uploadShp(
            final File file,
            final String workspace,
            final String storeName
        ) throws UnableToUploadShpException, IOException {


        final String url = this.baseUrl
                + "/rest/workspaces/"
                + workspace
                + "/datastores/" + storeName
                + "/file.shp?filename=" + storeName;

        InputStream request = new FileInputStream(file);

        try {
            return sendShpRequest(url, request);
        } catch (HttpException httpException) {
            throw new UnableToUploadShpException(httpException);
        }
    }

    /**
     * Get a list of layers for a data store.
     * @param workspace name of the workspace
     * @param storeName name of the data store
     * @return list with the names (strings) of the layers in that store
     * @throws IOException IOException if we have trouble interacting with the
     * geoserver or parsing its responses
     */
    @Override
    public List<String> getLayerNamesForDataStore(
            final String workspace,
            final String storeName
    ) throws IOException {
        final List<String> result = new ArrayList<>();
        final String featureTypesUrl =  this.baseUrl
                + "/rest/workspaces/"
                + workspace
                + "/datastores/" + storeName + "/featuretypes";

        final String featureTypesResp = sendFeatureInfo(featureTypesUrl);

        try {
            final JSONParser parser = new JSONParser();
            final JSONObject jsonObject =
                    (JSONObject) parser.parse(featureTypesResp);
            final JSONObject featureTypes =
                    (JSONObject) jsonObject.get("featureTypes");
            final JSONArray featureTypeArray =
                    (JSONArray) featureTypes.get("featureType");
            for (final Object featureTypeObject : featureTypeArray) {
                final JSONObject featureType = (JSONObject) featureTypeObject;
                final String layerName = (String) featureType.get("name");
                result.add(layerName);
            }

        } catch (ParseException parseException) {
            throw new IOException(parseException);
        }

        return result;
    }

    /**
     * Helper method to send the a get feature info request.
     * @param url to send the request to.
     * @return String of the response body
     * @throws IOException exception if we have trouble sending to the server
     */
    private String sendFeatureInfo(
            final String url
    ) throws IOException {
        final HttpClient client = new HttpClient();
        final GetMethod requestMethod = new GetMethod(url);
        requestMethod.setRequestHeader(
                Headers.ACCEPT, MimeTypes.APPLICATION_JSON);

        client.getState().setCredentials(
                new AuthScope(
                        AuthScope.ANY_HOST,
                        AuthScope.ANY_PORT,
                        AuthScope.ANY_REALM
                ),
                this.credentials
        );

        final int statusCode = client.executeMethod(requestMethod);

        if (
                !((statusCode == HttpStatus.SC_OK)
                        || (statusCode == HttpStatus.SC_CREATED))
        ) {
            LOGGER.error("Method failed: {}", requestMethod.getStatusLine());
        }

        // Read the response body.
        final byte[] responseBody = requestMethod.getResponseBody();
        return new String(responseBody);
    }

    /**
     * Send a shapefile request (PUT) to GeoServer.
     * @param target URL to send the request to
     * @param request the content to send
     * @return response of the request
     * @throws IOException if writing data to disk failed
     */
    private String sendShpRequest(
            final String target,
            final InputStream request
    )
            throws IOException {
        LOGGER.info("url to send shapefile to: {}", target);

        final byte[] content = IOUtils.toByteArray(request);

        // We found that the HttpClient doesn't work properly for larger files.
        // So we handle it here with HttpURLConnection (which is part of
        // the default JDK).
        // And we tried to have an interface that is a little more like the
        // requests lib by python.
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MimeTypes.APPLICATION_ZIP);

        final Response resp = Requests.put(
                target,
                content,
                headers,
                new HttpBasicAuth(this.user, this.password)
        );

        final int statusCode = resp.getStatusCode();

        if (
                !((statusCode == HttpStatus.SC_OK)
                        || (statusCode == HttpStatus.SC_CREATED))
        ) {
            LOGGER.error("Method failed: {}", resp.getStatusLine());
        }

        return resp.getText();
    }

    /**
     * An equivalent to requests.auth.HttpBasicAuth of the python requests lib.
     */
    private static class HttpBasicAuth {
        /**
         * Username entry.
         */
        private final String user;
        /**
         * Passwort entry.
         */
        private final String password;

        /**
         * Constructor for the HttpBasicAuth class.
         * @param aUser username
         * @param aPassword the corresponding password
         */
        HttpBasicAuth(final String aUser, final String aPassword) {
            this.user = aUser;
            this.password = aPassword;
        }

        /**
         * We use the "standard" method to send credentials:
         * A header with the "user:password" entry in base64 encoded.
         * @return value of the basic auth header.
         */
        String getBasicAuthHeader() {
            String auth = this.user + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.UTF_8));
            return "Basic " + new String(encodedAuth);
        }
    }

    /**
     * An equivalent of the python requests Response class.
     */
    private static class Response {
        /**
         * Status code of the response.
         */
        private final int statusCode;
        /**
         * Response status message.
         */
        private final String statusLine;
        /**
         * Content of the response (bytes).
         */
        private final byte[] content;

        /**
         * Constructor for hte Response class.
         * @param aStatusCode status code of the response
         * @param aStatusLine status line of th response
         * @param aContent body of the response in bytes
         */
        Response(
                final int aStatusCode,
                final String aStatusLine,
                final byte[] aContent
        ) {
            this.statusCode = aStatusCode;
            this.statusLine = aStatusLine;
            this.content = aContent;
        }

        /**
         * Return the status code as integer.
         * Examples are: 200, 201, 404, ...
         * @return status code
         */
        int getStatusCode() {
            return this.statusCode;
        }

        /**
         * Return the status line of the response.
         * @return status line
         */
        String getStatusLine() {
            return this.statusLine;
        }

        /**
         * Return the text of the response.
         * @return text of the response
         */
        String getText() {
            return new String(this.content);
        }
    }

    /**
     * Python requests like interface.
     */
    private enum Requests {
        /**
         * Singleton.
         */
        INSTANCE;

        /**
         * Set a put requests.
         * @param url url to send the request to
         * @param content byte array with the content
         * @param headers Map with header values
         * @param auth auth (in case it is necessary)
         * @return Response
         * @throws IOException Can throw an IOException. This should not
         * happen if the requests just fails (instead 400 or 500 responses).
         */
        public static Response put(
                final String url,
                final byte[] content,
                final Map<String, String> headers,
                final HttpBasicAuth auth
        ) throws IOException {
            final HttpURLConnection urlConnection =
                    (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("PUT");

            headers.keySet().forEach(header -> {
                final String value = headers.get(header);
                urlConnection.setRequestProperty(header, value);
            });

            urlConnection.setRequestProperty(
                    "Authorization", auth.getBasicAuthHeader());

            urlConnection.setConnectTimeout(Integer.MAX_VALUE);

            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(content);



            final int statusCode = urlConnection.getResponseCode();
            final String statusLine = urlConnection.getResponseMessage();
            final byte[] responponseContent =
                    IOUtils.toByteArray(urlConnection.getInputStream());

            return new Response(statusCode, statusLine, responponseContent);

        }
    }
}
