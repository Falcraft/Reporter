/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report;

import java.sql.SQLException;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionUpdater;

public class ReportTableVersion7
extends DatabaseTableVersionUpdater {
    private static final String version = "7";

    public ReportTableVersion7(Database database, String tableName) {
        super(database, version, tableName);
    }

    @Override
    public boolean needsToUpdate() throws InterruptedException, SQLException, ClassNotFoundException {
        this.startTransaction();
        List<String> columns = this.getColumns();
        return !columns.contains("Priority") || !columns.contains("ClaimStatus") || !columns.contains("ClaimDate") || !columns.contains("ClaimedBy") || !columns.contains("ClaimPriority");
    }

    @Override
    protected void apply() throws SQLException, ClassNotFoundException, InterruptedException {
        this.startTransaction();
        List<String> columns = this.getColumns();
        if (!columns.contains("Priority")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD Priority TINYINT NOT NULL DEFAULT '0'");
        }
        if (!columns.contains("ClaimStatus")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD ClaimStatus BOOLEAN NOT NULL DEFAULT '0'");
        }
        if (!columns.contains("ClaimDate")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD ClaimDate CHAR(19)");
        }
        if (!columns.contains("ClaimedBy")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD ClaimedBy VARCHAR(32)");
        }
        if (!columns.contains("ClaimPriority")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD ClaimPriority TINYINT");
        }
    }
}

