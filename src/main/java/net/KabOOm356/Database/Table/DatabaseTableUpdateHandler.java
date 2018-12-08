/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Database.Table;

import java.sql.SQLException;
import java.sql.Statement;
import net.KabOOm356.Database.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DatabaseTableUpdateHandler {
    private static final Logger log = LogManager.getLogger(DatabaseTableUpdateHandler.class);
    private final Database database;
    private final String databaseVersion;
    private final String tableName;
    private Integer connectionId;
    private Statement statement;

    private DatabaseTableUpdateHandler() {
        throw (InstantiationError)log.throwing((Throwable)new InstantiationError("Empty constructor not supported!"));
    }

    protected DatabaseTableUpdateHandler(Database database, String databaseVersion, String tableName) {
        this.database = database;
        this.databaseVersion = databaseVersion;
        this.tableName = tableName;
    }

    protected void startTransaction() throws InterruptedException, SQLException, ClassNotFoundException {
        if (!this.isTransactionInProgress()) {
            try {
                this.connectionId = this.getDatabase().openPooledConnection();
                this.statement = this.getDatabase().createStatement(this.connectionId);
            }
            catch (InterruptedException e) {
                log.warn("Failed to start transaction!");
                this.terminateTransaction();
                this.endTransaction();
                throw e;
            }
            catch (SQLException e) {
                log.warn("Failed to start transaction!");
                this.terminateTransaction();
                this.endTransaction();
                throw e;
            }
            catch (ClassNotFoundException e) {
                log.warn("Failed to start transaction!");
                this.terminateTransaction();
                this.endTransaction();
                throw e;
            }
        }
    }

    protected void addQueryToTransaction(String query) throws SQLException, IllegalStateException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("Adding query [%s] to transaction!", query));
        }
        if (this.isTransactionInProgress()) {
            try {
                this.getStatement().addBatch(query);
            }
            catch (SQLException e) {
                log.warn(String.format("Failed to add query [%s] to transaction!", query));
                throw e;
            }
        } else {
            IllegalStateException exception = new IllegalStateException(String.format("There is not a valid transaction started to add query [%s] to!", query));
            throw (IllegalStateException)log.throwing((Throwable)exception);
        }
    }

    protected void commitTransaction() throws SQLException, IllegalStateException {
        if (this.isTransactionInProgress()) {
            try {
                this.getStatement().executeBatch();
            }
            catch (SQLException e) {
                log.warn("Failed to execute batch statement!");
                this.terminateTransaction();
                throw e;
            }
            finally {
                this.endTransaction();
            }
        }
    }

    protected Database getDatabase() {
        return this.database;
    }

    protected String getDatabaseVersion() {
        return this.databaseVersion;
    }

    protected Integer getConnectionId() {
        return this.connectionId;
    }

    protected Statement getStatement() {
        return this.statement;
    }

    public String getTableName() {
        return this.tableName;
    }

    public boolean isTransactionInProgress() {
        return this.connectionId != null || this.statement != null;
    }

    protected void terminateTransaction() {
        if (log.isDebugEnabled()) {
            log.info("Rolling back transaction!");
        }
    }

    protected void endTransaction() {
        if (this.isTransactionInProgress()) {
            block4 : {
                try {
                    if (this.getStatement() != null) {
                        this.getStatement().close();
                    }
                }
                catch (SQLException e) {
                    if (!log.isDebugEnabled()) break block4;
                    log.debug("Suppressed exception while closing statement!", (Throwable)e);
                }
            }
            this.getDatabase().closeConnection(this.connectionId);
        }
        this.connectionId = null;
        this.statement = null;
    }
}

