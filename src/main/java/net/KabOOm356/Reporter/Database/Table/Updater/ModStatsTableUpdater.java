/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Reporter.Database.Table.Updater;

import java.util.ArrayList;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.DatabaseTableUpdater;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionUpdater;
import net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.ModStats.ModStatsTableVersion10;

public class ModStatsTableUpdater
extends DatabaseTableUpdater {
    private final List<DatabaseTableVersionUpdater> versionUpdaters = new ArrayList<DatabaseTableVersionUpdater>();

    public ModStatsTableUpdater(Database database, String updateVersion, String tableName) {
        super(database, updateVersion, tableName);
        this.versionUpdaters.add(new ModStatsTableVersion10(database, this.getTableName()));
    }

    @Override
    public List<DatabaseTableVersionUpdater> getDatabaseTableVersionUpdaters() {
        return this.versionUpdaters;
    }
}

