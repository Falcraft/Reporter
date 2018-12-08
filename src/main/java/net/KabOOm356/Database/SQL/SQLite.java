/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Database.SQL;

import net.KabOOm356.Database.Connection.ConnectionPoolConfig;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.DatabaseType;

public class SQLite
extends Database {
    public SQLite(String SQLFile, ConnectionPoolConfig connectionPoolConfig) {
        super(DatabaseType.SQLITE, "org.sqlite.JDBC", "jdbc:sqlite:" + SQLFile, connectionPoolConfig);
    }
}

