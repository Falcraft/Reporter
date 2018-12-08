/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.configuration.file.FileConfiguration
 */
package net.KabOOm356.Reporter.Database;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import net.KabOOm356.Database.Connection.ConnectionPoolConfig;
import net.KabOOm356.Database.Database;
import net.KabOOm356.Database.DatabaseType;
import net.KabOOm356.Database.ExtendedDatabaseHandler;
import net.KabOOm356.Reporter.Database.Table.Initializer.ModStatsTableInitializer;
import net.KabOOm356.Reporter.Database.Table.Initializer.PlayerStatsTableTableInitializer;
import net.KabOOm356.Reporter.Database.Table.Initializer.ReportTableInitializer;
import net.KabOOm356.Reporter.Reporter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.file.FileConfiguration;

public final class ReporterDatabaseUtil {
    private static final Logger log = LogManager.getLogger(ReporterDatabaseUtil.class);

    private ReporterDatabaseUtil() {
    }

    public static ExtendedDatabaseHandler initDB(FileConfiguration configuration, File dataFolder) throws IllegalArgumentException, IOException, ClassNotFoundException, SQLException, InterruptedException {
        ConnectionPoolConfig connectionPoolConfig;
        ExtendedDatabaseHandler databaseHandler = null;
        boolean connectionPoolLimit = configuration.getBoolean("database.connectionPool.enableLimiting", ConnectionPoolConfig.defaultInstance.isConnectionPoolLimited());
        int maxNumberOfConnections = configuration.getInt("database.connectionPool.maxNumberOfConnections", ConnectionPoolConfig.defaultInstance.getMaxConnections());
        int maxNumberOfAttemptsForConnection = configuration.getInt("database.connectionPool.maxNumberOfAttemptsForConnection", ConnectionPoolConfig.defaultInstance.getMaxAttemptsForConnection());
        long waitTimeBeforeUpdate = configuration.getLong("database.connectionPool.waitTimeBeforeUpdate", ConnectionPoolConfig.defaultInstance.getWaitTimeBeforeUpdate());
        try {
            connectionPoolConfig = new ConnectionPoolConfig(connectionPoolLimit, maxNumberOfConnections, waitTimeBeforeUpdate, maxNumberOfAttemptsForConnection);
        }
        catch (IllegalArgumentException e) {
            log.warn("Failed to configure connection pool!");
            throw e;
        }
        boolean fallbackToNextDB = false;
        if (configuration.getString("database.type", DatabaseType.SQLITE.toString()).equalsIgnoreCase(DatabaseType.MYSQL.toString())) {
            try {
                log.log(Level.INFO, Reporter.getDefaultConsolePrefix() + "Connecting to MySQL server...");
                String host = configuration.getString("database.host", "localhost:3306");
                String database = configuration.getString("database.database", "Reporter");
                String username = configuration.getString("database.username", "root");
                String password = configuration.getString("database.password", "root");
                databaseHandler = new ExtendedDatabaseHandler(host, database, username, password, connectionPoolConfig);
                ReporterDatabaseUtil.checkConnection(databaseHandler.getDatabase());
                ReporterDatabaseUtil.initDatabaseTables(databaseHandler.getDatabase());
            }
            catch (Exception e) {
                databaseHandler = null;
                fallbackToNextDB = true;
                log.log(Level.ERROR, Reporter.getDefaultConsolePrefix() + "Error connecting to MySQL server using SQLite.", (Throwable)e);
            }
        } else {
            fallbackToNextDB = true;
        }
        if (fallbackToNextDB) {
            String databaseName = configuration.getString("database.dbName", "reports.db");
            try {
                databaseHandler = new ExtendedDatabaseHandler(DatabaseType.SQLITE, dataFolder.getPath(), databaseName, connectionPoolConfig);
                ReporterDatabaseUtil.initDatabaseTables(databaseHandler.getDatabase());
            }
            catch (IOException e) {
                log.warn(Reporter.getDefaultConsolePrefix() + "Failed to initialize an SQLite database!");
                throw e;
            }
            catch (ClassNotFoundException e) {
                log.warn(Reporter.getDefaultConsolePrefix() + "Failed to initialize an SQLite database!");
                throw e;
            }
            catch (SQLException e) {
                log.warn(Reporter.getDefaultConsolePrefix() + "Failed to initialize an SQLite database!");
                throw e;
            }
            catch (InterruptedException e) {
                log.warn(Reporter.getDefaultConsolePrefix() + "Failed to initialize an SQLite database!");
                throw e;
            }
        }
        return databaseHandler;
    }

    private static void initDatabaseTables(Database database) throws ClassNotFoundException, SQLException, InterruptedException {
        log.info(Reporter.getDefaultConsolePrefix() + "Checking " + (Object)((Object)database.getDatabaseType()) + " tables...");
        String databaseVersion = Reporter.getDatabaseVersion();
        new ReportTableInitializer(database, databaseVersion).initialize();
        new ModStatsTableInitializer(database, databaseVersion).initialize();
        new PlayerStatsTableTableInitializer(database, databaseVersion).initialize();
    }

    private static void checkConnection(Database database) throws InterruptedException, SQLException, ClassNotFoundException {
        Integer connectionId = null;
        try {
            connectionId = database.openPooledConnection();
            database.checkTable(connectionId, "Reports");
        }
        catch (InterruptedException e) {
            log.warn("Failed to check connection!");
            throw e;
        }
        catch (SQLException e) {
            log.warn("Failed to check connection!");
            throw e;
        }
        catch (ClassNotFoundException e) {
            log.warn("Failed to check connection!");
            throw e;
        }
        finally {
            database.closeConnection(connectionId);
        }
    }
}

