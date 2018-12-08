/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.File.AbstractFiles;

import net.KabOOm356.File.AbstractFiles.NetworkFile;

public class UpdateSite
extends NetworkFile {
    private final Type type;

    public UpdateSite(String url, Type type) {
        super(url);
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public String toString() {
        String string = super.toString();
        string = string + "\nType: " + (Object)((Object)this.type);
        return string;
    }

    public static enum Type {
        RSS("RSS"),
        XML("XML");
        
        private final String name;

        private Type(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

}

