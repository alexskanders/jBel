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


package com.skanders.jbel.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skanders.jbel.def.SkandersException;
import com.skanders.jbel.def.Verify;
import com.skanders.jbel.model.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Objects;

public class Result
{
    private static final Logger LOG = LoggerFactory.getLogger(Result.class);

    public static final Result VALID;
    public static final Result UNDECLARED;
    public static final Result EXCEPTION;

    static {
        VALID      = declare(1, "Valid");
        UNDECLARED = declare(0, "Undeclared");
        EXCEPTION  = declare(-1, "Something went wrong", Status.INTERNAL_SERVER_ERROR);
    }

    @JsonProperty("code")
    private Integer code;
    @JsonProperty("message")
    private String  message;

    @JsonIgnore
    private Status    status;
    @JsonIgnore
    private Exception exception;

    private Result()
    {
        this.code    = UNDECLARED.code;
        this.message = UNDECLARED.message;
        this.status  = UNDECLARED.status;

        this.exception = null;
    }

    private Result(@Nonnull Integer code, @Nonnull String message)
    {
        Verify.notNull(code, "code cannot be null");
        Verify.notNull(message, "message cannot be null");

        this.code    = code;
        this.message = message;
        this.status  = Status.OK;

        this.exception = null;
    }

    private Result(@Nonnull Integer code, @Nonnull String message, @Nonnull Status status)
    {
        Verify.notNull(code, "code cannot be null");
        Verify.notNull(message, "message cannot be null");
        Verify.notNull(status, "status cannot be null");

        this.code    = code;
        this.message = message;
        this.status  = status;

        this.exception = null;
    }

    private Result(@Nonnull Exception exception)
    {
        Verify.notNull(exception, "exception cannot be null");

        this.code    = EXCEPTION.code;
        this.message = EXCEPTION.message;
        this.status  = EXCEPTION.status;

        this.exception = exception;
    }

    public static Result declare(@Nonnull Integer code, @Nonnull String message)
    {
        return new Result(code, message);
    }

    public static Result declare(@Nonnull Integer code, @Nonnull String message, @Nonnull Status status)
    {
        return new Result(code, message, status);
    }

    public static Result exception(@Nonnull Exception exception)
    {
        return new Result(exception);
    }

    public static Result exception(@Nonnull String message)
    {
        return new Result(new SkandersException(message));
    }

    @JsonIgnore
    public boolean notValid()
    {
        return this != VALID;
    }

    @JsonIgnore
    public <T extends ResponseModel> boolean notValid(T responseModel)
    {
        if (this != VALID) {
            responseModel.setResult(this);
            return true;
        }

        return false;
    }

    @JsonIgnore
    public void throwNotValid()
    {
        if (this != Result.VALID) {
            LOG.trace("Result in non valid result: {}", this.message());
            throw this.toThrowable();
        }
    }

    @JsonProperty("code")
    public Integer code()
    {
        return this.code;
    }

    @JsonProperty("message")
    public String message()
    {
        return this.message;
    }

    @JsonIgnore
    public Status status()
    {
        return this.status;
    }

    @JsonIgnore
    public Exception exception()
    {
        return exception;
    }

    @JsonIgnore
    public void setStatus(int status)
    {
        this.status = Status.fromStatusCode(status);
    }

    @JsonIgnore
    public void setStatus(Status status)
    {
        this.status = status;
    }

    @JsonIgnore
    public Response toResponse()
    {
        return new PlainResultResponse(this).toResponse();
    }

    @JsonIgnore
    public SkandersException toThrowable()
    {
        return exception != null ? new SkandersException(exception) : new SkandersException(message);
    }

    @JsonIgnore
    public Response toResponse(MultivaluedHashMap<String, Object> headers)
    {
        PlainResultResponse response = new PlainResultResponse(this);
        response.headers(headers);

        return response.toResponse();
    }

    @Override
    public String toString()
    {
        return this.message;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Result that = (Result) o;

        return code.equals(that.code) &&
                message.equals(that.message);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(code, message, status);
    }

}
