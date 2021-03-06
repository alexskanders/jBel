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

package com.skanders.jbel.worker.def;


import com.skanders.jbel.def.SkandersResult;
import com.skanders.jbel.result.Result;

public class WorkerValidate
{
    public static Result requestWorkerState(WorkerRequestState state)
    {
        if (state == null) {
            return SkandersResult.MISSING_REQUEST_STATE;

        } else if (state == WorkerRequestState.INVALID) {
            return SkandersResult.INVALID_REQUEST_STATE;

        } else {
            return Result.VALID;

        }
    }

    public static Result period(WorkerRequestState state, String period)
    {
        if (state != WorkerRequestState.DURATION) {
            return Result.VALID;

        } else if (period == null) {
            return SkandersResult.MISSING_REQUEST_PERIOD;

        } else if (period.length() < 2) {
            return SkandersResult.INVALID_REQUEST_PERIOD;

        }

        return Result.VALID;
    }
}
