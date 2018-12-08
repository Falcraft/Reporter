/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Locale.Entry;

import net.KabOOm356.Locale.Entry.LocaleEntry;

public class LocaleInfo
extends LocaleEntry {
    public static final LocaleInfo language = new LocaleInfo("language", "English");
    public static final LocaleInfo version = new LocaleInfo("version", "11");
    public static final LocaleInfo author = new LocaleInfo("author", "KabOOm 356");
    public static final String prefix = "info.";

    public LocaleInfo(String path, String def) {
        super(prefix + path, def);
    }
}

