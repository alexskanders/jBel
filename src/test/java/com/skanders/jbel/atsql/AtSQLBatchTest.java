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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

public class AtSQLBatchTest
{
    @BeforeAll
    public static void clearDb()
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

        if (resultedDrop.notValid())
            fail("Could not drop student table");

        Resulted<Integer> resultedCreate =
                Resources.AT_SQL.createQuery(create).executeUpdate();

        if (resultedCreate.notValid())
            fail("Could not drop student table");
    }

    @Test
    public void batchInsert()
    {
        String query = "\n" +
                "INSERT INTO student \n" +
                "     (id, name, age, major, year) \n" +
                "VALUES \n" +
                "     (?,?,?,?,?)";

        AtSQLBatch atSQLBatch = Resources.AT_SQL.createBatch(query)
                .setList(1, "Student1", 18, "CS", 1)
                .setList(2, "Student2", 19, "CS", 2)
                .setList(3, "Student3", 20, "CS", 3)
                .setList(4, "Student4", 21, "CS", 4)
                .setList(5, "Student5", 22, "CS", 5);

        Resulted<int[]> resulted = atSQLBatch.executeBatch();

        assertFalse(resulted.notValid());
    }

    @Test
    public void batchInsertAdd()
    {
        String query = "\n" +
                "INSERT INTO student \n" +
                "     (id, name, age, major, year) \n" +
                "VALUES \n" +
                "     (?,?,?,?,?)";

        AtSQLBatch atSQLBatch = Resources.AT_SQL.createBatch(query);

        for (int i = 6; i <= 10; i++) {
            atSQLBatch
                    .add(i)
                    .add("Student" + i)
                    .add(i + 17)
                    .add("CS")
                    .add(i)
                    .pushList();
        }


        Resulted<int[]> resulted = atSQLBatch.executeBatch();

        assertFalse(resulted.notValid());
    }

    @Test
    public void batchInsertAddPair()
    {
        String query = "\n" +
                "INSERT INTO student \n" +
                "     (id, name, age, major, year) \n" +
                "VALUES \n" +
                "     (?,?,?,?,?)";

        AtSQLBatch atSQLBatch = Resources.AT_SQL.createBatch(query);

        for (int i = 11; i <= 15; i++) {
            atSQLBatch
                    .add(Types.INTEGER, i)
                    .add(Types.VARCHAR, "Student" + i)
                    .add(Types.INTEGER, i + 17)
                    .add(Types.VARCHAR, "CS")
                    .add(Types.INTEGER, i)
                    .pushList();
        }

        Resulted<int[]> resulted = atSQLBatch.executeBatch();

        assertFalse(resulted.notValid());
    }
}
