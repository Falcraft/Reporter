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
import net.KabOOm356.Database.Table.Version.DatabaseTableVersionMigrator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DatabaseTableMigrator
extends DatabaseTableUpdateHandler {
    private static final Logger log = LogManager.getLogger(DatabaseTableMigrator.class);

    protected DatabaseTableMigrator(Database database, String databaseVersion, String tableName) {
        super(database, databaseVersion, tableName);
    }

    public void migrate() throws InterruptedException, SQLException, ClassNotFoundException {
        try {
            this.migrateTable();
        }
        catch (InterruptedException e) {
            log.warn(String.format("Failed to migrate table [%s]!", this.getTableName()));
            throw e;
        }
        catch (SQLException e) {
            log.warn(String.format("Failed to migrate table [%s]!", this.getTableName()));
            throw e;
        }
        catch (ClassNotFoundException e) {
            log.warn(String.format("Failed to migrate table [%s]!", this.getTableName()));
            throw e;
        }
    }

    private void migrateTable() throws InterruptedException, SQLException, ClassNotFoundException {
        for (DatabaseTableVersionMigrator versionMigrator : this.getDatabaseTableVersionMigrators()) {
            versionMigrator.migrate();
        }
    }

    public abstract List<DatabaseTableVersionMigrator> getDatabaseTableVersionMigrators();
}

