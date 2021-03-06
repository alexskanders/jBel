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

import com.skanders.jbel.atsql.AtSQL;
import com.skanders.jbel.atsql.AtSQLFactory;

public class Resources
{
    public static final AtSQL AT_SQL;

    static {
        AT_SQL = AtSQLFactory.newInstance(
                System.getenv("DBUSR"),
                System.getenv("DBPSW"),
                30000, 10)
                .withJdbcUrl(System.getenv("DBURL"))
                .withMySQLPerformanceSettings()
                .build();
    }
}
