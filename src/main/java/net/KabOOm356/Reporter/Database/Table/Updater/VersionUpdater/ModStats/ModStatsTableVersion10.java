/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.ModStats;

import java.sql.SQLException;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionUpdater;
import net.KabOOm356.Util.DatabaseUtil;

public class ModStatsTableVersion10
extends DatabaseTableVersionUpdater {
    private static final String version = "10";

    public ModStatsTableVersion10(Database database, String tableName) {
        super(database, version, tableName);
    }

    @Override
    public boolean needsToUpdate() throws InterruptedException, SQLException, ClassNotFoundException {
        this.startTransaction();
        return !this.getColumns().contains("ID") || !this.getColumns().contains("ModName") || !this.getColumns().contains("ModUUID") || !this.getColumns().contains("AssignCount") || !this.getColumns().contains("ClaimedCount") || !this.getColumns().contains("CompletionCount") || !this.getColumns().contains("DeletionCount") || !this.getColumns().contains("MoveCount") || !this.getColumns().contains("RespondCount") || !this.getColumns().contains("UnassignCount") || !this.getColumns().contains("UnclaimCount");
    }

    @Override
    protected void apply() throws SQLException, ClassNotFoundException, InterruptedException {
        this.startTransaction();
        if (!this.getColumns().contains("ID")) {
            this.addQueryToTransaction("ALTER TABLE ModStats ADD ID INTEGER PRIMARY KEY" + DatabaseUtil.getAutoIncrementingPrimaryKeySuffix(this.getDatabase()));
        }
        if (!this.getColumns().contains("ModName")) {
            this.addQueryToTransaction("ALTER TABLE ModStats ADD ModName VARCHAR(16) NOT NULL");
        }
        if (!this.getColumns().contains("ModUUID")) {
            this.addQueryToTransaction("ALTER TABLE ModStats ADD ModUUID VARCHAR(36) NOT NULL");
        }
        if (!this.getColumns().contains("AssignCount")) {
            this.addQueryToTransaction("ALTER TABLE ModStats ADD AssignCount INTEGER NOT NULL DEFAULT '0'");
        }
        if (!this.getColumns().contains("ClaimedCount")) {
            this.addQueryToTransaction("ALTER TABLE ModStats ADD ClaimedCount INTEGER NOT NULL DEFAULT '0'");
        }
        if (!this.getColumns().contains("CompletionCount")) {
            this.addQueryToTransaction("ALTER TABLE ModStats ADD CompletionCount INTEGER NOT NULL DEFAULT '0'");
        }
        if (!this.getColumns().contains("DeletionCount")) {
            this.addQueryToTransaction("ALTER TABLE ModStats ADD DeletionCount INTEGER NOT NULL DEFAULT '0'");
        }
        if (!this.getColumns().contains("MoveCount")) {
            this.addQueryToTransaction("ALTER TABLE ModStats ADD MoveCount INTEGER NOT NULL DEFAULT '0'");
        }
        if (!this.getColumns().contains("RespondCount")) {
            this.addQueryToTransaction("ALTER TABLE ModStats ADD RespondCount INTEGER NOT NULL DEFAULT '0'");
        }
        if (!this.getColumns().contains("UnassignCount")) {
            this.addQueryToTransaction("ALTER TABLE ModStats ADD UnassignCount INTEGER NOT NULL DEFAULT '0'");
        }
        if (!this.getColumns().contains("UnclaimCount")) {
            this.addQueryToTransaction("ALTER TABLE ModStats ADD UnclaimCount INTEGER NOT NULL DEFAULT '0'");
        }
    }
}

