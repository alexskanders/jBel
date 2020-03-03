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

package com.skanders.jbel.arg;

import com.skanders.jbel.def.SkandersException;

import java.util.Hashtable;

public class Args
{
    private Hashtable<String, String> argTable;

    private static final String MISSING = new String("_MISSING");
    private static final String FLAG    = new String("_FLAG");
    private static final String PREFIX  = "-";

    private Args(Hashtable<String, String> argTable)
    {
        this.argTable = argTable;
    }

    /**
     * Parses argNames and places the corresponding argKeys in a hashtable
     * <p>
     * -file file.txt (argValue 'file.txt' corresponding with the argKey
     * 'file')
     * <p>
     * All argNames will automatically be prefixed with '-'. If a argName is
     * given with a '-' already none will be added.
     * <p>
     * If a argName is placed before another argName it is considered a flag and
     * will be given a "true" value in the hashtable.
     * <p>
     * All argNames not called in the argKeys list are given a missing flag.
     *
     * @param argKeys  argument values to be attached to names
     * @param argNames argument names to search for
     * @return an instance of {@link Args}
     */
    public static Args parse(String[] argKeys, String... argNames)
    {
        Hashtable<String, String> argTable = new Hashtable<>();

        for (String arg : argNames)
            if (arg.startsWith(PREFIX))
                argTable.put(arg, MISSING);
            else
                argTable.put(PREFIX + arg, MISSING);


        int length = argKeys.length;

        for (int i = 0; i < length; i++)
            if (argTable.containsKey(argKeys[i])) {
                if (i + 1 < length) {
                    if (argKeys[i + 1].startsWith(PREFIX))
                        argTable.put(argKeys[i], FLAG);
                    else
                        argTable.put(argKeys[i], argKeys[++i]);

                } else {
                    argTable.put(argKeys[i], FLAG);

                }
            }

        return new Args(argTable);
    }

    /**
     * Checks to see if argument was given a value
     *
     * @param key argument name to check
     * @return true if value is missing
     */
    public boolean isMissing(String key)
    {
        // Purposely doing object reference comparison
        return MISSING == getValue(key);
    }

    /**
     * Checks to see if argument was given a flag status
     *
     * @param key argument name to check
     * @return true if value is flagged
     */
    public boolean isTrue(String key)
    {
        // Purposely doing object reference comparison
        return FLAG == getValue(key);
    }

    /**
     * Gets the corresponding argument value
     *
     * @param key argument name
     * @return argument value
     */
    private String getValue(String key)
    {
        String value = key.startsWith(PREFIX) ?
                       argTable.get(key) :
                       argTable.get(PREFIX + key);

        if (value == null)
            throw new SkandersException("Named Argument not given at parse.");

        return value;
    }

    /**
     * Gets the corresponding argument value
     *
     * @param key argument name
     * @return argument value, or null if missing or flag
     */
    public String get(String key)
    {
        String value = getValue(key);

        return needNull(value) ? null : value;
    }

    /**
     * Checks if value is either missing or flagged
     *
     * @param value value to check
     * @return true if value is FLAG or MISSING
     */
    private boolean needNull(String value)
    {
        return value == FLAG || value == MISSING;
    }
}