/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.File;

import java.util.Date;

public abstract class AbstractFile {
    public static final String defaultCharset = "UTF-8";
    private final String name;
    private final String encoding;
    private final String extension;
    private final String fileName;
    private Date modificationDate;

    public AbstractFile(String fileName) {
        this.name = AbstractFile.getName(fileName);
        this.extension = AbstractFile.getExtension(fileName);
        this.fileName = fileName;
        this.encoding = defaultCharset;
        this.modificationDate = null;
    }

    public AbstractFile(String fileName, String encoding) {
        this.name = AbstractFile.getName(fileName);
        this.extension = AbstractFile.getExtension(fileName);
        this.fileName = fileName;
        this.encoding = encoding;
        this.modificationDate = null;
    }

    public AbstractFile(String name, String extension, String fileName) {
        this.name = name;
        this.encoding = defaultCharset;
        this.extension = extension;
        this.fileName = fileName;
        this.modificationDate = null;
    }

    public AbstractFile(String name, String extension, String fileName, String encoding) {
        this.name = name;
        this.encoding = encoding;
        this.extension = extension;
        this.fileName = fileName;
        this.modificationDate = null;
    }

    public AbstractFile(String name, String extension, String fileName, Date modificationDate) {
        this.name = name;
        this.extension = extension;
        this.encoding = defaultCharset;
        this.fileName = fileName;
        this.modificationDate = modificationDate;
    }

    public AbstractFile(String name, String extension, String fileName, String encoding, Date modificationDate) {
        this.name = name;
        this.extension = extension;
        this.fileName = fileName;
        this.encoding = encoding;
        this.modificationDate = modificationDate;
    }

    private static String getName(String fileName) {
        int index = fileName.lastIndexOf(46);
        if (index != -1) {
            return fileName.substring(0, index);
        }
        return fileName;
    }

    private static String getExtension(String fileName) {
        int index = fileName.lastIndexOf(46);
        if (index != -1 && index + 1 < fileName.length()) {
            return fileName.substring(index + 1, fileName.length());
        }
        return "";
    }

    public String getName() {
        return this.name;
    }

    public Date getModificationDate() {
        return this.modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getExtension() {
        return this.extension;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String toString() {
        String string = "Name: " + this.getName();
        string = string + "\nExtension: " + this.getExtension();
        string = string + "\nFile Name: " + this.getFileName();
        string = string + "\nEncoding: " + this.encoding;
        string = string + "\nModification Date: " + this.getModificationDate();
        return string;
    }
}

