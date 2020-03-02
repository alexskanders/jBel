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

import com.skanders.jbel.def.LogPattern;
import com.skanders.jbel.def.SkandersException;
import com.skanders.jbel.model.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Resulted<T> implements AutoCloseable
{
    private static final Logger LOG = LoggerFactory.getLogger(Resulted.class);

    private final T      value;
    private       Result result;

    private Resulted(@Nullable T value, @Nonnull Result result)
    {
        this.value  = value;
        this.result = result;
    }

    public static <T> Resulted<T> inValue(@Nonnull T value)
    {
        return new Resulted<>(value, Result.VALID);
    }

    public static <T> Resulted<T> inResult(@Nonnull Result result)
    {
        return new Resulted<>(null, result);
    }

    public static <T> Resulted<T> inException(@Nonnull Exception exception)
    {
        return new Resulted<>(null, Result.exception(exception));
    }

    public static <T, S> Resulted<T> inResulted(@Nonnull Resulted<S> resulted)
    {
        return new Resulted<>(null, resulted.result);
    }

    public T value()
    {
        return value;
    }

    public Result result()
    {
        return result;
    }

    public boolean notValid()
    {
        if (this.result != Result.VALID) {
            LOG.trace("Result in non valid result: {}", this.result.message());
            return true;
        }

        return false;
    }

    public boolean notValid(ResponseModel responseModel)
    {
        if (this.result != Result.VALID) {
            LOG.trace("Result in non valid result: {}", this.result.message());
            responseModel.setResult(result);
            return true;
        }

        return false;
    }

    public void throwOnNotValid()
    {
        if (this.result != Result.VALID) {
            LOG.trace("Result in non valid result: {}", this.result.message());
            throw this.result.toThrowable();
        }
    }

    public SkandersException toThrowable()
    {
        return result.toThrowable();
    }

    @Override
    public void close()
    {
        LOG.trace("Close being called on WorkFlow Value");
        try {
            if (value == null) {
                LOG.trace("Value is null");

            } else if (value instanceof AutoCloseable) {
                ((AutoCloseable) value).close();
                LOG.trace("Value has been closed");

            } else {
                LOG.trace("Value is not instance of AutoCloseable");

            }


        } catch (Exception e) {
            LOG.error("SEVERE ERROR: Exception when trying to close");
            LOG.error(LogPattern.ERROR, e.getClass(), e.getMessage());

        }
    }
}
