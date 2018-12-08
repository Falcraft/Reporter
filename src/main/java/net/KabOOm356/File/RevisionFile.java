/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.File;

import java.io.File;
import net.KabOOm356.File.ExtendedFile;

public class RevisionFile
extends ExtendedFile {
    private final String name;
    private int revision;

    public RevisionFile(File parent, String child, int revision) {
        super(parent, child);
        if (revision < 0) {
            throw new IllegalArgumentException("Revision number must be greater than zero!");
        }
        this.name = child;
        this.revision = revision;
        this.setRevision(revision);
    }

    public RevisionFile(File parent, String child) {
        super(parent, child);
        this.name = child;
        this.revision = 0;
        this.setRevision(this.revision);
    }

    public RevisionFile(String parent, String child) {
        super(parent, child);
        this.name = child;
        this.revision = 0;
        this.setRevision(0);
    }

    public RevisionFile(String file) {
        super(file);
        this.name = file;
        this.revision = 0;
        this.setRevision(0);
    }

    public int getRevision() {
        return this.revision;
    }

    public boolean setRevision(int revision) {
        if (revision < 0) {
            throw new IllegalArgumentException("Revision number must be greater than zero!");
        }
        this.revision = revision;
        if (revision != 0) {
            int index = this.name.indexOf(46);
            if (index != -1) {
                this.setName(this.name.substring(0, index) + Integer.toString(revision) + this.name.substring(index));
            } else {
                this.setName(this.name + Integer.toString(revision));
            }
        } else {
            this.setName(this.name);
        }
        return this.exists();
    }

    public boolean incrementRevision() {
        this.setRevision(this.revision + 1);
        return super.exists();
    }

    public boolean decrementRevision() {
        if (this.revision != 0) {
            this.setRevision(this.revision - 1);
        }
        return super.exists();
    }

    public void incrementToLatestRevision() {
        this.incrementToNextRevision();
        this.decrementRevision();
    }

    public void incrementToNextRevision() {
        if (this.exists()) {
            while (this.incrementRevision()) {
            }
        }
    }

    public void toBaseRevision() {
        this.setRevision(0);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getFileName() {
        return super.getName();
    }

    @Override
    public String toString() {
        String str = super.toString() + '\n';
        str = str + "Revision of file: " + this.name;
        str = str + "\nRevision Number: " + this.revision;
        return str;
    }
}

