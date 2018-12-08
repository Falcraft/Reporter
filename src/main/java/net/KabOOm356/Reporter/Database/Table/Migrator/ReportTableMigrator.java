/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Reporter.Database.Table.Migrator;

import java.util.ArrayList;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.DatabaseTableMigrator;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionMigrator;
import net.KabOOm356.Reporter.Database.Table.Migrator.VersionMigrator.Report.ReportTableVersion7;
import net.KabOOm356.Reporter.Database.Table.Migrator.VersionMigrator.Report.ReportTableVersion8;
import net.KabOOm356.Reporter.Database.Table.Migrator.VersionMigrator.Report.ReportTableVersion9;

public class ReportTableMigrator
extends DatabaseTableMigrator {
    private final List<DatabaseTableVersionMigrator> versionMigrators = new ArrayList<DatabaseTableVersionMigrator>();

    public ReportTableMigrator(Database database, String databaseVersion, String tableName) {
        super(database, databaseVersion, tableName);
        this.versionMigrators.add(new ReportTableVersion7(this.getDatabase(), this.getTableName()));
        this.versionMigrators.add(new ReportTableVersion8(this.getDatabase(), this.getTableName()));
        this.versionMigrators.add(new ReportTableVersion9(this.getDatabase(), this.getTableName()));
    }

    @Override
    public List<DatabaseTableVersionMigrator> getDatabaseTableVersionMigrators() {
        return this.versionMigrators;
    }
}

