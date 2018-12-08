/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Database.Table;

import java.sql.SQLException;
import net.KabOOm356.Database.Table.DatabaseTableCreator;
import net.KabOOm356.Database.Table.DatabaseTableMigrator;
import net.KabOOm356.Database.Table.DatabaseTableUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class DatabaseTableInitializer {
    private static final Logger log = LogManager.getLogger(DatabaseTableInitializer.class);

    public void initialize() throws InterruptedException, SQLException, ClassNotFoundException {
        if (log.isDebugEnabled()) {
            log.trace("Begin table initialization!");
        }
        try {
            this.create();
            this.migrate();
            this.update();
        }
        catch (InterruptedException e) {
            log.warn("Failed to initialize table!");
            throw e;
        }
        catch (SQLException e) {
            log.warn("Failed to initialize table!");
            throw e;
        }
        catch (ClassNotFoundException e) {
            log.warn("Failed to initialize table!");
            throw e;
        }
    }

    protected void create() throws InterruptedException, SQLException, ClassNotFoundException {
        DatabaseTableCreator creator = this.getCreator();
        if (creator != null) {
            creator.create();
        } else if (log.isDebugEnabled()) {
            log.warn("No table creator given!");
        }
    }

    protected void migrate() throws InterruptedException, SQLException, ClassNotFoundException {
        DatabaseTableMigrator migrator = this.getMigrator();
        if (migrator != null) {
            migrator.migrate();
        } else if (log.isDebugEnabled()) {
            log.warn("No table migrator given!");
        }
    }

    protected void update() throws InterruptedException, SQLException, ClassNotFoundException {
        DatabaseTableUpdater updater = this.getUpdater();
        if (updater != null) {
            updater.update();
        } else if (log.isDebugEnabled()) {
            log.warn("No table updater given!");
        }
    }

    protected abstract DatabaseTableCreator getCreator();

    protected abstract DatabaseTableMigrator getMigrator();

    protected abstract DatabaseTableUpdater getUpdater();
}

