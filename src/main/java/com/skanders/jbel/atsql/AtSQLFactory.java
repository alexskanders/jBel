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
import com.zaxxer.hikari.HikariConfig;

import java.util.Map;

public class AtSQLFactory
{
    private HikariConfig hikariConfig;
    private boolean      driverOrUrlSet;

    private AtSQLFactory(
            String username, String password, long maxLifetime, int maxPoolSize)
    {
        this.hikariConfig = new HikariConfig();

        this.hikariConfig.setUsername(username);
        this.hikariConfig.setPassword(password);
        this.hikariConfig.setMaxLifetime(maxLifetime);
        this.hikariConfig.setMaximumPoolSize(maxPoolSize);

        this.driverOrUrlSet = false;
    }

    public static AtSQLFactory newInstance(
            String username, String password, long maxLifetime, int maxPoolSize)
    {
        return new AtSQLFactory(username, password, maxLifetime, maxPoolSize);
    }

    public AtSQLFactory withDriver(
            String driver, String hostname, int port, String name)
    {
        Verify.notTrue(this.driverOrUrlSet, "Cannot set both Driver and JDBC, only one choice is allowed.");
        this.driverOrUrlSet = true;

        hikariConfig.setDataSourceClassName(driver);
        hikariConfig.addDataSourceProperty("serverName", hostname);
        hikariConfig.addDataSourceProperty("portNumber", port);
        hikariConfig.addDataSourceProperty("databaseName", name);

        return this;
    }

    public AtSQLFactory withJdbcUrl(String url)
    {
        Verify.notTrue(this.driverOrUrlSet, "Cannot set both Driver and JDBC, only one choice is allowed.");
        this.driverOrUrlSet = true;

        hikariConfig.setJdbcUrl(url);

        return this;
    }

    public AtSQLFactory withDataSourceProperties(Map<String, Object> dbProperties)
    {
        if (dbProperties != null)
            for (String key : dbProperties.keySet())
                hikariConfig.addDataSourceProperty(key, dbProperties.get(key));

        return this;
    }

    public AtSQLFactory withMySQLPerformanceSettings()
    {
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", true);

        return this;
    }

    public AtSQL build()
    {
        Verify.isTrue(driverOrUrlSet, "Driver or URL must be set.");

        return new AtSQL(hikariConfig);
    }

}
