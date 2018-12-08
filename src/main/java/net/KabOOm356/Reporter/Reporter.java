package net.KabOOm356.Reporter;

import java.io.Reader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.KabOOm356.Command.ReporterCommandManager;
import net.KabOOm356.Database.ExtendedDatabaseHandler;
import net.KabOOm356.File.AbstractFiles.UpdateSite;
import net.KabOOm356.File.AbstractFiles.VersionedNetworkFile;
import net.KabOOm356.Listeners.ReporterPlayerListener;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Locale.Entry.LocaleInfo;
import net.KabOOm356.Permission.PermissionHandler;
import net.KabOOm356.Reporter.Configuration.ReporterConfigurationUtil;
import net.KabOOm356.Reporter.Database.ReporterDatabaseUtil;
import net.KabOOm356.Reporter.Locale.ReporterLocaleInitializer;
import net.KabOOm356.Service.ServiceModule;
import net.KabOOm356.Service.Messager.PlayerMessages;
import net.KabOOm356.Service.Store.StoreModule;
import net.KabOOm356.Service.Store.type.LastViewed;
import net.KabOOm356.Service.Store.type.PlayerReport;

public class Reporter
extends JavaPlugin {
    public static final String localeVersion = "11";
    public static final String configVersion = "15";
    public static final String databaseVersion = "10";
    public static final String anonymousPlayerName = "* (Anonymous)";
    public static final String console = "CONSOLE";
    private static final Logger log = LogManager.getLogger(Reporter.class);
    private static final String logPrefix = "[Reporter] ";
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final UpdateSite localeXMLUpdateSite = new UpdateSite(
            "https://www.dropbox.com/s/m75q8xsvc1swys0/latest.xml?dl=1", UpdateSite.Type.XML);
    private static String version;
    private static String defaultConsolePrefix;
    private static String versionString;
    private final Locale locale = new Locale();
    private ExtendedDatabaseHandler databaseHandler;
    private PermissionHandler permissionHandler;
    private ReporterPlayerListener playerListener;
    private ServiceModule serviceModule;
    private ReporterCommandManager commandManager;

    public Reporter() {
        version = this.getDescription().getVersion();
        versionString = "" + 'v' + version + " - ";
        defaultConsolePrefix = logPrefix + versionString;
    }

    public static String getLogPrefix() {
        return logPrefix;
    }

    public static String getVersion() {
        return version;
    }

    public static String getVersionString() {
        return versionString;
    }

    public static String getDefaultConsolePrefix() {
        return defaultConsolePrefix;
    }

    public static boolean isCommandSenderSupported(CommandSender cs) {
        return cs instanceof Player || cs instanceof ConsoleCommandSender || cs instanceof RemoteConsoleCommandSender;
    }

    public static DateFormat getDateformat() {
        return dateFormat;
    }

    public static UpdateSite getLocaleXMLUpdateSite() {
        return localeXMLUpdateSite;
    }

    public static String getConfigurationVersion() {
        return configVersion;
    }

    public static String getDatabaseVersion() {
        return databaseVersion;
    }

    public void onEnable() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        URL defaultConfigurationFile = this.getDefaultConfigurationFile();
        ReporterConfigurationUtil.initConfiguration(defaultConfigurationFile, this.getDataFolder(), this.getConfig());
        if (ReporterConfigurationUtil.updateConfiguration(this.getConfig())) {
            this.saveConfig();
            this.reloadConfig();
        }
        this.initializeLocale();
        this.initializeDatabase();
        this.initializePermissions();
        this.playerListener = new ReporterPlayerListener(this);
        LastViewed lastViewed = new LastViewed();
        PlayerMessages playerMessages = new PlayerMessages();
        PlayerReport playerReport = new PlayerReport();
        StoreModule storeModule = new StoreModule((Configuration)this.getConfig(), this.getDatabaseHandler(), this.getLocale(), this.getPermissionHandler(), lastViewed, playerMessages, playerReport);
        this.serviceModule = new ServiceModule(storeModule);
        this.commandManager = new ReporterCommandManager(this);
        this.setupCommands();
        this.getServer().getPluginManager().registerEvents((Listener)this.playerListener, (Plugin)this);
        log.info(defaultConsolePrefix + "Reporter enabled.");
    }

    public void onDisable() {
        log.info(defaultConsolePrefix + "Stopping threads...");
        this.getServer().getScheduler().cancelTasks((Plugin)this);
        if (this.databaseHandler != null) {
            log.info(defaultConsolePrefix + "Closing " + (Object)((Object)this.databaseHandler.getDatabaseType()) + " connections...");
            this.databaseHandler.closeConnections();
        }
        log.info(defaultConsolePrefix + "Reporter disabled.");
    }

    private void setupCommands() {
        String[] cmds = new String[]{"report", "rreport", "rep", "respond", "rrespond", "resp"};
        PluginCommand cmd = null;
        boolean error = false;
        for (String currentCmd : cmds) {
            cmd = this.getCommand(currentCmd);
            if (cmd != null) {
                cmd.setExecutor((CommandExecutor)this.commandManager);
                continue;
            }
            log.error(defaultConsolePrefix + "Unable to set executor for " + currentCmd + " command!");
            error = true;
        }
        if (error) {
            log.warn(defaultConsolePrefix + "plugin.yml may have been altered!");
            log.warn(defaultConsolePrefix + "Please re-download the plugin from BukkitDev.");
        }
    }

    private void initializeLocale() {
        String localeName = this.getConfig().getString("locale.locale", "en_US");
        boolean asynchronousUpdate = this.getConfig().getBoolean("locale.updates.asynchronousUpdate", true);
        boolean autoDownload = this.getConfig().getBoolean("locale.updates.autoDownload", true);
        boolean keepBackup = this.getConfig().getBoolean("locale.updates.keepBackup", false);
        VersionedNetworkFile.ReleaseLevel localeLevel = VersionedNetworkFile.ReleaseLevel.getByName(this.getConfig().getString("locale.updates.releaseLevel", "RELEASE"));
        ReporterLocaleInitializer localeInitializer = new ReporterLocaleInitializer(this, localeName, this.getDataFolder(), autoDownload, localeLevel, keepBackup);
        if (asynchronousUpdate) {
            this.getServer().getScheduler().runTaskAsynchronously((Plugin)this, (Runnable)localeInitializer);
        } else {
            localeInitializer.initLocale();
            this.loadLocale();
        }
    }

    private void initializeDatabase() {
        try {
            this.databaseHandler = ReporterDatabaseUtil.initDB(this.getConfig(), this.getDataFolder());
        }
        catch (Exception e) {
            log.fatal(Reporter.getDefaultConsolePrefix() + "Failed to initialize the database!", (Throwable)e);
        }
        if (this.databaseHandler == null) {
            log.fatal(Reporter.getDefaultConsolePrefix() + "Disabling plugin!");
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
        }
    }

    private void initializePermissions() {
        this.permissionHandler = new PermissionHandler();
    }

    public void loadLocale() {
        if (!this.setLocaleDefaults(this.locale)) {
            log.warn(Reporter.getDefaultConsolePrefix() + "Unable to set defaults for the locale!");
        }
        log.info(Reporter.getDefaultConsolePrefix() + "Language: " + this.locale.getString(LocaleInfo.language) + " v" + this.locale.getString(LocaleInfo.version) + " By " + this.locale.getString(LocaleInfo.author));
    }

    private boolean setLocaleDefaults(Locale locale) {
        Reader defaultLocaleReader = this.getTextResource("en_US.yml");
        if (defaultLocaleReader != null) {
            YamlConfiguration defaultLocale = new YamlConfiguration();
            try {
                defaultLocale.load(defaultLocaleReader);
                locale.setDefaults((Configuration)defaultLocale);
                return true;
            }
            catch (Exception e) {
                log.warn(Reporter.getDefaultConsolePrefix() + "Unable to read the default locale file!", (Throwable)e);
            }
        } else {
            log.warn(Reporter.getDefaultConsolePrefix() + "Unable to find the default locale file!");
        }
        return false;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public ExtendedDatabaseHandler getDatabaseHandler() {
        return this.databaseHandler;
    }

    public PermissionHandler getPermissionHandler() {
        return this.permissionHandler;
    }

    public ServiceModule getServiceModule() {
        return this.serviceModule;
    }

    public ReporterCommandManager getCommandManager() {
        return this.commandManager;
    }

    private URL getDefaultConfigurationFile() {
        return this.getClassLoader().getResource("config.yml");
    }
}

