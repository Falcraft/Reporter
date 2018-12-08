/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Locale.Entry;

import net.KabOOm356.Configuration.Entry;

public class LocaleEntry
extends Entry<String> {
    public static final String prefix = "locale.";

    public LocaleEntry(String path, String def) {
        super(prefix + path, def);
    }
}

