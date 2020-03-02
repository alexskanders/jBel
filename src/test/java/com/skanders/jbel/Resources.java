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

package com.skanders.jbel;

import com.skanders.jbel.arg.ArgFile;
import com.skanders.jbel.atsql.AtSQL;
import com.skanders.jbel.atsql.AtSQLFactory;

import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.fail;

public class Resources
{
    public static final AtSQL AT_SQL;

    static {
        ArgFile argFile = ArgFile.parse(getFilePath("db.txt"));

        System.out.println(argFile.copyAsString("jdbcUrl"));

        AT_SQL = AtSQLFactory.newInstance(
                argFile.copyAsString("username"),
                argFile.copyAsString("password"),
                30000, 10)
                .withJdbcUrl(argFile.copyAsString("jdbcUrl"))
                .withMySQLPerformanceSettings()
                .build();
    }

    public static String getFilePath(String fileName)
    {
        try {
            URL fileUrl = Resources.class.getClassLoader().getResource(fileName);

            if (fileUrl != null)
                return fileUrl.toURI().getPath();
            else
                fail("File not found");

        } catch (URISyntaxException e) {
            fail("File not found");

        }

        return null;
    }
}
