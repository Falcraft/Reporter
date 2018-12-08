/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.PlayerStats;

import java.sql.SQLException;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionUpdater;
import net.KabOOm356.Util.DatabaseUtil;

public class PlayerStatsTableVersion10
extends DatabaseTableVersionUpdater {
    private static final String version = "10";

    public PlayerStatsTableVersion10(Database database, String tableName) {
        super(database, version, tableName);
    }

    @Override
    public boolean needsToUpdate() throws InterruptedException, SQLException, ClassNotFoundException {
        this.startTransaction();
        return !this.getColumns().contains("ID") || !this.getColumns().contains("Name") || !this.getColumns().contains("UUID") || !this.getColumns().contains("FirstReportDate") || !this.getColumns().contains("LastReportDate") || !this.getColumns().contains("ReportCount") || !this.getColumns().contains("FirstReportedDate") || !this.getColumns().contains("LastReportedDate") || !this.getColumns().contains("ReportedCount");
    }

    @Override
    protected void apply() throws SQLException, ClassNotFoundException, InterruptedException {
        this.startTransaction();
        if (!this.getColumns().contains("ID")) {
            this.addQueryToTransaction("ALTER TABLE PlayerStats ADD ID INTEGER PRIMARY KEY" + DatabaseUtil.getAutoIncrementingPrimaryKeySuffix(this.getDatabase()));
        }
        if (!this.getColumns().contains("Name")) {
            this.addQueryToTransaction("ALTER TABLE PlayerStats ADD Name VARCHAR(16) NOT NULL");
        }
        if (!this.getColumns().contains("UUID")) {
            this.addQueryToTransaction("ALTER TABLE PlayerStats ADD UUID VARCHAR(36) NOT NULL");
        }
        if (!this.getColumns().contains("FirstReportDate")) {
            this.addQueryToTransaction("ALTER TABLE PlayerStats ADD FirstReportDate VARCHAR(19) NOT NULL DEFAULT ''");
        }
        if (!this.getColumns().contains("LastReportDate")) {
            this.addQueryToTransaction("ALTER TABLE PlayerStats ADD LastReportDate VARCHAR(19) NOT NULL DEFAULT ''");
        }
        if (!this.getColumns().contains("ReportCount")) {
            this.addQueryToTransaction("ALTER TABLE PlayerStats ADD ReportCount INTEGER NOT NULL DEFAULT '0'");
        }
        if (!this.getColumns().contains("FirstReportedDate")) {
            this.addQueryToTransaction("ALTER TABLE PlayerStats ADD FirstReportedDate VARCHAR(19) NOT NULL DEFAULT ''");
        }
        if (!this.getColumns().contains("LastReportedDate")) {
            this.addQueryToTransaction("ALTER TABLE PlayerStats ADD LastReportedDate VARCHAR(19) NOT NULL DEFAULT ''");
        }
        if (!this.getColumns().contains("ReportedCount")) {
            this.addQueryToTransaction("ALTER TABLE PlayerStats ADD ReportedCount INTEGER NOT NULL DEFAULT '0'");
        }
    }
}

