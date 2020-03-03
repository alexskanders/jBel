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

import com.skanders.jbel.def.LogPattern;
import com.skanders.jbel.def.Verify;
import com.skanders.jbel.result.Resulted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AtSQLQuery
{
    private static final Logger LOG = LoggerFactory.getLogger(AtSQLQuery.class);

    private final String         query;
    private final AtSQL          atSQL;
    private final AtSQLParamList atSQLParamList;

    private boolean closed;

    AtSQLQuery(@Nonnull String query, @Nonnull AtSQL atSQL)
    {
        Verify.notNull(query, "query cannot be null.");
        Verify.notNull(atSQL, "atSQL cannot be null.");

        this.query          = query;
        this.atSQL          = atSQL;
        this.atSQLParamList = new AtSQLParamList();
        this.closed         = false;
    }

    public AtSQLQuery setList(Object... params)
    {
        atSQLParamList.setList(params);

        return this;
    }

    public AtSQLQuery set(int type, Object param)
    {
        atSQLParamList.setPair(type, param);

        return this;
    }

    public Resulted<Integer> executeUpdate()
    {
        Verify.notTrue(closed, "SQLQuery cannot be called after closed");
        this.closed = true;

        LOG.debug(LogPattern.ENTER, "Database Execute Update");

        try (AtSQLConnection atSQLConnection = atSQL.newConnection()) {

            Integer updateCount = atSQLConnection
                    .preparedStatement(query)
                    .setParams(atSQLParamList)
                    .executeUpdate();

            return Resulted.inValue(updateCount);

        } catch (SQLException e) {
            LOG.error(LogPattern.EXIT_FAIL, "Prepare Database Update Execution", e.getClass(), e.getMessage());

            return Resulted.inException(e);

        }
    }

    public Resulted<AtSQLResult> executeQuery()
    {
        Verify.notTrue(closed, "SQLQuery cannot be called after closed");
        this.closed = true;

        LOG.debug(LogPattern.ENTER, "Database Execute Query");

        AtSQLConnection atSQLConnection = null;

        try {
            atSQLConnection = atSQL.newConnection();

            ResultSet rs = atSQLConnection
                    .preparedStatement(query)
                    .setParams(atSQLParamList)
                    .executeQuery();

            return Resulted.inValue(AtSQLResult.newInstance(atSQLConnection, rs));

        } catch (SQLException e) {
            LOG.error(LogPattern.EXIT_FAIL, "Prepare Database Query Execution", e.getClass(), e.getMessage());

            Verify.closed(atSQLConnection);

            return Resulted.inException(e);

        }
    }
}
