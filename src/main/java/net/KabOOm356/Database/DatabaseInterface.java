/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Database;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import net.KabOOm356.Database.DatabaseType;

public interface DatabaseInterface {
    public void openConnection() throws ClassNotFoundException, SQLException, InterruptedException;

    public ResultSet query(String var1) throws ClassNotFoundException, SQLException, InterruptedException;

    public void updateQuery(String var1) throws ClassNotFoundException, SQLException, InterruptedException;

    public ResultSet preparedQuery(String var1, List<String> var2) throws ClassNotFoundException, SQLException, InterruptedException;

    public void preparedUpdateQuery(String var1, List<String> var2) throws ClassNotFoundException, SQLException, InterruptedException;

    public boolean checkTable(String var1) throws ClassNotFoundException, SQLException, InterruptedException;

    public List<String> getColumnNames(String var1) throws SQLException, ClassNotFoundException, InterruptedException;

    public DatabaseMetaData getMetaData() throws ClassNotFoundException, SQLException, InterruptedException;

    public ResultSet getColumnMetaData(String var1) throws ClassNotFoundException, SQLException, InterruptedException;

    public void closeConnection();

    public Statement createStatement() throws SQLException, ClassNotFoundException, InterruptedException;

    public PreparedStatement prepareStatement(String var1) throws SQLException, ClassNotFoundException, InterruptedException;

    public DatabaseType getDatabaseType();
}

