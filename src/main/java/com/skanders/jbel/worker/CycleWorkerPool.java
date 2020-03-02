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

package com.skanders.jbel.worker;

import com.skanders.jbel.worker.def.WorkerStatus;

import java.util.ArrayList;

public class CycleWorkerPool
{
    private ArrayList<CycleWorker> cycleWorkers;

    public CycleWorkerPool()
    {
        this.cycleWorkers = new ArrayList<>();
    }

    public void add(CycleWorker cycleWorker)
    {
        cycleWorkers.add(cycleWorker);
    }

    public WorkerStatus[] getStatuses()
    {
        ArrayList<WorkerStatus> workerStatuses = new ArrayList<>();

        for (CycleWorker cw : cycleWorkers)
            workerStatuses.add(new WorkerStatus(cw.getName(), cw.getState()));

        return workerStatuses.toArray(new WorkerStatus[]{});
    }
}
