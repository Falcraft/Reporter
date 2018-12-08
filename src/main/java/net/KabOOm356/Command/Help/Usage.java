/*
 * Decompiled with CFR 0_132.
 */
package net.KabOOm356.Command.Help;

import net.KabOOm356.Configuration.ConstantEntry;
import net.KabOOm356.Configuration.Entry;
import net.KabOOm356.Util.ObjectPair;

public class Usage
extends ObjectPair<Entry<String>, Entry<String>> {
    public Usage(Entry<String> key, Entry<String> value) {
        super(key, value);
    }

    public Usage(String key, Entry<String> value) {
        super(new ConstantEntry<String>(key), value);
    }

    public Usage(Entry<String> key, String value) {
        super(key, new ConstantEntry<String>(value));
    }

    public Usage(String key, String value) {
        super(new ConstantEntry<String>(key), new ConstantEntry<String>(value));
    }
}

