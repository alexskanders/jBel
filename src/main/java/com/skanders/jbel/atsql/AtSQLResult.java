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


package com.skanders.jbel.atsql;

import com.skanders.jbel.def.Verify;

import javax.annotation.Nonnull;
import java.sql.ResultSet;

public class AtSQLResult implements AutoCloseable
{
    private AtSQLConnection atSQLConnection;
    private ResultSet       resultSet;

    private AtSQLResult(AtSQLConnection atSQLConnection, ResultSet resultSet)
    {
        Verify.notNull(atSQLConnection, "query cannot be null.");
        Verify.notNull(resultSet, "atSQL cannot be null.");

        this.atSQLConnection = atSQLConnection;
        this.resultSet       = resultSet;
    }

    static AtSQLResult newInstance(@Nonnull AtSQLConnection atSQLConnection, @Nonnull ResultSet resultSet)
    {
        return new AtSQLResult(atSQLConnection, resultSet);
    }

    public ResultSet getResultSet()
    {
        return resultSet;
    }

    @Override
    public void close()
    {
        atSQLConnection.close();
    }
}

