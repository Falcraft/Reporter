/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Reporter.Database.Table.Updater.VersionUpdater.Report;

import java.sql.SQLException;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReportTableVersion3
extends DatabaseTableVersionUpdater {
    private static final Logger log = LogManager.getLogger(ReportTableVersion3.class);
    private static final String version = "3";

    public ReportTableVersion3(Database database, String tableName) {
        super(database, version, tableName);
    }

    @Override
    public boolean needsToUpdate() throws InterruptedException, SQLException, ClassNotFoundException {
        this.startTransaction();
        List<String> columns = this.getColumns();
        return !columns.contains("SenderWorld") || !columns.contains("ReportedWorld");
    }

    @Override
    protected void apply() throws SQLException, ClassNotFoundException, InterruptedException {
        this.startTransaction();
        List<String> columns = this.getColumns();
        if (!columns.contains("SenderWorld")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD SenderWorld VARCHAR(100) DEFAULT ''");
        }
        if (!columns.contains("ReportedWorld")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD ReportedWorld VARCHAR(100) DEFAULT ''");
        }
    }
}

