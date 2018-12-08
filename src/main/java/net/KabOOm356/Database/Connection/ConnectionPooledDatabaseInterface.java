/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Database.Connection;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface ConnectionPooledDatabaseInterface {
    public int openPooledConnection() throws ClassNotFoundException, SQLException, InterruptedException;

    public void closeConnection(Integer var1);

    public void closeConnections();

    public ResultSet query(Integer var1, String var2) throws SQLException;

    public void updateQuery(Integer var1, String var2) throws SQLException;

    public ResultSet preparedQuery(Integer var1, String var2, List<String> var3) throws SQLException;

    public void preparedUpdateQuery(Integer var1, String var2, List<String> var3) throws SQLException;

    public boolean checkTable(Integer var1, String var2) throws SQLException;

    public List<String> getColumnNames(Integer var1, String var2) throws SQLException;

    public DatabaseMetaData getMetaData(Integer var1) throws SQLException;

    public ResultSet getColumnMetaData(Integer var1, String var2) throws SQLException;

    public Statement createStatement(Integer var1) throws SQLException;

    public PreparedStatement prepareStatement(Integer var1, String var2) throws SQLException;
}

