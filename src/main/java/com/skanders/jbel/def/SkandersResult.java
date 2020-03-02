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

package com.skanders.jbel.def;

import com.skanders.jbel.result.Result;

import javax.ws.rs.core.Response.Status;

public final class SkandersResult
{
    /* Three Main Result Values */
    public static final Result VALID;
    public static final Result UNDECLARED;
    public static final Result EXCEPTION;


    /* Mapping Results */
    public static final Result JSON_MAPPING_EXCEPT;
    public static final Result JSON_PARSE_EXCEPT;

    public static final Result MODEL_VALUE_MISSING;

    public static final Result FROM_JSON_NO_VALUE_FOUND;


    /* Worker Results */
    public static final Result INVALID_REQUEST_STATE;
    public static final Result MISSING_REQUEST_STATE;
    public static final Result MISSING_REQUEST_PERIOD;
    public static final Result INVALID_PERIOD_UNIT;
    public static final Result INVALID_REQUEST_PERIOD;

    public static final Result FINISHED_TASK;

    public static final Result WORKER_STARTED;
    public static final Result WORKER_HAS_NOT_STARTED;

    public static final Result WORKER_ALREADY_STARTED;
    public static final Result WORKER_STOPPED;

    public static final Result WORKER_RESTARTED;
    public static final Result WORKER_ALREADY_STOPPED;

    public static final Result WORKER_INVOKED;
    public static final Result WORKER_CANNOT_INVOKE;

    public static final Result WORKER_STARTED_DURATION;
    public static final Result WORKER_RESTARTED_DURATION;

    public static final Result WORKER_STATUS_NONE;
    public static final Result WORKER_STATUS_WORKING;
    public static final Result WORKER_STATUS_STOPPED;

    static {
        /* Three Main Result Values */
        VALID      = Result.VALID;
        UNDECLARED = Result.UNDECLARED;
        EXCEPTION  = Result.EXCEPTION;



        /* Mapping Results */
        JSON_MAPPING_EXCEPT = Result.declare(-100, "JSON mapping Exception", Status.BAD_REQUEST);
        JSON_PARSE_EXCEPT   = Result.declare(-101, "JSON parse Exception", Status.BAD_REQUEST);

        MODEL_VALUE_MISSING = Result.declare(-102, "Required model value missing", Status.BAD_REQUEST);

        FROM_JSON_NO_VALUE_FOUND = Result.declare(100, "FromJson conversion failed");



        /* Worker Results */
        INVALID_REQUEST_STATE  = Result.declare(-200, "Invalid worker state provided", Status.BAD_REQUEST);
        MISSING_REQUEST_STATE  = Result.declare(-201, "No worker state provided", Status.BAD_REQUEST);
        MISSING_REQUEST_PERIOD = Result.declare(-202, "No period value provided", Status.BAD_REQUEST);
        INVALID_PERIOD_UNIT    = Result.declare(-203, "Invalid period unit provided", Status.BAD_REQUEST);
        INVALID_REQUEST_PERIOD = Result.declare(-204, "Invalid period value provided", Status.BAD_REQUEST);

        FINISHED_TASK = Result.declare(200, "Finished Task.");

        WORKER_STARTED         = Result.declare(210, "Worker started.");
        WORKER_HAS_NOT_STARTED = Result.declare(211, "Worker not started yet.");

        WORKER_ALREADY_STARTED = Result.declare(212, "Worker already started.");
        WORKER_STOPPED         = Result.declare(213, "Worker stopped.");

        WORKER_RESTARTED       = Result.declare(214, "Worker restarted.");
        WORKER_ALREADY_STOPPED = Result.declare(215, "Worker already stopped.");

        WORKER_INVOKED       = Result.declare(216, "Worker invoked.");
        WORKER_CANNOT_INVOKE = Result.declare(217, "Worker cannot Invoke, already stopped.");

        WORKER_STARTED_DURATION   = Result.declare(218, "Worker started with new duration");
        WORKER_RESTARTED_DURATION = Result.declare(219, "Worker restarted with new duration");

        WORKER_STATUS_NONE    = Result.declare(220, "Worker is currently uninitialized.");
        WORKER_STATUS_WORKING = Result.declare(221, "Worker is currently working.");
        WORKER_STATUS_STOPPED = Result.declare(223, "Worker is currently stopped.");
    }
}
