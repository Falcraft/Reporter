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

public class ReportTableVersion2
extends DatabaseTableVersionUpdater {
    private static final Logger log = LogManager.getLogger(ReportTableVersion2.class);
    private static final String version = "2";

    public ReportTableVersion2(Database database, String tableName) {
        super(database, version, tableName);
    }

    @Override
    public boolean needsToUpdate() throws InterruptedException, SQLException, ClassNotFoundException {
        this.startTransaction();
        List<String> columns = this.getColumns();
        return !columns.contains("SenderX") || !columns.contains("SenderY") || !columns.contains("SenderZ") || !columns.contains("ReportedX") || !columns.contains("ReportedY") || !columns.contains("ReportedZ");
    }

    @Override
    protected void apply() throws SQLException, ClassNotFoundException, InterruptedException {
        this.startTransaction();
        List<String> columns = this.getColumns();
        if (!columns.contains("SenderX")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD SenderX DOUBLE NOT NULL DEFAULT '0.0'");
        }
        if (!columns.contains("SenderY")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD SenderY DOUBLE NOT NULL DEFAULT '0.0'");
        }
        if (!columns.contains("SenderZ")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD SenderZ DOUBLE NOT NULL DEFAULT '0.0'");
        }
        if (!columns.contains("ReportedX")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD ReportedX DOUBLE DEFAULT '0.0'");
        }
        if (!columns.contains("ReportedY")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD ReportedX DOUBLE DEFAULT '0.0'");
        }
        if (!columns.contains("ReportedZ")) {
            this.addQueryToTransaction("ALTER TABLE Reports ADD ReportedX DOUBLE DEFAULT '0.0'");
        }
    }
}

