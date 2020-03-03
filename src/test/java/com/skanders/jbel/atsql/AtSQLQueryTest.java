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

import com.skanders.jbel.Resources;
import com.skanders.jbel.result.Resulted;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertFalse;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AtSQLQueryTest
{
    @Test
    @Order(1)
    public void queryInitialTest()
    {
        String drop =
                "DROP TABLE IF EXISTS student;";

        String create =
                "CREATE TABLE student " +
                        "(" +
                        "    id    INT         NOT NULL PRIMARY KEY," +
                        "    name  VARCHAR(64) NOT NULL," +
                        "    age   INT         NOT NULL," +
                        "    major VARCHAR(64) NULL," +
                        "    year  INT         NOT NULL" +
                        ");";

        Resulted<Integer> resultedDrop =
                Resources.AT_SQL.createQuery(drop).executeUpdate();

        assertFalse(resultedDrop.notValid());

        Resulted<Integer> resultedCreate =
                Resources.AT_SQL.createQuery(create).executeUpdate();

        assertFalse(resultedCreate.notValid());
    }

    @Test
    @Order(2)
    public void querySetListTest()
    {
        String query = "\n" +
                "INSERT INTO student \n" +
                "     (id, name, age, major, year) \n" +
                "VALUES \n" +
                "     (?,?,?,?,?)";

        AtSQLQuery atSQLQuery = Resources.AT_SQL.createQuery(query)
                .setList(1, "Student1", 18, "CS", 1);

        Resulted<Integer> resulted = atSQLQuery.executeUpdate();

        assertFalse(resulted.notValid());
    }

    @Test
    @Order(3)
    public void querySetTypeTest()
    {
        String query = "\n" +
                "INSERT INTO student \n" +
                "     (id, name, age, major, year) \n" +
                "VALUES \n" +
                "     (?,?,?,?,?)";

        AtSQLQuery atSQLQuery = Resources.AT_SQL.createQuery(query)
                .set(Types.INTEGER, 3)
                .set(Types.VARCHAR, "Student3")
                .set(Types.INTEGER, 20)
                .set(Types.VARCHAR, "CS")
                .set(Types.INTEGER, 3);

        Resulted<Integer> resulted = atSQLQuery.executeUpdate();

        assertFalse(resulted.notValid());
    }
}
