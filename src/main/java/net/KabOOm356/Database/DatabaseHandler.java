/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Database;

import java.io.File;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import net.KabOOm356.Database.Connection.ConnectionPoolConfig;
import net.KabOOm356.Database.Connection.ConnectionPooledDatabaseInterface;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.DatabaseInterface;
import net.KabOOm356.Database.DatabaseType;
import net.KabOOm356.Database.SQL.MySQL;
import net.KabOOm356.Database.SQL.SQLite;
import net.KabOOm356.Util.FormattingUtil;

public class DatabaseHandler
implements DatabaseInterface,
ConnectionPooledDatabaseInterface {
    private Database database;

    public DatabaseHandler(String host, String database, String username, String password, ConnectionPoolConfig connectionPoolConfig) {
        this.database = new MySQL(host, database, username, password, connectionPoolConfig);
    }

    public DatabaseHandler(DatabaseType type, String path, String name, ConnectionPoolConfig connectionPoolConfig) throws IOException {
        if (name.contains("/") || name.contains("\\")) {
            name = name.substring(name.lastIndexOf(92));
            name = name.substring(name.lastIndexOf(47));
        }
        File SQLFile = new File(path, name);
        SQLFile.createNewFile();
        if (type == DatabaseType.SQLITE) {
            this.database = new SQLite(SQLFile.getAbsolutePath(), connectionPoolConfig);
        }
    }

    public DatabaseHandler(Database database) {
        this.database = database;
    }

    public Database getDatabase() {
        return this.database;
    }

    @Override
    public void openConnection() throws ClassNotFoundException, SQLException, InterruptedException {
        this.database.openConnection();
    }

    @Override
    public void closeConnection() {
        this.database.closeConnection();
    }

    @Override
    public void closeConnections() {
        this.database.closeConnections();
    }

    public boolean usingSQLite() {
        return this.database.getDatabaseType() == DatabaseType.SQLITE;
    }

    public boolean usingMySQL() {
        return this.database.getDatabaseType() == DatabaseType.MYSQL;
    }

    @Override
    public boolean checkTable(String table) throws ClassNotFoundException, SQLException, InterruptedException {
        return this.database.checkTable(table);
    }

    @Override
    public void updateQuery(String query) throws ClassNotFoundException, SQLException, InterruptedException {
        this.database.updateQuery(query);
    }

    @Override
    public List<String> getColumnNames(String table) throws SQLException, ClassNotFoundException, InterruptedException {
        return this.database.getColumnNames(table);
    }

    @Override
    public DatabaseMetaData getMetaData() throws ClassNotFoundException, SQLException, InterruptedException {
        return this.database.getMetaData();
    }

    @Override
    public ResultSet getColumnMetaData(String table) throws ClassNotFoundException, SQLException, InterruptedException {
        return this.database.getColumnMetaData(table);
    }

    @Override
    public ResultSet query(String query) throws ClassNotFoundException, SQLException, InterruptedException {
        return this.database.query(query);
    }

    @Override
    public ResultSet preparedQuery(String query, List<String> params) throws ClassNotFoundException, SQLException, InterruptedException {
        return this.database.preparedQuery(query, params);
    }

    @Override
    public void preparedUpdateQuery(String query, List<String> params) throws ClassNotFoundException, SQLException, InterruptedException {
        this.database.preparedUpdateQuery(query, params);
    }

    @Override
    public Statement createStatement() throws ClassNotFoundException, SQLException, InterruptedException {
        return this.database.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String query) throws ClassNotFoundException, SQLException, InterruptedException {
        return this.database.prepareStatement(query);
    }

    @Override
    public DatabaseType getDatabaseType() {
        return this.database.getDatabaseType();
    }

    public String toString() {
        String toString = "Database Handler:\n" + this.database;
        toString = FormattingUtil.addTabsToNewLines(toString, 1);
        return toString;
    }

    @Override
    public int openPooledConnection() throws ClassNotFoundException, SQLException, InterruptedException {
        return this.database.openPooledConnection();
    }

    @Override
    public void closeConnection(Integer connectionId) {
        this.database.closeConnection(connectionId);
    }

    @Override
    public ResultSet query(Integer connectionId, String query) throws SQLException {
        return this.database.query(connectionId, query);
    }

    @Override
    public void updateQuery(Integer connectionId, String query) throws SQLException {
        this.database.updateQuery(connectionId, query);
    }

    @Override
    public ResultSet preparedQuery(Integer connectionId, String query, List<String> params) throws SQLException {
        return this.database.preparedQuery(connectionId, query, params);
    }

    @Override
    public void preparedUpdateQuery(Integer connectionId, String query, List<String> params) throws SQLException {
        this.database.preparedUpdateQuery(connectionId, query, params);
    }

    @Override
    public boolean checkTable(Integer connectionId, String table) throws SQLException {
        return this.database.checkTable(connectionId, table);
    }

    @Override
    public List<String> getColumnNames(Integer connectionId, String table) throws SQLException {
        return this.database.getColumnNames(connectionId, table);
    }

    @Override
    public DatabaseMetaData getMetaData(Integer connectionId) throws SQLException {
        return this.database.getMetaData(connectionId);
    }

    @Override
    public ResultSet getColumnMetaData(Integer connectionId, String table) throws SQLException {
        return this.database.getColumnMetaData(connectionId, table);
    }

    @Override
    public Statement createStatement(Integer connectionId) throws SQLException {
        return this.database.createStatement(connectionId);
    }

    @Override
    public PreparedStatement prepareStatement(Integer connectionId, String query) throws SQLException {
        return this.database.prepareStatement(connectionId, query);
    }
}

