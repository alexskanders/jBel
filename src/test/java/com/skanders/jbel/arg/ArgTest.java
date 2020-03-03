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

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ArgTest
{
    private String getFilePath(String fileName)
    {
        try {
            URL fileUrl = getClass().getClassLoader().getResource(fileName);

            if (fileUrl == null)
                fail("File not found");

            File file = new File(fileUrl.getFile());

            return file.getAbsolutePath();

        } catch (Exception e) {
            fail("Ran into exception getting file");

            return null;
        }

    }

    @Test
    public void argFile()
    {
        ArgFile argFile = ArgFile.parse(getFilePath("arg.txt"));

        assertEquals("ValueOne", argFile.copyAsString("One"));
        assertEquals("ValueTWO", argFile.copyAsString("TWO"));
        assertEquals("Valuethree", argFile.copyAsString("three"));
        assertEquals("ValueFoUr", argFile.copyAsString("FoUr"));
    }
}
