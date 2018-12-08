/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package net.KabOOm356.Reporter.Locale;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import net.KabOOm356.File.AbstractFiles.UpdateSite;
import net.KabOOm356.File.AbstractFiles.VersionedNetworkFile;
import net.KabOOm356.File.RevisionFile;
import net.KabOOm356.Locale.Locale;
import net.KabOOm356.Reporter.Reporter;
import net.KabOOm356.Updater.LocaleUpdater;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.xml.sax.SAXException;

public class ReporterLocaleInitializer
implements Runnable {
    private static final Logger log = LogManager.getLogger(ReporterLocaleInitializer.class);
    private final Reporter plugin;
    private final String localeName;
    private final File dataFolder;
    private final boolean autoDownload;
    private final VersionedNetworkFile.ReleaseLevel lowestLevel;
    private final boolean keepBackup;
    private final Locale locale;
    private boolean update = false;

    public ReporterLocaleInitializer(Reporter plugin, String localeName, File dataFolder, boolean autoDownload, VersionedNetworkFile.ReleaseLevel lowestLevel, boolean keepBackup) {
        this.plugin = plugin;
        this.locale = plugin.getLocale();
        this.localeName = localeName;
        this.dataFolder = dataFolder;
        this.autoDownload = autoDownload;
        this.lowestLevel = lowestLevel;
        this.keepBackup = keepBackup;
    }

    private static File getLocaleFile(File dataFolder, String localeName) {
        if (localeName.contains(".")) {
            localeName = localeName.substring(0, localeName.indexOf(46));
        }
        return new File(dataFolder, localeName + ".yml");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        Locale locale = this.locale;
        synchronized (locale) {
            this.initLocale();
            this.locale.notify();
        }
        this.plugin.loadLocale();
    }

    public Locale initLocale() {
        if (this.locale.isInitialized()) {
            return this.locale;
        }
        if (this.localeName.equalsIgnoreCase("en_US")) {
            this.locale.initialized();
            return this.locale;
        }
        File localeFile = ReporterLocaleInitializer.getLocaleFile(this.dataFolder, this.localeName);
        boolean downloaded = false;
        if (this.autoDownload) {
            try {
                downloaded = this.downloadOrUpdate(localeFile);
            }
            catch (Exception e) {
                log.error(Reporter.getDefaultConsolePrefix() + "Error downloading or updating the locale file!", (Throwable)e);
            }
        } else if (!localeFile.exists()) {
            log.warn(Reporter.getDefaultConsolePrefix() + "Locale file " + this.localeName + " does not exist locally!");
            log.warn(Reporter.getDefaultConsolePrefix() + "Try setting locale.updates.autoDownload to true in the configuration.");
            log.warn(Reporter.getDefaultConsolePrefix() + "Using English default.");
        }
        if (!localeFile.exists()) {
            localeFile = null;
        }
        RevisionFile localeBackupFile = new RevisionFile(this.dataFolder, this.localeName + ".yml" + ".backup");
        localeBackupFile.incrementToLatestRevision();
        try {
            if (localeFile != null) {
                log.info(Reporter.getDefaultConsolePrefix() + "Loading locale file: " + localeFile.getName());
                this.locale.load(localeFile);
                if (this.update && downloaded) {
                    if (!this.keepBackup) {
                        log.info(Reporter.getDefaultConsolePrefix() + "Purging backup file " + localeBackupFile.getFileName());
                        localeBackupFile.delete();
                    } else {
                        log.info(Reporter.getDefaultConsolePrefix() + "Retaining backup file " + localeBackupFile.getFileName());
                    }
                }
            }
        }
        catch (Exception e) {
            log.log(Level.ERROR, Reporter.getDefaultConsolePrefix() + "There was an error loading " + localeFile.getName(), (Throwable)e);
            if (e.getMessage().contains("unacceptable character")) {
                log.warn(Reporter.getDefaultConsolePrefix() + "Try converting the file to UTF-8 without BOM (Byte Order Marks) then try to reload it.");
            } else {
                log.warn(Reporter.getDefaultConsolePrefix() + "Please let the author know this.");
            }
            if (this.update) {
                this.restoreBackup(localeFile, localeBackupFile);
            }
            log.warn(Reporter.getDefaultConsolePrefix() + "Using English default.");
        }
        this.locale.initialized();
        return this.locale;
    }

    private LocaleUpdater initUpdater(File localeFile) throws IOException {
        String localLocaleVersion = "1";
        try {
            YamlConfiguration localLocale = YamlConfiguration.loadConfiguration((File)localeFile);
            localLocaleVersion = localLocale.getString("locale.info.version", "1");
        }
        catch (Exception e) {
            log.log(Level.WARN, "Failed to pre-load locale file!", (Throwable)e);
        }
        UpdateSite localeXMLUpdateSite = Reporter.getLocaleXMLUpdateSite();
        return new LocaleUpdater(localeXMLUpdateSite, this.localeName, localLocaleVersion, this.lowestLevel);
    }

    private boolean downloadOrUpdate(File localeFile) throws ParserConfigurationException, SAXException, IOException {
        LocaleUpdater updater = this.initUpdater(localeFile);
        if (localeFile.exists()) {
            this.update = true;
            return updater.localeUpdateProcess(localeFile);
        }
        this.update = false;
        return updater.localeDownloadProcess(localeFile);
    }

    private void restoreBackup(File localeFile, RevisionFile localeBackupFile) {
        if (!localeBackupFile.exists()) {
            log.warn(Reporter.getDefaultConsolePrefix() + "The backup file does not exist.");
            log.warn(Reporter.getDefaultConsolePrefix() + "Using English default.");
        } else {
            boolean loadSuccessful = this.attemptToLoadBackups(localeFile, localeBackupFile);
            if (loadSuccessful) {
                log.info(Reporter.getDefaultConsolePrefix() + "Successfully restored and loaded backup file.");
            } else {
                localeFile.delete();
                log.warn(Reporter.getDefaultConsolePrefix() + "Failed to restore backups.");
                log.warn(Reporter.getDefaultConsolePrefix() + "Using English default.");
            }
        }
    }

    private boolean attemptToLoadBackups(File localeFile, RevisionFile localeBackupFile) {
        boolean loadSuccessful = false;
        localeBackupFile.incrementToLatestRevision();
        do {
            log.info(Reporter.getDefaultConsolePrefix() + "Attempting to restore backup revision: " + localeBackupFile.getRevision());
            localeBackupFile.renameTo(localeFile);
            localeBackupFile.delete();
            try {
                this.locale.load(localeFile);
            }
            catch (Exception e) {
                log.warn(Reporter.getDefaultConsolePrefix() + "Failed to load backup revision: " + localeBackupFile.getRevision(), (Throwable)e);
                localeBackupFile.decrementRevision();
                loadSuccessful = false;
            }
        } while (!loadSuccessful && localeBackupFile.exists());
        return loadSuccessful;
    }
}

