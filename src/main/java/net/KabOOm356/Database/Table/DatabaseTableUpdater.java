/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Database.Table;

import java.sql.SQLException;
import java.util.List;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.Table.DatabaseTableUpdateHandler;
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionUpdater;
import net.KabOOm356.Reporter.Reporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DatabaseTableUpdater
extends DatabaseTableUpdateHandler {
    private static final Logger log = LogManager.getLogger(DatabaseTableUpdater.class);

    public DatabaseTableUpdater(Database database, String updateVersion, String tableName) {
        super(database, updateVersion, tableName);
    }

    public void update() throws InterruptedException, SQLException, ClassNotFoundException {
        try {
            log.info(Reporter.getDefaultConsolePrefix() + String.format("Updating table [%s]...", this.getTableName()));
            this.updateTable();
        }
        catch (InterruptedException e) {
            log.warn(String.format("Failed to update table [%s]!", this.getTableName()));
            throw e;
        }
        catch (SQLException e) {
            log.warn(String.format("Failed to update table [%s]!", this.getTableName()));
            throw e;
        }
        catch (ClassNotFoundException e) {
            log.warn(String.format("Failed to update table [%s]!", this.getTableName()));
            throw e;
        }
        finally {
            try {
                this.commitTransaction();
            }
            catch (IllegalStateException e) {
                log.error("Failed to commit update transaction!");
                throw e;
            }
            catch (SQLException e) {
                log.error("Failed to commit update transaction!");
                throw e;
            }
        }
    }

    private void updateTable() throws InterruptedException, SQLException, ClassNotFoundException {
        for (DatabaseTableVersionUpdater versionUpdater : this.getDatabaseTableVersionUpdaters()) {
            versionUpdater.update();
        }
    }

    public abstract List<DatabaseTableVersionUpdater> getDatabaseTableVersionUpdaters();
}

