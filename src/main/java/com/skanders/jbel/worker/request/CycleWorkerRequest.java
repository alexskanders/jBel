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

package com.skanders.jbel.worker.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skanders.jbel.def.SkandersResult;
import com.skanders.jbel.model.RequestModel;
import com.skanders.jbel.result.Result;
import com.skanders.jbel.worker.def.WorkerRequestState;
import com.skanders.jbel.worker.def.WorkerValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class CycleWorkerRequest extends RequestModel
{
    private static final Logger LOG = LoggerFactory.getLogger(CycleWorkerRequest.class);

    @JsonProperty("request")
    private String             request;
    @JsonProperty("period")
    private String             period;
    @JsonIgnore
    private WorkerRequestState requestState;
    @JsonIgnore
    private Duration           duration;

    @JsonCreator
    public CycleWorkerRequest(
            @JsonProperty(value = "request", required = true) String request,
            @JsonProperty(value = "period") String period)
    {
        this.request = request;
        this.period  = period;
    }

    public WorkerRequestState getRequestState()
    {
        return requestState;
    }

    public Duration getDuration()
    {
        return duration;
    }

    private Result parseRequest()
    {
        if (period == null)
            return Result.VALID;

        int last = period.length() - 1;

        try {
            long time = Long.parseLong(period.substring(0, last));
            char unit = period.charAt(last);
            LOG.info("Worker Duration set to: " + time + " " + unit);

            switch (unit) {
                case 'S':
                case 's':
                    duration = Duration.ofSeconds(time);
                    LOG.info("Duration of Seconds");
                    return Result.VALID;
                case 'M':
                case 'm':
                    duration = Duration.ofMinutes(time);
                    LOG.info("Duration of Minutes");
                    return Result.VALID;
                case 'H':
                case 'h':
                    duration = Duration.ofHours(time);
                    LOG.info("Duration of Hours");
                    return Result.VALID;
                case 'D':
                case 'd':
                    duration = Duration.ofDays(time);
                    LOG.info("Duration of Days");
                    return Result.VALID;
                default:
                    return SkandersResult.INVALID_PERIOD_UNIT;
            }

        } catch (NumberFormatException e) {
            return SkandersResult.INVALID_PERIOD_UNIT;

        }
    }

    @Override
    public Result validate()
    {
        Result result;

        this.requestState = WorkerRequestState.toState(request);

        result = WorkerValidate.requestWorkerState(requestState);
        if (result.notValid())
            return result;

        result = WorkerValidate.period(requestState, period);
        if (result.notValid())
            return result;

        result = parseRequest();
        if (result.notValid())
            return result;

        return Result.VALID;
    }
}
