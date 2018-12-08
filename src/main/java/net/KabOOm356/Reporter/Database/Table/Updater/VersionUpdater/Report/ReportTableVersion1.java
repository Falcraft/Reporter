/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report;

import java.sql.SQLException;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReportTableVersion1
extends DatabaseTableVersionUpdater {
    private static final Logger log = LogManager.getLogger(ReportTableVersion1.class);
    private static final String version = "1";

    public ReportTableVersion1(Database database, String tableName) {
        super(database, version, tableName);
    }

    @Override
    public boolean needsToUpdate() throws InterruptedException, SQLException, ClassNotFoundException {
        this.startTransaction();
        List<String> columns = this.getColumns();
        return !columns.contains("ID") || !columns.contains("Sender") || !columns.contains("Reported") || !columns.contains("Details") || !columns.contains("Date");
    }

    @Override
    protected void apply() throws InterruptedException, SQLException, ClassNotFoundException {
        this.startTransaction();
        List<String> columns = this.getColumns();
        try {
            if (!columns.contains("ID")) {
                this.addQueryToTransaction("ALTER TABLE Reports ADD ID INTEGER PRIMARY KEY");
            }
            if (!columns.contains("Sender")) {
                this.addQueryToTransaction("ALTER TABLE Reports ADD Sender VARCHAR(32)");
            }
            if (!columns.contains("Reported")) {
                this.addQueryToTransaction("ALTER TABLE Reports ADD Reported VARCHAR(32) NOT NULL DEFAULT '* (Anonymous)'");
            }
            if (!columns.contains("Details")) {
                this.addQueryToTransaction("ALTER TABLE Reports ADD Details VARCHAR(200) NOT NULL");
            }
            if (!columns.contains("Date")) {
                this.addQueryToTransaction("ALTER TABLE Reports ADD Date CHAR(19) NOT NULL DEFAULT 'N/A'");
            }
        }
        catch (SQLException e) {
            log.warn(String.format("Failed to update table [%s] to version [%s]!", this.getTableName(), this.getDatabaseVersion()));
            throw e;
        }
    }
}

