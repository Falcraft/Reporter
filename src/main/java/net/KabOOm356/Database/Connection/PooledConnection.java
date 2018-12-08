/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Database.Connection;

import java.sql.Connection;
import net.KabOOm356.Database.Connection.ConnectionWrapper;

public class PooledConnection
extends ConnectionWrapper {
    private final int connectionId;

    public PooledConnection(int connectionId, Connection connection) {
        super(connection);
        this.connectionId = connectionId;
    }

    public int getConnectionId() {
        return this.connectionId;
    }
}

