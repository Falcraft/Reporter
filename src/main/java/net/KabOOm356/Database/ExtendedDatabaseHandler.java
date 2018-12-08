/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Database;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.KabOOm356.Database.Connection.ConnectionPoolConfig;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.DatabaseHandler;
import net.KabOOm356.Database.DatabaseType;
import net.KabOOm356.Database.SQLResultSet;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtendedDatabaseHandler
extends DatabaseHandler {
    private static final Logger log = LogManager.getLogger(ExtendedDatabaseHandler.class);

    public ExtendedDatabaseHandler(String host, String database, String username, String password, ConnectionPoolConfig connectionPoolConfig) {
        super(host, database, username, password, connectionPoolConfig);
    }

    public ExtendedDatabaseHandler(DatabaseType type, String path, String name, ConnectionPoolConfig connectionPoolConfig) throws IOException {
        super(type, path, name, connectionPoolConfig);
    }

    public ExtendedDatabaseHandler(Database database) {
        super(database);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SQLResultSet sqlQuery(String query) throws ClassNotFoundException, SQLException, InterruptedException {
        ResultSet resultSet = null;
        try {
            resultSet = super.query(query);
            if (!this.usingSQLite() && !resultSet.isBeforeFirst()) {
                resultSet.beforeFirst();
            }
            SQLResultSet sQLResultSet = new SQLResultSet(resultSet);
            return sQLResultSet;
        }
        finally {
            block8 : {
                try {
                    resultSet.close();
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block8;
                    log.log(Level.WARN, "Failed to close result set!", (Throwable)e);
                }
            }
            this.closeConnection();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SQLResultSet preparedSQLQuery(String query, List<String> params) throws ClassNotFoundException, SQLException, InterruptedException {
        ResultSet resultSet = null;
        try {
            resultSet = super.preparedQuery(query, params);
            if (!this.usingSQLite() && !resultSet.isBeforeFirst()) {
                resultSet.beforeFirst();
            }
            SQLResultSet sQLResultSet = new SQLResultSet(resultSet);
            return sQLResultSet;
        }
        finally {
            block8 : {
                try {
                    resultSet.close();
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block8;
                    log.log(Level.WARN, "Failed to close result set!", (Throwable)e);
                }
            }
            this.closeConnection();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SQLResultSet getSQLColumnMetaData(String table) throws ClassNotFoundException, SQLException, InterruptedException {
        ResultSet resultSet = null;
        try {
            resultSet = super.getColumnMetaData(table);
            if (!this.usingSQLite() && !resultSet.isBeforeFirst()) {
                resultSet.beforeFirst();
            }
            SQLResultSet sQLResultSet = new SQLResultSet(resultSet);
            return sQLResultSet;
        }
        finally {
            block8 : {
                try {
                    resultSet.close();
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block8;
                    log.log(Level.WARN, "Failed to close result set!", (Throwable)e);
                }
            }
            this.closeConnection();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SQLResultSet sqlQuery(int connectionId, String query) throws SQLException {
        ResultSet resultSet = null;
        try {
            resultSet = super.query(connectionId, query);
            if (!this.usingSQLite() && !resultSet.isBeforeFirst()) {
                resultSet.beforeFirst();
            }
            SQLResultSet sQLResultSet = new SQLResultSet(resultSet);
            return sQLResultSet;
        }
        finally {
            block8 : {
                try {
                    resultSet.close();
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block8;
                    log.log(Level.WARN, "Failed to close result set!", (Throwable)e);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SQLResultSet preparedSQLQuery(int connectionId, String query, List<String> params) throws SQLException {
        ResultSet resultSet = null;
        try {
            resultSet = super.preparedQuery(connectionId, query, params);
            if (!this.usingSQLite() && !resultSet.isBeforeFirst()) {
                resultSet.beforeFirst();
            }
            SQLResultSet sQLResultSet = new SQLResultSet(resultSet);
            return sQLResultSet;
        }
        finally {
            block8 : {
                try {
                    resultSet.close();
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block8;
                    log.log(Level.WARN, "Failed to close result set!", (Throwable)e);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SQLResultSet getSQLColumnMetaData(int connectionId, String table) throws SQLException {
        ResultSet resultSet = null;
        try {
            resultSet = super.getColumnMetaData(connectionId, table);
            if (!this.usingSQLite() && !resultSet.isBeforeFirst()) {
                resultSet.beforeFirst();
            }
            SQLResultSet sQLResultSet = new SQLResultSet(resultSet);
            return sQLResultSet;
        }
        finally {
            block8 : {
                try {
                    resultSet.close();
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block8;
                    log.log(Level.WARN, "Failed to close result set!", (Throwable)e);
                }
            }
        }
    }
}

