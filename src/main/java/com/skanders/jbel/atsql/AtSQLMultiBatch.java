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

public class AtSQLMultiBatch
{
    private static final Logger LOG = LoggerFactory.getLogger(AtSQLMultiBatch.class);

    private final AtSQL                atSQL;
    private final List<String>         queryList;
    private final List<AtSQLParamList> atSQLParamList;

    private AtSQLParamList singleList;

    private boolean closed;
    private int     listIndex;

    AtSQLMultiBatch(@Nonnull AtSQL atSQL)
    {
        Verify.notNull(atSQL, "atSQL cannot be null.");

        this.atSQL          = atSQL;
        this.queryList      = new ArrayList<>();
        this.atSQLParamList = new ArrayList<>();
        this.closed         = false;
        this.listIndex      = -1;
    }

    public AtSQLMultiBatch setQuery(String query)
    {
        queryList.add(query);
        listIndex++;
        atSQLParamList.add(new AtSQLParamList());

        return this;
    }

    public AtSQLMultiBatch setList(Object... params)
    {
        Verify.notTrue(listIndex == -1, "Must set a query before setting params!");

        atSQLParamList.get(listIndex).setList(params);

        return this;
    }

    public AtSQLMultiBatch set(int type, Object param)
    {
        Verify.notTrue(listIndex == -1, "Must set a query before setting params!");

        atSQLParamList.get(listIndex).setPair(type, param);

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

            int[] rowsUpdated = new int[queryList.size()];

            for (int i = 0; i < queryList.size(); i++) {
                AtSQLStatement atSQLStatement = atSQLConnection.preparedStatement(queryList.get(i));

                atSQLStatement.setParams(atSQLParamList.get(i));

                rowsUpdated[i] = atSQLStatement.executeUpdate();
            }

            atSQLConnection.commit();

            return Resulted.inValue(rowsUpdated);

        } catch (SQLException e) {
            LOG.error(LogPattern.EXIT_FAIL, "Prepare Database Update Execution", e.getClass(), e.getMessage());

            return Resulted.inException(e);

        }
    }
}
