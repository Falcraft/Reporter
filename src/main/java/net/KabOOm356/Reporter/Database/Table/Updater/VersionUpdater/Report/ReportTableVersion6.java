/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report;

import java.sql.SQLException;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionUpdater;

public class ReportTableVersion6
extends DatabaseTableVersionUpdater {
    private static final String version = "6";

    public ReportTableVersion6(Database database, String tableName) {
        super(database, version, tableName);
    }

    @Override
    public boolean needsToUpdate() throws InterruptedException, SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    protected void apply() throws SQLException, ClassNotFoundException, InterruptedException {
    }
}

