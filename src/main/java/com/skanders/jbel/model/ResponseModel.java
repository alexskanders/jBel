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


package com.skanders.jbel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skanders.jbel.def.Verify;
import com.skanders.jbel.result.Result;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.util.Objects;

/**
 * Base Response Model class. Outgoing requests should use this class to hold
 * response data as well as response headers.
 * <p>
 * This class MUST have a {@link Result} value set before finishing with {@link
 * #toResponse()} otherwise the response will contain a status 200 result of
 * UNDECLARED.
 * <p>
 * A typical response in JSON will have at the very least this response:
 * <pre>
 *     {
 *         result: {
 *             code: 0,
 *             message: "Undeclared"
 *         }
 *     }
 * </pre>
 */
public abstract class ResponseModel
{
    @JsonProperty("result")
    private Result                             result;
    @JsonIgnore
    private MultivaluedHashMap<String, Object> headers;

    /**
     * Default constructor, sets result to UNDECLARED to ensure proper creation
     * by user
     */
    public ResponseModel()
    {
        this.result  = Result.UNDECLARED;
        this.headers = new MultivaluedHashMap<>();
    }

    /**
     * Constructor to be called by super()
     *
     * @param result a instance of Result
     * @see Result
     */
    public ResponseModel(@Nonnull Result result)
    {
        Verify.notNull(result, "result cannot be null");

        this.result = result;
    }

    /**
     * @return Current Result of Response
     * @see Result
     */
    public Result getResult()
    {
        return result;
    }

    /**
     * @param result sets the Current Result of the WorkFlow
     */
    public void setResult(@Nonnull Result result)
    {
        Verify.notNull(result, "result cannot be null");

        this.result = result;
    }

    /**
     * @param status status to set the internal Result to
     */
    public void setStatus(int status)
    {
        this.result.setStatus(status);
    }

    /**
     * @param status status to set the internal Result to
     */
    public void setStatus(Status status)
    {
        this.result.setStatus(status);
    }

    /**
     * Adds all headers from the given headers and to the response headers
     *
     * @param headers a MultivaluedMap instance
     */
    @JsonIgnore
    public void headers(@Nonnull MultivaluedHashMap<String, Object> headers)
    {
        this.headers.putAll(headers);
    }

    /**
     * Adds a single header value to the response
     *
     * @param key   key for header value
     * @param value value for header key
     */
    @JsonIgnore
    public void header(@Nonnull String key, @Nonnull Object value)
    {
        this.headers.add(key, value);
    }

    /**
     * Builds a response based on this ResponseModel and its result as well as
     * any headers that are contained in the headers HashMap
     *
     * @return an instance of {@link Response}
     */
    @JsonIgnore
    public Response toResponse()
    {
        ResponseBuilder builder = responseBuilder();

        if (headers != null)
            for (String key : headers.keySet())
                builder = builder.header(key, headers.getFirst(key));

        return builder.build();
    }

    /**
     * Builds a response based on this ResponseModel checking for any errors in
     * result
     *
     * @return an instance of {@link ResponseBuilder}
     */
    @JsonIgnore
    private ResponseBuilder responseBuilder()
    {
        if (result == null) {
            System.err.println("Request ending with null Result");
            return Response.status(Status.INTERNAL_SERVER_ERROR);

        } else if (result.exception() != null) {
            System.err.println("Request ending with exception: " + result.exception().getMessage());
            return Response.status(Status.INTERNAL_SERVER_ERROR);

        } else if (result.status() == Status.INTERNAL_SERVER_ERROR) {
            System.err.println("Request ending with Internal Server Error");
            return Response.status(Status.INTERNAL_SERVER_ERROR);

        } else {
            return Response.status(result.status()).entity(this);

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ResponseModel that = (ResponseModel) o;

        return result == that.result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(result);
    }
}
