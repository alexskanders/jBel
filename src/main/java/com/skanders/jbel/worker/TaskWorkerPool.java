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

import com.skanders.jbel.worker.def.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskWorkerPool
{
    private static final Logger LOG = LoggerFactory.getLogger(TaskWorkerPool.class);

    private ArrayList<TaskWorker> taskWorkers;
    private BlockingQueue<Task>   taskQueue;

    private Integer workerCount;
    private String  name;

    private TaskWorkerPool(String name, int workerCount)
    {
        this.name        = name;
        this.workerCount = workerCount;

        taskQueue   = new LinkedBlockingQueue<>();
        taskWorkers = new ArrayList<>();

        initPool();
    }

    public static TaskWorkerPool create(String name, int workerCount)
    {
        return new TaskWorkerPool(name, workerCount);
    }

    private void initPool()
    {
        for (int i = 0; i < workerCount; i++)
             taskWorkers.add(new TaskWorker(name, this));
    }

    Task takeTask()
    {
        try {
            return taskQueue.take();
        } catch (InterruptedException e) {
            LOG.error("Failed to take task from task queue");
            return null;
        }
    }

    public boolean putTask(Task task)
    {
        try {
            taskQueue.put(task);
            return true;
        } catch (InterruptedException e) {
            LOG.error("Failed to put task into task queue");
            return false;
        }
    }

    public void startPool()
    {
        for (TaskWorker tw : taskWorkers)
            tw.start();
    }
}
