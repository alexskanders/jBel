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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;

public class ArgFile implements AutoCloseable
{

    private static final char EMPTY_CHAR = '\u0000';

    private static final int EOF         = -1;
    private static final int BUFFER_SIZE = 4096;
    private static final int ASSIGN      = '=';
    private static final int END         = '\n';

    private enum ReadState
    {KEY, VALUE}

    private Hashtable<String, char[]> charTable;

    /**
     * Reads in values in the format:
     * <p>
     * """ key=value keyTwo=valueTwo """
     * <p>
     * using '=' as the delimiter and treating all following '=' characters as
     * part of the value as such: pass=123=456  (value of pass would be
     * "123=456")
     * <p>
     * spaces are treated as part of the key AND value, they are not trimmed.
     * key = value ("key " would equal " value", highly discouraged)
     *
     * @param filePath filePath with the correct format
     * @return an instance of Args with the values stored
     */
    public static ArgFile parse(String filePath)
    {

        final int[] buffer = new int[BUFFER_SIZE];

        Hashtable<String, char[]> argTable = new Hashtable<>();

        try (FileInputStream inputStream = new FileInputStream(new File(filePath))) {
            ReadState state = ReadState.KEY;
            String    key   = null;
            int       index = 0;

            while ((buffer[index] = inputStream.read()) != EOF) {
                // Eat Carriage Return's
                if (buffer[index] == '\r') continue;

                if (buffer[index] == ASSIGN && state == ReadState.KEY) {
                    if (index == 0)
                        throw new SkandersException("Invalid Arg File format: Invalid Key");

                    key = String.copyValueOf(getCharArray(buffer, index));

                    state = ReadState.VALUE;
                    index = 0;

                } else if (buffer[index] == END) {
                    if (index == 0 || state != ReadState.VALUE)
                        throw new SkandersException("Invalid Arg File format: Invalid Value");

                    argTable.put(key, getCharArray(buffer, index));

                    state = ReadState.KEY;
                    index = 0;

                } else {
                    index++;

                }
            }

            if (state == ReadState.VALUE) {
                if (index == 0)
                    throw new SkandersException("Invalid Arg File format: Invalid Value");

                argTable.put(key, getCharArray(buffer, index));
            }

            return new ArgFile(argTable);

        } catch (FileNotFoundException e) {
            throw new SkandersException("Failed to find Args File");

        } catch (IOException e) {
            throw new SkandersException("Failed to read Args File");

        } finally {
            Arrays.fill(buffer, EMPTY_CHAR);

        }
    }

    private ArgFile(Hashtable<String, char[]> charTable)
    {
        this.charTable = charTable;
    }

    public String copyAsString(String arg)
    {
        char[] charArray = charTable.get(arg);

        return charArray == null ? null : String.copyValueOf(charTable.get(arg));
    }

    public char[] get(String arg)
    {
        return charTable.get(arg);
    }

    private static char[] getCharArray(int[] buffer, int index)
    {
        char[] charArray = new char[index];

        for (int i = 0; i < index; i++)
             charArray[i] = (char) buffer[i];

        return charArray;
    }

    @Override
    public void close()
    {
        for (String key : charTable.keySet())
            Arrays.fill(charTable.get(key), EMPTY_CHAR);
    }
}
