/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report;

import java.sql.SQLException;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionUpdater;

public class ReportTableVersion8
extends DatabaseTableVersionUpdater {
    private static final String version = "8";

    public ReportTableVersion8(Database database, String tableName) {
        super(database, version, tableName);
    }

    @Override
    public boolean needsToUpdate() throws InterruptedException, SQLException, ClassNotFoundException {
        this.startTransaction();
        List<String> columns = this.getColumns();
        return !columns.contains("SenderUUID") || !columns.contains("ReportedUUID") || !columns.contains("CompletedByUUID") || !columns.contains("ClaimedByUUID");
    }

    @Override
    protected void apply() throws SQLException, ClassNotFoundException, InterruptedException {
        this.startTransaction();
        List<String> columns = this.getColumns();
        if (!columns.contains("SenderUUID")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD SenderUUID CHAR(36) DEFAULT ''");
        }
        if (!columns.contains("ReportedUUID")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD ReportedUUID CHAR(36) DEFAULT ''");
        }
        if (!columns.contains("CompletedByUUID")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD CompletedByUUID CHAR(36) DEFAULT ''");
        }
        if (!columns.contains("ClaimedByUUID")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD ClaimedByUUID CHAR(36) DEFAULT ''");
        }
    }
}

