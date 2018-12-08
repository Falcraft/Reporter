/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Database.Connection;

public class ConnectionPoolConfig {
    public static final ConnectionPoolConfig defaultInstance = new ConnectionPoolConfig();
    private static final int minimumMaxConnections = 1;
    private static final int minimumConnectionPoolUpdate = 10;
    private static final int minimumMaxAttemptsForConnection = 50;
    private boolean connectionPoolLimit = true;
    private int maxConnections = 10;
    private long waitTimeBeforeUpdate = 10L;
    private int maxAttemptsForConnection = 200;

    private ConnectionPoolConfig() {
    }

    public ConnectionPoolConfig(boolean connectionPoolLimit, int maxConnections, long connectionPoolUpdate, int maxAttemptsForConnection) {
        if (maxConnections < 1) {
            throw new IllegalArgumentException(String.format("Parameter 'maxConnections' cannot be less than [%d]!", 1));
        }
        if (connectionPoolUpdate < 10L) {
            throw new IllegalArgumentException(String.format("Parameter 'connectionPoolUpdate' cannot be less than [%d]!", 10));
        }
        if (maxAttemptsForConnection < 50) {
            throw new IllegalArgumentException(String.format("Parameter 'maxAttemptsForConnection' cannot be less than [%d]!", 50));
        }
        this.connectionPoolLimit = connectionPoolLimit;
        this.maxConnections = maxConnections;
        this.waitTimeBeforeUpdate = connectionPoolUpdate;
        this.maxAttemptsForConnection = maxAttemptsForConnection;
    }

    public boolean isConnectionPoolLimited() {
        return this.connectionPoolLimit;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public long getWaitTimeBeforeUpdate() {
        return this.waitTimeBeforeUpdate;
    }

    public int getMaxAttemptsForConnection() {
        return this.maxAttemptsForConnection;
    }
}

