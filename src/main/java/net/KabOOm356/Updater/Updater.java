/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.KabOOm356.Updater;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.ParserConfigurationException;
import net.KabOOm356.File.AbstractFiles.UpdateSite;
import net.KabOOm356.File.AbstractFiles.VersionedNetworkFile;
import net.KabOOm356.Util.UrlIO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

public abstract class Updater
implements Runnable {
    private static final Logger log = LogManager.getLogger(Updater.class);
    private final URL url;
    private final URLConnection connection;
    private String name;
    private String localVersion;
    private VersionedNetworkFile.ReleaseLevel lowestLevel;

    protected Updater(UpdateSite updateSite, String name, String localVersion, VersionedNetworkFile.ReleaseLevel lowestLevel) throws IOException {
        if (updateSite == null) {
            throw new IllegalArgumentException("The update site cannot be null!");
        }
        if (updateSite.getURL() == null) {
            throw new IllegalArgumentException("The url from the update site cannot be null!");
        }
        this.name = name;
        this.localVersion = localVersion;
        this.lowestLevel = lowestLevel;
        this.url = new URL(updateSite.getURL());
        this.connection = this.url.openConnection();
    }

    protected Updater(URLConnection connection, String name, String localVersion, VersionedNetworkFile.ReleaseLevel lowestLevel) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null!");
        }
        this.name = name;
        this.localVersion = localVersion;
        this.lowestLevel = lowestLevel;
        this.connection = connection;
        this.url = connection.getURL();
    }

    protected abstract VersionedNetworkFile findLatestFile() throws SAXException, IOException, ParserConfigurationException;

    public VersionedNetworkFile checkForUpdates() throws IOException, SAXException, ParserConfigurationException {
        VersionedNetworkFile remoteFile;
        if (this.connection != null && UrlIO.isResponseValid(this.connection) && VersionedNetworkFile.compareVersionTo(this.localVersion, (remoteFile = this.findLatestFile()).getVersion()) < 0) {
            return remoteFile;
        }
        return null;
    }

    @Override
    public void run() {
        try {
            VersionedNetworkFile latestFile = this.checkForUpdates();
            if (latestFile == null) {
                System.out.println(this.name + " is up to date!");
            } else if (latestFile.getVersion() != null) {
                System.out.println("There is a new update available for " + this.name + ": Version " + latestFile.getVersion());
            } else {
                System.out.println("There is a new update available for " + this.name + '!');
            }
        }
        catch (Exception e) {
            log.log(Level.FATAL, this.name + " update thread failed!", (Throwable)e);
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalVersion() {
        return this.localVersion;
    }

    public void setLocalVersion(String localVersion) {
        this.localVersion = localVersion;
    }

    public VersionedNetworkFile.ReleaseLevel getLowestLevel() {
        return this.lowestLevel;
    }

    public void setLowestLevel(VersionedNetworkFile.ReleaseLevel lowestLevel) {
        this.lowestLevel = lowestLevel;
    }

    public URL getUrl() {
        return this.url;
    }

    public URLConnection getConnection() {
        return this.connection;
    }
}

