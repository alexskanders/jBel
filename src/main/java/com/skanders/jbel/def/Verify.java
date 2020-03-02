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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Verify
{
    private static final Logger LOG = LoggerFactory.getLogger(Verify.class);

    public static void notNull(Object ob)
    {
        if (ob == null)
            throw new SkandersException();
    }

    public static void notNull(Object ob, String message)
    {
        if (ob == null)
            throw new SkandersException(message);
    }

    public static void notTrue(boolean arg)
    {
        if (arg)
            throw new SkandersException();
    }

    public static void notTrue(boolean arg, String message)
    {
        if (arg)
            throw new SkandersException(message);
    }

    public static void isTrue(boolean arg)
    {
        if (!arg)
            throw new SkandersException();
    }

    public static void isTrue(boolean arg, String message)
    {
        if (!arg)
            throw new SkandersException(message);
    }

    public static <T extends AutoCloseable> void closed(T closeableObject)
    {
        try {
            if (closeableObject != null)
                closeableObject.close();

        } catch (Exception e) {
            LOG.error("SEVERE ERROR: Exception when trying to close");
            LOG.error(LogPattern.ERROR, e.getClass(), e.getMessage());

        }
    }
}
