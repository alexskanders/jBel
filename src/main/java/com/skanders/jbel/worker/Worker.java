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

import com.skanders.jbel.worker.def.WorkerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class Worker extends Thread
{
    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    private static volatile Integer threadCount = 0;
    Integer threadID;
    String  name;

    private volatile WorkerState state;

    Worker(String name)
    {
        this.state = WorkerState.NONE;

        setThreadID();
        this.name = name + " [" + threadID + "] ";
    }

    synchronized private void setThreadID()
    {
        this.threadID = threadCount++;
    }

    void finish()
    {
        state = WorkerState.STOPPED;
        LOG.info(name + "Has been requested to finish");
    }

    WorkerState getWorkerState()
    {
        return state;
    }

    @Override
    public void run()
    {
        state = WorkerState.WORKING;
        LOG.info(name + "Has started");

        while (state == WorkerState.WORKING)
            runTask();

        state = WorkerState.STOPPED;
        LOG.info(name + "Has finished");
    }

    protected abstract void runTask();
}
