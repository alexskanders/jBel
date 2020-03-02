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

import com.skanders.jbel.def.SkandersException;

import javax.annotation.Nonnull;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;

public class HttpSocket
{
    private Client client;

    private final URI       uri;
    private final MediaType acceptType;
    private final String    path;

    private MultivaluedHashMap<String, Object> headers;
    private MultivaluedHashMap<String, Object> queries;


    /**
     * Creates an instance of APISocket
     *
     * @param uri        URL of the target
     * @param acceptType MediaType of response from the target
     * @param path       path to the specific endpoint
     */
    HttpSocket(URI uri, MediaType acceptType, String path, Client client)
    {
        this.uri        = uri;
        this.acceptType = acceptType;
        this.path       = path;

        this.headers = new MultivaluedHashMap<>();
        this.queries = new MultivaluedHashMap<>();

        this.client = client;
    }

    /**
     * Calls the method on the target according to the HTTPMethod type given
     * <p>
     * This will only work on {@link HttpMethods#HEAD}, {@link
     * HttpMethods#DELETE}, {@link HttpMethods#OPTIONS}, and {@link
     * HttpMethods#TRACE}
     * <p>
     * For use with {@link HttpMethods#POST} and {@link HttpMethods#PUT} use
     * {@link #call(HttpMethods, Entity)}
     *
     * @param method
     * @return a Response instance created by the method call
     * @throws SkandersException on invalid method or use of {@link
     *                           HttpMethods#POST} or {@link HttpMethods#PUT}
     */
    public Response call(HttpMethods method)
    {
        switch (method) {
            case HEAD:
                return head();
            case DELETE:
                return delete();
            case OPTIONS:
                return options();
            case TRACE:
                return trace();
            case PATCH:
                throw new SkandersException("Patch is not supported at this time");
            default:
                throw new SkandersException("Cannot call post or put method without entity: use call(method, entity)");
        }
    }

    /**
     * Calls the method on the target according to the HTTPMethod type given
     * with the given entity attached
     * <p>
     * This will only work on {@link HttpMethods#POST} and {@link
     * HttpMethods#PUT}
     * <p>
     * For use with {@link HttpMethods#HEAD}, {@link HttpMethods#DELETE}, {@link
     * HttpMethods#OPTIONS}, and {@link HttpMethods#TRACE} use {@link
     * #call(HttpMethods, Entity)}
     *
     * @param method
     * @return a Response instance created by the method call
     * @throws SkandersException on invalid method or use of {@link
     *                           HttpMethods#HEAD}, {@link HttpMethods#DELETE},
     *                           {@link HttpMethods#OPTIONS}, or {@link
     *                           HttpMethods#TRACE}
     */
    public <T> Response call(HttpMethods method, Entity<T> entity)
    {
        switch (method) {
            case POST:
                return post(entity);
            case PUT:
                return put(entity);
            case PATCH:
                throw new SkandersException("Patch is not supported at this time");
            default:
                throw new SkandersException("Cannot call non post, put method with entity: use call(method)");
        }
    }

    /**
     * Calls a GET request
     *
     * @return an instance of Response
     */
    public Response get()
    {
        return createBuilder().get();
    }

    /**
     * Calls a HEAD request
     *
     * @return an instance of Response
     */
    public Response head()
    {
        return createBuilder().head();
    }

    /**
     * Calls a POST request
     *
     * @param entity entity
     * @param <T>    entity type
     * @return an instance of Response
     */
    public <T> Response post(Entity<T> entity)
    {
        return createBuilder().post(entity);
    }

    /**
     * Calls a PUT request
     *
     * @param entity entity
     * @param <T>    entity type
     * @return an instance of Response
     */
    public <T> Response put(Entity<T> entity)
    {
        return createBuilder().put(entity);
    }

    /**
     * Calls a DELETE request
     *
     * @return an instance of Response
     */
    public Response delete()
    {
        return createBuilder().delete();
    }

    /**
     * Calls a OPTIONS request
     *
     * @return an instance of Response
     */
    public Response options()
    {
        return createBuilder().options();
    }

    /**
     * Calls a TRACE request
     *
     * @return an instance of Response
     */
    public Response trace()
    {
        return createBuilder().trace();
    }

    /**
     * Adds the headers to the socket call
     *
     * @param headerMap a MultivaluedMap instance
     * @return the object being called
     */
    public HttpSocket headers(@Nonnull MultivaluedMap<String, Object> headerMap)
    {
        headers.putAll(headerMap);

        return this;
    }

    /**
     * Adds a single header value to the socket call
     *
     * @param key   key for header value
     * @param value value for header key
     * @return the object being called
     */
    public HttpSocket header(@Nonnull String key, @Nonnull Object value)
    {
        headers.add(key, value);

        return this;
    }

    /**
     * Adds the queries to the socket call
     *
     * @param queryMap a MultivaluedMap instance
     * @return the object being called
     */
    public HttpSocket queries(@Nonnull MultivaluedMap<String, Object> queryMap)
    {
        queries.putAll(queryMap);

        return this;
    }

    /**
     * Adds a single query value to the socket call
     *
     * @param key   key for query value
     * @param value value for query key
     * @return the object being called
     */
    public HttpSocket query(String key, Object value)
    {
        queries.add(key, value);

        return this;
    }

    /**
     * Internal builder creator, uses the instances path, uri, queries and
     * header to construct a {@link WebTarget} to instantiate a {@link Builder}
     *
     * @return an instance of {@link Builder} to be used to query the
     * MicroService
     */
    private Builder createBuilder()
    {
        WebTarget webTarget = client.target(uri).path(path);

        if (queries != null)
            for (String key : queries.keySet())
                webTarget = webTarget.queryParam(key, queries.getFirst(key));

        Builder builder = webTarget.request(acceptType);

        if (headers != null)
            for (String key : headers.keySet())
                builder.header(key, headers.getFirst(key));

        return builder;
    }
}
