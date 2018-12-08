/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.KabOOm356.Database.Connection.AlertingPooledConnection;
import net.KabOOm356.Database.Connection.ConnectionPoolConfig;
import net.KabOOm356.Database.Connection.ConnectionPoolManager;
import net.KabOOm356.Database.Connection.ConnectionPooledDatabaseInterface;
import net.KabOOm356.Database.Connection.ConnectionWrapper;
import net.KabOOm356.Database.DatabaseInterface;
import net.KabOOm356.Database.DatabaseType;
import net.KabOOm356.Util.ArrayUtil;
import net.KabOOm356.Util.DatabaseUtil;
import net.KabOOm356.Util.FormattingUtil;
import org.apache.commons.lang.Validate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Database
implements DatabaseInterface,
ConnectionPooledDatabaseInterface,
ConnectionPoolManager {
    private static final Logger log = LogManager.getLogger(Database.class);
    private static final Random idGenerator = new Random();
    private final DatabaseType databaseType;
    private final String databaseDriver;
    private final String connectionURL;
    private final HashMap<Integer, ConnectionWrapper> connectionPool;
    private final ConnectionPoolConfig connectionPoolConfig;
    private Integer localConnectionId;

    public Database(DatabaseType databaseType, String databaseDriver, String connectionURL, ConnectionPoolConfig connectionPoolConfig) {
        Validate.notNull((Object)connectionPoolConfig, (String)"Parameter 'connectionPoolConfig' cannot be null!");
        this.databaseType = databaseType;
        this.databaseDriver = databaseDriver;
        this.connectionURL = connectionURL;
        this.connectionPoolConfig = connectionPoolConfig;
        this.localConnectionId = null;
        this.connectionPool = new HashMap();
    }

    @Override
    public void openConnection() throws ClassNotFoundException, SQLException, InterruptedException {
        if (this.localConnectionId != null) {
            throw new IllegalStateException("There is already an open non-pooled connection in use!");
        }
        this.localConnectionId = this.openPooledConnection();
        if (log.isDebugEnabled()) {
            log.debug("New non-pooled connection created with id [" + this.localConnectionId + ']');
        }
    }

    @Override
    public synchronized int openPooledConnection() throws ClassNotFoundException, SQLException, InterruptedException {
        return this.openPooledConnection(null, null);
    }

    private boolean isConnectionSlotAvailable() {
        boolean isConnectionSlotAvailable;
        boolean bl = isConnectionSlotAvailable = this.connectionPool.size() < this.connectionPoolConfig.getMaxConnections();
        if (log.isDebugEnabled()) {
            if (isConnectionSlotAvailable) {
                log.debug("New connection slot is available in the connection pool");
            } else {
                log.debug("No connection slot available in the connection pool");
            }
        }
        return isConnectionSlotAvailable;
    }

    protected void openConnection(String username, String password) throws SQLException, ClassNotFoundException, InterruptedException {
        if (this.localConnectionId != null) {
            throw new IllegalStateException("There is already an open connection in use!");
        }
        this.localConnectionId = this.openPooledConnection(username, password);
        if (log.isDebugEnabled()) {
            log.debug("New non-pooled connection created with id [" + this.localConnectionId + ']');
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected synchronized int openPooledConnection(String username, String password) throws ClassNotFoundException, SQLException, InterruptedException {
        try {
            HashMap<Integer, ConnectionWrapper> hashMap = this.connectionPool;
            synchronized (hashMap) {
                long startWaitTime = System.currentTimeMillis();
                boolean isWaiting = false;
                int updateCount = 0;
                long currentWaitTime = 0L;
                while (!this.isConnectionSlotAvailable()) {
                    if (!isWaiting) {
                        if (log.isDebugEnabled()) {
                            log.warn("Thread has begun waiting on new connection to become available");
                        }
                    } else {
                        currentWaitTime = System.currentTimeMillis() - startWaitTime;
                        if (log.isDebugEnabled()) {
                            log.warn(String.format("Thread has been waiting for a new connection for [%dms] this is update [%d]; possible bottleneck!", currentWaitTime, updateCount));
                        }
                        if (this.connectionPoolConfig.isConnectionPoolLimited() && updateCount >= this.connectionPoolConfig.getMaxAttemptsForConnection()) {
                            log.warn("Thread has reached the max number of updates! Cancelling operation!");
                            throw new InterruptedException(String.format("Thread has reached the cycle limit [%d] after waiting for [%dms] for a new connection!", this.connectionPoolConfig.getMaxAttemptsForConnection(), currentWaitTime));
                        }
                    }
                    this.connectionPool.wait(this.connectionPoolConfig.getWaitTimeBeforeUpdate());
                    isWaiting = true;
                    ++updateCount;
                }
                currentWaitTime = System.currentTimeMillis() - startWaitTime;
                if (isWaiting && log.isDebugEnabled()) {
                    log.debug(String.format("A connection is now available in the connection pool after waiting [%dms]... proceeding!", currentWaitTime));
                }
            }
        }
        catch (InterruptedException e) {
            if (log.isDebugEnabled()) {
                log.warn("Waiting for available connection was interrupted!");
            }
            throw e;
        }
        try {
            int connectionId;
            Class.forName(this.databaseDriver);
            Connection connection = username == null || password == null ? DriverManager.getConnection(this.connectionURL) : DriverManager.getConnection(this.connectionURL, username, password);
            while (this.connectionPool.containsKey(connectionId = idGenerator.nextInt())) {
            }
            AlertingPooledConnection ConnectionWrapper2 = new AlertingPooledConnection(this, connectionId, connection);
            this.connectionPool.put(connectionId, ConnectionWrapper2);
            if (log.isDebugEnabled()) {
                log.debug("New pooled connection created with id [" + connectionId + ']');
                log.debug("Connection pool size [" + this.connectionPool.size() + ']');
            }
            return connectionId;
        }
        catch (ClassNotFoundException e) {
            if (log.isDebugEnabled()) {
                log.log(Level.WARN, "Failed to open connection to database!");
            }
            throw e;
        }
        catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.log(Level.WARN, "Failed to open connection to database!");
            }
            throw e;
        }
    }

    @Override
    public ResultSet query(String query) throws ClassNotFoundException, SQLException, InterruptedException {
        this.openNonPooledConnection();
        return this.query(this.localConnectionId, query);
    }

    @Override
    public void updateQuery(String query) throws ClassNotFoundException, SQLException, InterruptedException {
        this.openNonPooledConnection();
        this.updateQuery(this.localConnectionId, query);
    }

    @Override
    public ResultSet preparedQuery(String query, List<String> params) throws ClassNotFoundException, SQLException, InterruptedException {
        this.openNonPooledConnection();
        return this.preparedQuery(this.localConnectionId, query, params);
    }

    @Override
    public void preparedUpdateQuery(String query, List<String> params) throws ClassNotFoundException, SQLException, InterruptedException {
        this.openNonPooledConnection();
        this.preparedUpdateQuery(this.localConnectionId, query, params);
    }

    @Override
    public boolean checkTable(String table) throws ClassNotFoundException, SQLException, InterruptedException {
        this.openNonPooledConnection();
        return this.checkTable(this.localConnectionId, table);
    }

    @Override
    public List<String> getColumnNames(String table) throws SQLException, ClassNotFoundException, InterruptedException {
        this.openNonPooledConnection();
        return this.getColumnNames(this.localConnectionId, table);
    }

    @Override
    public DatabaseMetaData getMetaData() throws ClassNotFoundException, SQLException, InterruptedException {
        this.openNonPooledConnection();
        return this.getMetaData(this.localConnectionId);
    }

    @Override
    public ResultSet getColumnMetaData(String table) throws ClassNotFoundException, SQLException, InterruptedException {
        this.openNonPooledConnection();
        return this.getColumnMetaData(this.localConnectionId, table);
    }

    @Override
    public void closeConnection() {
        if (this.localConnectionId != null) {
            if (log.isDebugEnabled()) {
                log.debug("Closing non-pooled connection with id [" + this.localConnectionId + ']');
            }
            this.closeConnection(this.localConnectionId);
            this.localConnectionId = null;
        }
    }

    @Override
    public void closeConnection(Integer connectionId) {
        block7 : {
            if (this.doesConnectionExist(connectionId)) {
                ConnectionWrapper connection = this.getConnection(connectionId);
                try {
                    if (!connection.isClosed()) {
                        if (log.isDebugEnabled()) {
                            log.debug("Closing pooled connection with id [" + connectionId + ']');
                        }
                        connection.close();
                    }
                    break block7;
                }
                catch (SQLException e) {
                    if (log.isDebugEnabled()) {
                        log.log(Level.WARN, "Failed to close connection with id [" + connectionId + "]!", (Throwable)e);
                    }
                    break block7;
                }
            }
            if (log.isDebugEnabled()) {
                log.warn("Connection with id [" + connectionId + "] is not in the connection pool!");
            }
        }
    }

    @Override
    public void closeConnections() {
        if (log.isDebugEnabled()) {
            log.info("Closing all connections!");
            log.info("Current connection pool size: " + this.connectionPool.size());
        }
        this.closeConnection();
        Integer[] connectionIds = new Integer[this.connectionPool.size()];
        for (Integer connectionId : connectionIds = this.connectionPool.keySet().toArray(connectionIds)) {
            this.closeConnection(connectionId);
        }
        if (log.isDebugEnabled()) {
            log.info("All connections closed!");
        }
    }

    @Override
    public Statement createStatement() throws SQLException, ClassNotFoundException, InterruptedException {
        this.openNonPooledConnection();
        return this.createStatement(this.localConnectionId);
    }

    @Override
    public PreparedStatement prepareStatement(String query) throws SQLException, ClassNotFoundException, InterruptedException {
        this.openNonPooledConnection();
        return this.prepareStatement(this.localConnectionId, query);
    }

    @Override
    public DatabaseType getDatabaseType() {
        return this.databaseType;
    }

    @Override
    public void connectionClosed(Integer connectionId) {
        if (log.isDebugEnabled()) {
            log.debug("Connection close detected for connection with id [" + connectionId + ']');
        }
        this.removeConnectionFromPool(connectionId);
    }

    @Override
    public ResultSet query(Integer connectionId, String query) throws SQLException {
        try {
            return this.createStatement(connectionId).executeQuery(query);
        }
        catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.log(Level.WARN, "Failed to execute query!");
            }
            throw e;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void updateQuery(Integer connectionId, String query) throws SQLException {
        Statement statement = null;
        try {
            statement = this.createStatement(connectionId);
            try {
                statement.executeUpdate(query);
            }
            catch (SQLException e) {
                if (log.isDebugEnabled()) {
                    log.log(Level.WARN, "Failed to execute update query!");
                }
                throw e;
            }
        }
        finally {
            block12 : {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block12;
                    log.log(Level.WARN, "Failed to close statement!", (Throwable)e);
                }
            }
            this.closeConnection(connectionId);
        }
    }

    @Override
    public ResultSet preparedQuery(Integer connectionId, String query, List<String> params) throws SQLException {
        PreparedStatement preparedStatement = this.prepareStatement(connectionId, query);
        this.bindParametersToPreparedStatement(preparedStatement, query, params);
        try {
            return preparedStatement.executeQuery();
        }
        catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.log(Level.WARN, "Failed to execute query!");
            }
            throw e;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void preparedUpdateQuery(Integer connectionId, String query, List<String> params) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.prepareStatement(connectionId, query);
            this.bindParametersToPreparedStatement(preparedStatement, query, params);
            try {
                preparedStatement.executeUpdate();
            }
            catch (SQLException e) {
                if (log.isDebugEnabled()) {
                    log.log(Level.WARN, "Failed to execute prepared query!");
                }
                throw e;
            }
        }
        finally {
            block12 : {
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block12;
                    log.log(Level.WARN, "Failed to close prepared statement!", (Throwable)e);
                }
            }
            this.closeConnection(connectionId);
        }
    }

    private void bindParametersToPreparedStatement(PreparedStatement preparedStatement, String query, List<String> parameters) throws SQLException {
        try {
            DatabaseUtil.bindParametersToPreparedStatement(preparedStatement, query, parameters);
        }
        catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.warn("Failed to bind parameters to prepared statement!");
            }
            throw e;
        }
    }

    @Override
    public boolean checkTable(Integer connectionId, String table) throws SQLException {
        ResultSet tables = null;
        try {
            DatabaseMetaData dbm;
            ConnectionWrapper connection = this.getConnection(connectionId);
            try {
                dbm = connection.getMetaData();
            }
            catch (SQLException e) {
                if (log.isDebugEnabled()) {
                    log.log(Level.WARN, "Failed to get connection meta data!");
                }
                throw e;
            }
            try {
                tables = dbm.getTables(null, null, table, null);
            }
            catch (SQLException e2) {
                if (log.isDebugEnabled()) {
                    log.log(Level.WARN, "Failed to get tables from connection meta data!");
                }
                throw e2;
            }
            boolean e2 = tables.next();
            return e2;
        }
        catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.log(Level.WARN, "Failed to check table!");
            }
            throw e;
        }
        finally {
            block17 : {
                try {
                    if (tables != null) {
                        tables.close();
                    }
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block17;
                    log.log(Level.DEBUG, "Failed to close ResultSet!");
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<String> getColumnNames(Integer connectionId, String table) throws SQLException {
        ArrayList<String> col = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = this.getColumnMetaData(connectionId, table);
            while (rs.next()) {
                col.add(rs.getString("COLUMN_NAME"));
            }
            ArrayList<String> arrayList = col;
            return arrayList;
        }
        finally {
            block9 : {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block9;
                    log.log(Level.DEBUG, "Failed to close ResultSet!", (Throwable)e);
                }
            }
        }
    }

    @Override
    public DatabaseMetaData getMetaData(Integer connectionId) throws SQLException {
        ConnectionWrapper connection = this.getConnection(connectionId);
        try {
            return connection.getMetaData();
        }
        catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.log(Level.DEBUG, "Failed to get meta data from the connection!");
            }
            throw e;
        }
    }

    @Override
    public ResultSet getColumnMetaData(Integer connectionId, String table) throws SQLException {
        try {
            return this.getMetaData(connectionId).getColumns(null, null, table, null);
        }
        catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.log(Level.DEBUG, "Failed to get table columns!");
            }
            throw e;
        }
    }

    @Override
    public Statement createStatement(Integer connectionId) throws SQLException {
        ConnectionWrapper connection = this.getConnection(connectionId);
        try {
            return connection.createStatement();
        }
        catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.log(Level.WARN, "Failed to create statement!");
            }
            throw e;
        }
    }

    @Override
    public PreparedStatement prepareStatement(Integer connectionId, String query) throws SQLException {
        ConnectionWrapper connection = this.getConnection(connectionId);
        try {
            return connection.prepareStatement(query);
        }
        catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.log(Level.WARN, "Failed to prepare statement!");
            }
            throw e;
        }
    }

    private boolean doesConnectionExist(Integer connectionId) {
        return this.connectionPool.containsKey(connectionId);
    }

    private ConnectionWrapper getConnection(Integer connectionId) {
        Validate.notNull((Object)connectionId, (String)"Connection id cannot be null!");
        if (this.doesConnectionExist(connectionId)) {
            return this.connectionPool.get(connectionId);
        }
        throw new IllegalArgumentException("Connection with connection id [" + connectionId + "] does not exist!");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeConnectionFromPool(int connectionId) {
        if (log.isDebugEnabled()) {
            log.debug("Removing connection with id [" + connectionId + "] from connection pool");
        }
        if (this.localConnectionId != null && connectionId == this.localConnectionId) {
            this.localConnectionId = null;
        }
        HashMap<Integer, ConnectionWrapper> hashMap = this.connectionPool;
        synchronized (hashMap) {
            this.connectionPool.remove(connectionId);
            this.connectionPool.notify();
        }
        if (log.isDebugEnabled()) {
            log.debug("Current connection pool size [" + this.connectionPool.size() + ']');
        }
    }

    private void openNonPooledConnection() throws ClassNotFoundException, SQLException, InterruptedException {
        if (this.localConnectionId == null) {
            this.openConnection();
        }
    }

    public String toString() {
        String toString = "Database Type: " + this.databaseType.toString() + "\nDatabase Driver: " + this.databaseDriver + "\nConnection URL: " + this.connectionURL + "\nConnection Pool Size: " + this.connectionPool.size() + "\nConnection Pool: " + '\n' + ArrayUtil.indexesToString(this.connectionPool.keySet());
        return FormattingUtil.addTabsToNewLines(toString, 1);
    }
}

