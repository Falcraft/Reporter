/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.configuration.Configuration
 */
package net.KabOOm356.Service.Store;

import net.KabOOm356.Database.ExtendedDatabaseHandler;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Permission.PermissionHandler;
import net.KabOOm356.Service.Messager.PlayerMessages;
import net.KabOOm356.Service.Store.Store;
import net.KabOOm356.Service.Store.type.LastViewed;
import net.KabOOm356.Service.Store.type.PlayerReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.Configuration;

public class StoreModule {
    private static final Logger log = LogManager.getLogger(StoreModule.class);
    private final Store<Configuration> configurationStore;
    private final Store<ExtendedDatabaseHandler> databaseStore;
    private final Store<Locale> localeStore;
    private final Store<PermissionHandler> permissionStore;
    private final Store<LastViewed> lastViewedStore;
    private final Store<PlayerMessages> playerMessagesStore;
    private final Store<PlayerReport> playerReportStore;

    public StoreModule(Configuration configuration, ExtendedDatabaseHandler database, Locale locale, PermissionHandler permission, LastViewed lastViewed, PlayerMessages playerMessages, PlayerReport playerReport) {
        if (log.isDebugEnabled()) {
            log.info("Initializing service store...");
        }
        this.configurationStore = new Store<Configuration>(configuration);
        this.databaseStore = new Store<ExtendedDatabaseHandler>(database);
        this.localeStore = new Store<Locale>(locale);
        this.permissionStore = new Store<PermissionHandler>(permission);
        this.lastViewedStore = new Store<LastViewed>(lastViewed);
        this.playerMessagesStore = new Store<PlayerMessages>(playerMessages);
        this.playerReportStore = new Store<PlayerReport>(playerReport);
    }

    public Store<Configuration> getConfigurationStore() {
        return this.configurationStore;
    }

    public Store<ExtendedDatabaseHandler> getDatabaseStore() {
        return this.databaseStore;
    }

    public Store<Locale> getLocaleStore() {
        return this.localeStore;
    }

    public Store<PermissionHandler> getPermissionStore() {
        return this.permissionStore;
    }

    public Store<LastViewed> getLastViewedStore() {
        return this.lastViewedStore;
    }

    public Store<PlayerMessages> getPlayerMessagesStore() {
        return this.playerMessagesStore;
    }

    public Store<PlayerReport> getPlayerReportStore() {
        return this.playerReportStore;
    }
}

