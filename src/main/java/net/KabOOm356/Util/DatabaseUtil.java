/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 */
package net.KabOOm356.Util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.DatabaseType;
import net.KabOOm356.Throwable.WrongNumberOfSQLParametersException;
import net.KabOOm356.Util.Util;
import org.apache.commons.lang.Validate;

public final class DatabaseUtil {
    public static final char queryParameter = '?';

    private DatabaseUtil() {
    }

    public static String getAutoIncrementingPrimaryKeyQuery(Database database, String columnName) {
        Validate.notNull((Object)database);
        Validate.notNull((Object)columnName);
        Validate.notEmpty((String)columnName);
        return columnName + " INTEGER PRIMARY KEY" + DatabaseUtil.getAutoIncrementingPrimaryKeySuffix(database);
    }

    public static String getAutoIncrementingPrimaryKeySuffix(Database database) {
        return database.getDatabaseType() == DatabaseType.MYSQL ? " AUTO_INCREMENT" : "";
    }

    public static String getColumnsSizeName(Database database) {
        return database.getDatabaseType() == DatabaseType.SQLITE ? "TYPE_NAME" : "COLUMN_SIZE";
    }

    public static boolean checkPreparedStatementParameters(String query, List<String> parameters) {
        int numberOfOccurrences = Util.countOccurrences(query, '?');
        return parameters.size() == numberOfOccurrences;
    }

    public static void bindParametersToPreparedStatement(PreparedStatement preparedStatement, String query, List<String> parameters) throws SQLException {
        if (DatabaseUtil.checkPreparedStatementParameters(query, parameters)) {
            for (int LCV = 0; LCV < parameters.size(); ++LCV) {
                preparedStatement.setString(LCV + 1, parameters.get(LCV));
            }
        } else {
            int numberOfOccurrences = Util.countOccurrences(query, '?');
            String exceptionMessage = "Required number of parameters: " + parameters.size() + " got: " + Integer.toString(numberOfOccurrences);
            throw new WrongNumberOfSQLParametersException(exceptionMessage);
        }
    }
}

