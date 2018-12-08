/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Reporter.Database.Table.Updater;

import java.util.ArrayList;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.DatabaseTableUpdater;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionUpdater;
import net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report.ReportTableVersion1;
import net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report.ReportTableVersion2;
import net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report.ReportTableVersion3;
import net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report.ReportTableVersion4;
import net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report.ReportTableVersion5;
import net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report.ReportTableVersion6;
import net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report.ReportTableVersion7;
import net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report.ReportTableVersion8;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReportTableUpdater
extends DatabaseTableUpdater {
    private static final Logger log = LogManager.getLogger(ReportTableUpdater.class);
    private final List<DatabaseTableVersionUpdater> versionUpdaters = new ArrayList<DatabaseTableVersionUpdater>();

    public ReportTableUpdater(Database database, String updateVersion, String tableName) {
        super(database, updateVersion, tableName);
        this.versionUpdaters.add(new ReportTableVersion1(database, this.getTableName()));
        this.versionUpdaters.add(new ReportTableVersion2(database, this.getTableName()));
        this.versionUpdaters.add(new ReportTableVersion3(database, this.getTableName()));
        this.versionUpdaters.add(new ReportTableVersion4(database, this.getTableName()));
        this.versionUpdaters.add(new ReportTableVersion5(database, this.getTableName()));
        this.versionUpdaters.add(new ReportTableVersion6(database, this.getTableName()));
        this.versionUpdaters.add(new ReportTableVersion7(database, this.getTableName()));
        this.versionUpdaters.add(new ReportTableVersion8(database, this.getTableName()));
    }

    @Override
    public List<DatabaseTableVersionUpdater> getDatabaseTableVersionUpdaters() {
        return this.versionUpdaters;
    }
}

