/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Configuration;

public class Entry<T> {
    private final String path;
    private final T def;

    public Entry(String path, T def) {
        this.path = path;
        this.def = def;
    }

    public String getPath() {
        return this.path;
    }

    public T getDefault() {
        return this.def;
    }

    public String toString() {
        return "Path: " + this.getPath() + "\nDefault: " + this.getDefault();
    }
}

