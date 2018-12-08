/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Database.SQL;

import java.sql.SQLException;
import net.KabOOm356.Database.Connection.ConnectionPoolConfig;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.DatabaseType;
import net.KabOOm356.Util.FormattingUtil;

public class MySQL
extends Database {
    private final String username;
    private final String password;

    public MySQL(String host, String database, String username, String password, ConnectionPoolConfig connectionPoolConfig) {
        super(DatabaseType.MYSQL, "com.mysql.jdbc.Driver", "jdbc:mysql://" + host + '/' + database, connectionPoolConfig);
        this.username = username;
        this.password = password;
    }

    @Override
    public void openConnection() throws ClassNotFoundException, SQLException, InterruptedException {
        super.openConnection(this.username, this.password);
    }

    @Override
    public int openPooledConnection() throws ClassNotFoundException, SQLException, InterruptedException {
        return super.openPooledConnection(this.username, this.password);
    }

    @Override
    public String toString() {
        String toString = "Database Type: MySQL\n";
        toString = toString + "Database Username: " + this.username;
        toString = toString + '\n' + super.toString();
        toString = FormattingUtil.addTabsToNewLines(toString, 1);
        return toString;
    }
}

