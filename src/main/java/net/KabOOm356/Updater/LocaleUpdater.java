/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.KabOOm356.File.RevisionFile;
import net.KabOOm356.File.AbstractFiles.UpdateSite;
import net.KabOOm356.File.AbstractFiles.VersionedNetworkFile;
import net.KabOOm356.Reporter.Reporter;
import net.KabOOm356.Util.FileIO;
import net.KabOOm356.Util.UrlIO;
import net.KabOOm356.Util.Util;

public class LocaleUpdater extends Updater {
    private static final Logger log = LogManager.getLogger(LocaleUpdater.class);

    public LocaleUpdater(UpdateSite updateSite, String name, String localVersion,
            VersionedNetworkFile.ReleaseLevel lowestLevel) throws IOException {
        super(updateSite, name, localVersion, lowestLevel);
    }

    @Override
    public VersionedNetworkFile findLatestFile() throws SAXException, IOException, ParserConfigurationException {
        URLConnection connection = this.getConnection();
        String name = this.getName();
        VersionedNetworkFile.ReleaseLevel lowestLevel = this.getLowestLevel();
        if (connection == null) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Both the connection and the name cannot be null!");
            }
            throw new IllegalArgumentException("The connection cannot be null!");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("File name to search for cannot be null!");
        }
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(connection.getInputStream());
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("locale");
        VersionedNetworkFile file = null;
        VersionedNetworkFile latestFile = null;
        for (int LCV = 0; LCV < nodeList.getLength(); ++LCV) {
            String fileName;
            String version;
            Element element;
            String encoding;
            String link;
            Node nNode = nodeList.item(LCV);
            if (nNode.getNodeType() != 1 || !Util.startsWithIgnoreCase(UrlIO.getNodeValue(element = (Element)nNode, "file_name"), name) || (file = new VersionedNetworkFile(fileName = UrlIO.getNodeValue(element, "file_name"), version = UrlIO.getNodeValue(element, "version"), encoding = UrlIO.getNodeValue(element, "encoding"), link = UrlIO.getNodeValue(element, "download_link"))).getReleaseLevel().compareToByValue(lowestLevel) < 0 || latestFile != null && latestFile.compareVersionTo(file) >= 0) continue;
            latestFile = file;
        }
        if (latestFile == null) {
            throw new FileNotFoundException("File " + name + " could not be found!");
        }
        return latestFile;
    }

    public boolean localeDownloadProcess(File destination) throws ParserConfigurationException, SAXException, IOException {
        String localeName = this.getName();
        if (localeName.contains(".yml")) {
            localeName = localeName.substring(0, localeName.indexOf(".yml"));
        }
        log.log(Level.INFO, Reporter.getDefaultConsolePrefix() + "Checking for file: " + destination.getName());
        VersionedNetworkFile downloadFile = null;
        try {
            downloadFile = this.findLatestFile();
        }
        catch (FileNotFoundException e) {
            log.log(Level.WARN, Reporter.getDefaultConsolePrefix() + "Could not find the locale file " + localeName + ".yml!", (Throwable)e);
        }
        if (downloadFile != null) {
            log.log(Level.INFO, Reporter.getDefaultConsolePrefix() + "Downloading the locale file: " + localeName + ".yml version " + downloadFile.getVersion() + "...");
            UrlIO.downloadFile(downloadFile, destination);
            log.log(Level.INFO, Reporter.getDefaultConsolePrefix() + "Locale file successfully downloaded.");
            return true;
        }
        log.log(Level.WARN, Reporter.getDefaultConsolePrefix() + "Failed to download locale file!");
        log.log(Level.WARN, Reporter.getDefaultConsolePrefix() + "Using English default.");
        return false;
    }

    public boolean localeUpdateProcess(File destination) throws ParserConfigurationException, SAXException, IOException {
        String localeName = this.getName();
        if (localeName.contains(".yml")) {
            localeName = localeName.substring(0, localeName.indexOf(".yml"));
        }
        log.log(Level.INFO, Reporter.getDefaultConsolePrefix() + "Checking for update for file: " + destination.getName());
        VersionedNetworkFile updateNetworkFile = null;
        try {
            updateNetworkFile = this.checkForUpdates();
        }
        catch (FileNotFoundException e) {
            log.log(Level.WARN, Reporter.getDefaultConsolePrefix() + "Could not find the locale file " + localeName + ".yml!", (Throwable)e);
            log.log(Level.WARN, Reporter.getDefaultConsolePrefix() + "Failed to check for locale update!");
        }
        if (updateNetworkFile != null) {
            RevisionFile localeBackupFile = null;
            log.log(Level.INFO, Reporter.getDefaultConsolePrefix() + "Updating " + localeName + ".yml from version " + this.getLocalVersion() + " to version " + updateNetworkFile.getVersion() + "...");
            log.log(Level.INFO, Reporter.getDefaultConsolePrefix() + "Creating backup of the locale file...");
            localeBackupFile = FileIO.createBackup(destination);
            if (localeBackupFile != null) {
                log.log(Level.INFO, Reporter.getDefaultConsolePrefix() + "Locale backup successful.");
                log.log(Level.INFO, Reporter.getDefaultConsolePrefix() + "Locale backup created in file: " + localeBackupFile.getFileName());
                destination.delete();
            } else {
                log.log(Level.WARN, Reporter.getDefaultConsolePrefix() + "Creating backup unsuccessful.");
            }
            try {
                UrlIO.downloadFile(updateNetworkFile, destination);
            }
            catch (IOException e) {
                log.log(Level.WARN, Reporter.getDefaultConsolePrefix() + "Updating the locale file failed.", (Throwable)e);
                destination.delete();
                return false;
            }
            log.log(Level.INFO, Reporter.getDefaultConsolePrefix() + "Locale file successfully updated.");
            return true;
        }
        log.log(Level.INFO, Reporter.getDefaultConsolePrefix() + "Locale file is up to date.");
        return false;
    }
}

