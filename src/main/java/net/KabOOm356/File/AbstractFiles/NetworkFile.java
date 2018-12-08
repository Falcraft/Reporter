/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.File.AbstractFiles;

import java.util.Date;
import net.KabOOm356.File.AbstractFile;

public class NetworkFile
extends AbstractFile {
    private String url;

    public NetworkFile(String url) {
        super(NetworkFile.getFileName(url));
        this.url = url;
    }

    public NetworkFile(String fileName, String encoding, String url) {
        super(fileName, encoding);
        this.url = url;
    }

    public NetworkFile(String fileName, String url) {
        super(fileName);
        this.url = url;
    }

    public NetworkFile(String name, String extension, String fileName, String url) {
        super(name, extension, fileName);
        this.url = url;
    }

    public NetworkFile(String name, String extension, String fileName, String encoding, String url) {
        super(name, extension, fileName, encoding);
        this.url = url;
    }

    public NetworkFile(String name, String extension, String fileName, Date modificationDate, String url) {
        super(name, extension, fileName, modificationDate);
        this.url = url;
    }

    public NetworkFile(String name, String extension, String fileName, Date modificationDate) {
        super(name, extension, fileName, modificationDate);
        this.url = null;
    }

    public NetworkFile(String name, String extension, String fileName, String encoding, Date modificationDate, String url) {
        super(name, extension, fileName, encoding, modificationDate);
        this.url = url;
    }

    private static String getFileName(String url) {
        int index = url.lastIndexOf(47);
        if (index != -1 && index + 1 < url.length()) {
            return url.substring(index);
        }
        return url;
    }

    public String getURL() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        String string = super.toString();
        string = string + "\nURL: " + this.url;
        return string;
    }
}

