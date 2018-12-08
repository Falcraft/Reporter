/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import net.KabOOm356.Database.ResultRow;

public class SQLResultSet
extends ArrayList<ResultRow> {
    public static final int FIRSTROW = 0;
    public static final int FIRSTCOLUMN = 0;
    private static final long serialVersionUID = 2992528074740195473L;

    public SQLResultSet() {
    }

    public SQLResultSet(ResultSet resultSet) throws SQLException {
        this.set(resultSet);
    }

    public void set(ResultSet resultSet) throws SQLException {
        this.clear();
        try {
            resultSet.beforeFirst();
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
        while (resultSet.next()) {
            this.add(new ResultRow(resultSet));
        }
    }

    public String getString(int row, String colName) {
        return ((ResultRow)this.get(row)).getString(colName);
    }

    public String getString(String colName) {
        return this.getString(0, colName);
    }

    public Boolean getBoolean(int row, String colName) {
        return ((ResultRow)this.get(row)).getBoolean(colName);
    }

    public Boolean getBoolean(String colName) {
        return this.getBoolean(0, colName);
    }

    public Integer getInt(int row, String colName) {
        return ((ResultRow)this.get(row)).getInt(colName);
    }

    public Integer getInt(String colName) {
        return this.getInt(0, colName);
    }

    public Double getDouble(int row, String colName) {
        return ((ResultRow)this.get(row)).getDouble(colName);
    }

    public Double getDouble(String colName) {
        return this.getDouble(0, colName);
    }

    public boolean contains(String colName, Object value) {
        return this.get(colName, value) != null;
    }

    public ResultRow get(String colName, Object value) {
        for (ResultRow row : this) {
            Object rowValue = row.get(colName);
            if (rowValue == null || !rowValue.equals(value)) continue;
            return row;
        }
        return null;
    }

    public SQLResultSet getAll(String colName, Object value) {
        SQLResultSet set = new SQLResultSet();
        for (ResultRow row : this) {
            Object rowValue = row.get(colName);
            if (rowValue == null || !rowValue.equals(value)) continue;
            set.add(row);
        }
        return set;
    }
}

