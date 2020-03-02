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
import com.skanders.jbel.def.SkandersException;
import com.skanders.jbel.def.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

class AtSQLConnection implements AutoCloseable
{
    private static final Logger LOG = LoggerFactory.getLogger(AtSQLConnection.class);

    private Connection connection;
    private boolean    autoCommit;
    private boolean    closed;
    private boolean    toggledCommit;

    AtSQLConnection(Connection connection, boolean autoCommit)
    {
        Verify.notNull(connection, "connection cannot be null.");

        this.connection = connection;
        this.autoCommit = autoCommit;
        this.closed     = false;
    }

    void setAutoCommitOff()
            throws SQLException
    {
        if (this.autoCommit) {
            connection.setAutoCommit(false);
            this.toggledCommit = true;
            this.autoCommit    = false;
        }
    }

    void commit()
            throws SQLException
    {
        if (!this.autoCommit) {
            connection.commit();
        } else {
            throw new SkandersException("Attempting to commit on a autoCommit connection");
        }
    }

    AtSQLStatement preparedStatement(String query)
            throws SQLException
    {
        return new AtSQLStatement(connection.prepareStatement(query));
    }

    @Override
    public void close()
    {
        if (this.closed)
            return;

        try {
            if (this.toggledCommit)
                connection.setAutoCommit(this.autoCommit);

            connection.close();
            this.closed = true;

        } catch (SQLException e) {
            LOG.error(LogPattern.EXIT_FAIL, "Could close connection", e.getClass(), e.getMessage());

        }
    }
}
