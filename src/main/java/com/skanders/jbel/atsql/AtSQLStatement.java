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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class AtSQLStatement
{
    private static final Logger LOG = LoggerFactory.getLogger(AtSQLStatement.class);

    private PreparedStatement preparedStatement;

    private boolean closed;
    private boolean toggledCommit;

    AtSQLStatement(PreparedStatement preparedStatement)
    {
        this.preparedStatement = preparedStatement;
    }

    AtSQLStatement setParams(AtSQLParamList atSQLParamList)
            throws SQLException
    {
        int count = 1;

        for (AtSQLParam atSQLParam : atSQLParamList.getList())
            if (atSQLParam.getType() == null)
                preparedStatement.setObject(count++, atSQLParam.getValue());
            else
                preparedStatement.setObject(count++, atSQLParam.getValue(), atSQLParam.getType());

        return this;
    }

    AtSQLStatement setBatch(AtSQLParamList atSQLParamList)
            throws SQLException
    {
        setParams(atSQLParamList);

        preparedStatement.addBatch();

        return this;
    }


    int[] executeBatch()
            throws SQLException
    {
        return preparedStatement.executeBatch();
    }

    int executeUpdate()
            throws SQLException
    {
        return preparedStatement.executeUpdate();
    }

    ResultSet executeQuery()
            throws SQLException
    {
        return preparedStatement.executeQuery();
    }
}

