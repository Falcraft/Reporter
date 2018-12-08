/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.File;

import java.io.File;
import java.io.IOException;

public class ExtendedFile {
    private File file;

    public ExtendedFile(File parent, String child) {
        this.file = new File(parent, child);
    }

    public ExtendedFile(String parent, String child) {
        this.file = new File(parent, child);
    }

    public ExtendedFile(String file) {
        this.file = new File(file);
    }

    public void setParent(File parent) {
        this.file = new File(parent, this.file.getName());
    }

    public File getFile() {
        return this.file;
    }

    public boolean createNewFile() throws IOException {
        return this.file.createNewFile();
    }

    public boolean renameTo(File dest) {
        return this.file.renameTo(dest);
    }

    public boolean delete() {
        return this.file.delete();
    }

    public boolean exists() {
        return this.file.exists();
    }

    public String getName() {
        return this.file.getName();
    }

    public void setName(String child) {
        String parent = this.file.getParent();
        this.file = parent != null ? new File(this.file.getParent(), child) : new File(child);
    }

    public String toString() {
        String str = "Current File: " + this.file.getAbsolutePath();
        str = str + "\nParent: " + this.file.getParent();
        str = str + "\nChild: " + this.file.getName();
        return str;
    }
}

