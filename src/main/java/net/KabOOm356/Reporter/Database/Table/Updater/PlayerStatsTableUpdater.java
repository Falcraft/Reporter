/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Reporter.Database.Table.Updater;

import java.util.ArrayList;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.DatabaseTableUpdater;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionUpdater;
import net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.PlayerStats.PlayerStatsTableVersion10;

public class PlayerStatsTableUpdater
extends DatabaseTableUpdater {
    private final List<DatabaseTableVersionUpdater> versionUpdaters = new ArrayList<DatabaseTableVersionUpdater>();

    public PlayerStatsTableUpdater(Database database, String updateVersion, String tableName) {
        super(database, updateVersion, tableName);
        this.versionUpdaters.add(new PlayerStatsTableVersion10(this.getDatabase(), this.getTableName()));
    }

    @Override
    public List<DatabaseTableVersionUpdater> getDatabaseTableVersionUpdaters() {
        return this.versionUpdaters;
    }
}

