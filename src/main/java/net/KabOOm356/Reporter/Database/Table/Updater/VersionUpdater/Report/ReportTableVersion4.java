/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report;

import java.sql.SQLException;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionUpdater;

public class ReportTableVersion4
extends DatabaseTableVersionUpdater {
    private static final String version = "4";

    public ReportTableVersion4(Database database, String tableName) {
        super(database, version, tableName);
    }

    @Override
    public boolean needsToUpdate() throws InterruptedException, SQLException, ClassNotFoundException {
        this.startTransaction();
        List<String> columns = this.getColumns();
        return !columns.contains("CompletionStatus") || !columns.contains("CompletedBy") || !columns.contains("CompletionDate") || !columns.contains("CompletionSummary");
    }

    @Override
    protected void apply() throws SQLException, ClassNotFoundException, InterruptedException {
        this.startTransaction();
        List<String> columns = this.getColumns();
        if (!columns.contains("CompletionStatus")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD CompletionStatus BOOLEAN DEFAULT '0'");
        }
        if (!columns.contains("CompletedBy")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD CompletedBy VARCHAR(32) DEFAULT ''");
        }
        if (!columns.contains("CompletionDate")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD CompletionDate CHAR(19) DEFAULT ''");
        }
        if (!columns.contains("CompletionSummary")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD CompletionSummary VARCHAR(200) DEFAULT ''");
        }
    }
}

