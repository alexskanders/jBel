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
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Autonomous SQL (AtSQL)
 * <p>
 * A SQL manager that manages most of the inner workings of SQL. With reliance
 * on {@link AutoCloseable} and {@link com.zaxxer.hikari.pool.HikariPool} most
 * of the resource management and query creation details are abstracted away.
 */
public class AtSQL
{
    private static final Logger LOG = LoggerFactory.getLogger(AtSQL.class);

    private HikariDataSource hikariDataSource;

    AtSQL(HikariConfig config)
    {
        LOG.trace(LogPattern.ENTER, "Connection Pool Constructor");

        hikariDataSource = new HikariDataSource(config);
    }

    public AtSQLMultiBatch createMultiBatch()
    {
        return new AtSQLMultiBatch(this);
    }

    public AtSQLBatch createBatch(@Nonnull String query)
    {
        return new AtSQLBatch(query, this);
    }

    public AtSQLQuery createQuery(@Nonnull String query)
    {
        return new AtSQLQuery(query, this);
    }


    AtSQLConnection newConnection()
            throws SQLException
    {
        LOG.trace(LogPattern.ENTER, "Request Connection");

        Connection connection = null;
        boolean    autoCommit;

        try {
            connection = hikariDataSource.getConnection();
            autoCommit = connection.getAutoCommit();

        } catch (SQLException e) {
            if (connection != null)
                connection.close();

            throw e;
        }

        return new AtSQLConnection(connection, autoCommit);
    }

    void releaseCon(Connection connection)
    {
        hikariDataSource.evictConnection(connection);
    }
}