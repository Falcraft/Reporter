/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Database.Connection;

import java.sql.Connection;
import java.sql.SQLException;
import net.KabOOm356.Database.Connection.ConnectionPoolManager;
import net.KabOOm356.Database.Connection.PooledConnection;

public class AlertingPooledConnection
extends PooledConnection {
    private final ConnectionPoolManager manager;

    public AlertingPooledConnection(ConnectionPoolManager manager, int connectionId, Connection connection) {
        super(connectionId, connection);
        this.manager = manager;
    }

    @Override
    public void close() throws SQLException {
        super.close();
        int connectionId = this.getConnectionId();
        this.manager.connectionClosed(connectionId);
    }
}

