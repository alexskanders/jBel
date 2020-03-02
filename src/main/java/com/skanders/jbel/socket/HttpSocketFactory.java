/*
 * Copyright (c) 2020 Alexander Iskander
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.skanders.jbel.socket;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.net.URI;

public class HttpSocketFactory
{
    private static final Client DEFAULT_CLIENT;

    static {
        DEFAULT_CLIENT = ClientBuilder.newClient().register(JacksonJaxbJsonProvider.class);
    }

    private final URI       uri;
    private final MediaType requestType;
    private       Client    socketClient;

    /**
     * Creates and instance of ServiceSocketFactory.
     * <p>
     * On default all ServiceSocketFactory's use the default static {@link
     * Client}.
     *
     * @param uri         URL of the target
     * @param requestType {@link MediaType} type of response from the target
     * @return an instance of ServiceSocketFactory
     */
    public static HttpSocketFactory newInstance(URI uri, MediaType requestType)
    {
        return new HttpSocketFactory(uri, requestType);
    }

    /**
     * Creates and instance of ServiceSocketFactory.
     * <p>
     * On default all ServiceSocketFactory's use the default static {@link
     * Client}.
     *
     * @param uri         the string URL of the target
     * @param requestType {@link MediaType} type of response from the target
     * @return an instance of ServiceSocketFactory
     */
    public static HttpSocketFactory newInstance(String uri, MediaType requestType)
    {
        return new HttpSocketFactory(URI.create(uri), requestType);
    }


    /**
     * Creates an instance of ServiceSocketFactory `
     *
     * @param uri         URL of the target
     * @param requestType MediaType of response from the target
     */
    private HttpSocketFactory(URI uri, MediaType requestType)
    {
        this.uri          = uri;
        this.requestType  = requestType;
        this.socketClient = DEFAULT_CLIENT;
    }

    /**
     * Creates an instance of a ServiceSocket using path and queries
     *
     * @param path path of the target
     * @return an instance of ServiceSocket
     */
    public HttpSocket createSocket(String path)
    {
        return new HttpSocket(uri, requestType, path, socketClient);
    }


    /**
     * Creates a new {@link Client} that manages the specific {@link
     * SSLContext}.
     * <p>
     * This ServiceSocketFactory would use its own instance of {@link Client}
     * allowing callers to create multiple ServiceSocketFactory's with different
     * {@link SSLContext}'s
     *
     * @param sslContext an instance of SSLContext to attach to Client
     * @return returns this instance of ServiceSocketFactory
     */
    public HttpSocketFactory withSSLContext(SSLContext sslContext)
    {
        socketClient = ClientBuilder.newBuilder()
                .sslContext(sslContext).build().register(JacksonJaxbJsonProvider.class);

        return this;
    }


    /**
     * This ServiceSocketFactory would use the user given {@link Client}
     * allowing the caller complete control over its settings.
     *
     * @param client an instance of Client
     * @return returns this instance of ServiceSocketFactory
     */
    public HttpSocketFactory withClient(Client client)
    {
        socketClient = client;

        return this;
    }

    /**
     * If the same {@link SSLContext} is being used between multiple
     * ServiceSocketFactory's then this function will use the supplied
     * ServiceSocketFactory's {@link Client}
     *
     * @param socketFactory an instance of SSLContext to attach to Client
     * @return returns this instance of ServiceSocketFactory
     */
    public HttpSocketFactory withFactoryClient(HttpSocketFactory socketFactory)
    {
        this.socketClient = socketFactory.socketClient;

        return this;
    }
}
