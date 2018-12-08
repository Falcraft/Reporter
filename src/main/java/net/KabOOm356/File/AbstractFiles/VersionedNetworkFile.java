/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.File.AbstractFiles;

import java.util.ArrayList;
import java.util.Date;
import net.KabOOm356.File.AbstractFiles.NetworkFile;

public class VersionedNetworkFile
extends NetworkFile {
    private String version;
    private ArrayList<String> versions;
    private ReleaseLevel releaseLevel;
    private int releaseLevelVersion;

    public VersionedNetworkFile(String url) {
        super(url);
        this.version = "";
        this.setVersion(this.version);
    }

    public VersionedNetworkFile(String fileName, String version, String url) {
        super(fileName, url);
        this.setVersion(version);
    }

    public VersionedNetworkFile(String fileName, String url) {
        super(fileName, url);
        this.setVersion("");
    }

    public VersionedNetworkFile(String fileName, String version, String encoding, String url) {
        super(fileName, encoding, url);
        this.setVersion(version);
    }

    public VersionedNetworkFile(String name, String extension, String fileName, Date modificationDate, String url) {
        super(name, extension, fileName, modificationDate, url);
        this.setVersion("");
    }

    public VersionedNetworkFile(String name, String extension, String fileName, String version, Date modificationDate, String url) {
        super(name, extension, fileName, modificationDate, url);
        this.setVersion(version);
    }

    public VersionedNetworkFile(String name, String extension, String fileName, String version, String encoding, Date modificationDate, String url) {
        super(name, extension, fileName, encoding, modificationDate, url);
        this.setVersion(version);
    }

    private static ArrayList<String> separateVersion(String version) {
        String[] array;
        ArrayList<String> list = new ArrayList<String>();
        for (String str : array = version.split("[\\.]|[-]|[_]|[ ]")) {
            list.add(str.toLowerCase());
        }
        return list;
    }

    public static int compareVersionTo(String comp1, String comp2) {
        if (comp1 == null) {
            if (comp2 == null) {
                throw new IllegalArgumentException("Both versions to compare cannot be null!");
            }
            throw new IllegalArgumentException("First version cannot be null!");
        }
        if (comp2 == null) {
            throw new IllegalArgumentException("Second version cannot be null!");
        }
        VersionedNetworkFile f = new VersionedNetworkFile("temporary.tmp");
        f.setVersion(comp1);
        return f.compareVersionTo(comp2);
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
        ArrayList<String> separatedVersions = this.separateVersion();
        int lastIndex = separatedVersions.size() - 1;
        if (separatedVersions.contains("alpha")) {
            this.releaseLevel = ReleaseLevel.ALPHA;
            int index = separatedVersions.indexOf("alpha");
            if (index == lastIndex - 1) {
                this.releaseLevelVersion = this.parseInt(separatedVersions.remove(lastIndex));
            }
            separatedVersions.remove(index);
        } else if (separatedVersions.contains("beta")) {
            this.releaseLevel = ReleaseLevel.BETA;
            int index = separatedVersions.indexOf("beta");
            if (index == lastIndex - 1) {
                this.releaseLevelVersion = this.parseInt(separatedVersions.remove(lastIndex));
            }
            separatedVersions.remove(index);
        } else if (separatedVersions.contains("rc")) {
            this.releaseLevel = ReleaseLevel.RC;
            int index = separatedVersions.indexOf("rc");
            if (index == lastIndex - 1) {
                this.releaseLevelVersion = this.parseInt(separatedVersions.remove(lastIndex));
            }
            separatedVersions.remove(index);
        } else {
            this.releaseLevel = ReleaseLevel.RELEASE;
            this.releaseLevelVersion = 0;
        }
        this.versions = separatedVersions;
    }

    public int getMajorVersion() {
        return this.getVersion(0);
    }

    public int getMinorVersion() {
        return this.getVersion(1);
    }

    public int getFixVersion() {
        return this.getVersion(2);
    }

    public int getReleaseLevelVersion() {
        return this.releaseLevelVersion;
    }

    public int getVersion(int depth) {
        String versionNumber = this.versions.size() > depth ? this.versions.get(depth) : "0";
        return this.parseInt(versionNumber);
    }

    public ReleaseLevel getReleaseLevel() {
        return this.releaseLevel;
    }

    public void setReleaseLevel(ReleaseLevel level) {
        this.releaseLevel = level;
    }

    public ArrayList<String> getVersions() {
        return this.versions;
    }

    private ArrayList<String> separateVersion() {
        return VersionedNetworkFile.separateVersion(this.version);
    }

    public int compareVersionTo(VersionedNetworkFile comp) {
        if (comp == null) {
            throw new IllegalArgumentException("Object to compare to cannot be null!");
        }
        int difference = 0;
        int length = this.versions.size() > comp.getVersions().size() ? this.versions.size() : comp.getVersions().size();
        for (int LCV = length - 1; LCV >= 0; --LCV) {
            difference += (length - LCV + Math.abs(difference)) * (this.getVersion(LCV) - comp.getVersion(LCV));
        }
        if (difference == 0) {
            difference = this.getReleaseLevel().value - comp.getReleaseLevel().value;
        }
        if (difference == 0) {
            difference = this.getReleaseLevelVersion() - comp.getReleaseLevelVersion();
        }
        return difference;
    }

    public int compareVersionTo(String compVersion) {
        if (compVersion == null) {
            throw new IllegalArgumentException("Object to compare to cannot be null!");
        }
        VersionedNetworkFile comp = new VersionedNetworkFile("temporary.tmp");
        comp.setVersion(compVersion);
        return this.compareVersionTo(comp);
    }

    private int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        String string = super.toString();
        string = string + "\nVersion: " + this.version;
        string = string + "\nRelease Level: " + (Object)((Object)this.releaseLevel);
        if (this.releaseLevelVersion != 0) {
            string = string + " " + this.releaseLevelVersion;
        }
        return string;
    }

    public static enum ReleaseLevel {
        RELEASE("RELEASE", 4),
        RC("RC", 3),
        BETA("BETA", 2),
        ALPHA("ALPHA", 1),
        ANY("ANY", 0);
        
        final String name;
        final int value;

        private ReleaseLevel(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public static ReleaseLevel getByName(String name) {
            if (name.equalsIgnoreCase(ANY.getName())) {
                return ANY;
            }
            if (name.equalsIgnoreCase(ALPHA.getName())) {
                return ALPHA;
            }
            if (name.equalsIgnoreCase(BETA.getName())) {
                return BETA;
            }
            if (name.equalsIgnoreCase(RC.getName())) {
                return RC;
            }
            return RELEASE;
        }

        public String getName() {
            return this.name;
        }

        public int getValue() {
            return this.value;
        }

        public int compareToByValue(ReleaseLevel level) {
            return this.value - level.value;
        }

        public String toString() {
            return this.name;
        }
    }

}

