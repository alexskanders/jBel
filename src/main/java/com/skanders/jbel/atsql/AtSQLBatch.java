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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AtSQLBatch
{
    private static final Logger LOG = LoggerFactory.getLogger(AtSQLBatch.class);

    private final AtSQL                atSQL;
    private final String               query;
    private final List<AtSQLParamList> atSQLParamList;

    private AtSQLParamList singleList;

    private boolean closed;

    AtSQLBatch(String query, @Nonnull AtSQL atSQL)
    {
        Verify.notNull(query, "query cannot be null.");
        Verify.notNull(atSQL, "atSQL cannot be null.");

        this.query          = query;
        this.atSQL          = atSQL;
        this.atSQLParamList = new ArrayList<>();
        this.closed         = false;
    }

    public AtSQLBatch setList(Object... params)
    {
        Verify.isTrue(singleList == null, "addBatchList() was not called after using add(...)");

        atSQLParamList.add(new AtSQLParamList(params));

        return this;
    }

    public AtSQLBatch add(int type, Object value)
    {
        if (singleList == null)
            singleList = new AtSQLParamList();

        singleList.setPair(type, value);

        return this;
    }


    public AtSQLBatch add(Object value)
    {
        if (singleList == null)
            singleList = new AtSQLParamList();

        singleList.set(value);

        return this;
    }

    public AtSQLBatch pushList()
    {
        Verify.isTrue(singleList != null, "addBatchList() cannot be called until add() is used to start a list");

        atSQLParamList.add(singleList);
        singleList = null;

        return this;
    }

    public Resulted<int[]> executeBatch()
    {
        Verify.notTrue(closed, "SQLQuery cannot be called after closed");
        Verify.notTrue(singleList != null, "using add() requires the use of setBatchList() between set lists");

        this.closed = true;

        LOG.debug(LogPattern.ENTER, "Database Execute Update");

        try (AtSQLConnection atSQLConnection = atSQL.newConnection()) {

            atSQLConnection.setAutoCommitOff();

            AtSQLStatement atSQLStatement = atSQLConnection.preparedStatement(query);

            for (AtSQLParamList params : atSQLParamList)
                atSQLStatement.setBatch(params);

            int[] rowUpdates = atSQLStatement.executeBatch();

            atSQLConnection.commit();

            return Resulted.inValue(rowUpdates);

        } catch (SQLException e) {
            LOG.error(LogPattern.EXIT_FAIL, "Prepare Database Update Execution", e.getClass(), e.getMessage());

            return Resulted.inException(e);

        }
    }
}
