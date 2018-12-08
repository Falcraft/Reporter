/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResultRow
extends HashMap<String, Object> {
    private static final Logger log = LogManager.getLogger(ResultRow.class);
    private static final long serialVersionUID = -1489657675159738791L;

    public ResultRow() {
    }

    public ResultRow(ResultSet result) throws SQLException {
        this.set(result);
    }

    public void set(ResultSet result) throws SQLException {
        try {
            ResultSetMetaData metaData = result.getMetaData();
            int columns = metaData.getColumnCount();
            this.clear();
            for (int LCV = 1; LCV <= columns; ++LCV) {
                this.put(metaData.getColumnName(LCV), result.getObject(LCV));
            }
        }
        catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.log(Level.WARN, "Failed to set ResultRow contents!");
            }
            throw e;
        }
    }

    public String getString(String colName) {
        if (this.get(colName) == null) {
            return null;
        }
        return this.get(colName).toString();
    }

    public Boolean getBoolean(String colName) {
        if (this.get(colName) == null) {
            return null;
        }
        Boolean value = Boolean.parseBoolean(this.getString(colName));
        value = value != false || this.getString(colName).equals("1");
        return value;
    }

    public Integer getInt(String colName) {
        if (this.get(colName) == null) {
            return null;
        }
        return Integer.parseInt(this.getString(colName));
    }

    public Double getDouble(String colName) {
        if (this.get(colName) == null) {
            return null;
        }
        return Double.parseDouble(this.getString(colName));
    }
}

